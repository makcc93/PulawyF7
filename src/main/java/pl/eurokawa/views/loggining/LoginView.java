package pl.eurokawa.views.loggining;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.weaver.tools.cache.CachedClassEntry;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.eurokawa.EmptyLayout;
import pl.eurokawa.services.UserService;

import java.util.Collection;


@AnonymousAllowed
@Route(value = "login", layout = EmptyLayout.class)
@RouteAlias(value = "", layout = EmptyLayout.class)
@RouteAlias(value = "register",layout = EmptyLayout.class)
public class LoginView extends Div {
    AuthenticationManager authenticationManager;
    UserService userService;
    private static final Logger logger = LogManager.getLogger(LoginView.class);

    public LoginView(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;

        HorizontalLayout layout = pageView();

        H1 h1 = new H1("Witaj!");
        h1.getStyle()
                .set("text-align", "center")
                .set("margin-bottom", "var(--lumo-space-l)");
        H1 h2 = new H1("Zaloguj się lub załóż konto");
        h2.getStyle()
                .set("text-align", "center")
                .set("margin-bottom", "var(--lumo-space-l)");

        VerticalLayout fullLayout = new VerticalLayout();
        fullLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        fullLayout.getStyle().set("margin-top", "5rem");
        Span footerText = new Span("by Mateusz Kruk © All rights reserved");
        footerText.getStyle()
                .set("font-size", "very small")
                .set("margin-top", "auto")
                .set("padding-top", "1em")
                .set("color", "#666669");

        fullLayout.add(h1, h2, layout, footerText);

        add(fullLayout);
    }

    private HorizontalLayout pageView(){
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setWidth("auto");
        layout.setHeight("550px");
        layout.setSpacing(false);
        layout.getStyle()
                .set("margin", "0 auto")
                .set("background", "dark")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 8px rgba(0,4,0,0.1)");

        Div formsContainer = new Div();
        formsContainer.getStyle()
                .set("display", "flex")
                .set("justify-content", "center")
                .set("gap", "var(--lumo-space-xl)");

        VerticalLayout loginPanel = createLoginView();
        Div divider = divider();
        VerticalLayout registerPanel = createRegisterView();

        layout.add(loginPanel,divider,registerPanel);

        return layout;
    }

    private Div divider(){
        Div div = new Div();
        div.getStyle()
                .set("border-left", "2px solid var(--lumo-contrast-20pct)")
                .set("height", "300px")
                .set("background", "dark")
                .set("margin", "0 2rem")
                .set("align-self", "center");

        return div;
    }


    private VerticalLayout createRegisterView() {
        VerticalLayout registerPanel = new VerticalLayout();
        registerPanel.setPadding(true);
        registerPanel.setWidth("450px");
        registerPanel.setHeight("100%");
        registerPanel.getStyle().set("align-self", "flex-start");
        registerPanel.getStyle().set("padding", "2rem");

        H2 registerHeader = new H2("Rejestracja");
        registerHeader.getStyle()
                .set("margin", "0 auto")
                .set("margin-top", "0")
                .set("text-align", "center");


        TextField firstNameField = new TextField("Imię");
        TextField lastNameField = new TextField("Nazwisko");
        TextField emailField = new TextField("Email");
        PasswordField passwordField = new PasswordField("Hasło");
        PasswordField confirmPasswordField = new PasswordField("Powtórz hasło");
        confirmPasswordField.getStyle().set("margin-bottom", "0.5em");

        Button registerButton = new Button("Zarejestruj się", event -> {
            String firstName = firstNameField.getValue();
            String lastName = lastNameField.getValue();
            String email = emailField.getValue();
            String password = passwordField.getValue();
            String confirmPassword = confirmPasswordField.getValue();

            if (validateRegistration(firstName,lastName,email,password,confirmPassword)){
                userService.registerUser(firstName,lastName,email,password);

                Notification.show("Rejestracja udana! Możesz się teraz zalogować",
                        3000, Notification.Position.MIDDLE);

                clearForm(firstNameField,lastNameField,emailField);
            }
        });
        registerButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        FormLayout registerForm = new FormLayout();
        registerForm.add(firstNameField,lastNameField,emailField,passwordField,confirmPasswordField,registerButton);

        registerForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        registerPanel.add(registerHeader,registerForm);

        return registerPanel;
    }

