package com.mewebstudio.javaspringbootboilerplate.dto.request.user;

import java.util.List;

import com.mewebstudio.javaspringbootboilerplate.dto.annotation.ValueOfEnum;
import com.mewebstudio.javaspringbootboilerplate.entity.InteressesUsuario;
import com.mewebstudio.javaspringbootboilerplate.entity.TipoUsuario;
import com.mewebstudio.javaspringbootboilerplate.util.Constants;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UpdateProfileRequest extends AbstractBaseUpdateUserRequest {
    
    @Schema(
        description = "New password for the user. If you don't want to change the password, leave this field empty.",
        type = "String",
        example = "newSecurePassword123!",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @Size(min = Constants.PASSWORD_MIN_LENGTH, max = Constants.PASSWORD_MAX_LENGTH, message = "{min_max_length_password}")
    @Pattern(regexp = Constants.PASSWORD_REGEX, message = "{invalid_password}")
    private String password;

    @Schema(description = "Current semester of the student user", type = "Integer", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer semestre;

    @Schema(description = "User type", type = "String", allowableValues = {"Estudante", "Anunciante"}, example = "ESTUDANTE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @ValueOfEnum(enumClass = TipoUsuario.class, message = "{invalid_enum_value}")
    private String tipoUsuario;

    @Schema(description = "Biography of the user", type = "String", example = "A passionate developer and student.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 500, message = "{max_length}")
    private String biografia;

    @Schema(description = "Profile photo path/URL of the user", type = "String", example = "/path/to/profile_photo.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 255, message = "{max_length}")
    private String caminhoFoto;

    @Schema(description = "Course of the student user", type = "String", example = "Ciência da Computação", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 100, message = "{max_length}")
    private String curso;

    @Schema(description = "Region of interest for the user", type = "String", example = "Pampulha, Belo Horizonte - MG", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 255, message = "{max_length}")
    private String regiaoDeInteresse;

    @Schema(description = "List of user's interest tags (max 5).",
            type = "List<String>",
            example = "[\"JOGOS\", \"CINEMA\", \"TECNOLOGIA\"]",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 5, message = "{max_list_size}")
    private List<@ValueOfEnum(enumClass = InteressesUsuario.class) String> interesses;
}
