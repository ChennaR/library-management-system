package com.identity.lms.controller;

import com.identity.lms.domain.AuthRequest;
import com.identity.lms.domain.AuthResponse;
import com.identity.lms.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to provide library user authentication endpoint.
 */
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/api/v1/library/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        return authenticationService.authenticate(authRequest);
    }
}
