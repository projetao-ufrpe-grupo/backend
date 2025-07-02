package com.mewebstudio.javaspringbootboilerplate.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EnumResponse {
    @Schema(description = "O valor da constante do enum (usado para envio)", example = "MOBILIADO")
    private String value;

    @Schema(description = "A descrição amigável do enum (usada para exibição)", example = "Mobiliado")
    private String description;
}