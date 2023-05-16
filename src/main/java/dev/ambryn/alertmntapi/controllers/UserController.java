package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.beans.Role;
import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.dto.RoleDTO;
import dev.ambryn.alertmntapi.dto.mappers.dto.UserMapper;
import dev.ambryn.alertmntapi.dto.user.UserCreateDTO;
import dev.ambryn.alertmntapi.dto.user.UserGetDTO;
import dev.ambryn.alertmntapi.dto.user.UserGetFinestDTO;
import dev.ambryn.alertmntapi.errors.DataAccessException;
import dev.ambryn.alertmntapi.errors.InternalServerException;
import dev.ambryn.alertmntapi.errors.NotFoundException;
import dev.ambryn.alertmntapi.repositories.RoleRepository;
import dev.ambryn.alertmntapi.repositories.UserRepository;
import dev.ambryn.alertmntapi.responses.Created;
import dev.ambryn.alertmntapi.responses.Ok;
import dev.ambryn.alertmntapi.security.JwtUtils;
import dev.ambryn.alertmntapi.validators.BeanValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    Logger logger = LoggerFactory.getLogger("UserController");
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<UserGetDTO>> getUsers() {
        logger.debug("Getting all users");
        List<UserGetDTO> users = userRepository.findAll()
                                               .stream()
                                               .map(UserMapper::toDto)
                                               .toList();
        return Ok.build(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGetFinestDTO> getUser(@PathVariable("id") Long id) {
        logger.debug("Getting user with id={}", id);
        return userRepository.findById(id)
                             .map(UserMapper::toFinestDto)
                             .map(Ok::build)
                             .orElseThrow(() -> new NotFoundException("Could not find user with id=" + id));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserGetFinestDTO> getProfile(@RequestHeader("Authorization") String token) {
        logger.debug("Getting user profile");
        return jwtUtils.getEmailFromBearer(token)
                       .flatMap(email -> userRepository.findByEmail(email))
                       .map(UserMapper::toFinestDto)
                       .map(user -> Ok.build(user))
                       .orElseThrow(() -> new NotFoundException("Could not find user corresponding to Jwt"));
    }

    @PostMapping
    public ResponseEntity<UserGetDTO> saveUser(@RequestBody UserCreateDTO userDTO) {
        logger.debug("Saving user={}", userDTO);
        BeanValidator.validate(userDTO);

        User user = UserMapper.toUser(userDTO);
        user.setPassword("testtest");
        System.out.println(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            userRepository.save(user);
            System.out.println(user);
            return Created.build(UserMapper.toDto(user));
        } catch (DataAccessException dae) {
            throw new InternalServerException(dae.getMessage());
        }
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity addRole(@PathVariable("id") Long id, RoleDTO roleToAdd) {
        logger.debug("Add role {} to user with id={}", roleToAdd, id);
        BeanValidator.validate(roleToAdd);

        Optional<User> oUser = userRepository.findById(id);
        Optional<Role> oRole = roleRepository.findById(roleToAdd.id());
        if (oUser.isPresent() && oRole.isPresent()) {
            User user = oUser.get();
            user.addRole(oRole.get());
            userRepository.save(user);
            return Ok.build();
        }
        throw new NotFoundException("Could not find user with id=" + id + " or role to add is not valid");
    }
}
