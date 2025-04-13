package pl.eurokawa.services;

import java.util.List;
import java.util.Optional;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.eurokawa.data.User;
import pl.eurokawa.data.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String firstName,String lastName,String email, String password){ //String role
        String securedPassword = passwordEncoder.encode(password);

        User user = new User(firstName,lastName,email,securedPassword);
        userRepository.save(user);

    }

    public void setUserNewPassword (String email, String password){
        Optional<User> userByEmail = getUserByEmail(email);

        if (userByEmail.isPresent()){
            User user = userByEmail.orElseThrow();

            user.setPassword(passwordEncoder.encode(password));
            save(user);

            Notification notification = Notification.show("Poprawnie zmieniono hasło",3000, Notification.Position.BOTTOM_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
        else {
            Notification notification = Notification.show("Taki użytkownik nie jest zarejestrowany!",3000, Notification.Position.BOTTOM_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    public Optional<User> getUserByEmail(String email){
        Optional<User> userByEmail = userRepository.findUserByEmail(email);

        return userByEmail;
    }

    public Optional<User> getUserById(Integer id){
        Optional<User> userById = userRepository.findUserById(id);

        return userById;
    }

    public Optional<User> get(Integer id) {
        return userRepository.findById(id);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User save(User entity) {
        return userRepository.save(entity);
    }

    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    public Page<User> list(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return userRepository.findAll(filter, pageable);
    }

    public int count() {
        return (int) userRepository.count();
    }

}
