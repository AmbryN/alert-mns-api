package dev.ambryn.alertmntapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Value("${cors.domain}")
    String domain;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic()
            .disable()
            .csrf()
            .disable()
            .cors()
            .configurationSource(request -> {
                CorsConfiguration cors = new CorsConfiguration();
                cors.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "PATCH"));
                cors.setExposedHeaders(List.of("Allow-Access-Control-Origin",
                                               "Allow-Access-Control-Method",
                                               "Content-Disposition"));
                cors.setAllowedOrigins(List.of(domain));

                cors.applyPermitDefaultValues();
                return cors;
            })
            .and()
            .authorizeHttpRequests(authorize -> authorize.requestMatchers("/login", "/chat")
                                                         .permitAll()
                                                         .requestMatchers("/groups/**", "/roles/**")
                                                         .hasRole("ADMIN")
                                                         .requestMatchers("/**")
                                                         .hasAnyRole("USER", "ADMIN")
                                                         .anyRequest()
                                                         .authenticated())

            .exceptionHandling()
            .and()
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder creationPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
                                                                                                                Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
