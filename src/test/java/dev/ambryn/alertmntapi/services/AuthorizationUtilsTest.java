package dev.ambryn.alertmntapi.services;

import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.beans.Role;
import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.enums.ERole;
import dev.ambryn.alertmntapi.enums.EVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AuthorizationUtilsTest {

    Channel channel;
    User user;

    @BeforeEach
    void setup() {
        channel = new Channel("Test", EVisibility.PUBLIC);
        user = new User("test@test.com", "test", "Test", "Test");
    }

    @Test
    void isAdmin_ReturnTrueWhenUserHasRoleAdmin() {
        // WHEN
        user.addRole(new Role(ERole.ROLE_ADMIN));

        assertTrue(AuthorizationUtils.isAdmin(user));
    }

    @Test
    void isAdmin_ReturnFalseWhenUserIsNotAdmin() {
        // WHEN
        user.addRole(new Role(ERole.ROLE_USER));

        assertFalse(AuthorizationUtils.isAdmin(user));
    }

    @Test
    void isMember_ReturnsTrueWhenUserIsMember() {
        // WHEN
        channel.addMember(user);

        assertTrue(AuthorizationUtils.isMember(user, channel));
    }

    @Test
    void isMember_ReturnsFalseWhenUserIsNotMember() {
        assertFalse(AuthorizationUtils.isMember(user, channel));
    }

    @Test
    void isMemberOrAdmin_ReturnsFalseWhenUserNorMemberNorAdmin() {
        assertFalse(AuthorizationUtils.isMemberOrAdmin(user, channel));
    }

    @Test
    void isMemberOrAdmin_ReturnsTrueWhenUserMemberAndAdmin() {
        channel.addMember(user);
        user.addRole(new Role(ERole.ROLE_ADMIN));

        assertTrue(AuthorizationUtils.isMemberOrAdmin(user, channel));
    }

    @Test
    void isMemberOrAdmin_ReturnsTrueWhenUserIsMember() {
        channel.addMember(user);

        assertTrue(AuthorizationUtils.isMemberOrAdmin(user, channel));
    }

    @Test
    void isMemberOrAdmin_ReturnsTrueWhenUserIsAdmin() {
        user.addRole(new Role(ERole.ROLE_ADMIN));

        assertTrue(AuthorizationUtils.isMemberOrAdmin(user, channel));
    }
}