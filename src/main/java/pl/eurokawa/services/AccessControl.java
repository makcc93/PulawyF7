//package pl.eurokawa.services;
//
//import com.vaadin.flow.component.UI;
//import com.vaadin.flow.component.notification.Notification;
//import com.vaadin.flow.router.BeforeEnterEvent;
//import com.vaadin.flow.router.BeforeEnterListener;
//import com.vaadin.flow.spring.security.AuthenticationContext;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//import pl.eurokawa.views.MainLayout;
//
//import java.util.Collection;
//import java.util.Optional;
//
//public class AccessControl implements BeforeEnterListener {
//    private final Logger log = LogManager.getLogger(AccessControl.class);
//    private AuthenticationContext authenticationContext;
//
//    public AccessControl(AuthenticationContext authenticationContext) {
//        this.authenticationContext = authenticationContext;
//    }
//
//    @Override
//    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
//        boolean isLogged = SecurityUtils.isUserLoggedIn();
//
//        if (!SecurityUtils.isUserLoggedIn()) {
//
//            beforeEnterEvent.rerouteTo("login");
//
//        }
//
//    }
//
//    public boolean hasRole(String role){
//        String targetRole = normalizeString(role);
//
//        Optional<UserDetails> userOpt = authenticationContext.getAuthenticatedUser(UserDetails.class);
//        if (userOpt.isEmpty()) {
//
//            return false;
//        }
//
//
//        return authenticationContext.getAuthenticatedUser(UserDetails.class)
//                .map(user -> checkUserRole(user,targetRole))
//                .orElse(false);
//
//    }
//
//    private String normalizeString(String role){
//        if (role == null) return "";
//        String trimmed = role.trim().toUpperCase();
//        return trimmed.startsWith("ROLE_") ? trimmed : "ROLE_" + trimmed;
//
//    }
//
//    private boolean checkUserRole(UserDetails user, String targetRole){
//        return user.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .anyMatch(authority -> authority.equalsIgnoreCase(targetRole));
//    }
//}
