package dev.ambryn.alertmntapi.dto.channel;

import dev.ambryn.alertmntapi.enums.EVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.apache.commons.text.StringEscapeUtils;

public record ChannelUpdateDTO(

        @NotNull @Positive Long id,

        @NotNull(message = "ne peut être vide") @NotBlank @Size(min = 2, max = 50, message =
                "doit contenir entre 1 " + "et 50 caractères") String name,

        @NotNull(message = "ne peut être vide") EVisibility visibility) {

    public ChannelUpdateDTO(Long id, String name, EVisibility visibility) {
        this.id = id;
        this.name = name != null ? StringEscapeUtils.escapeHtml4(name.trim()
                                                                     .toUpperCase()) : null;
        this.visibility = visibility;
    }
}
