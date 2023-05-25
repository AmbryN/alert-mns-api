package dev.ambryn.alertmntapi.services;

import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.beans.Role;
import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.enums.ERole;
import dev.ambryn.alertmntapi.repositories.UserRepository;
import dev.ambryn.alertmntapi.repositories.ChannelRepository;
import dev.ambryn.alertmntapi.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthorizationService {
  
    public boolean isMemberOrAdmin(User user, Channel channel) {
        return isAdmin(user) || isMember(user, channel);
    }

    public boolean isAdmin(User user) {
        return user.getRoles()
                   .stream()
                   .map(Role::getName)
                   .toList()
                   .contains(ERole.ROLE_ADMIN);
    }

    public boolean isMember(User user, Channel channel) {
        return user.getChannels()
                   .stream()
                   .filter(userChannel -> Objects.equals(userChannel.getId(), channel.getId()))
                   .toList()
                   .size() > 0;
    }
}
