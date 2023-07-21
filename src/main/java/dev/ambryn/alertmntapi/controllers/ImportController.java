package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.beans.Role;
import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.dto.mappers.dto.UserMapper;
import dev.ambryn.alertmntapi.enums.ERole;
import dev.ambryn.alertmntapi.errors.BadRequestException;
import dev.ambryn.alertmntapi.errors.InternalServerException;
import dev.ambryn.alertmntapi.repositories.RoleRepository;
import dev.ambryn.alertmntapi.repositories.UserRepository;
import dev.ambryn.alertmntapi.responses.Ok;
import dev.ambryn.alertmntapi.services.FileService;
import dev.ambryn.alertmntapi.validators.BeanValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/import")
public class ImportController {

    Logger logger = LoggerFactory.getLogger("ImportController");

    @Autowired
    FileService fileService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestBody MultipartFile file) {
        logger.debug("Import file");

        try {
            Role role = roleRepository.findByName(ERole.ROLE_USER)
                                      .orElseThrow(() -> new InternalServerException("Could not find role ROLE_USER"));

            String csv = new String(file.getBytes());
            String[] lines = csv.split("\n");
            List<User> users = Arrays.stream(lines)
                                     .map(line -> {
                                         String[] fields = line.split(",");
                                         User user = new User();
                                         user.setLastname(fields[0]);
                                         user.setFirstname(fields[1]);
                                         user.setEmail(fields[2]);
                                         user.setPassword(passwordEncoder.encode(fields[3]));
                                         user.addRole(role);
                                         return user;
                                     })
                                     .toList();

            users.forEach(BeanValidator::validate);

            userRepository.saveAll(users);

            return Ok.build(users.stream()
                                 .map(UserMapper::toDto)
                                 .toList());
        } catch (IOException e) {
            throw new InternalServerException("Could not read file");
        }
    }
}
