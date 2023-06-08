package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.beans.Group;
import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.dto.AddDTO;
import dev.ambryn.alertmntapi.dto.GroupGetFinestDTO;
import dev.ambryn.alertmntapi.dto.group.GroupCreateDTO;
import dev.ambryn.alertmntapi.dto.group.GroupGetDTO;
import dev.ambryn.alertmntapi.dto.group.GroupUpdateDTO;
import dev.ambryn.alertmntapi.dto.mappers.dto.GroupMapper;
import dev.ambryn.alertmntapi.dto.mappers.dto.UserMapper;
import dev.ambryn.alertmntapi.dto.user.UserGetDTO;
import dev.ambryn.alertmntapi.enums.EError;
import dev.ambryn.alertmntapi.errors.ApplicationError;
import dev.ambryn.alertmntapi.errors.DataAccessException;
import dev.ambryn.alertmntapi.errors.InternalServerException;
import dev.ambryn.alertmntapi.errors.NotFoundException;
import dev.ambryn.alertmntapi.repositories.GroupRepository;
import dev.ambryn.alertmntapi.repositories.UserRepository;
import dev.ambryn.alertmntapi.responses.Ok;
import dev.ambryn.alertmntapi.validators.BeanValidator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final Logger logger = LoggerFactory.getLogger("GroupController");

    @Autowired
    GroupRepository groupRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<GroupGetDTO>> getGroups() {
        logger.debug("Getting all groups");
        List<GroupGetDTO> groups = groupRepository.findAll()
                                                  .stream()
                                                  .map(GroupMapper::toDTO)
                                                  .toList();
        return Ok.build(groups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupGetFinestDTO> getGroup(@PathVariable("id") Long id) {
        return groupRepository.findById(id)
                              .map(GroupMapper::toFinestDTO)
                              .map(Ok::build)
                              .orElseThrow(() -> new NotFoundException("Could not find group with id=" + id));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<UserGetDTO>> getMembers(@PathVariable("id") Long id) {
        return groupRepository.findById(id)
                              .map(Group::getMembers)
                              .map(users -> users.stream()
                                                 .map(UserMapper::toDto)
                                                 .toList())
                              .map(Ok::build)
                              .orElseThrow(() -> new NotFoundException("Could not find group with id=" + id));
    }

    @PostMapping
    public ResponseEntity<GroupGetDTO> createGroup(@RequestBody GroupCreateDTO newGroup) {
        BeanValidator.validate(newGroup);

        Group group = GroupMapper.toGroup(newGroup);

        try {
            groupRepository.save(group);
            return Ok.build(GroupMapper.toDTO(group));
        } catch (DataAccessException dae) {
            throw new InternalServerException(dae.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<GroupGetFinestDTO> updateGroup(@RequestBody GroupUpdateDTO updatedGroup) {
        BeanValidator.validate(updatedGroup);

        Group group = groupRepository.findById(updatedGroup.id())
                                     .orElseThrow(() -> new NotFoundException("Could not find group with id=" + updatedGroup.id()));
        group.setName(updatedGroup.name());
        try {
            groupRepository.save(group);
            return Ok.build(GroupMapper.toFinestDTO(group));
        } catch (DataAccessException ex) {
            throw new InternalServerException(ex.getMessage());
        }
    }

    @PostMapping("/{id:[0-9]+}/members")
    public ResponseEntity<?> addMembers(@PathVariable("id") Long id, @RequestBody List<AddDTO> usersToAdd) {
        usersToAdd.forEach(userToAdd -> BeanValidator.validate(userToAdd));

        try {
            Group group = groupRepository.findById(id)
                                         .orElseThrow(() -> new NotFoundException("Could not find group with id=" + id));

            usersToAdd.stream()
                      .map(user -> userRepository.findById(user.id())
                                                 .orElseThrow(() -> new NotFoundException("Could not find user with " + "id=" + user.id())))
                      .forEach(user -> group.addMember(user));

            groupRepository.save(group);

            return Ok.build(GroupMapper.toFinestDTO(group));
        } catch (DataAccessException dae) {
            throw new InternalServerException(dae.getMessage());
        }
    }

    @DeleteMapping("/{id:[0-9]+}/members/{userId:[0-9]+}")
    public ResponseEntity<?> addMember(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        try {
            Group group = groupRepository.findById(id)
                                         .orElseThrow(() -> new NotFoundException("Could not find group with id=" + id));

            User user = userRepository.findById(userId)
                                      .orElseThrow(() -> new NotFoundException("Could not find user with id=" + userId));

            group.removeMember(user);
            groupRepository.save(group);

            return Ok.build(GroupMapper.toFinestDTO(group));
        } catch (DataAccessException dae) {
            throw new InternalServerException(dae.getMessage());
        }
    }
}
