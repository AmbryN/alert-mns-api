package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.beans.Group;
import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.dto.AddDTO;
import dev.ambryn.alertmntapi.dto.GroupGetFinestDTO;
import dev.ambryn.alertmntapi.dto.group.GroupCreateDTO;
import dev.ambryn.alertmntapi.dto.group.GroupGetDTO;
import dev.ambryn.alertmntapi.dto.mappers.dto.GroupMapper;
import dev.ambryn.alertmntapi.dto.mappers.dto.UserMapper;
import dev.ambryn.alertmntapi.dto.user.UserGetDTO;
import dev.ambryn.alertmntapi.enums.EError;
import dev.ambryn.alertmntapi.errors.ApplicationError;
import dev.ambryn.alertmntapi.errors.DataAccessException;
import dev.ambryn.alertmntapi.errors.NotFoundException;
import dev.ambryn.alertmntapi.repositories.GroupRepository;
import dev.ambryn.alertmntapi.repositories.UserRepository;
import dev.ambryn.alertmntapi.responses.Ok;
import dev.ambryn.alertmntapi.validators.BeanValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
                .stream().map(GroupMapper::toDTO)
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
                .map(users -> users.stream().map(UserMapper::toDto).toList())
                .map(Ok::build)
                .orElseThrow(() -> new NotFoundException("Could not find group with id=" + id));
    }

    @PostMapping
    public ResponseEntity<GroupGetDTO> createGroup(@RequestBody GroupCreateDTO newGroup) {
        BeanValidator.validate(newGroup);

        Group group = GroupMapper.toGroup(newGroup);

        ResponseEntity response = null;
        try {
            groupRepository.save(group);
            response = ResponseEntity.ok(group);
        } catch (DataAccessException dae) {
            ApplicationError error = new ApplicationError.Builder()
                    .setCode(EError.ServerError)
                    .setMessage(dae.getMessage())
                    .build();
            response = ResponseEntity.internalServerError().body(error);;
        }
        return response;
    }

    @PostMapping("/{id:[0-9]+}/members")
//    @Authorize(level = ERole.ADMIN)
    public ResponseEntity<UserGetDTO> addMember(@PathVariable("id") Long id, @RequestBody AddDTO userToAdd) {
        BeanValidator.validate(userToAdd);

        try {
            Optional<Group> oGroup = groupRepository.findById(id);

            if (oGroup.isPresent()) {
                Group group = oGroup.get();
                Optional<User> oUser = userRepository.findById(userToAdd.id());

                if (oUser.isPresent()) {
                    User user = oUser.get();
                    group.addMember(user);
                    groupRepository.save(group);

                    return ResponseEntity.ok().build();
                } else {
                    throw new NotFoundException("Could not find user with id=" + userToAdd.id());
                }
            } else {
                throw new NotFoundException("Could not find group with id=" + id);
            }
        } catch (DataAccessException dae) {
            throw new InternalError(dae.getMessage());
        }
    }
}
