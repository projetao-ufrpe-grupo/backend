package com.mewebstudio.javaspringbootboilerplate.entity;

public enum PrivacidadePerfil {
    PUBLICO("Perfil visível para todos"),
    PRIVADO("Perfil visível apenas para o próprio usuário"),
    APENAS_LOCADORES("Perfil visível apenas para locadores");

    private final String descricao;

    PrivacidadePerfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
} 