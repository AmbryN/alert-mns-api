package dev.ambryn.alertmntapi.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.apache.commons.text.StringEscapeUtils;

public record GroupUpdateDTO(@NotNull @Positive Long id,

                             @NotNull(message = "ne peut être vide") @NotBlank @Size(min = 1, max = 50, message =
                                     "doit contenir entre 1 et 50 caractères") String name) {
    public GroupUpdateDTO(Long id, String name) {
        this.id = id;
        this.name = name != null ? StringEscapeUtils.escapeHtml4(name.trim()
                                                                     .toUpperCase()) : null;
    }
}
