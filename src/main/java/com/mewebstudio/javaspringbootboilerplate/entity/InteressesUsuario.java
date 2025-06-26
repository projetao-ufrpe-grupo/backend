package com.mewebstudio.javaspringbootboilerplate.entity;

import lombok.Getter;

@Getter
public enum InteressesUsuario {
    TECNOLOGIA("Tecnologia"),
    MUSICA("Música"),
    ESPORTES("Esportes"),
    JOGOS("Jogos"),
    CINEMA("Cinema"),
    VIAGENS("Viagens"),
    MODA("Moda"),
    CULINARIA("Culinária"),
    LITERATURA("Literatura"),
    FOTOGRAFIA("Fotografia"),
    ARTE("Arte"),
    NATUREZA("Natureza"),
    NEGOCIOS("Negócios"),
    SAUDE_E_BEM_ESTAR("Saúde e Bem-Estar"),
    EDUCACAO("Educação");

    private final String descricao;

    InteressesUsuario(String descricao) {
        this.descricao = descricao;
    }
}
