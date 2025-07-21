package com.mewebstudio.javaspringbootboilerplate.service.specification;

import com.mewebstudio.javaspringbootboilerplate.entity.Anuncio;
import com.mewebstudio.javaspringbootboilerplate.entity.Caracteristica;
import com.mewebstudio.javaspringbootboilerplate.entity.Imovel;
import com.mewebstudio.javaspringbootboilerplate.entity.TipoImovel;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

public final class AnuncioSpecification {

    /**
     * Retorna uma Specification para filtrar pelo título/descrição do anúncio.
     * A busca é case-insensitive e procura pelo termo em qualquer parte da descrição do imóvel.
     * @param titulo O termo a ser buscado.
     * @return Specification para o filtro.
     */
    public static Specification<Anuncio> porTitulo(String titulo) {
        return (root, query, criteriaBuilder) -> {
            if (titulo == null || titulo.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            Join<Anuncio, Imovel> imovelJoin = root.join("imovel");
            // Usa lower para busca case-insensitive e like para busca parcial
            return criteriaBuilder.like(
                criteriaBuilder.lower(imovelJoin.get("descricao")),
                "%" + titulo.toLowerCase() + "%"
            );
        };
    }

    /**
     * Retorna uma Specification para filtrar por tipo de imóvel.
     * @param tipoImovel O tipo do imóvel.
     * @return Specification para o filtro.
     */
    public static Specification<Anuncio> porTipo(TipoImovel tipoImovel) {
        return (root, query, criteriaBuilder) -> {
            Join<Anuncio, Imovel> imovelJoin = root.join("imovel");
            return criteriaBuilder.equal(imovelJoin.get("tipo"), tipoImovel);
        };
    }

    /**
     * Retorna uma Specification para filtrar por um intervalo de área.
     * @param areaMin A área mínima.
     * @param areaMax A área máxima.
     * @return Specification para o filtro.
     */
    public static Specification<Anuncio> porArea(Integer areaMin, Integer areaMax) {
        return (root, query, criteriaBuilder) -> {
            Join<Anuncio, Imovel> imovelJoin = root.join("imovel");
            if (areaMin != null && areaMax != null) {
                return criteriaBuilder.between(imovelJoin.get("area"), areaMin, areaMax);
            } else if (areaMin != null) {
                return criteriaBuilder.greaterThanOrEqualTo(imovelJoin.get("area"), areaMin);
            } else if (areaMax != null) {
                return criteriaBuilder.lessThanOrEqualTo(imovelJoin.get("area"), areaMax);
            }
            return null;
        };
    }

    /**
     * Retorna uma Specification para filtrar pelo preço total (aluguel + condomínio).
     * @param precoMin O preço total mínimo.
     * @param precoMax O preço total máximo.
     * @return Specification para o filtro.
     */
    public static Specification<Anuncio> porPrecoTotal(Double precoMin, Double precoMax) {
        return (root, query, criteriaBuilder) -> {
            // Expressão para somar aluguel e condomínio (tratando condomínio nulo como 0)
            Expression<Double> precoTotal = criteriaBuilder.sum(
                root.get("aluguel"),
                criteriaBuilder.coalesce(root.get("condominio"), 0.0)
            );

            if (precoMin != null && precoMax != null) {
                return criteriaBuilder.between(precoTotal, precoMin, precoMax);
            } else if (precoMin != null) {
                return criteriaBuilder.greaterThanOrEqualTo(precoTotal, precoMin);
            } else if (precoMax != null) {
                return criteriaBuilder.lessThanOrEqualTo(precoTotal, precoMax);
            }
            return null;
        };
    }

    /**
     * Retorna uma Specification para filtrar por características do imóvel.
     * Garante que o imóvel possua TODAS as características fornecidas.
     * @param caracteristicas A lista de características desejadas.
     * @return Specification para o filtro.
     */
    public static Specification<Anuncio> porCaracteristicas(List<Caracteristica> caracteristicas) {
        return (root, query, criteriaBuilder) -> {
            if (caracteristicas == null || caracteristicas.isEmpty()) {
                return criteriaBuilder.conjunction(); // Retorna um predicado sempre verdadeiro se a lista for vazia
            }
            
            query.distinct(true);
            Join<Anuncio, Imovel> imovelJoin = root.join("imovel");
            
            Predicate finalPredicate = criteriaBuilder.conjunction();
            for (Caracteristica c : caracteristicas) {
                // Para cada característica na lista, cria um predicado que verifica se ela é membro da coleção de características do imóvel.
                finalPredicate = criteriaBuilder.and(finalPredicate, criteriaBuilder.isMember(c, imovelJoin.get("caracteristicas")));
            }
            return finalPredicate;
        };
    }
}