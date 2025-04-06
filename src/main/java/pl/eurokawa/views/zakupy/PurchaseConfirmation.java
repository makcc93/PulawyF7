package pl.eurokawa.views.zakupy;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import pl.eurokawa.data.Purchase;
import pl.eurokawa.data.PurchaseRepository;
import pl.eurokawa.services.MoneyService;
import pl.eurokawa.services.PurchaseService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Route("purchase-confirmation")
@RolesAllowed("ADMIN")
@Menu(order = 3, icon = LineAwesomeIconUrl.CHECK_CIRCLE)
public class PurchaseConfirmation extends Div {

    private PurchaseService purchaseService;
    private PurchaseRepository purchaseRepository;
    private MoneyService moneyService;
    private List<Purchase> purchases = new ArrayList<>();
    private ListDataProvider<Purchase> dataProvider;
    private  static final Logger logger = LogManager.getLogger(PurchaseConfirmation.class);


    public PurchaseConfirmation(PurchaseService purchaseService, PurchaseRepository purchaseRepository, MoneyService moneyService) {
        this.purchaseService = purchaseService;
        this.purchaseRepository = purchaseRepository;
        this.moneyService = moneyService;
        this.purchases = purchaseService.getSavedNotConfirmedPurchases();;

        Grid<Purchase> grid = new Grid<> (Purchase.class,false);
        dataProvider = new ListDataProvider<>(purchases);

        grid.setItems(dataProvider);
        grid.setAllRowsVisible(true);

        grid.addColumn(Purchase::getId).setHeader("NUMER ZAMÓWIENIA").setAutoWidth(true);

        grid.addColumn(Purchase::getUser).setHeader("OSOBA").setAutoWidth(true);

        grid.addColumn(Purchase::getProduct).setHeader("PRODUKT").setAutoWidth(true);

        grid.addColumn(Purchase::getQuantity).setHeader("ILOŚĆ").setAutoWidth(true);

        grid.addColumn(Purchase::getPrice).setHeader("CENA").setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(purchase -> {
            String createdAt = String.valueOf(purchase.getCreatedAt());
            LocalDateTime date = LocalDateTime.parse(createdAt);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss", Locale.of("pl", "PL"));

            String finalDate = date.format(dateTimeFormatter);

            return new Span(finalDate);

        })).setHeader("DATA").setAutoWidth(true);

        grid.addColumn(Purchase::getTotal).setHeader("WARTOŚĆ CAŁOŚCIOWA").setAutoWidth(true);

        createActionButtons(grid);

        add(grid);
    }

    private void createActionButtons(Grid<Purchase> grid) {
        grid.addColumn(new ComponentRenderer<>(event ->{
            Button save = new Button(new Icon(VaadinIcon.CHECK));
            save.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            save.setTooltipText("Zatwierdź");
            save.addClickListener(click -> {
                event.setConfirmed(true);
                purchaseRepository.save(event);

                moneyService.addWithdrawal(event.getUser(),event.getTotal());

                purchases.remove(event);
                dataProvider.refreshAll();

            });

            Button delete = new Button(new Icon(VaadinIcon.TRASH));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_CONTRAST);
            delete.setTooltipText("Usuń zamówienie");
            delete.addClickListener(click ->{
                event.setConfirmed(false);
                event.setSaved(false);

                purchaseRepository.delete(event);
                purchases.remove(event);
                dataProvider.refreshAll();
            });

            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.add(save,delete);

            return horizontalLayout;
        })).setHeader("Akcje").setAutoWidth(true);
    }

}
