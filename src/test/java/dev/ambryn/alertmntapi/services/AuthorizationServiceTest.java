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
class AuthorizationServiceTest {

    Channel channel;
    User user;

    @Autowired
    AuthorizationService authorizationService;

    @BeforeEach
    void setup() {
        channel = new Channel("Test", EVisibility.PUBLIC);
        user = new User("test@test.com", "test", "Test", "Test");
    }

    @Test
    void isAdmin_ReturnTrueWhenUserHasRoleAdmin() {
        // WHEN
        user.addRole(new Role(ERole.ROLE_ADMIN));

        assertTrue(authorizationService.isAdmin(user));
    }

    @Test
    void isAdmin_ReturnFalseWhenUserIsNotAdmin() {
        // WHEN
        user.addRole(new Role(ERole.ROLE_USER));

        assertFalse(authorizationService.isAdmin(user));
    }

    @Test
    void isMember_ReturnsTrueWhenUserIsMember() {
        // WHEN
        channel.addMember(user);

        assertTrue(authorizationService.isMember(user, channel));
    }

    @Test
    void isMember_ReturnsFalseWhenUserIsNotMember() {
        assertFalse(authorizationService.isMember(user, channel));
    }

    @Test
    void isMemberOrAdmin_ReturnsFalseWhenUserNorMemberNorAdmin() {
        assertFalse(authorizationService.isMemberOrAdmin(user, channel));
    }

    @Test
    void isMemberOrAdmin_ReturnsTrueWhenUserMemberAndAdmin() {
        channel.addMember(user);
        user.addRole(new Role(ERole.ROLE_ADMIN));

        assertTrue(authorizationService.isMemberOrAdmin(user, channel));
    }

    @Test
    void isMemberOrAdmin_ReturnsTrueWhenUserIsMember() {
        channel.addMember(user);

        assertTrue(authorizationService.isMemberOrAdmin(user, channel));
    }

    @Test
    void isMemberOrAdmin_ReturnsTrueWhenUserIsAdmin() {
        user.addRole(new Role(ERole.ROLE_ADMIN));

        assertTrue(authorizationService.isMemberOrAdmin(user, channel));
    }
}