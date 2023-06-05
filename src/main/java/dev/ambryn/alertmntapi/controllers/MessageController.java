package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.beans.Message;
import dev.ambryn.alertmntapi.beans.Notification;
import dev.ambryn.alertmntapi.dto.mappers.dto.UserMapper;
import dev.ambryn.alertmntapi.dto.message.OutSocketMessage;
import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.dto.message.InSocketMessage;
import dev.ambryn.alertmntapi.errors.DataAccessException;
import dev.ambryn.alertmntapi.errors.InternalServerException;
import dev.ambryn.alertmntapi.errors.NotFoundException;
import dev.ambryn.alertmntapi.repositories.ChannelRepository;
import dev.ambryn.alertmntapi.repositories.MessageRepository;
import dev.ambryn.alertmntapi.repositories.NotificationRepository;
import dev.ambryn.alertmntapi.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Controller
public class MessageController {
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/chat/{channelId}")
    @SendTo("/topic/messages/{channelId}")
    @Transactional
    public OutSocketMessage send(@DestinationVariable Long channelId, @RequestBody InSocketMessage inSocketMessage) {
        Channel channel = channelRepository.findById(channelId)
                                           .orElseThrow(() -> new NotFoundException("Could not find channel with id=" + channelId));

        User user = userRepository.findById(inSocketMessage.userId())
                                  .orElseThrow(() -> new NotFoundException("Could not find user with id=" + channelId));

        Message message = new Message(channel, user, inSocketMessage.content());
        channel.addMessage(message);

        try {
            channelRepository.save(channel);
        } catch (DataAccessException e) {
            throw new InternalServerException("Could not save message");
        }

        return new OutSocketMessage(UserMapper.toDto(user), inSocketMessage.content());
    }
}
