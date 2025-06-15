package com.mewebstudio.javaspringbootboilerplate.entity;

import lombok.Getter;

@Getter
public enum Caracteristica {
    MOBILIADO("Mobiliado"),
    AR_CONDICIONADO("Ar Condicionado"),
    INTERNET("Internet");


    private final String descricao;

    Caracteristica(String descricao) {
        this.descricao = descricao;
    }
}
