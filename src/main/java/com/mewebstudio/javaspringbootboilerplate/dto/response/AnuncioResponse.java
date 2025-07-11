package com.mewebstudio.javaspringbootboilerplate.dto.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mewebstudio.javaspringbootboilerplate.entity.Anuncio;
import com.mewebstudio.javaspringbootboilerplate.entity.Imovel;
import com.mewebstudio.javaspringbootboilerplate.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AnuncioResponse {

    // --- Dados do Anúncio ---
    @Schema(description = "ID único do anúncio", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Valor do aluguel mensal", example = "1500.00")
    private Double aluguel;

    @Schema(description = "Valor do condomínio", example = "350.00")
    private Double condominio;

    @Schema(description = "Valor do caução", example = "3000.00")
    private Double caucao;

    @Schema(description = "Duração mínima do contrato em meses", example = "12")
    private Integer duracaoMinimaContrato;

    @Schema(description = "Indica se o anúncio está pausado", example = "false")
    private boolean pausado;

    // --- Dados do Imóvel (achatados na resposta) ---
    @Schema(description = "Descrição detalhada do imóvel", example = "Apartamento arejado com vista para o parque.")
    private String descricao;

    @Schema(description = "Tipo do imóvel", example = "APARTAMENTO")
    private String tipo;

    @Schema(description = "Quantidade de quartos", example = "2")
    private Integer qtdQuartos;

    @Schema(description = "Quantidade de banheiros", example = "1")
    private Integer qtdBanheiros;

    @Schema(description = "Área do imóvel em m²", example = "55")
    private Integer area;

    @Schema(description = "Data de disponibilidade do imóvel", example = "2025-08-01")
    private LocalDate dataDisponibilidade;

    @Schema(description = "CEP do imóvel", example = "31270-901")
    private String cep;

    @Schema(description = "Endereço completo formatado")
    private String enderecoCompleto;

    @Schema(description = "Lista de características do imóvel", example = "[\"MOBILIADO\", \"ACEITA_PETS\"]")
    private List<String> caracteristicas;

    @Schema(description = "Lista de fotos do imóvel em formato Base64")
    private List<FotoResponse> fotos;

    // --- Dados do Anunciante ---
    @Schema(description = "Informações do anunciante")
    private AnuncianteResponse anunciante;

    /**
     * DTO aninhado para informações do anunciante.
     */
    @Getter
    @Setter
    @Builder
    public static class AnuncianteResponse {
        private UUID id;
        private String name;
        private String fotoPerfilBase64;
    }

    /**
     * DTO aninhado para informações da foto.
     */
    @Getter
    @Setter
    @Builder
    public static class FotoResponse {
        @Schema(description = "ID único da foto", example = "123e4567-e89b-12d3-a456-426614174001")
        private UUID id;

        @Schema(description = "Dados da imagem em formato Base64")
        private String dadosBase64;
    }

    /**
     * Converte uma entidade Anuncio para um AnuncioResponse.
     * @param anuncio A entidade a ser convertida.
     * @return O DTO de resposta.
     */
    public static AnuncioResponse convert(Anuncio anuncio) {
        if (anuncio == null) {
            return null;
        }

        Imovel imovel = anuncio.getImovel();
        User anunciante = anuncio.getAnunciante();

        AnuncianteResponse anuncianteResponse = null;
        if (anunciante != null) {
            anuncianteResponse = AnuncianteResponse.builder()
                .id(anunciante.getId())
                .name(anunciante.getName())
                .fotoPerfilBase64(anunciante.getFotoPerfil())
                .build();
        }

        AnuncioResponseBuilder builder = AnuncioResponse.builder()
            .id(anuncio.getId())
            .aluguel(anuncio.getAluguel())
            .condominio(anuncio.getCondominio())
            .caucao(anuncio.getCaucao())
            .duracaoMinimaContrato(anuncio.getDuracaoMinimaContrato())
            .pausado(anuncio.isPausado())
            .anunciante(anuncianteResponse);
        
        if (imovel != null) {
            // Lógica para construir o endereço completo
            StringBuilder enderecoBuilder = new StringBuilder();
            enderecoBuilder.append(imovel.getLogradouro()).append(", ").append(imovel.getNumero());
            
            // Adiciona o complemento apenas se ele existir
            if (imovel.getComplemento() != null && !imovel.getComplemento().isBlank()) {
                enderecoBuilder.append(", ").append(imovel.getComplemento());
            }
            
            enderecoBuilder.append(" - ").append(imovel.getBairro())
                         .append(", ").append(imovel.getCidade())
                         .append(" - ").append(imovel.getEstado().name());


            builder.descricao(imovel.getDescricao())
                .tipo(imovel.getTipo() != null ? imovel.getTipo().name() : null)
                .qtdQuartos(imovel.getQtdQuartos())
                .qtdBanheiros(imovel.getQtdBanheiros())
                .area(imovel.getArea())
                .dataDisponibilidade(imovel.getDataDisponibilidade())
                .cep(imovel.getCep())
                .enderecoCompleto(enderecoBuilder.toString())
                .caracteristicas(imovel.getCaracteristicas() != null ?
                    new ArrayList<>(imovel.getCaracteristicas().stream().map(Enum::name).collect(Collectors.toList())) : null)
                .fotos(imovel.getFotos() != null ?
                    imovel.getFotos().stream()
                        .map(foto -> FotoResponse.builder()
                                .id(foto.getId())
                                .dadosBase64(foto.getDadosBase64())
                                .build())
                        .collect(Collectors.toList()) : null);
        }

        return builder.build();
    }
}    