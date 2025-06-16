package com.mewebstudio.javaspringbootboilerplate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
