package com.mewebstudio.javaspringbootboilerplate.entity;

import lombok.Getter;

@Getter
public enum TipoImovel {
    APARTAMENTO("Apartamento"), CASA("Casa"), QUARTO("Quarto"), KITNET_STUDIO("Kitnet/Studio");

    private final String descricao;

    TipoImovel(String descricao) {
        this.descricao = descricao;
    }
}
