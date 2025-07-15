package com.mewebstudio.javaspringbootboilerplate.dto.request.comentario;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComentarioResponseDTO {
    private UUID id;
    private String texto;
    private String autor;
    private LocalDateTime criadoEm;
    private List<ComentarioResponseDTO> respostas = new ArrayList<>();
}