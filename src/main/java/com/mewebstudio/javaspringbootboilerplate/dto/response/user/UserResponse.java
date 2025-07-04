package com.mewebstudio.javaspringbootboilerplate.dto.response.user;

import com.mewebstudio.javaspringbootboilerplate.dto.response.AbstractBaseResponse;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
public class UserResponse extends AbstractBaseResponse {
    @Schema(
        name = "id",
        description = "UUID",
        type = "String",
        example = "91b2999d-d327-4dc8-9956-2fadc0dc8778"
    )
    private String id;

    @Schema(
        name = "email",
        description = "E-mail of the user",
        type = "String",
        example = "mail@example.com"
    )
    private String email;

    @Schema(
        name = "name",
        description = "Name of the user",
        type = "String",
        example = "John"
    )
    private String name;

    @Schema(
        name = "roles",
        description = "role of the user",
        type = "List",
        example = "[\"USER\"]"
    )
    private List<String> roles;

    @Schema(
        name = "tipoUsuario",
        description = "Type of the user",
        type = "String",
        example = "ESTUDANTE"
    )
    private String tipoUsuario;

    @Schema(
        name = "fotoPerfil",
        description = "Profile photo in Base64 format", 
        type = "String"
        )
    private String fotoPerfil;

    @Schema(
        name = "biografia",
        description = "User's biography", 
        type = "String", 
        example = "Estudante de Ciência da Computação na UFMG."
        )
    private String biografia;

    @Schema(
        name = "semestre",
        description = "Current semester of the student", 
        type = "Integer", 
        example = "5"
        )
    private Integer semestre;

    @Schema(
        name = "curso",
        description = "User's course", 
        type = "String", 
        example = "Ciência da Computação"
        )
    private String curso;

    @Schema(
        name = "regiaoDeInteresse",
        description = "User's region of interest", 
        type = "String", 
        example = "Pampulha, Belo Horizonte - MG"
        )
    private String regiaoDeInteresse;

    @Schema(
        name = "interesses",
        description = "List of user's interests", 
        type = "List<String>", 
        example = "[\"JOGOS\", \"TECNOLOGIA\"]"
        )
    private List<String> interesses;

    /**
     * Convert User to UserResponse
     * @param user User
     * @return UserResponse
     */
    public static UserResponse convert(User user) {
        return UserResponse.builder()
            .id(user.getId().toString())
            .email(user.getEmail())
            .name(user.getName())
            .roles(user.getRoles().stream().map(role -> role.getName().name()).toList())
            .tipoUsuario(user.getTipoUsuario() != null ? user.getTipoUsuario().name() : null)
            .fotoPerfil(user.getFotoPerfil())
            .biografia(user.getBiografia())
            .semestre(user.getSemestre())
            .curso(user.getCurso())
            .regiaoDeInteresse(user.getRegiaoDeInteresse())
            .interesses(user.getInteresses() != null ?
                user.getInteresses().stream().map(Enum::name).collect(Collectors.toList()) : null)
            .build();
    }
}
