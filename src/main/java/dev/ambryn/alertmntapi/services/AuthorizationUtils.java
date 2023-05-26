package dev.ambryn.alertmntapi.services;

import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.beans.Role;
import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.enums.ERole;

public class AuthorizationUtils {

    public static boolean isMemberOrAdmin(User user, Channel channel) {
        return isAdmin(user) || isMember(user, channel);
    }

    public static boolean isAdmin(User user) {
        return user.getRoles()
                   .stream()
                   .map(Role::getName)
                   .toList()
                   .contains(ERole.ROLE_ADMIN);
    }

    public static boolean isMember(User user, Channel channel) {
        return channel.getMembers()
                      .contains(user);
    }
}
