package pl.eurokawa.security;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.eurokawa.data.User;
import pl.eurokawa.data.UserRepository;
import pl.eurokawa.services.UserService;

import java.util.Optional;

@Service
public class SecurityService {

    private UserRepository userRepository;

    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean hasRole(String role){
        Authentication authentication = VaadinSession.getCurrent().getAttribute(Authentication.class);

        if (authentication == null || !authentication.isAuthenticated()){
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authRole -> authRole.equals("ROLE_" + role));
    }

    public String getLoggedUserFirstAndLastName(){
        Authentication authentication = VaadinSession.getCurrent().getAttribute(Authentication.class);

        if (authentication != null || authentication.getPrincipal() instanceof ModifiedUserDetails){
            ModifiedUserDetails user = (ModifiedUserDetails) authentication.getPrincipal();

            String email = user.getUsername();
            User userByEmail = userRepository.findUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Nie mogę znaleźć zalogowanego użytkownika"));

            String firstName = userByEmail.getFirstName();
            String lastName = userByEmail.getLastName();

            return firstName + " " + lastName;
        }
        else {

            return "";
        }

    }

    public User getLoggedUser(){
        Authentication authentication = VaadinSession.getCurrent().getAttribute(Authentication.class);

        if (authentication != null || authentication.getPrincipal() instanceof ModifiedUserDetails){
            ModifiedUserDetails user = (ModifiedUserDetails) authentication.getPrincipal();

            String email = user.getUsername();
            User userByEmail = userRepository.findUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Nie mogę znaleźć zalogowanego użytkownika"));

            return userByEmail;
        }

        return new User();

    }
}
