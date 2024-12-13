package com.identity.lms.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration class for library security jwt
 */
@Getter
@Setter
@ConfigurationProperties("library.security.jwt")
public class LibrarySecurityJwtConfig {
    private String secret;
    private Duration expiry;
    private String issuer;
}
