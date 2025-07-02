package com.mewebstudio.javaspringbootboilerplate.entity;

import lombok.Getter;

@Getter
public enum TipoUsuario {

    ESTUDANTE("ESTUDANTE"), ANUNCIANTE("ANUNCIANTE");

    private final String descricao;

    TipoUsuario(String descricao) {
        this.descricao = descricao;
    }

}
