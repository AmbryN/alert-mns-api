package dev.ambryn.alertmntapi.dto.mappers.dto;

import dev.ambryn.alertmntapi.beans.Group;
import dev.ambryn.alertmntapi.dto.GroupGetFinestDTO;
import dev.ambryn.alertmntapi.dto.group.GroupCreateDTO;
import dev.ambryn.alertmntapi.dto.group.GroupGetDTO;

public class GroupMapper {

    public static GroupGetDTO toDTO(Group group) {
        return new GroupGetDTO(group.getId(), group.getName());
    }

    public static GroupGetFinestDTO toFinestDTO(Group group) {
        return new GroupGetFinestDTO(
                group.getId(),
                group.getName(),
                group.getMembers().stream().map(UserMapper::toDto).toList());
    }

    public static Group toGroup(GroupCreateDTO dto) {
        Group group = new Group();
        group.setId(group.getId());
        group.setName(dto.name());
        return group;
    }
}
