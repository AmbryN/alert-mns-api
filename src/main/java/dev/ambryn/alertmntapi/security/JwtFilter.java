package dev.ambryn.alertmntapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Optional.ofNullable(request.getHeader("Authorization"))
                .flatMap(header -> jwtUtils.extractJwtFromBearer(header))
                .map(jwtUtils::getData)
                .map(data -> userDetailsService.loadUserByUsername(data.getSubject()))
                .ifPresent(userDetails -> {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                            request));

                    SecurityContextHolder.getContext()
                                         .setAuthentication(usernamePasswordAuthenticationToken);
                });
        filterChain.doFilter(request, response);
    }
}
