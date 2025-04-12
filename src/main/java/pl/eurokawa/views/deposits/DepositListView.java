package pl.eurokawa.views.deposits;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import pl.eurokawa.data.Money;
import pl.eurokawa.data.MoneyRepository;
import pl.eurokawa.services.MoneyService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
@UIScope
@Data
@PageTitle("Historia wpłat")
@Route("deposithistory")
@Menu(order = 2.1, icon = LineAwesomeIconUrl.PEPPER_HOT_SOLID)
public class DepositListView extends Div {
    MoneyRepository moneyRepository;
    MoneyService moneyService;
    private static final Logger logger = LogManager.getLogger(DepositListView.class);



    public DepositListView(MoneyRepository moneyRepository, MoneyService moneyService) {
        this.moneyRepository = moneyRepository;
        this.moneyService = moneyService;

        List<Money> orders = moneyService.getOrderHistory();

        Grid<Money> orderGrid = new Grid<>(Money.class,false);

        orderGrid.setItems(orders);
        orderGrid.setAllRowsVisible(true);

        orderGrid.addColumn(Money::getUser).setHeader("OSOBA").setAutoWidth(true);

        orderGrid.addColumn(Money::getDeposit).setHeader("WPŁATA").setAutoWidth(true);

        orderGrid.addColumn(new ComponentRenderer<>(money -> {
            String createdAt = String.valueOf(money.getCreatedAt());
            LocalDateTime date = LocalDateTime.parse(createdAt);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss",Locale.of("pl","PL"));

            String finalDate = date.format(dateTimeFormatter);

            return new Span(finalDate);
        })).setHeader("DATA").setAutoWidth(true);

        orderGrid.addColumn(Money::getBalance).setHeader("ŚRODKI PO WPŁACIE").setAutoWidth(true);

        add(orderGrid);
    }

}
