package dev.ambryn.alertmntapi.dto.mappers.dto;

import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.dto.channel.ChannelCreateDTO;
import dev.ambryn.alertmntapi.dto.channel.ChannelGetDTO;
import dev.ambryn.alertmntapi.dto.channel.ChannelGetFinestDTO;
import dev.ambryn.alertmntapi.dto.group.GroupGetDTO;
import dev.ambryn.alertmntapi.dto.message.MessageGetDTO;
import dev.ambryn.alertmntapi.dto.user.UserGetDTO;
import dev.ambryn.alertmntapi.enums.EVisibility;

import java.util.List;

public class ChannelMapper {

    public static Channel toChannel(ChannelCreateDTO channelDTO) {
        String name = channelDTO.name();
        EVisibility visibility = channelDTO.visibility();
        return new Channel(name, visibility);
    }

    public static ChannelGetDTO toDTO(Channel channel) {
        Long id = channel.getId();
        String name = channel.getName();
        String visibility = channel.getVisibility()
                                   .toString();
        return new ChannelGetDTO(id, name, visibility);
    }

    public static ChannelGetFinestDTO toFinestDTO(Channel channel) {
        Long id = channel.getId();
        String name = channel.getName();
        String visibility = channel.getVisibility()
                                   .toString();
        List<UserGetDTO> members = channel.getMembers()
                                          .stream()
                                          .map(UserMapper::toDto)
                                          .toList();
        List<GroupGetDTO> groups = channel.getGroups()
                                          .stream()
                                          .map(GroupMapper::toDTO)
                                          .toList();

        return new ChannelGetFinestDTO(id, name, visibility, members, groups);
    }
}
