package com.identity.lms.service;

import com.identity.lms.config.LibrarySecurityJwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service to generate JWT tokens and validating
 * it with default signing key.
 */
@Service
@Slf4j
public class JwtAuthenticationService {
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private final Key signingKey;
    private final LibrarySecurityJwtConfig librarySecurityJwtConfig;

    public JwtAuthenticationService(LibrarySecurityJwtConfig librarySecurityJwtConfig) {
        this.librarySecurityJwtConfig = librarySecurityJwtConfig;
        signingKey = new SecretKeySpec(librarySecurityJwtConfig.getSecret().getBytes(),
                SIGNATURE_ALGORITHM.getJcaName());
    }

    /**
     * Creates JSON web token using username.
     *
     * @param authentication {@link Authentication}
     * @return the jwt
     */
    public String createToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<String> grantedAuthorities = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return new DefaultJwtBuilder()
                .setId(createJwtId())
                .setIssuedAt(new Date())
                .setSubject(user.getUsername())
                .setIssuer(librarySecurityJwtConfig.getIssuer())
                .setExpiration(new Date(System.currentTimeMillis() +
                        librarySecurityJwtConfig.getExpiry().toMillis()))
                .signWith(SIGNATURE_ALGORITHM, signingKey)
                .claim("username", user.getUsername())
                .claim("roles", grantedAuthorities)
                .compact();
    }

    /**
     * Validates token and returns username
     * throws exception if jwt is not valid
     *
     * @param token the token
     * @return string username
     */
    public Optional<Claims> validateToken(String token) {
        Optional<Claims> claims = Optional.empty();
        try {
            claims = Optional.of(Jwts.parser()
                    .setSigningKey(librarySecurityJwtConfig.getSecret().getBytes())
                    .parseClaimsJws(token)
                    .getBody());
        } catch (Exception ex) {
            log.error("Failed to parse Jwt returning empty", ex);
            return claims;
        }
        return claims;
    }

    private static String createJwtId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
