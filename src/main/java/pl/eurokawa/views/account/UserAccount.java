package pl.eurokawa.views.account;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import pl.eurokawa.data.User;
import pl.eurokawa.security.SecurityService;
import pl.eurokawa.services.UserService;
import pl.eurokawa.views.HomeView;
import pl.eurokawa.views.MainLayout;

import java.util.Optional;
//@UIScope
@Route(value = "/account/:userId?", layout = MainLayout.class)
public class UserAccount extends Div implements BeforeEnterObserver {

    private static final Logger log = LogManager.getLogger(UserAccount.class);
    private UserService userService;
    private SecurityService securityService;
    private String USER_ID = "userId";
    private User loggedUser;
    private BeanValidationBinder<User> binder;

    public UserAccount(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
        loggedUser = securityService.getLoggedUser();

        binder = new BeanValidationBinder<>(User.class);

        VerticalLayout layout = new VerticalLayout();
        VerticalLayout userContent = createUserInformations(loggedUser);



        log.info("Wchodze do UserAccount jako: " + loggedUser.toString());

        H1 h1 = new H1("Cześć, " + loggedUser.toString() + "!");
        h1.getStyle()
                .set("text-align", "center")
                .set("margin-bottom", "var(--lumo-space-l)");
        H1 h2 = new H1("Oto dane Twojego konta");
        h2.getStyle()
                .set("text-align", "center")
                .set("margin-bottom", "var(--lumo-space-l)");


        createActionButtons(layout);
        add(h1,h2,userContent,layout);
    }

    private void createActionButtons(VerticalLayout layout){
        Button save = new Button("Zapisz");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Anuluj");
        cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);

        save.addClickListener(event -> {
           try {
               if (this.loggedUser != null) {
                   binder.writeBean(this.loggedUser);
                   userService.save(this.loggedUser);

                   UI.getCurrent().navigate(HomeView.class);

                   Notification notification = Notification
                           .show("Dane zaktualizowane pomyślnie", 3000, Notification.Position.MIDDLE);
                   notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                   UI.getCurrent().navigate("/account");
               }
           } catch (DataIntegrityViolationException validationException){
                Notification notification = Notification
                        .show("Błąd! Sprawdź poprawność danych!",3000,Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException e) {
               throw new RuntimeException(e);
           }
        });

        cancel.addClickListener(event -> {
            binder.readBean(loggedUser);
        });

        layout.add(save,cancel);
    }

    private VerticalLayout createUserInformations(User loggedUser){

        VerticalLayout userInfo = new VerticalLayout();

        userInfo.setPadding(true);
        userInfo.setWidth("450px");
        userInfo.setHeight("100%");
        userInfo.getStyle().set("align-self", "flex-start");
        userInfo.getStyle().set("padding", "2rem");

        TextField firstNameField = new TextField("Imię");
        firstNameField.setValue(loggedUser.getFirstName());

        TextField lastNameField = new TextField("Nazwisko");
        lastNameField.setValue(loggedUser.getLastName());

        TextField emailField = new TextField("Email");
        emailField.setValue(loggedUser.getEmail());
        emailField.setReadOnly(true);

        userInfo.add(firstNameField,lastNameField,emailField);
        binder.bind(firstNameField,User::getFirstName,User::setFirstName);
        binder.bind(lastNameField,User::getLastName,User::setLastName);
        binder.bind(emailField,User::getEmail,User::setEmail);

        return userInfo;
    }




    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> userId = event.getRouteParameters().get(USER_ID).map(Integer::parseInt);

        if (userId.isPresent()){
            Optional<User> userFromBackend = userService.get(userId.get());
            binder.readBean(userFromBackend.get());
        }
    }
}
