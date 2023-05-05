package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.beans.User;
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
import dev.ambryn.alertmntapi.repositories.UserRepository;
import dev.ambryn.alertmntapi.responses.Created;
import dev.ambryn.alertmntapi.responses.Ok;
import dev.ambryn.alertmntapi.security.JwtUtils;
import dev.ambryn.alertmntapi.services.AuthorizationService;
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
    JwtUtils jwtUtils;

    @Autowired
    AuthorizationService authorizationService;

    @GetMapping
    public ResponseEntity<List<ChannelGetDTO>> getChannels() {
        List<ChannelGetDTO> channels = channelRepository.findAll().stream().map(ChannelMapper::toDTO).toList();
        return Ok.build(channels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChannelGetDTO> getChannel(@PathVariable("id") Long id) {
        return channelRepository.findById(id)
                                .map(ChannelMapper::toDTO)
                                .map(Ok::build)
                                .orElseThrow(() -> new NotFoundException("Could not find channel with id=" + id));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageGetDTO>> getMessages(@PathVariable("id") Long id) {
        return channelRepository.findById(id)
                                .map(Channel::getMessages)
                                .map(messages -> messages.stream().map(MessageMapper::toDTO).toList())
                                .map(Ok::build)

                                .orElseThrow(() -> new NotFoundException("Could not find messages of channel with " +
                                                                                 "id=" + id));
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
                boolean isAdmin = authorizationService.isAdmin(user);

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
}
