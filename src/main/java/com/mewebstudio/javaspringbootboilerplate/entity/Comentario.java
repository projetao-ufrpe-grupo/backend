package com.mewebstudio.javaspringbootboilerplate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(
        name = "comentario-with-autor-e-respostas",
        attributeNodes = {
                @NamedAttributeNode("autor"),
                @NamedAttributeNode(value = "respostas", subgraph = "respostas-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "respostas-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("autor")
                        }
                )
        }
)
public class Comentario extends AbstractBaseEntity{

    private String texto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "autor_id")
    private User autor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "anuncio_id", nullable = false)
    private Anuncio anuncio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comentario_pai_id")
    private Comentario comentarioPai;

    @OneToMany(mappedBy = "comentarioPai", cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.EAGER)
    private List<Comentario> respostas = new ArrayList<>();

}