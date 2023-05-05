package dev.ambryn.alertmntapi.dto.mappers.dto;


import dev.ambryn.alertmntapi.beans.Role;
import dev.ambryn.alertmntapi.dto.RoleDTO;
import dev.ambryn.alertmntapi.enums.ERole;

public class RoleMapper {

    public static RoleDTO toDTO(Role role) {
        return new RoleDTO(role.getId(), role.getName());
    }

    public static Role toRole(RoleDTO dto) {
        return new Role(ERole.valueOf(dto.name().toString()));
    }
}
