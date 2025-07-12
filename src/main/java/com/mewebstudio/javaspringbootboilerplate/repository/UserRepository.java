package com.mewebstudio.javaspringbootboilerplate.repository;

import com.mewebstudio.javaspringbootboilerplate.entity.PrivacidadePerfil;
import com.mewebstudio.javaspringbootboilerplate.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles LEFT JOIN FETCH u.interesses WHERE u.id = :id")
    Optional<User> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT DISTINCT u FROM User u " +
       "LEFT JOIN FETCH u.roles " +
       "LEFT JOIN FETCH u.interesses " + 
       "JOIN u.anunciosFavoritos af " +
       "WHERE af.id = :anuncioId AND u.privacidadePerfil = :privacidadePerfil")
    List<User> findUsersByFavoritedAnuncioAndPublicProfile(@Param("anuncioId") UUID anuncioId, 
                                                       @Param("privacidadePerfil") PrivacidadePerfil privacidadePerfil);
}
