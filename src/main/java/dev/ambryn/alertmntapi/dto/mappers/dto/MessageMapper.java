package dev.ambryn.alertmntapi.dto.mappers.dto;

import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.beans.Message;
import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.dto.message.InSocketMessage;
import dev.ambryn.alertmntapi.dto.message.MessageGetDTO;
import dev.ambryn.alertmntapi.dto.user.UserGetDTO;
import dev.ambryn.alertmntapi.repositories.ChannelRepository;
import dev.ambryn.alertmntapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageMapper {
    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    UserRepository userRepository;

    public static MessageGetDTO toDTO(Message message) {
        User sender = message.getCreator();
        UserGetDTO senderDTO = UserMapper.toDto(sender);
        return new MessageGetDTO(message.getId(), senderDTO, message.getContent(), null, null);
    }

    public static Message toMessage(Channel channel, User user, InSocketMessage dto) {
        return new Message(channel, user, dto.content());
    }
}
