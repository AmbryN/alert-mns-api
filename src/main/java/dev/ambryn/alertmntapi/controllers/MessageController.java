package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.beans.Message;
import dev.ambryn.alertmntapi.dto.message.OutSocketMessage;
import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.dto.message.InSocketMessage;
import dev.ambryn.alertmntapi.repositories.ChannelRepository;
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
    private UserRepository userRepository;

    @MessageMapping("/chat/{id}")
    @SendTo("/topic/messages/{id}")
    @Transactional
    public OutSocketMessage send(@DestinationVariable Long id, @RequestBody InSocketMessage inSocketMessage) {
        Optional<Channel> oChannel = channelRepository.findById(id);
        Optional<User> oUser = userRepository.findById(inSocketMessage.userId());

        if (oChannel.isPresent() && oUser.isPresent()) {
            Channel channel = oChannel.get();
            User user = oUser.get();
            Message message = new Message(channel, user, inSocketMessage.content());
            System.out.println(message);
            channel.addMessage(message);
            channelRepository.save(channel);
        }
        return new OutSocketMessage(inSocketMessage.content());
    }
}
