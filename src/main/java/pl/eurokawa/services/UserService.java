package pl.eurokawa.services;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public Optional<User> getUserByEmail(String email){
        Optional<User> userByEmail = userRepository.findUserByEmail(email);

        return userByEmail;
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
