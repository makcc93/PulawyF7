package pl.eurokawa.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.RolesAllowed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.eurokawa.EmptyLayout;
import pl.eurokawa.security.SecurityService;

//@SpringComponent
//@RolesAllowed("NOTCONFIRMED")
@UIScope
@Route(value = "notconfirmed", layout = EmptyLayout.class)
public class RegisteredUserNotAccepted extends Div {

private SecurityService securityService;
    private static final Logger logger = LogManager.getLogger(RegisteredUserNotAccepted.class);

    public RegisteredUserNotAccepted(SecurityService securityService){
        this.securityService = securityService;

        logger.info("ROLE_NOTCONFIRMED w registeredNotConfirmed: " + securityService.hasRole("NOTCONFIRMED"));

        logger.info("Klasa registeredNotAccepted została uruchomiona!");

        TextArea text = new TextArea();
        text.setReadOnly(true);
        text.addThemeVariants(TextAreaVariant.LUMO_ALIGN_CENTER);
        text.setValue("Konto zarejestrowane prawidłowo, oczekuje na weryfikację przez administratora" +
                "\n\n" +
                "Dostęp do strony po prawidłowej weryfikacji przyznawany jest zazwyczaj w ciągu doby." +
                "\n" +
                "Jeśli masz bezpośredni kontakt z administratorem powiadom go o Twojej rejestracji." +
                "\n" +
                "Znacznie przyspieszy to proces.");
        text.setHeightFull();
        text.setMinHeight("800px");
        text.setWidthFull();
        text.getStyle()
                .set("text-align", "center")
                .set("font-size", "48px")
                .set("margin-top", "auto");


        add(text);
    }
}
