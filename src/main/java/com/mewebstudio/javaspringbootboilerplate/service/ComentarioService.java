package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.dto.request.comentario.ComentarioMapper;
import com.mewebstudio.javaspringbootboilerplate.dto.request.comentario.ComentarioRequestDTO;
import com.mewebstudio.javaspringbootboilerplate.dto.request.comentario.ComentarioResponseDTO;
import com.mewebstudio.javaspringbootboilerplate.entity.Anuncio;
import com.mewebstudio.javaspringbootboilerplate.entity.Comentario;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.exception.NotFoundException;
import com.mewebstudio.javaspringbootboilerplate.repository.AnuncioRepository;
import com.mewebstudio.javaspringbootboilerplate.repository.ComentarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final AnuncioRepository anuncioRepository;
    private final UserService userService;
    private final ComentarioMapper comentarioMapper;

    public Comentario comentar(UUID anuncioId, ComentarioRequestDTO dto) {
        User autor = userService.getUser();

        Anuncio anuncio = anuncioRepository.findById(anuncioId)
                .orElseThrow(() -> new NotFoundException("Anúncio não encontrado com o ID: " + anuncioId));

        Comentario comentario = new Comentario();
        comentario.setTexto(dto.getTexto());
        comentario.setAutor(autor);
        comentario.setAnuncio(anuncio);

        if (dto.getComentarioPaiId() != null) {
            Comentario pai = comentarioRepository.findById(dto.getComentarioPaiId())
                    .orElseThrow(() -> new EntityNotFoundException("Comentário pai não encontrado"));
            comentario.setComentarioPai(pai);
        }

        return comentarioRepository.save(comentario);
    }

    public List<ComentarioResponseDTO> listarComentariosPorAnuncio(UUID anuncioId) {
        List<Comentario> comentariosRaiz = comentarioRepository.findByAnuncioIdAndComentarioPaiIsNull(anuncioId);
        return comentariosRaiz.stream().map(comentarioMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void deletarComentarioComFilhos(UUID comentarioId, String username) throws AccessDeniedException, BindException {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new NotFoundException("Comentário não encontrado"));

        if (comentario.getAutor() == null && comentario.getTexto().equals("Este comentário foi excluído pelo autor.")) {
            throw new NotFoundException("Comentário já excluído.");
        } else {
            assert comentario.getAutor() != null;
            if (!comentario.getAutor().getEmail().equals(username)) {
                throw new AccessDeniedException("Você não tem permissão para deletar este comentário.");
            }
        }

        if (comentario.getRespostas() == null || comentario.getRespostas().isEmpty()) {
            Comentario comentarioPai = comentario.getComentarioPai();
            if (comentarioPai != null) {
                comentarioPai.getRespostas().remove(comentario); // remove a referência do pai
            }

            comentarioRepository.delete(comentario);
        } else {
            // Tem filhos: marca como excluído
            comentario.setTexto("Este comentário foi excluído pelo autor.");
            comentarioRepository.save(comentario);
        }
    }

}