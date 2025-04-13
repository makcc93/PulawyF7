package pl.eurokawa.views.account;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atmosphere.interceptor.AtmosphereResourceStateRecovery;
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

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        VerticalLayout userContent = createUserInformations(loggedUser);
        VerticalLayout changePassword = changeUserPassword(loggedUser);



        log.info("Wchodze do UserAccount jako: " + loggedUser.toString());

        H1 h1 = new H1("Cześć, " + loggedUser.toString() + "!");
        h1.getStyle()
                .set("text-align", "center")
                .set("margin-bottom", "var(--lumo-space-l)");

        horizontalLayout.add(userContent,changePassword);

        add(h1,horizontalLayout);
    }

    private void createActionButtons(VerticalLayout layout,Dialog dialog){
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

                   dialog.close();
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
            dialog.close();
        });

        layout.add(save,cancel);
    }

    private VerticalLayout createUserInformations(User loggedUser){
        VerticalLayout userInfo = new VerticalLayout();

        Button button = new Button("Chcę zmienić swoje dane");
        button.setSizeFull();
        button.setWidthFull();
        button.getStyle().set("font-size","30px");
        button.addThemeVariants(ButtonVariant.LUMO_LARGE);
        button.addClickListener(event ->{

            VerticalLayout insideLayout = new VerticalLayout();
            Dialog dialog = new Dialog();

            TextField firstNameField = new TextField("Imię");
            firstNameField.setValue(loggedUser.getFirstName());

            TextField lastNameField = new TextField("Nazwisko");
            lastNameField.setValue(loggedUser.getLastName());

            TextField emailField = new TextField("Email");
            emailField.setValue(loggedUser.getEmail());
            emailField.setReadOnly(true);

            insideLayout.add(firstNameField,lastNameField,emailField);
            binder.bind(firstNameField,User::getFirstName,User::setFirstName);
            binder.bind(lastNameField,User::getLastName,User::setLastName);
            binder.bind(emailField,User::getEmail,User::setEmail);

            createActionButtons(insideLayout,dialog);

            dialog.add(insideLayout);
            dialog.open();
        });

        userInfo.add(button);
        return userInfo;
    }

    private VerticalLayout changeUserPassword(User user){
        VerticalLayout layout = new VerticalLayout();

        Button changePasswordButton = new Button("Chcę zmienić swoje hasło");
        changePasswordButton.setSizeFull();
        changePasswordButton.setWidthFull();
        changePasswordButton.getStyle().set("font-size","30px");
        changePasswordButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        changePasswordButton.addClickListener(event ->{
            Dialog dialog = new Dialog();
            VerticalLayout insideDialogLayout = new VerticalLayout();

            PasswordField passwordField = new PasswordField("Wpisz nowe hasło");
            PasswordField passwordFieldRepeated = new PasswordField("Powtórz nowe hasło");

            Button confirmButton = new Button("Zapisz nowe hasło",click ->{
                String password = passwordField.getValue();
                String repeatedPassword = passwordFieldRepeated.getValue();

                if (password.equals(repeatedPassword)){
                    userService.setUserNewPassword(user.getEmail(),password);

                    dialog.close();
                }
                else {
                    Notification notification = Notification.show("Hasła do siebie nie pasują",3000, Notification.Position.BOTTOM_CENTER);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            confirmButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

            Button cancelButton = new Button("Anuluj", click ->{
                dialog.close();
            });
            cancelButton.addThemeVariants(ButtonVariant.LUMO_WARNING);

            insideDialogLayout.add(passwordField,passwordFieldRepeated,confirmButton,cancelButton);

            dialog.add(insideDialogLayout);
            dialog.open();
        });

        layout.add(changePasswordButton);

        return layout;
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
