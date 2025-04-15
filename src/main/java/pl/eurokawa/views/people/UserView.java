package pl.eurokawa.views.people;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import pl.eurokawa.data.MoneyRepository;
import pl.eurokawa.data.User;
import pl.eurokawa.security.SecurityService;
import pl.eurokawa.services.UserService;
import pl.eurokawa.views.MainLayout;
import java.util.Optional;

@UIScope
@PageTitle("Ludzie")
@Route(value = "users/:peopleID?/:action?(edit)", layout = MainLayout.class)
@Menu(order = 0, icon = LineAwesomeIconUrl.ANKH_SOLID)
public class UserView extends Div implements BeforeEnterObserver {

    private SecurityService securityService;
    private static final Logger logger = LogManager.getLogger(UserView.class);

    private String PEOPLE_ID = "peopleID";
    private String PEOPLE_EDIT_ROUTE_TEMPLATE = "users/%s/edit";

    private final Grid<User> grid = new Grid<>(User.class, false);

    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private ComboBox<String> role;
    private ComboBox<Boolean> isCoffeeMember;
    private final Button cancel = new Button("Anuluj");
    private final Button save = new Button("Zapisz");
    private final Button delete = new Button("Usuń");

    private final BeanValidationBinder<User> binder;
    private User user;
    private final UserService userService;
    private MoneyRepository moneyRepository;

    public UserView(SecurityService securityService, UserService userService, MoneyRepository moneyRepository) {
        this.securityService = securityService;
        this.userService = userService;
        this.moneyRepository = moneyRepository;

        addClassNames("ludzie-view");
        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);


        binder = new BeanValidationBinder<>(User.class);

        binder.bindInstanceFields(this);

        add(splitLayout);

        grid.addColumn("firstName").setAutoWidth(true).setHeader("IMIĘ");
        grid.addColumn("lastName").setAutoWidth(true).setHeader("NAZWISKO");
//        grid.addColumn("email").setAutoWidth(true).setHeader("EMAIL");
        grid.addColumn(user -> {
            Double sum = moneyRepository.getSumOfUserDeposit(user.getId());

            return sum != null ? String.format("%.2f", sum) : "0.00";
        }).setAutoWidth(true).setHeader("SUMA WPŁAT");

        grid.setItems(query -> userService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                populateForm(event.getValue());
                UI.getCurrent().navigate(String.format(PEOPLE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(UserView.class);
            }
        });

        logger.info("HasRole(ADMIN) = " +securityService.hasRole("ADMIN"));

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.user != null) {
                    binder.writeBean(this.user);
                    userService.save(this.user);

                    refreshGrid();
                    clearForm();

                    Notification notification = Notification.show("Dane zaktualizowane pomyślnie");
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    notification.setPosition(Position.MIDDLE);
                    UI.getCurrent().navigate(UserView.class);
                }
                else {
                    Notification notification = Notification.show("Błędne dane!",3000,Position.BOTTOM_CENTER);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

                    refreshGrid();
                    clearForm();
                }
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Błąd w aktualizacji danych. W miedzyczasie ktoś inny próbował aktualizować dane");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException | DataIntegrityViolationException validationException) {
                Notification n = Notification.show("Błąd w aktualizacji danych. Sprawdź czy wszystkie dane są poprawne");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        save.setVisible(securityService.hasRole("ADMIN"));

        delete.addClickListener(e ->{
            try {
                if (user != null) {
                    binder.writeBean(this.user);
                    userService.delete(this.user.getId());
                    clearForm();
                    refreshGrid();

                    Notification notification = Notification.show("Osoba została usunięta poprawnie");
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    notification.setPosition(Position.MIDDLE);
                }
            } catch (ValidationException ex) {
                throw new RuntimeException(ex);
            }
        });
        delete.setVisible(securityService.hasRole("ADMIN"));
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        firstName = new TextField("Imię");
        firstName.setPlaceholder("Wprowadź imię..");
        firstName.setReadOnly(!securityService.hasRole("ADMIN"));

        lastName = new TextField("Nazwisko");
        lastName.setPlaceholder("Wprowadź nazwisko..");
        lastName.setReadOnly(!securityService.hasRole("ADMIN"));

        email = new TextField("Email");
        email.setPlaceholder("Wprowadź maila..");
        email.setReadOnly(!securityService.hasRole("ADMIN"));

        role = new ComboBox<>("Rodzaj użytkownika");
        role.setVisible(securityService.hasRole("ADMIN"));
        role.setAllowCustomValue(false);
        role.setItems("NOTCONFIRMED","USER","ADMIN");
        role.setHelperText("Rozwiń listę, aby nadać uprawnienia");

        isCoffeeMember = new ComboBox<>("Należy do grupy kawoszy");
        isCoffeeMember.setHelperText("Rozwiń listę, aby nadać uprawnienia");
        isCoffeeMember.setItems(true,false);
        isCoffeeMember.setItemLabelGenerator(value -> value ? "Tak" : "Nie");
        isCoffeeMember.setVisible(securityService.hasRole("ADMIN"));

        formLayout.add(firstName, lastName, email,role,isCoffeeMember);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

        buttonLayout.add(save, cancel,delete);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(User value) {
        this.user = value;

        if (value != null){
            binder.readBean(value);
        }
        else {
            binder.removeBean();
            firstName.clear();
            lastName.clear();
            email.clear();
            isCoffeeMember.clear();
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> peopleId = event.getRouteParameters().get(PEOPLE_ID).map(Integer::parseInt);

        if (peopleId.isPresent()) {
            Optional<User> peopleFromBackend = userService.get(peopleId.get());

            if (peopleFromBackend.isPresent()) {
                grid.select(peopleFromBackend.get());
                populateForm(peopleFromBackend.get());
            } else {
                Notification.show(String.format("The requested user was not found, ID = %s", peopleId.get()), 3000,
                        Notification.Position.BOTTOM_START);

                refreshGrid();
                event.forwardTo(UserView.class);
            }
        }
        else {
            clearForm();
        }
    }
}
