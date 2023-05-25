package dev.ambryn.alertmntapi.dto.user;

import dev.ambryn.alertmntapi.beans.Role;
import dev.ambryn.alertmntapi.dto.AddDTO;
import jakarta.validation.constraints.*;
import org.springframework.lang.Nullable;

import java.util.List;

public record UserUpdateDTO(@Positive Long id,
                            @NotNull(message = "ne peut être vide") @Pattern(regexp =
                                    "^[a-zA-Z0-9-_]+\\" + ".*[a-zA" + "-Z0-9-_]*@([a-zA-Z0-9]+\\.{1})+([a-zA-Z]){2," + "3}$", message = "doit être un" + " " + "email valide") String email,
                            @NotNull(message = "ne peut être vide") @NotBlank(message = "doit contenir des " +
                                    "characters" + " autre que espace, tabulation etc.") @Size(min = 2, max = 50,
                                    message = "doit " + "contenir entre 2 et 50 caractères") @Pattern(regexp = "^[a" +
                                    "-zA-Z" + "àâçéèếïîôöùûüÀÂÇÉÈẾÏÎÔÖÙÛÜ -]+$", message = "ne doit pas contenir de " +
                                    "caractères " + "spéciaux") String lastname,
                            @NotNull(message = "ne peut être vide") @NotBlank(message = "doit contenir des " +
                                    "characters" + " autre que espace, tabulation etc.") @Size(min = 2, max = 50,
                                    message = "doit " + "contenir entre 2 et 50 caractères") @Pattern(regexp = "^[a" +
                                    "-zA-Z" + "àâçéèếïîôöùûüÀÂÇÉÈẾÏÎÔÖÙÛÜ -]+$", message = "ne doit pas contenir de " +
                                    "caractères " + "spéciaux") String firstname,
                            @Nullable List<AddDTO> roles) {}
