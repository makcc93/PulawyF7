package pl.eurokawa.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.netty.handler.codec.mqtt.MqttReasonCodes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.eurokawa.security.SecurityService;
import com.vaadin.flow.component.textfield.TextArea;

import javax.swing.*;
import java.awt.*;

@SpringComponent
@Route("home")
@UIScope
public class HomeView extends VerticalLayout implements BeforeEnterObserver {
    private SecurityService securityService;
    private static final Logger logger = LogManager.getLogger(HomeView.class);

    public HomeView(SecurityService securityService) {
        this.securityService = securityService;

        logger.info("    ROLE_NOTCONFIRMED w homeeeeeeeeeeeeeeeeeee: " + securityService.hasRole("NOTCONFIRMED"));

        TextArea welcome = new TextArea();
        welcome.addThemeVariants(TextAreaVariant.LUMO_ALIGN_CENTER);
        welcome.setReadOnly(true);
        welcome.getStyle()
                .set("font-size", "28px")
                .set("margin-top", "auto");
        welcome.setValue("""
                Witaj na stronie!
                
                W lewym górnym rogu strony widoczne są obecne zgromadzone środki, którymi możemy wspólnie dysponować
                
                Zakładki:
                
                W zakładce \"Ludzie\"  możesz sprawdzić wszystkie osoby oraz łączną sumę ich wpłat
                
                Do zakładki \"Wpłaty\" ma dostęp obecny skarbnik, to on rejestruje Twoje wpłaty
                W zakładce \"Historia wpłat\" możesz zobaczyć wpłaty wszystkich osób,
                jeśli dokonałeś wpłaty, a jej nie widzisz powiadom skarbnika o tym fakcie
                
                W zakładce \"Zakupy\" możesz zarejestrować dokonany przez Ciebie zakup,
                wszystkie potwierdzone przez skarbnika zakupy są widoczne w zakładce \"Historia zakupów\"
                
                """);
        welcome.setHeightFull();
        welcome.setWidthFull();



//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Authentication auth = VaadinSession.getCurrent().getAttribute(Authentication.class);
        Notification notification = Notification.show(".....................Twoje role: " + auth.getAuthorities().toString());
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        notification.setPosition(Notification.Position.BOTTOM_CENTER);


//        Button button = new Button("PRZYCISK ADMINA");
//
//        button.addClickListener(event -> {
////            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            Authentication authentication = VaadinSession.getCurrent().getAttribute(Authentication.class);
//            Notification n = Notification.show("------ TA ROLA TO: " + authentication.getAuthorities());
//        });
//        button.setVisible(securityService.hasRole("ADMIN"));
//
//        Button button1 = new Button("PRZYCISK USERA");
//        button1.addClickListener(event -> {
////            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            Authentication authentication = VaadinSession.getCurrent().getAttribute(Authentication.class);
//            logger.info("Zalogowany użytkownik toooo: " + authentication.getName());
//            logger.info("A jego super rola toooo: " + authentication.getAuthorities());
//        });


        add(welcome);

    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        logger.info("HOMEview wchodze do beforeEntera i notconfirmed to: " + securityService.hasRole("NOTCONFIRMED"));

        if (securityService.hasRole("NOTCONFIRMED")){
            beforeEnterEvent.rerouteTo(RegisteredUserNotAccepted.class);
        }
    }
}

