package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.dto.RoleDTO;
import dev.ambryn.alertmntapi.dto.mappers.dto.RoleMapper;
import dev.ambryn.alertmntapi.repositories.RoleRepository;
import dev.ambryn.alertmntapi.responses.Ok;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
//@Authorize(level = ERole.ADMIN)
public class RoleController {

    private final Logger logger = LoggerFactory.getLogger("RoleController");

    @Autowired
    RoleRepository roleRepository;

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getRoles() {
        logger.debug("Getting all roles");
        List<RoleDTO> roles = roleRepository.findAll()
                .stream().map(RoleMapper::toDTO)
                .toList();
        return Ok.build(roles);
    }
}
