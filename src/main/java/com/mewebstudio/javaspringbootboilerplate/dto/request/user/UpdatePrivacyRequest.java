package com.mewebstudio.javaspringbootboilerplate.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePrivacyRequest {
    @Schema(
        description = "Tipo de privacidade do perfil",
        example = "PUBLICO",
        required = true
    )
    @NotNull(message = "{required}")
    private String privacidadePerfil;
} 