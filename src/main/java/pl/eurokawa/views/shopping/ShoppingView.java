package pl.eurokawa.views.shopping;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import pl.eurokawa.data.*;
import  com.vaadin.flow.component.notification.Notification;
import pl.eurokawa.services.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UIScope
@Data
@PageTitle("Zakupy")
@Route("order")
@Menu(order = 1, icon = LineAwesomeIconUrl.SHOPPING_CART_SOLID)
public class ShoppingView extends VerticalLayout {
    private ProductService productService;
    private PurchaseService purchaseService;
    private PurchaseRepository purchaseRepository;
    private UserService userService;
    private UserRepository userRepository;
    private MoneyService moneyService;

    private static final Logger logger = LogManager.getLogger(ShoppingView.class);

    private static final String UPLOAD_PATH = "C:\\Users\\Gosia\\Desktop\\upload\\receipts\\";

    private Grid<Purchase> grid;
    private List<Purchase> purchases = new ArrayList<>();
    private ListDataProvider<Purchase> dataProvider;

    public ShoppingView(ProductService productService, PurchaseService purchaseService, UserService userService,
                        PurchaseRepository purchaseRepository, UserRepository userRepository, MoneyService moneyService) {
        this.productService = productService;
        this.purchaseService = purchaseService;
        this.purchaseRepository = purchaseRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.moneyService = moneyService;



        dataProvider = new ListDataProvider<>(purchases);

        grid = new Grid<>(Purchase.class,false);
        grid.setDataProvider(dataProvider);
        grid.setItems(dataProvider);
        grid.setAllRowsVisible(true);

        createProductColumn(grid);
        createQuantityColumn(grid);
        createPriceColumn(grid);
        createSumColumn(grid);
        createImageAdderColumn(grid);
        createActionsButtons(grid);
        addEmptyRow();

        Button plusButton = new Button(new Icon(VaadinIcon.PLUS));
        plusButton.setTooltipText("Dodaj kolejne zamówienie");
        plusButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        plusButton.addClickListener(event -> addEmptyRow());

        Button purchaseConfirmation = new Button("Zatwierdź zamówienie");
        purchaseConfirmation.setSizeFull();
        purchaseConfirmation.addThemeVariants(ButtonVariant.LUMO_LARGE,ButtonVariant.LUMO_SUCCESS);
        purchaseConfirmation.addClickListener(event -> confirmPurchase(grid));

        add(grid, plusButton);
    }

    private void createImageAdderColumn(Grid<Purchase> grid) {
        grid.addColumn(new ComponentRenderer<>(purchase ->{
        Upload upload = new Upload();
        upload.setAcceptedFileTypes("image/jpeg", "image/jpg", "image/png");

        MemoryBuffer buffer = new MemoryBuffer();
        upload.setReceiver(buffer);

        upload.addSucceededListener(event -> {
            String fileName = UUID.randomUUID() + "_" + event.getFileName();
            Path path = Paths.get(UPLOAD_PATH + fileName);

            try (InputStream inputStream = buffer.getInputStream()) {
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

                purchase.setReceiptImagePath("/receipts/" + fileName);
                logger.info("Poprawnie dodano zdjęcie");
                Notification n = Notification.show("Zdjęcie dodane prawidłowo!");

            } catch (IOException e) {
                logger.info("Nie udało się dodać zdjęcia");
                Notification n = Notification.show("Zdjęcie nie dodane prawidłowo!");
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                throw new RuntimeException(e);

            }

        });

        return upload;
        }));};

    private void addEmptyRow() {
        Purchase purchase = new Purchase();
        purchase.setQuantity(1);
        purchase.setPrice(0.00);
        purchase.setTotal(0.00);

        purchases.add(purchase);
        dataProvider.refreshAll();
    }


