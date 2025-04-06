//package pl.eurokawa.services;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import pl.eurokawa.data.User;
//import pl.eurokawa.data.UserRepository;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private static final Logger log = LogManager.getLogger(CustomUserDetailsService.class);
//    UserRepository userRepository;
//
//    public CustomUserDetailsService(UserRepository userRepository){
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Nie ma takiego użytkownika!"));
//        Set<GrantedAuthority> authorities = new HashSet<>();
//        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
//
//
//        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),authorities);
//
//
////        return userRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + email));
//    }
//}
