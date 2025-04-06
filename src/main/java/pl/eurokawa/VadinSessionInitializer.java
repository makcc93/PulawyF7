//package pl.eurokawa;
//
//import com.vaadin.flow.server.ServiceInitEvent;
//import com.vaadin.flow.server.VaadinServiceInitListener;
//import com.vaadin.flow.server.VaadinSession;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//@Component
//public class VadinSessionInitializer implements VaadinServiceInitListener {
//    @Override
//    public void serviceInit(ServiceInitEvent serviceInitEvent) {
//        serviceInitEvent.getSource().addSessionInitListener((sessionInitEvent -> {
//            VaadinSession session = VaadinSession.getCurrent();
//
//            if (session != null){
//                session.setAttribute(SecurityContext.class, SecurityContextHolder.getContext());
//            }
//        }));
//
//        serviceInitEvent.getSource().addSessionDestroyListener(sessionDestroyEvent -> {
//            SecurityContextHolder.clearContext();
//        });
//    }
//}
