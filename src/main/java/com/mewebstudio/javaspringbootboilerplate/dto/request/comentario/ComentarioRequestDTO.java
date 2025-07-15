package com.mewebstudio.javaspringbootboilerplate.dto.request.comentario;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ComentarioRequestDTO {
    private String texto;
    private UUID comentarioPaiId;
}