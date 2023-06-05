package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.beans.Role;
import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.dto.mappers.dto.UserMapper;
import dev.ambryn.alertmntapi.dto.user.UserCreateDTO;
import dev.ambryn.alertmntapi.dto.user.UserGetDTO;
import dev.ambryn.alertmntapi.dto.user.UserGetFinestDTO;
import dev.ambryn.alertmntapi.dto.user.UserUpdateDTO;
import dev.ambryn.alertmntapi.enums.ERole;
import dev.ambryn.alertmntapi.errors.DataAccessException;
import dev.ambryn.alertmntapi.errors.InternalServerException;
import dev.ambryn.alertmntapi.errors.NotFoundException;
import dev.ambryn.alertmntapi.repositories.*;
import dev.ambryn.alertmntapi.responses.Created;
import dev.ambryn.alertmntapi.responses.NoContent;
import dev.ambryn.alertmntapi.responses.Ok;
import dev.ambryn.alertmntapi.security.JwtUtils;
import dev.ambryn.alertmntapi.validators.BeanValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    Logger logger = LoggerFactory.getLogger("UserController");
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    MeetingRepository meetingRepository;
    @Autowired
    GroupRepository groupRepository;

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
                       .map(Ok::build)
                       .orElseThrow(() -> new NotFoundException("Could not find user corresponding to Jwt"));
    }

    @PutMapping
    public ResponseEntity<UserGetFinestDTO> updateUser(@RequestBody UserUpdateDTO userDTO) {
        logger.debug("Updating user={}", userDTO);
        BeanValidator.validate(userDTO);

        User user = userRepository.findById(userDTO.id())
                                  .orElseThrow(() -> new NotFoundException("Could not find User with id=" + userDTO.id()));

        user.setFirstname(userDTO.firstname());
        user.setLastname(userDTO.lastname());
        user.setEmail(userDTO.email());

        if (userDTO.roles() != null) {
            Set<Role> roles = userDTO.roles()
                                     .stream()
                                     .map(role -> roleRepository.findById(role.id())
                                                                .orElseThrow(() -> new NotFoundException(
                                                                        "Could not find role with id=" + role.id())))
                                     .collect(Collectors.toSet());

            roles.add(roleRepository.findByName(ERole.ROLE_USER)
                                    .orElseThrow(() -> new InternalServerException("Could not find role user")));
            user.setRoles(roles);
        }
        try {
            userRepository.save(user);
            return Ok.build(UserMapper.toFinestDto(user));
        } catch (DataAccessException dae) {
            throw new InternalServerException(dae.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<UserGetDTO> saveUser(@RequestBody UserCreateDTO userDTO) {
        logger.debug("Saving user={}", userDTO);
        BeanValidator.validate(userDTO);

        User user = UserMapper.toUser(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepository.findByName(ERole.ROLE_USER)
                                  .orElseThrow(() -> new InternalServerException("Could not find role USER"));
        user.addRole(role);
        try {
            userRepository.save(user);
            return Created.build(UserMapper.toDto(user));
        } catch (DataAccessException dae) {
            throw new InternalServerException(dae.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        logger.debug("Delete user with id={}", id);

        channelRepository.deleteUserFromChannels(id);
        messageRepository.deleteAllByCreatorId(id);
        meetingRepository.deleteAllByCreatorId(id);
        notificationRepository.deleteAllByReceiverId(id);
        groupRepository.deleteUserFromGroups(id);

        userRepository.deleteById(id);
        return NoContent.build();
    }
}
