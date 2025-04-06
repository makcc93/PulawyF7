//package pl.eurokawa.services;
//import com.vaadin.flow.server.VaadinSession;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//@EnableMethodSecurity
//@EnableWebSecurity
//@Configuration
//public class SecurityUtils {
//    private static Logger log = LogManager.getLogger();
//
//    public static boolean isUserLoggedIn(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        log.info("Aktualne uwierzytelnienie: {}", authentication);
//
//        if (authentication == null) {
//            log.error("Brak obiektu Authentication w SecurityContextHolder!");
//            return false;
//        }
//
//        log.info("Czy użytkownik jest uwierzytelniony? {}", authentication.isAuthenticated());
//
//        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
//            log.info("Zalogowany użytkownik: {}", userDetails.getUsername());
//        } else {
//            log.warn("Principal nie jest instancją UserDetails: {}", authentication.getPrincipal());
//        }
//
//        return authentication != null
//                && authentication.isAuthenticated()
//                && !(authentication.getPrincipal() instanceof String); //bylo AnonymousAuthenticationToken
//
//    }
//
//
//    public String getAuthenticatedUsername() {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
//                 return userDetails.getUsername();
//        }
//
//        return null;
//    }
//}
