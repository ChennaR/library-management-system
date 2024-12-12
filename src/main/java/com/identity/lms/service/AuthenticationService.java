package com.identity.lms.service;

import com.identity.lms.domain.AuthRequest;
import com.identity.lms.domain.AuthResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service to generate JWT tokens and validating
 * it with default signing key.
 */
@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtAuthenticationService jwtAuthenticationService;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtAuthenticationService jwtAuthenticationService) {
        this.authenticationManager = authenticationManager;
        this.jwtAuthenticationService = jwtAuthenticationService;
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                        authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return new AuthResponse(jwtAuthenticationService.createToken(authentication));
        }
        throw new UsernameNotFoundException("User not found  in the system.");
    }
}
