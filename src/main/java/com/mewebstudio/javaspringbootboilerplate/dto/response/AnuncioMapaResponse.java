package com.mewebstudio.javaspringbootboilerplate.dto.response;

import com.mewebstudio.javaspringbootboilerplate.entity.Anuncio;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class AnuncioMapaResponse {
    @Schema(description = "ID do anúncio", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID id;

    @Schema(description = "Latitude da localização do imóvel", example = "-19.865308")
    private Double latitude;

    @Schema(description = "Longitude da localização do imóvel", example = "-43.964995")
    private Double longitude;

    @Schema(description = "Valor do aluguel", example = "1500.00")
    private double aluguel;

    @Schema(description = "Tipo do imóvel", example = "APARTAMENTO")
    private String tipoImovel;

    /**
     * Converte uma entidade Anuncio para um AnuncioMapaResponse.
     * @param anuncio A entidade a ser convertida.
     * @return O DTO de resposta para o mapa.
     */
    public static AnuncioMapaResponse convert(Anuncio anuncio) {
        if (anuncio == null || anuncio.getImovel() == null) {
            return null;
        }

        return AnuncioMapaResponse.builder()
                .id(anuncio.getId())
                .latitude(anuncio.getImovel().getLatitude())
                .longitude(anuncio.getImovel().getLongitude())
                .aluguel(anuncio.getAluguel())
                .tipoImovel(anuncio.getImovel().getTipo().name())
                .build();
    }
}