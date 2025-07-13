package com.mewebstudio.javaspringbootboilerplate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToOne;
import lombok.*;

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
}
