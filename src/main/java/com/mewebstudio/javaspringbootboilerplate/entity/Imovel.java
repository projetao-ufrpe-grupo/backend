package com.mewebstudio.javaspringbootboilerplate.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Imovel extends AbstractBaseEntity {
    private Integer area;
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

    @Builder.Default
    private Integer vagas = 1;

    @Enumerated(EnumType.STRING)
    private TipoImovel tipo;

    private LocalDate dataDisponibilidade;

    @OneToMany(mappedBy = "imovel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Foto> fotos = new HashSet<>();

    @ElementCollection(targetClass = Caracteristica.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    @Builder.Default
    private Set<Caracteristica> caracteristicas = new HashSet<>();
}
