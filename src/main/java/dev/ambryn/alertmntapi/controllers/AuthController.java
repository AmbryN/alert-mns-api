package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.dto.JwtResponse;
import dev.ambryn.alertmntapi.dto.LoginDTO;
import dev.ambryn.alertmntapi.errors.UnauthorizedException;
import dev.ambryn.alertmntapi.repositories.UserRepository;
import dev.ambryn.alertmntapi.responses.Ok;
import dev.ambryn.alertmntapi.security.JwtUtils;
import dev.ambryn.alertmntapi.security.MyUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired
    UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger("UserController");

    @Autowired
    JwtUtils jwt;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginDTO login) {
        logger.debug("Try to login with email=" + login.email());

        MyUserDetails userDetails;
        try {
            userDetails = (MyUserDetails) authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                                                                       login.email(),
                                                                       login.password()))
                                                               .getPrincipal();
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Email and/or password is/are incorrect");
        }

        return Ok.build(new JwtResponse(jwt.generateJwtToken(userDetails)));
    }
}
