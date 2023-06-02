package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.beans.Group;
import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.dto.AddDTO;
import dev.ambryn.alertmntapi.dto.channel.ChannelGetFinestDTO;
import dev.ambryn.alertmntapi.dto.message.MessageGetDTO;
import dev.ambryn.alertmntapi.dto.channel.ChannelCreateDTO;
import dev.ambryn.alertmntapi.dto.channel.ChannelGetDTO;
import dev.ambryn.alertmntapi.dto.mappers.dto.ChannelMapper;
import dev.ambryn.alertmntapi.dto.mappers.dto.MessageMapper;
import dev.ambryn.alertmntapi.enums.EVisibility;
import dev.ambryn.alertmntapi.errors.DataAccessException;
import dev.ambryn.alertmntapi.errors.ForbiddenException;
import dev.ambryn.alertmntapi.errors.InternalServerException;
import dev.ambryn.alertmntapi.errors.NotFoundException;
import dev.ambryn.alertmntapi.repositories.ChannelRepository;
import dev.ambryn.alertmntapi.repositories.GroupRepository;
import dev.ambryn.alertmntapi.repositories.UserRepository;
import dev.ambryn.alertmntapi.responses.Created;
import dev.ambryn.alertmntapi.responses.Ok;
import dev.ambryn.alertmntapi.security.JwtUtils;
import dev.ambryn.alertmntapi.services.AuthorizationUtils;
import dev.ambryn.alertmntapi.validators.BeanValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/channels")
public class ChannelController {

    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<List<ChannelGetDTO>> getChannels() {
        List<ChannelGetDTO> channels = channelRepository.findAll()
                                                        .stream()
                                                        .map(ChannelMapper::toDTO)
                                                        .toList();
        return Ok.build(channels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChannelGetFinestDTO> getChannel(@PathVariable("id") Long id) {
        return channelRepository.findById(id)
                                .map(ChannelMapper::toFinestDTO)
                                .map(Ok::build)
                                .orElseThrow(() -> new NotFoundException("Could not find channel with id=" + id));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageGetDTO>> getMessages(@PathVariable("id") Long id) {
        return channelRepository.findById(id)
                                .map(Channel::getMessages)
                                .map(messages -> messages.stream()
                                                         .map(MessageMapper::toDTO)
                                                         .toList())
                                .map(Ok::build)

                                .orElseThrow(() -> new NotFoundException("Could not find messages of channel with " + "id=" + id));
    }

    /**
     * Allows for the creation of a new channel if the authorized user has ADMIN privileges
     * OR if the user does not have ADMIN privileges but creates a PRIVATE channel.
     *
     * @param bearer           JSON Web Token
     * @param channelCreateDTO the channel to create
     * @return HTTP Response
     */
    @PostMapping
    public ResponseEntity<ChannelGetDTO> createChannel(@RequestHeader("Authorization") String bearer,
                                                       @RequestBody ChannelCreateDTO channelCreateDTO) {
        BeanValidator.validate(channelCreateDTO);

        Optional<String> oEmail = jwtUtils.getEmailFromBearer(bearer);
        if (oEmail.isPresent()) {
            Optional<User> oUser = userRepository.findByEmail(oEmail.get());

            if (oUser.isPresent()) {
                User user = oUser.get();
                boolean isAdmin = AuthorizationUtils.isAdmin(user);

                if (isAdmin || channelCreateDTO.visibility() == EVisibility.PRIVATE) {
                    Channel newChannel = ChannelMapper.toChannel(channelCreateDTO);

                    try {
                        channelRepository.save(newChannel);
                        return Created.build(ChannelMapper.toDTO(newChannel));
                    } catch (DataAccessException dae) {
                        throw new InternalServerException(dae.getMessage());
                    }
                }
            }
        }

        throw new ForbiddenException();
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ChannelGetFinestDTO> addMembers(@PathVariable("id") Long id,
                                                          @RequestBody List<AddDTO> membersToAdd) {
        membersToAdd.forEach(BeanValidator::validate);

        try {
            Channel channel = channelRepository.findById(id)
                                               .orElseThrow(() -> new NotFoundException(
                                                       "Could not find channel with id=" + id));

            membersToAdd.stream()
                        .map(member -> userRepository.findById(member.id())
                                                     .orElseThrow(() -> new NotFoundException(
                                                             "Could not find user with id=" + member.id())))
                        .forEach(channel::addMember);
            channelRepository.save(channel);
            return Ok.build(ChannelMapper.toFinestDTO(channel));
        } catch (DataAccessException dae) {
            throw new InternalServerException(dae.getMessage());
        }
    }

    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<ChannelGetFinestDTO> removeMember(@PathVariable("id") Long id,
                                                            @PathVariable("memberId") Long memberId) {
        Channel channel = channelRepository.findById(id)
                                           .orElseThrow(() -> new NotFoundException("Could not find channel with id=" + id));
        User user = userRepository.findById(memberId)
                                  .orElseThrow(() -> new NotFoundException("Could not find user with id=" + memberId));

        channel.removeMember(user);
        channelRepository.save(channel);

        return Ok.build(ChannelMapper.toFinestDTO(channel));
    }

    @PostMapping("/{id}/groups")
    public ResponseEntity<ChannelGetFinestDTO> addGroups(@PathVariable("id") Long id,
                                                         @RequestBody List<AddDTO> groupsToAdd) {
        groupsToAdd.forEach(BeanValidator::validate);

        try {
            Channel channel = channelRepository.findById(id)
                                               .orElseThrow(() -> new NotFoundException(
                                                       "Could not find channel with id=" + id));

            groupsToAdd.stream()
                       .map(group -> groupRepository.findById(group.id())
                                                    .orElseThrow(() -> new NotFoundException(
                                                            "Could not find group with id=" + group.id())))
                       .forEach(channel::addGroup);

            channelRepository.save(channel);

            return Ok.build(ChannelMapper.toFinestDTO(channel));
        } catch (DataAccessException dae) {
            throw new InternalServerException(dae.getMessage());
        }
    }

    @DeleteMapping("/{id}/groups/{groupId}")
    public ResponseEntity<ChannelGetFinestDTO> removeGroup(@PathVariable("id") Long id,
                                                           @PathVariable("groupId") Long groupId) {
        Channel channel = channelRepository.findById(id)
                                           .orElseThrow(() -> new NotFoundException("Could not find channel with id=" + id));
        Group group = groupRepository.findById(groupId)
                                     .orElseThrow(() -> new NotFoundException("Could not find group with id=" + groupId));

        channel.removeGroup(group);
        channelRepository.save(channel);

        return Ok.build(ChannelMapper.toFinestDTO(channel));
    }
}
