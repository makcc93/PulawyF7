package pl.eurokawa.views.wplaty;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import pl.eurokawa.data.*;
import pl.eurokawa.security.SecurityService;
import pl.eurokawa.services.*;
import com.vaadin.flow.component.textfield.TextArea;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@UIScope
@Data
@PageTitle("Wpłaty")
@Route("deposit")
@Menu(order = 2, icon = LineAwesomeIconUrl.DOLLAR_SIGN_SOLID)
public class DepositAdderView extends VerticalLayout {
    private SecurityService securityService;
    private ProductService productService;
    private PurchaseService purchaseService;
    private PurchaseRepository purchaseRepository;
    private UserService userService;
    private UserRepository userRepository;
    private MoneyService moneyService;
    private MoneyRepository moneyRepository;
    private Grid<Money> grid;
    private ListDataProvider<Money> dataProvider;
    private List<Money> transactions = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(DepositAdderView.class);


    public DepositAdderView(SecurityService securityService, ProductService productService,
                            PurchaseRepository purchaseRepository, UserService userService,
                            UserRepository userRepository, MoneyService moneyService,
                            MoneyRepository moneyRepository) {
        this.securityService = securityService;
        this.productService = productService;
        this.purchaseRepository = purchaseRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.moneyService = moneyService;
        this.moneyRepository = moneyRepository;
        dataProvider = new ListDataProvider<>(transactions);

        grid = new Grid<> (Money.class,false);
        grid.setDataProvider(dataProvider);
        grid.setAllRowsVisible(true);
        grid.setVisible(securityService.hasRole("ADMIN"));
        
        if (!securityService.hasRole("ADMIN")){

            TextArea information = new TextArea();
            information.setValue("Nie posiadasz dostępu do dodawania wpłat, aby zobaczyć ich historię przejdź do zakładki \"Historia wpłat\"");
            information.setWidthFull();
            information.setAutofocus(true);
            information.setHeightFull();

            add(information);
        }

        createUserColumn(grid);
        createDepositColumn(grid);
        createActionsButtons(grid);

        addEmptyRow(grid);
        
        add(grid);
    }

    private void addEmptyRow(Grid<Money> grid) {
        Money money = new Money();

        transactions.add(money);
        refreshGrid(grid);
    }

    private void createUserColumn(Grid<Money> grid){
        grid.addColumn(new ComponentRenderer<>(money ->{
            ComboBox<User> comboBox = new ComboBox<>();
            comboBox.setItemLabelGenerator(User::toString);
            comboBox.setItems(userRepository.findAll());
            comboBox.setClearButtonVisible(true);
            comboBox.setTooltipText("Wybierz osobę");

            comboBox.addValueChangeListener(event -> {
                User selectedUser = event.getValue();
                if (selectedUser != null) {
                    money.setUser(selectedUser);
                }
            });

            return comboBox;
        })).setHeader("OSOBA").setAutoWidth(true);
    }

    private void createDepositColumn(Grid<Money> grid){
        grid.addColumn(new ComponentRenderer<>(money ->{
            TextField depositField = new TextField();
            depositField.setPlaceholder("Wprowadź wpłatę");
            depositField.setClearButtonVisible(true);

            depositField.addValueChangeListener(event ->{
                String value = event.getValue().replace(",",".");

                depositField.setValue(value);
                try{
                    double deposit = Double.parseDouble(value);
                    if (deposit < 0) {
                        Notification notification = Notification.show("Cena nie może być ujemna!");
                        notification.setPosition(Notification.Position.MIDDLE);
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                    else {
                        money.setDeposit(deposit);
                    }
                }
                catch (NumberFormatException e) {
                    Notification n = Notification.show("Wprowadź poprawną cenę");
                    n.setPosition(Notification.Position.MIDDLE);
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            return depositField;

        })).setHeader("WPŁATA").setAutoWidth(true);
    }

    private void createActionsButtons(Grid<Money> grid){
        grid.addColumn(new ComponentRenderer<>(money ->{
            Button save = new Button(new Icon(VaadinIcon.CHECK));
            save.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            save.setTooltipText("Zapisz");

            save.addClickListener(event ->{
                confirmOrCancel(money);
            });

            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.add(save);

            return horizontalLayout;
        })).setHeader("AKCJE");
    }

    private void confirmOrCancel(Money money){
            Span status = new Span();
            status.setVisible(false);

            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("POTWIERDZENIE PRZYJĘCIA WPŁATY");
            dialog.setText(
                    "Czy potwierdzasz przyjecię wpłaty?");

            dialog.setCancelable(true);
            dialog.setCancelText("Anuluj");
            dialog.addCancelListener(cancelEvent -> {

            });


            dialog.setConfirmText("Potwierdź");
            dialog.addConfirmListener(confirmEvent -> {
                User user = money.getUser(); //here should be logged user

                double currentBalance = moneyRepository.findLatestBalance().map(Money::getBalance).orElse(0.00);
                double updatedBalance = currentBalance + money.getDeposit();

                Money moneyDeposit = new Money();
                moneyDeposit.setUser(user);
                moneyDeposit.setDeposit(money.getDeposit());
                moneyDeposit.setBalance(updatedBalance);

                BalanceBroadcaster.broadcast(updatedBalance);

                moneyRepository.save(moneyDeposit);


                Notification notification = Notification.show("Wpłata pracownika poprawnie wprowadzona");
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setDuration(1500);

            });

            dialog.open();


            HorizontalLayout layout = new HorizontalLayout();
            layout.setAlignItems(Alignment.CENTER);
            layout.setJustifyContentMode(JustifyContentMode.CENTER);
            layout.add(status);
            add(layout);
    }

    private void refreshGrid(Grid<Money> grid) {
        grid.getDataProvider().refreshAll();
    }
}
