package com.mewebstudio.javaspringbootboilerplate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Imovel extends AbstractBaseEntity {
    private Integer qtdBanheiros;
    private Integer qtdQuartos;
    private String cep;
    private String cidade;
    private String descricao;

    @Enumerated(EnumType.STRING)
    private Estado estado;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private TipoImovel tipo;

    @OneToMany(mappedBy = "imovel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Foto> fotos = new ArrayList<>();

    @ElementCollection(targetClass = Caracteristica.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private List<Caracteristica> caracteristicas = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TagImovel> tagImovel = new ArrayList<>();

}
