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

@Service
public class AuthorizationService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    JwtUtils jwtUtils;

    public boolean isAdmin(User user) {
        return user.getRoles().stream().map(Role::getName).toList().contains(ERole.ROLE_ADMIN);
    }

    public boolean isMember(String jwt, Long channelId) {
        return jwtUtils.getEmailFromBearer(jwt)
                .flatMap(userRepository::findByEmail)
                .flatMap(user -> channelRepository.findById(channelId)
                        .map(Channel::getMembers)
                        .map(users -> users.contains(user)))
                .orElse(false);
    }
}
