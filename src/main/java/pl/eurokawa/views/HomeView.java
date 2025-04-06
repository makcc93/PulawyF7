package pl.eurokawa.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.eurokawa.security.SecurityService;
import com.vaadin.flow.component.textfield.TextArea;

import java.awt.*;

@SpringComponent
@Route("home")
@UIScope
public class HomeView extends VerticalLayout {
    private SecurityService securityService;
    Logger logger = LogManager.getLogger(HomeView.class);

    public HomeView(SecurityService securityService) {
        this.securityService = securityService;

        TextArea welcome = new TextArea();
        welcome.setValue("""
                Witaj na stronie!
                """);
        welcome.setWidthFull();
        welcome.addThemeVariants(TextAreaVariant.LUMO_ALIGN_CENTER);


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Notification notification = Notification.show(".....................Twoje role: " + auth.getAuthorities().toString());
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        notification.setPosition(Notification.Position.BOTTOM_CENTER);


        Button button = new Button("PRZYCISK ADMINA");

        button.addClickListener(event -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Notification n = Notification.show("------ TA ROLA TO: " + authentication.getAuthorities());
        });
        button.setVisible(securityService.hasRole("ADMIN"));

        Button button1 = new Button("PRZYCISK USERA");
        button1.addClickListener(event -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            logger.info("Zalogowany użytkownik toooo: " + authentication.getName());
            logger.info("A jego super rola toooo: " + authentication.getAuthorities());
        });


        add(welcome,button, button1);

    }
}

