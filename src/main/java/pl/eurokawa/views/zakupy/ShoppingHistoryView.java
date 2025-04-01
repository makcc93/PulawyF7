package pl.eurokawa.views.zakupy;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import pl.eurokawa.data.Product;
import pl.eurokawa.data.Purchase;
import pl.eurokawa.services.AccessControl;
import pl.eurokawa.services.PurchaseService;
import pl.eurokawa.views.HomeView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Data
@PageTitle("Historia zakupów")
@Route("orderhistory")
@Menu(order = 1.1, icon = LineAwesomeIconUrl.GRIP_LINES_SOLID)
public class ShoppingHistoryView extends Div {
    private PurchaseService purchaseService;
    private AccessControl accessControl;
    Logger logger = LogManager.getLogger(ShoppingHistoryView.class);

    public ShoppingHistoryView(PurchaseService purchaseService, AccessControl accessControl){
        this.purchaseService = purchaseService;
        this.accessControl = accessControl;

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


}
