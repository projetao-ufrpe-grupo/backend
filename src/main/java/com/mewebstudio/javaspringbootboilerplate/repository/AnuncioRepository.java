package com.mewebstudio.javaspringbootboilerplate.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mewebstudio.javaspringbootboilerplate.entity.Anuncio;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, UUID> {
    
    @Query("SELECT DISTINCT a FROM Anuncio a " +
           "LEFT JOIN FETCH a.imovel i " +
           "LEFT JOIN FETCH i.caracteristicas " +
           "LEFT JOIN FETCH i.fotos")
    List<Anuncio> findAllWithImovelAndCaracteristicas();

    @Query("SELECT DISTINCT a FROM Anuncio a " +
           "LEFT JOIN FETCH a.imovel i " +
           "LEFT JOIN FETCH i.caracteristicas " +
           "LEFT JOIN FETCH i.fotos " +
           "WHERE a.id = :id")
    Optional<Anuncio> findByIdWithImovelAndCaracteristicas(@Param("id") UUID id);
}