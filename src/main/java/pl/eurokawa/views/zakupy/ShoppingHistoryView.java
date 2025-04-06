package pl.eurokawa.views.zakupy;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.RolesAllowed;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import pl.eurokawa.data.Purchase;
import pl.eurokawa.security.SecurityService;
import pl.eurokawa.services.PurchaseService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@UIScope
@Data
@PageTitle("Historia zakupów")
@Route("orderhistory")
@Menu(order = 1.1, icon = LineAwesomeIconUrl.GRIP_LINES_SOLID)
public class ShoppingHistoryView extends Div implements BeforeEnterObserver {
    private PurchaseService purchaseService;
    private SecurityService securityService;
    private  static final Logger logger = LogManager.getLogger(ShoppingHistoryView.class);

    public ShoppingHistoryView(PurchaseService purchaseService, SecurityService securityService){
        this.purchaseService = purchaseService;
        this.securityService = securityService;

        Authentication auth = VaadinSession.getCurrent().getAttribute(Authentication.class);
            if (auth != null) {
                logger.info("z VAADIN - User: " + auth.getName());
                logger.info("z VAADIN - Roles: " + auth.getAuthorities());
            }
            else {
                logger.info("             auth musi byc nullem :(");
            }


        List<Purchase> purchases = purchaseService.getConfirmedPurchases();

        Grid<Purchase> grid = new Grid<>(Purchase.class,false);
        grid.setItems(purchases);
        grid.setAllRowsVisible(true);

        grid.addColumn(Purchase::getId).setHeader("NUMER ZAMÓWIENIA").setAutoWidth(true);

        grid.addColumn(Purchase::getUser).setHeader("OSOBA").setAutoWidth(true);

        grid.addColumn(Purchase::getProduct).setHeader("PRODUKT").setAutoWidth(true);

        grid.addColumn(Purchase::getQuantity).setHeader("ILOŚĆ").setAutoWidth(true);

        grid.addColumn(Purchase::getPrice).setHeader("CENA").setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(purchase -> {
            String createdAt = String.valueOf(purchase.getCreatedAt());
            LocalDateTime date = LocalDateTime.parse(createdAt);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MM yyyy, HH:mm:ss", Locale.of("pl", "PL"));

            String finalDate = date.format(dateTimeFormatter);

            return new Span(finalDate);

        })).setHeader("DATA").setAutoWidth(true);

        grid.addColumn(Purchase::getTotal).setHeader("WARTOŚĆ CAŁOŚCIOWA").setAutoWidth(true);

        add(grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event){
        if (!(securityService.hasRole("ADMIN") || securityService.hasRole("USER"))){
            logger.warn("Użytkownik nie ma uprawnień do tej zakładki!");
            Notification notification = Notification.show("Twoje uprawnienia nie pozwają korzystać z tej zakładki!", 3000, Notification.Position.BOTTOM_CENTER);

            add(notification);
            event.rerouteTo("home");
        }
    }
}
