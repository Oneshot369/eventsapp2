package com.shadsluiter.eventsapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shadsluiter.eventsapp.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserDetailsService uDetails, PasswordEncoder pEncoder, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = uDetails;
        this.passwordEncoder = pEncoder;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers("/users/register", "/api/users/login","users/loginForm",  "events", "/events/search", "users/", "/index", "/css/**", "/js/**").permitAll()
                .requestMatchers("orders/edit/**", "orders/delete/**", "/api/events/getAll").authenticated() // Require authentication for all order-related endpoints
                .anyRequest().authenticated())
            // .formLogin(loginform -> loginform
            //         .loginPage("/users/loginForm")
            //         .loginProcessingUrl("/login")
            //         .defaultSuccessUrl("/users/", true)
            //         .failureUrl("/users/loginForm?error=true")
            //         .permitAll())
            // .logout(logout -> logout
            //         .logoutUrl("/logout")
            //         .logoutSuccessUrl("/index")
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    // @Autowired
    // public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
    //     auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    // }
}
