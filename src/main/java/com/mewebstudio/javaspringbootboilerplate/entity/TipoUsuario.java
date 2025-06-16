package com.mewebstudio.javaspringbootboilerplate.entity;

import lombok.Getter;

@Getter
public enum TipoUsuario {

    ESTUDANTE("Estudante"), ANUNCIANTE("Anunciante");

    private final String descricao;

    TipoUsuario(String descricao) {
        this.descricao = descricao;
    }

}
