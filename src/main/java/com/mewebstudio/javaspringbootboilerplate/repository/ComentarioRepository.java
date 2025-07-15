package com.mewebstudio.javaspringbootboilerplate.repository;

import com.mewebstudio.javaspringbootboilerplate.entity.Comentario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ComentarioRepository extends JpaRepository<Comentario, UUID> {
    @EntityGraph(value = "comentario-with-autor-e-respostas", type = EntityGraph.EntityGraphType.LOAD)
    List<Comentario> findByAnuncioIdAndComentarioPaiIsNull(UUID anuncioId);
}
