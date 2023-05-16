package dev.ambryn.alertmntapi.security;

import dev.ambryn.alertmntapi.errors.InvalidTokenException;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Service
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

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

    public Claims getData(String jwt) {
        try {
            return Jwts.parser()
                       .setSigningKey(jwtSecret)
                       .parseClaimsJws(jwt)
                       .getBody();
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            throw new InvalidTokenException("Invalid Token Signature");
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new InvalidTokenException("Malformed Token");
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
            throw new InvalidTokenException("Token is expired. Please login again.");
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            throw new InvalidTokenException("Invalid Token");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            throw new InvalidTokenException("Invalid Token");
        }
    }
}
