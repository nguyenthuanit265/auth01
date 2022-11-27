package com.auth.utils;

import com.auth.model.entity.User;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtils {
    private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtils.class);
    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 hour

    @Value("${app.jwt.secret:secret1}")
    private String SECRET_KEY;

    /**
     * Used to verify a given JWT. It returns true if the JWT is verified, or false otherwise.
     */
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            LOGGER.error("JWT expired: {}. Error {}", ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Token is null, empty or only whitespace: {}, {}", ex.getMessage(), ex);
        } catch (MalformedJwtException ex) {
            LOGGER.error("JWT is invalid", ex);
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("JWT is not supported", ex);
        } catch (SignatureException ex) {
            LOGGER.error("Signature validation failed");
        }

        return false;
    }

    /**
     * Gets the value of the subject field of a given token.
     * The subject contains User ID and email, which will be used to recreate a User object.
     */
    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateAccessToken(User user) {
        try {
            return Jwts.builder()
                    .setSubject(String.format("%s_%s", user.getUserId(), user.getEmail()))
                    .setIssuer(user.getEmail())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                    .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                    .compact();
        } catch (Exception ex) {
            LOGGER.error("Error generateAccessToken. Exception: {}, {}", ex.getMessage(), ex);
            return null;
        }
    }

}
