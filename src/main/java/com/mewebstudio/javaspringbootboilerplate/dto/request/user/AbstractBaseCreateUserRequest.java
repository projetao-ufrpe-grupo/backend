package com.mewebstudio.javaspringbootboilerplate.dto.request.user;

import com.mewebstudio.javaspringbootboilerplate.dto.annotation.FieldMatch;
import com.mewebstudio.javaspringbootboilerplate.dto.annotation.Password;
import com.mewebstudio.javaspringbootboilerplate.dto.annotation.ValueOfEnum;
import com.mewebstudio.javaspringbootboilerplate.entity.TipoUsuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@FieldMatch(first = "password", second = "passwordConfirm", message = "{password_mismatch}")
public abstract class AbstractBaseCreateUserRequest {
    @NotBlank(message = "{not_blank}")
    @Email(message = "{invalid_email}")
    @Size(max = 100, message = "{max_length}")
    @Schema(
        name = "email",
        description = "Email of the user",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "mail@example.com"
    )
    private String email;

    @NotBlank(message = "{not_blank}")
    @Password(message = "{invalid_password}")
    @Schema(
        name = "password",
        description = "Password of the user",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "P@sswd123."
    )
    private String password;

    @NotBlank(message = "{not_blank}")
    @Schema(
        name = "passwordConfirm",
        description = "Password for confirmation",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "P@sswd123."
    )
    private String passwordConfirm;

    @NotBlank(message = "{not_blank}")
    @Size(max = 100, message = "{max_length}")
    @Schema(
        name = "name",
        description = "Full name of the user",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "Maria Carvalho da Silva"
    )
    private String name;

    @NotBlank(message = "{not_blank}")
    @ValueOfEnum(enumClass = TipoUsuario.class, message = "{invalid_enum_value}")
    @Schema(
        description = "User type",
        type = "String",
        allowableValues = {"ESTUDANTE", "ANUNCIANTE"},
        example = "ESTUDANTE",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoUsuario;
}
