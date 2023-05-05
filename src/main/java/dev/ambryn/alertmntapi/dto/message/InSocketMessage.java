package dev.ambryn.alertmntapi.dto.message;

import jakarta.validation.constraints.*;
import org.apache.commons.text.StringEscapeUtils;

public record InSocketMessage(
        @NotNull(message = "ne peut être vide")
        @Digits(message = "doit être un entier positif", integer = Integer.SIZE, fraction = 0)
        @Positive(message = "doit être supérieur à 0")
        Long channelId,
        @NotNull(message = "ne peut être vide")
        @Digits(message = "doit être un entier positif", integer = Integer.SIZE, fraction = 0)
        @Positive(message = "doit être supérieur à 0")
        Long userId,

        @NotNull(message = "ne peut être vide")
        @NotBlank
        @Size(min = 1, max = 2000, message = "doit contenir entre 1 et 2000 caractères")
        String content) {

        public InSocketMessage(Long channelId, Long userId, String content) {
                this.channelId = channelId;
                this.userId = userId;
                this.content = StringEscapeUtils.escapeHtml4(content);
        }
}
