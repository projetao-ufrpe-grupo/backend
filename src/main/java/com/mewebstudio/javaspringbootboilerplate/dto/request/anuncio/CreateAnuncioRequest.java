package com.mewebstudio.javaspringbootboilerplate.dto.request.anuncio;

import com.mewebstudio.javaspringbootboilerplate.dto.annotation.ValueOfEnum;
import com.mewebstudio.javaspringbootboilerplate.entity.Caracteristica;
import com.mewebstudio.javaspringbootboilerplate.entity.Estado;
import com.mewebstudio.javaspringbootboilerplate.entity.TipoImovel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CreateAnuncioRequest {

    // --- Dados do Anúncio ---
    @Schema(description = "Valor do aluguel mensal", example = "1500.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{not_null}")
    @Positive(message = "{positive}")
    private Double aluguel;

    @Schema(description = "Valor do condomínio", example = "350.00")
    @Positive(message = "{positive}")
    private Double condominio;

    @Schema(description = "Valor do caução (se aplicável)", example = "3000.00")
    @Positive(message = "{positive}")
    private Double caucao;

    @Schema(description = "Duração mínima do contrato em meses", example = "12")
    private Integer duracaoMinimaContrato;

    // --- Dados do Imóvel ---
    @Schema(description = "Área do imóvel em metros quadrados", example = "55")
    @Positive(message = "{positive}")
    private Integer area;

    @Schema(description = "Descrição detalhada do imóvel", example = "Apartamento arejado com vista para o parque.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{not_blank}")
    private String descricao;

    @Schema(description = "Tipo do imóvel", example = "APARTAMENTO", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{not_null}")
    @ValueOfEnum(enumClass = TipoImovel.class)
    private String tipo;

    @Schema(description = "Data a partir da qual o imóvel está disponível (formato YYYY-MM-DD)", example = "2025-08-01")
    private LocalDate dataDisponibilidade;

    @Schema(description = "Quantidade de quartos", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{not_null}")
    @Positive(message = "{positive}")
    private Integer qtdQuartos;

    @Schema(description = "Quantidade de banheiros", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{not_null}")
    @Positive(message = "{positive}")
    private Integer qtdBanheiros;

    @Schema(description = "CEP do imóvel", example = "31270-901", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{not_blank}")
    private String cep;

    @Schema(description = "Cidade", example = "Belo Horizonte", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{not_blank}")
    private String cidade;

    @Schema(description = "Estado (sigla com 2 letras)", example = "MG", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{not_null}")
    @ValueOfEnum(enumClass = Estado.class)
    private String estado;

    @Schema(description = "Logradouro (Rua, Avenida)", example = "Av. Antônio Carlos", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{not_blank}")
    private String logradouro;

    @Schema(description = "Número do endereço", example = "6627", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{not_blank}")
    private String numero;

    @Schema(description = "Bairro", example = "Pampulha", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{not_blank}")
    private String bairro;

    @Schema(description = "Complemento do endereço", example = "Apto 301")
    private String complemento;

    @Schema(description = "Lista de características do imóvel", example = "[\"MOBILIADO\", \"PERMITE_ANIMAIS\"]")
    private List<@ValueOfEnum(enumClass = Caracteristica.class) String> caracteristicas;

}
