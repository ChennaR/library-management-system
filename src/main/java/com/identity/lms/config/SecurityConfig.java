package com.identity.lms.config;

import com.identity.lms.web.AuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for library management service
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationFilter authenticationFilter;

    public SecurityConfig(AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/library/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/library/books/isbn/").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/library/books/author/").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/library/books/borrow/").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/library/books/return/").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/library/books").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/library/books").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
