package com.mewebstudio.javaspringbootboilerplate.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.domain.Specification;

import com.mewebstudio.javaspringbootboilerplate.entity.Anuncio;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, UUID>, JpaSpecificationExecutor<Anuncio> {
    
    @Query("SELECT DISTINCT a FROM Anuncio a " +
           "LEFT JOIN FETCH a.imovel i " +
           "LEFT JOIN FETCH i.caracteristicas " +
           "LEFT JOIN FETCH i.fotos")
    List<Anuncio> findAllWithImovelAndCaracteristicas();

    @Query("SELECT DISTINCT a FROM Anuncio a " +
           "LEFT JOIN FETCH a.imovel i " +
           "LEFT JOIN FETCH a.anunciante " +
           "LEFT JOIN FETCH i.caracteristicas " +
           "LEFT JOIN FETCH i.fotos " +
           "WHERE a.id = :id")
    Optional<Anuncio> findByIdWithImovelAndCaracteristicas(@Param("id") UUID id);

    @Query("SELECT DISTINCT a FROM Anuncio a " +
           "LEFT JOIN FETCH a.imovel i " +
           "LEFT JOIN FETCH a.anunciante " +
           "LEFT JOIN FETCH i.caracteristicas " +
           "LEFT JOIN FETCH i.fotos " +
           "WHERE a.anunciante.id = :anuncianteId")
    List<Anuncio> findAllByAnuncianteIdWithDetails(@Param("anuncianteId") UUID anuncianteId);

    /**
     * Sobrescreve o método findAll de JpaSpecificationExecutor para aplicar
     * um EntityGraph que carrega os detalhes do anúncio
     *
     * @param spec A especificação com os filtros de busca.
     * @return Uma lista de anúncios com seus detalhes carregados.
     */
    @Override
    @EntityGraph(value = "anuncio-with-details", type = EntityGraph.EntityGraphType.LOAD)
    List<Anuncio> findAll(Specification<Anuncio> spec);
}