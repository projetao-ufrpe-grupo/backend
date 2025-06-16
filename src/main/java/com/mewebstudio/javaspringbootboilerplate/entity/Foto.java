package com.mewebstudio.javaspringbootboilerplate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Foto extends AbstractBaseEntity {

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imovel_id")
    private Imovel imovel;
}