    private void createProductColumn(Grid<Purchase> grid) {
        grid.addColumn(new ComponentRenderer<>(purchase -> {
        ComboBox<Product> comboBox = new ComboBox<>();
        comboBox.setItemLabelGenerator(Product::getName);
        comboBox.setItems(productService.getAllProducts());
        comboBox.setClearButtonVisible(true);
        comboBox.setValue(purchase.getProduct());
        comboBox.setReadOnly(purchase.isSaved());

        comboBox.addValueChangeListener(event -> {
            Product selectedProduct = event.getValue();
            purchase.setProduct(selectedProduct);

            if (!productService.getAllProducts().contains(selectedProduct)) {
                Notification n = Notification.show("Błędnie wybrany towar");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

            refreshGrid(grid);
        });

        return comboBox;
    })).setHeader("PRODUKT").setAutoWidth(true);
}

    private void createQuantityColumn(Grid<Purchase> grid) {
        grid.addColumn(new ComponentRenderer<>(purchase -> {
            ComboBox<Integer> comboBox = new ComboBox<>();
            comboBox.setItems(1, 2, 3, 4, 5);
            comboBox.setValue(1);
            comboBox.setValue(purchase.getQuantity());
            comboBox.setReadOnly(purchase.isSaved());

            comboBox.addValueChangeListener(event -> {
                purchase.setQuantity(event.getValue());
                purchase.updateTotal();
                refreshGrid(grid);
            });

            return comboBox;
        })).setHeader("ILOŚĆ").setAutoWidth(true);
    }

    private void createPriceColumn(Grid<Purchase> grid) {
        grid.addColumn(new ComponentRenderer<>(purchase -> {
            TextField priceField = new TextField();
            priceField.setClearButtonVisible(true);
            priceField.setPlaceholder("Wpisz kwotę");
            priceField.setValue(String.format("%.2f",purchase.getPrice()));
            priceField.setReadOnly(purchase.isSaved());

            priceField.addValueChangeListener(event -> {
                String value = event.getValue().replace(",", ".");
                priceField.setValue(value);

                try {
                    double price = Double.parseDouble(value);
                    if (price < 0) {
                        Notification notification = Notification.show("Cena nie może być ujemna!");
                        notification.setPosition(Notification.Position.MIDDLE);
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                    else {
                        purchase.setPrice(price);
                    }
                } catch (NumberFormatException e) {
                    Notification n = Notification.show("Wprowadź poprawną cenę");
                    n.setPosition(Notification.Position.MIDDLE);
                    n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }

                purchase.updateTotal();
                refreshGrid(grid);

                if (value == null) {
                    priceField.setValue("0.00");
                }
            });

            return priceField;
        })).setHeader("CENA").setAutoWidth(true);
    }

    private void createSumColumn(Grid<Purchase> grid) {
        grid.addColumn(purchase -> String.format("%.2f", purchase.getTotal()))
                .setHeader("SUMA").setAutoWidth(true).setAutoWidth(true);
    }

    private void createActionsButtons(Grid<Purchase> grid){

        grid.addColumn(new ComponentRenderer<>(purchase -> {
            Button save = new Button(new Icon(VaadinIcon.CHECK));
            save.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            save.setTooltipText("Zapisz");

            save.setVisible(!purchase.isSaved());

            save.addClickListener(event -> {

                if (purchase.getProduct() != null && purchase.getPrice() != null && purchase.getPrice() != 0) {
                    User user = loggedUser();

                    if (purchase.getReceiptImagePath() != null){
                        purchaseService.addPurchase(user, purchase.getProduct().getId(), purchase.getPrice(), purchase.getQuantity(),purchase.getReceiptImagePath());

                    } else {
                        purchaseService.addPurchase(user, purchase.getProduct().getId(), purchase.getPrice(), purchase.getQuantity());
                    }
                    purchase.setSaved(true);
                    dataProvider.refreshAll();

                    Notification notification = Notification.show("Dodano do zamówienia");
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    notification.setDuration(1500);

                    addEmptyRow();
                }
                else {
                    Notification notification = Notification.show("Uzupełnij poprawnie wszystkie dane");
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setDuration(1500);

                }
            });

            Button reset = new Button(new Icon(VaadinIcon.CLOSE));
            reset.addThemeVariants(ButtonVariant.LUMO_ERROR);
            reset.setTooltipText("Resetuj");
            reset.setVisible(!purchase.isSaved());

            reset.addClickListener(event -> {
                purchase.setProduct(null);
                purchase.setQuantity(1);
                purchase.setPrice(0.00);
                purchase.updateTotal();

                dataProvider.refreshItem(purchase);

                Notification notification = Notification.show("Zresetowano");
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                notification.setDuration(1500);
            });

            Button delete = new Button(new Icon(VaadinIcon.TRASH));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.setTooltipText("Usuń");
            delete.setVisible(!purchase.isConfirmed());

            delete.addClickListener(event ->{
                purchases.remove(purchase);
                dataProvider.refreshAll();

                for (Purchase p : purchaseRepository.findAll()){
                    if (p.getProduct().equals(purchase.getProduct()) &&
                        p.getQuantity().equals(purchase.getQuantity()) &&
                        p.getPrice().equals(purchase.getPrice()) &&
                        !p.isConfirmed()){
                        purchases.remove(p);
                        purchaseRepository.delete(p);

                        dataProvider.getItems().remove(purchase);
                        dataProvider.refreshAll();

                        Notification notification = Notification.show("Usunięto");
                        notification.setPosition(Notification.Position.MIDDLE);
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        notification.setDuration(1500);
                    }
                }
            });

            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.add(save, reset,delete);

            return horizontalLayout;
        })).setHeader("AKCJE").setAutoWidth(true);

    }

    private void confirmPurchase(Grid<Purchase> grid){
        User user = loggedUser();//here should be logged user
        boolean savedAnyPurchase = false;
        Double total = 0.00;


            for (Purchase p : purchaseRepository.findAll()) {
                if (p.isSaved() && !p.isConfirmed() & p.getUser().equals(user)) {
                    p.setConfirmed(true);
                    purchaseRepository.save(p);

                    total = total + p.getTotal();

                    savedAnyPurchase = true;
                }
            }

            if (savedAnyPurchase){
                Notification notification = Notification.show("Zamówienie wprowadzone poprawnie",1500, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            else {
                Notification notification = Notification.show("Brak zamówień do zapisania",1500, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

            purchases.clear();
            dataProvider.refreshAll();
            addEmptyRow();

            moneyService.addWithdrawal(user,total);

    }

    private void refreshGrid(Grid<Purchase> grid){

        grid.getDataProvider().refreshAll();
    }

    private User loggedUser(){
        Authentication authentication = VaadinSession.getCurrent().getAttribute(Authentication.class);
        String userEmail = authentication.getName();

        return userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Nie rozpoznano zalogowanego użytkownika!"));
    }


}
