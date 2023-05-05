package dev.ambryn.alertmntapi.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Service
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private String jwtSecret = "my_secret";

    private final int jwtExpirationMs = 10 * 60 * 60 * 1000; // 10 hours in ms

    public String generateJwtToken(MyUserDetails userDetails) {
        ;
        HashMap<String, Object> data = new HashMap<>();
        data.put("firstname",
                 userDetails.getUser()
                            .getFirstname());
        data.put("lastname",
                 userDetails.getUser()
                            .getLastname());
        data.put("roles",
                 userDetails.getUser()
                            .getRoles());

        return Jwts.builder()
                   .setClaims(data)
                   .setSubject(userDetails.getUsername())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                   .signWith(SignatureAlgorithm.HS512, jwtSecret)
                   .compact();
    }

    public Optional<String> getEmailFromBearer(String bearer) {
        return extractJwtFromBearer(bearer).map(jwt -> Jwts.parser()
                                                           .setSigningKey(jwtSecret)
                                                           .parseClaimsJws(jwt)
                                                           .getBody()
                                                           .getSubject());
    }

    public Optional<String> extractJwtFromBearer(String authHeader) {
        return authHeader.startsWith("Bearer ") ? Optional.of(authHeader.substring(7)) : Optional.empty();
    }

    public boolean validateJwtToken(String bearer) {
        try {
            Optional<String> oJwt = extractJwtFromBearer(bearer);
            if (oJwt.isPresent()) {
                Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(oJwt.get());
                return true;
            }
            return false;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public Optional<Claims> getData(String jwt) {
        Optional<Claims> claims = Optional.empty();
        try {
            claims = Optional.of(Jwts.parser()
                                     .setSigningKey(jwtSecret)
                                     .parseClaimsJws(jwt)
                                     .getBody());
        } catch (SignatureException se) {
            System.out.println("Signature is invalid");
        }
        return claims;
    }
}
