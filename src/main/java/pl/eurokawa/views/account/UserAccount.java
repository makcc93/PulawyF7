package pl.eurokawa.views.account;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.eurokawa.data.User;
import pl.eurokawa.security.ModifiedUserDetails;
import pl.eurokawa.security.SecurityService;
import pl.eurokawa.services.UserService;
import pl.eurokawa.views.MainLayout;

import java.util.Optional;

@Route(value = "/account/:userId?", layout = MainLayout.class)
public class UserAccount extends Div implements BeforeEnterObserver {


    private static final Logger log = LogManager.getLogger(UserAccount.class);
    private UserService userService;
    private SecurityService securityService;
    private String USER_ID = "userId";
    private User loggedUser;

    public UserAccount(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
        loggedUser = securityService.getLoggedUser();

        log.info("Wchodze do UserAccount jako: " + loggedUser.toString());

        H1 h1 = new H1("Cześć, " + loggedUser.toString() + "!");
        h1.getStyle()
                .set("text-align", "center")
                .set("margin-bottom", "var(--lumo-space-l)");
        H1 h2 = new H1("Oto dane Twojego konta");
        h2.getStyle()
                .set("text-align", "center")
                .set("margin-bottom", "var(--lumo-space-l)");

        VerticalLayout layout = new VerticalLayout();
        VerticalLayout userContent = createUserInformations(loggedUser);

        add(h1,h2,userContent,layout);
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

        userInfo.add(firstNameField,lastNameField,emailField);

        return userInfo;
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> userId = event.getRouteParameters().get(USER_ID).map(Integer::parseInt);

        if (userId.isPresent()){
            Optional<User> userFromBackend = userService.get(userId.get());
        }
    }
}
