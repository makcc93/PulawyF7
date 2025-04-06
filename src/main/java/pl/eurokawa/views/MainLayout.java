package pl.eurokawa.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import pl.eurokawa.security.SecurityService;
import pl.eurokawa.services.BalanceBroadcaster;
import pl.eurokawa.services.MoneyService;
import pl.eurokawa.views.ludzie.UserView;
import pl.eurokawa.views.wplaty.DepositAdderView;
import pl.eurokawa.views.wplaty.DepositListView;
import pl.eurokawa.views.zakupy.PurchaseConfirmation;
import pl.eurokawa.views.zakupy.ShoppingHistoryView;
import pl.eurokawa.views.zakupy.ShoppingView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * The main view is a top-level placeholder for other views.
 */

@Layout
public class MainLayout extends AppLayout {
    private static final Logger log = LogManager.getLogger(MainLayout.class);
    private MoneyService moneyService;
    private NumberField balanceField;
    private SecurityService securityService;

    private H1 viewTitle;

    @Autowired
    public MainLayout(MoneyService moneyService, SecurityService securityService) {
        this.moneyService = moneyService;
        this.securityService = securityService;
        this.viewTitle = new H1();

        init(moneyService,securityService);
    }

    private void init(MoneyService moneyService,SecurityService securityService) {
        setPrimarySection(Section.DRAWER);
        addDrawerContent(moneyService,securityService);
        addHeaderContent();
    }


    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent(MoneyService moneyService,SecurityService securityService) {
        UI ui = UI.getCurrent();
        if (ui != null) {
            BalanceBroadcaster.register(newBalance -> {
                ui.access(() -> updateBalance(newBalance));
            });
        }

        Span appName = new Span("Puławy F7");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        TextField balanceLabel = new TextField();
        balanceLabel.setValue("DOSTĘPNE ŚRODKI");
        balanceLabel.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
        balanceLabel.setReadOnly(true);


        balanceField = new NumberField();
        balanceField.addClassNames(
                LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.EXTRABOLD,
                LumoUtility.Background.SHADE, LumoUtility.Position.STATIC,
                LumoUtility.Position.Bottom.XLARGE, LumoUtility.Border.BOTTOM, LumoUtility.TextAlignment.CENTER);
        balanceField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
        balanceField.setReadOnly(true);
        balanceField.getElement().getStyle().set("transition", "opacity 0.5s ease-in-out");
        balanceField.setValue(moneyService.getCurrentBalance());

        BalanceBroadcaster.register(this::updateBalance);

        Scroller scroller = new Scroller(createNavigation(securityService));

        addToDrawer(balanceLabel, balanceField, header, scroller, createFooter());
    }

    private void updateBalance(double newBalance) {
        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.access(() -> {
                balanceField.getElement().getStyle().set("opacity", "0");
                ui.setPollInterval(500);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ui.access(() -> {
                            balanceField.setValue(newBalance);
                            balanceField.getElement().getStyle().set("opacity", "1");
                            ui.setPollInterval(-1);
                        });
                    }
                }, 500);
            });
        }
    }

    private SideNav createNavigation(SecurityService securityService) {
        SideNav navigation = new SideNav();

        SideNavItem people = new SideNavItem("Ludzie", UserView.class, VaadinIcon.MALE.create());

        SideNavItem deposit = new SideNavItem("Wpłaty", DepositAdderView.class, VaadinIcon.DOLLAR.create());
        deposit.addItem(new SideNavItem("Historia wpłat", DepositListView.class, VaadinIcon.BOOK_DOLLAR.create()));

        SideNavItem shopping = new SideNavItem("Zakupy", ShoppingView.class, VaadinIcon.CART.create());
        shopping.addItem(new SideNavItem("Historia zakupów", ShoppingHistoryView.class, VaadinIcon.LINES_LIST.create()));
        if (securityService.hasRole("ADMIN")){
            shopping.addItem(new SideNavItem("Zatwierdzanie zamówień", PurchaseConfirmation.class,VaadinIcon.CHECK_CIRCLE.create()));
        }

        navigation.addItem(people, deposit, shopping);

        return navigation;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        setContent(getContent());
    }
}
