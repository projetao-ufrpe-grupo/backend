package com.mewebstudio.javaspringbootboilerplate.controller;

import com.mewebstudio.javaspringbootboilerplate.dto.request.comentario.ComentarioMapper;
import com.mewebstudio.javaspringbootboilerplate.dto.request.comentario.ComentarioRequestDTO;
import com.mewebstudio.javaspringbootboilerplate.dto.request.comentario.ComentarioResponseDTO;
import com.mewebstudio.javaspringbootboilerplate.entity.Comentario;
import com.mewebstudio.javaspringbootboilerplate.service.ComentarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static com.mewebstudio.javaspringbootboilerplate.util.Constants.SECURITY_SCHEME_NAME;

@RestController
@RequestMapping("/api/anuncios/{anuncioId}/comentarios")
@RequiredArgsConstructor
@Tag(name = "006. Comentario", description = "Comentario API")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final ComentarioMapper comentarioMapper;

    @PostMapping
    @Operation(
            summary = "Comentar em um anúncio",
            description = "Permite que um usuário comente em um anúncio.",
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<ComentarioResponseDTO> comentar(@PathVariable UUID anuncioId,
                                                          @RequestBody ComentarioRequestDTO dto,
                                                          Principal principal) {
        Comentario comentario = comentarioService.comentar(anuncioId, dto);
        ComentarioResponseDTO response = comentarioMapper.toDto(comentario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
            summary = "Listar comentários de um anúncio",
            description = "Retorna uma lista de comentários para um anúncio específico.",
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<List<ComentarioResponseDTO>> listar(@PathVariable UUID anuncioId) {
        List<ComentarioResponseDTO> comentarios = comentarioService.listarComentariosPorAnuncio(anuncioId);
        return ResponseEntity.ok(comentarios);
    }

    @DeleteMapping("/{comentarioId}")
    @Operation(
            summary = "Deletar comentário",
            description = "Permite que um usuário delete um comentário e todos os seus filhos.",
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<Void> deletar(@PathVariable UUID comentarioId, Principal principal) throws AccessDeniedException, BindException {
        comentarioService.deletarComentarioComFilhos(comentarioId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}