    private void clearForm(TextField... fields) {
        for (TextField field : fields){
            field.clear();
        }
    }

    private boolean validateRegistration(String firstName, String lastName, String email,String password, String confirmPassword){
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||password.isEmpty() || confirmPassword.isEmpty()){
            Notification.show("Uzupełnij wszystkie pola",3000, Notification.Position.BOTTOM_CENTER);

            return false;
        }

        if (!password.equals(confirmPassword)){
            Notification.show("Wpisane hasła nie są takie same",3000, Notification.Position.BOTTOM_CENTER);

            return false;
        }

        if(userService.getUserByEmail(email).isPresent()){
            Notification.show("Użytkownik z podanym mailem już istnieje!",3000, Notification.Position.BOTTOM_CENTER);

            return false;
        }

        return true;
    }

    private VerticalLayout createLoginView(){

        VerticalLayout loginPanel = new VerticalLayout();

        loginPanel.setPadding(true);
        loginPanel.setWidth("450px");
        loginPanel.setHeight("120%");
        loginPanel.getStyle().set("flex-grow", "1");
        loginPanel.getStyle().set("align-self", "flex-start");
        loginPanel.getStyle().set("padding", "2rem");

        H2 loginHeader = new H2();
        loginHeader.setText("Logowanie");
        loginHeader.getStyle()
                .set("margin", "0 auto")
                .set("margin-top", "0")
                .set("text-align", "center");

        LoginForm loginForm = new LoginForm();
        loginForm.getStyle().set("margin-top", "1.5rem");
        configureLoginForm(loginForm);

        loginPanel.add(loginHeader,loginForm);

        return loginPanel;
    }


    private void configureLoginForm(LoginForm loginForm){
        LoginI18n i18n = new LoginI18n();
        LoginI18n.Form form = new LoginI18n.Form();

        form.setUsername("Email");
        form.setPassword("Hasło");
        form.setSubmit("Zaloguj się");
        form.setForgotPassword("Nie pamiętasz hasła?");
        i18n.setForm(form);

        LoginI18n.ErrorMessage errorMessage = new LoginI18n.ErrorMessage();
        errorMessage.setTitle("Błąd");
        errorMessage.setMessage("Sprawdź poprawność danych.");
        i18n.setErrorMessage(errorMessage);

        loginForm.setI18n(i18n);
        loginForm.addLoginListener(event -> {
            try{
                authenticate(event.getUsername(),event.getPassword());
                UI.getCurrent().navigate("/home");
            }
            catch(Exception e){
                loginForm.setError(true);
            }
        });

        loginForm.addForgotPasswordListener(event -> {
            setNewPassword();
        });
    }

    private void setNewPassword(){
        VerticalLayout verticalLayout = new VerticalLayout();
        Dialog dialog = new Dialog();

        TextField emailField = new TextField("Email");
        PasswordField newPasswordField = new PasswordField("Nowe hasło");

        FormLayout layout = new FormLayout();
        layout.add(emailField,newPasswordField);

        Button confirmButton = new Button("Zapisz nowe hasło", event ->{
            String email = emailField.getValue();
            String password = newPasswordField.getValue();

            userService.setUserNewPassword(email,password);

            dialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelResetButton = new Button("Anuluj",event ->{
            dialog.close();
        });
        cancelResetButton.addThemeVariants(ButtonVariant.LUMO_WARNING);

        verticalLayout.add(layout,confirmButton,cancelResetButton);

        dialog.add(verticalLayout);
        dialog.setHeaderTitle("Ustalenie nowego hasła");
        dialog.open();
    }


    private void authenticate(String email,String password) throws AuthenticationException{
        Authentication authentication = new UsernamePasswordAuthenticationToken(email,password);

        Authentication authenticated = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authenticated);

        VaadinSession.getCurrent().setAttribute(Authentication.class,authenticated);
    }

}