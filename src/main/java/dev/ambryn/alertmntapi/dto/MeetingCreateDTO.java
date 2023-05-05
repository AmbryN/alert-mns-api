package dev.ambryn.alertmntapi.dto;

import jakarta.validation.constraints.*;
import org.apache.commons.text.StringEscapeUtils;

import java.sql.Timestamp;

public record MeetingCreateDTO(
        @NotNull(message = "ne peut être vide")
        @Digits(message = "doit être un entier", integer = Integer.SIZE, fraction = 0)
        @Positive(message = "doit être positif")
        Long organizerId,

        @NotNull(message = "ne peut être vide")
        @NotBlank(message = "doit contenir des caractères autres que espaces et tabulations")
        @Size(min = 2, max = 50)
        String name,

        @NotNull(message = "ne peut être vide")
        @FutureOrPresent
        Timestamp datetime,

        @NotNull(message = "ne peut être vide")
        @Digits(integer = 1440, fraction = 0, message = "doit être exprimé en minutes avec un maximum de 1440 minutes (24h)")
        @Positive(message = "doit être positif")
        int duration) {

        public MeetingCreateDTO(Long organizerId, String name, Timestamp datetime, int duration) {
                this.organizerId = organizerId;
                this.name = name != null ? StringEscapeUtils.escapeHtml4(name).trim() : null;
                this.datetime = datetime;
                this.duration = duration;
        }
}


