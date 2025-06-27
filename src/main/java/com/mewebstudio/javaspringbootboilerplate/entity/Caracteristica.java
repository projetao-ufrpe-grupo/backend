package com.mewebstudio.javaspringbootboilerplate.entity;

import lombok.Getter;

@Getter
public enum Caracteristica {

    MOBILIADO("Mobiliado"),
    AR_CONDICIONADO("Ar Condicionado"),
    INTERNET("Internet"),
    ACEITA_PETS("Aceita Pets"),
    PISCINA("Piscina"),
    CHURRASQUEIRA("Churrasqueira"),
    ACADEMIA("Academia"),
    GARAGEM("Garagem"),
    VARANDA("Varanda"),
    AREA_DE_SERVICO("Área de serviço"),
    PORTARIA_24H("Portaria 24h"),
    SALAO_DE_FESTAS("Salão de festas"),
    ELEVADOR("Elevador"),
    GAS_ENCANADO("Gás encanado"),
    ARMARIOS_EMBUTIDOS("Armários embutidos"),
    LAVANDERIA_NO_LOCAL("Lavanderia no Local"),
    BICICLETARIO("Bicicletário"),
    TRANSPORTE_PUBLICO_PROXIMO("Transporte Público Próximo");

    private final String descricao;

    Caracteristica(String descricao) {
        this.descricao = descricao;
    }
}
