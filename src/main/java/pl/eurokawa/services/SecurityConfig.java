//package pl.eurokawa.services;
//
//import com.vaadin.flow.spring.security.AuthenticationContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import pl.eurokawa.data.UserRepository;
//
//import java.lang.reflect.Executable;
//@EnableMethodSecurity
//@EnableWebSecurity
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
//        return http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/",
//                                "/login",
//                                "/login/**",
//                                "/register",
//                                "/error",
//                                "/webjars/**",
//                                "/static/**",
//                                "/VAADIN/**",
//                                "/frontend/**",
//                                "/webapp/**",
//                                "/sw.js",
//                                "/sw-runtime-resources-precache.js"
//                        ).permitAll()
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/user/**").hasRole("USER")
//                        .anyRequest().authenticated()
//                )
//                .formLogin(login -> login
//                        .loginPage("/login")
//                        .defaultSuccessUrl("/home", true) //wczesniej bylo /
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/login?logout")
//                        .invalidateHttpSession(true)
//                        .clearAuthentication(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                )
//                .sessionManagement(session ->
//                        session
//                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
////                                .maximumSessions(1))
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers(
//                                "/",
//                                "/VAADIN/**",
//                                "/frontend/**",
//                                "/webapp/**",
//                                "/sw.js",
//                                "/sw-runtime-resources-precache.js"
//                        )
//                )
//                .anonymous(anonymous -> anonymous.disable())
//                .build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
////    @Bean
////    public AuthenticationManager authenticationManager (
////        AuthenticationConfiguration authenticationConfiguration,
////        CustomUserDetailsService userDetailsService,  //tu robie zmiane 01.04
//////        UserDetailsService userDetailsService,
////        PasswordEncoder passwordEncoder) throws Exception {
////        var provider = new DaoAuthenticationProvider();
////        provider.setUserDetailsService(userDetailsService);
////        provider.setPasswordEncoder(passwordEncoder);
////
////        return new ProviderManager(provider);
////
////        //wczesniej bylo UserDetailsService zamiast Custom...
////    }
////
////
////    @Bean
////    public UserDetailsService userDetailsService (CustomUserDetailsService customUserDetailsService){
////        return customUserDetailsService;
////    }
//
//    @Bean
//    public AuthenticationContext authenticationContext() {
//        return new AuthenticationContext();
//    }
//}
