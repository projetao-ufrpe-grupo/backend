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
@NamedEntityGraph(
    name = "anuncio-with-details",
    attributeNodes = {
        // Este nó vincula o atributo "imovel" da entidade Anuncio ao subgrafo "imovel-subgraph".
        @NamedAttributeNode(value = "imovel", subgraph = "imovel-subgraph")
    },
    subgraphs = {
        // Aqui definimos o subgrafo. 
        @NamedSubgraph(
            name = "imovel-subgraph",
            attributeNodes = {
                @NamedAttributeNode("caracteristicas"),
                @NamedAttributeNode("fotos")
            }
        )
    }
)
public class Anuncio extends AbstractBaseEntity {
    private double aluguel;
    private Double caucao;
    private Double condominio;
    private Integer duracaoMinimaContrato;
    private boolean pausado;

    @ManyToOne
    private User anunciante;

    @OneToOne
    private Imovel imovel;

    @OneToMany(mappedBy = "anuncio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();
}
