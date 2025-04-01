package pl.eurokawa.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Route("home")
public class HomeView extends VerticalLayout {
    Logger logger = LogManager.getLogger(HomeView.class);
}
