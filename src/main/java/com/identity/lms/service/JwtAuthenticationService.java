package com.identity.lms.service;

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
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.*;

/**
 * Service to generate JWT tokens and validating
 * it with default signing key.
 */
@Service
@Slf4j
public class JwtAuthenticationService {
    private static final String SECRET = Base64.getEncoder().encodeToString("Testing JWT".getBytes());
    private static final String ISSUER = "secretService";
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private static final Key SIGNING_KEY = new SecretKeySpec(DatatypeConverter.parseBase64Binary(SECRET),
            SIGNATURE_ALGORITHM.getJcaName());
    private static final int DEFAULT_EXPIRY_TIME = 1000 * 60 * 30;//30sec

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
                .setIssuer(ISSUER)
                .setExpiration(new Date(System.currentTimeMillis() + DEFAULT_EXPIRY_TIME))
                .signWith(SIGNATURE_ALGORITHM, SIGNING_KEY)
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
                    .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET))
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
