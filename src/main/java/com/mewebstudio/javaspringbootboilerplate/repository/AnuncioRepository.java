package com.mewebstudio.javaspringbootboilerplate.repository;

import com.mewebstudio.javaspringbootboilerplate.entity.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, UUID> {
    // Query 1: Busca os anúncios e as coleções que não são List
    @Query("SELECT DISTINCT a FROM Anuncio a LEFT JOIN FETCH a.imovel i LEFT JOIN FETCH a.anunciante")
    List<Anuncio> findAllWithImovelAndAnunciante();

    // Query 2: Busca os anúncios novamente, mas agora com as coleções List
    @Query("SELECT DISTINCT a FROM Anuncio a LEFT JOIN FETCH a.imovel i LEFT JOIN FETCH i.fotos LEFT JOIN FETCH i.caracteristicas WHERE a IN :anuncios")
    List<Anuncio> findAllWithCollections(List<Anuncio> anuncios);
}