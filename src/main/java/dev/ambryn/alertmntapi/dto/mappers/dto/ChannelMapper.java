package dev.ambryn.alertmntapi.dto.mappers.dto;

import dev.ambryn.alertmntapi.beans.Channel;
import dev.ambryn.alertmntapi.dto.channel.ChannelCreateDTO;
import dev.ambryn.alertmntapi.dto.channel.ChannelGetDTO;
import dev.ambryn.alertmntapi.enums.EVisibility;

public class ChannelMapper {

    public static Channel toChannel(ChannelCreateDTO channelDTO) {
        String name = channelDTO.name();
        EVisibility visibility = channelDTO.visibility();
        return new Channel(name, visibility);
    }

    public static ChannelGetDTO toDTO(Channel channel) {
        Long id = channel.getId();
        String name = channel.getName();
        String visibility = channel.getVisibility().toString();
        return new ChannelGetDTO(id, name, visibility);
    }
}
