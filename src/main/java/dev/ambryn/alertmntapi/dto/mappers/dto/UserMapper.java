package dev.ambryn.alertmntapi.dto.mappers.dto;

import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.beans.Role;
import dev.ambryn.alertmntapi.dto.channel.ChannelGetDTO;
import dev.ambryn.alertmntapi.dto.user.UserCreateDTO;
import dev.ambryn.alertmntapi.dto.user.UserGetDTO;
import dev.ambryn.alertmntapi.dto.user.UserGetFinestDTO;
import dev.ambryn.alertmntapi.dto.user.UserUpdateDTO;

import java.util.List;
import java.util.Objects;

public class UserMapper {
    public static User toUser(UserCreateDTO userDTO) {
        Long id = userDTO.id();
        String email = userDTO.email();
        String password = userDTO.password();
        String lastname = userDTO.lastname();
        String firstname = userDTO.firstname();

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setLastname(lastname);
        user.setFirstname(firstname);

        return user;
    }

    public static User toUser(UserUpdateDTO userDTO) {
        Long id = userDTO.id();
        String email = userDTO.email();
        String lastname = userDTO.lastname();
        String firstname = userDTO.firstname();

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setLastname(lastname);
        user.setFirstname(firstname);

        return user;
    }

    public static UserGetDTO toDto(User user) {
        Long id = user.getId();
        String email = user.getEmail();
        String lastname = user.getLastname();
        String firstname = user.getFirstname();
        return new UserGetDTO(id, email, lastname, firstname);
    }

    public static UserGetFinestDTO toFinestDto(User user) {
        Long id = user.getId();
        String email = user.getEmail();
        String lastname = user.getLastname();
        String firstname = user.getFirstname();
        List<Role> roles = user.getRoles()
                               .stream()
                               .toList();
        List<ChannelGetDTO> channels = user.getChannels()
                                           .stream()
                                           .map(ChannelMapper::toDTO)
                                           .toList();
        return new UserGetFinestDTO(id, email, lastname, firstname, roles, channels);
    }
}
