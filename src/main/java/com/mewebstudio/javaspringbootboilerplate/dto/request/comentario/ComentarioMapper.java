package com.mewebstudio.javaspringbootboilerplate.dto.request.comentario;

import com.mewebstudio.javaspringbootboilerplate.entity.Comentario;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ComentarioMapper {

    public ComentarioResponseDTO toDto(Comentario comentario) {
        return ComentarioResponseDTO.builder()
                .id(comentario.getId())
                .texto(comentario.getTexto())
                .autor(comentario.getAutor().getName())
                .criadoEm(comentario.getCreatedAt())
                .respostas(
                        comentario.getRespostas().stream()
                                .map(this::toDto)
                                .collect(Collectors.toList())
                )
                .build();
    }
}