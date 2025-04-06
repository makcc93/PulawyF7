package pl.eurokawa.security;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    public boolean hasRole(String role){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
Authentication authentication = VaadinSession.getCurrent().getAttribute(Authentication.class);

        if (authentication == null || !authentication.isAuthenticated()){
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authRole -> authRole.equals("ROLE_" + role));
    }
}
