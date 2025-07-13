package com.mewebstudio.javaspringbootboilerplate.service;

import java.io.IOException;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Objects;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mewebstudio.javaspringbootboilerplate.dto.request.anuncio.CreateAnuncioRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.anuncio.UpdateAnuncioRequest;
import com.mewebstudio.javaspringbootboilerplate.entity.Anuncio;
import com.mewebstudio.javaspringbootboilerplate.entity.Caracteristica;
import com.mewebstudio.javaspringbootboilerplate.entity.Estado;
import com.mewebstudio.javaspringbootboilerplate.entity.Foto;
import com.mewebstudio.javaspringbootboilerplate.entity.Imovel;
import com.mewebstudio.javaspringbootboilerplate.entity.PrivacidadePerfil;
import com.mewebstudio.javaspringbootboilerplate.entity.TipoImovel;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.exception.ForbiddenException;
import com.mewebstudio.javaspringbootboilerplate.exception.NotFoundException;
import com.mewebstudio.javaspringbootboilerplate.repository.AnuncioRepository;
import com.mewebstudio.javaspringbootboilerplate.repository.ImovelRepository;
import com.mewebstudio.javaspringbootboilerplate.repository.UserRepository;
import com.mewebstudio.javaspringbootboilerplate.service.specification.AnuncioSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnuncioService {

    private final ImovelRepository imovelRepository;
    private final AnuncioRepository anuncioRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional(rollbackFor = {IOException.class, Exception.class})
    public Anuncio create(CreateAnuncioRequest request, List<MultipartFile> fotos) throws IOException {
        Imovel imovel = createImovelFromRequest(request, fotos);
        Imovel savedImovel = imovelRepository.save(imovel);

        User anunciante = userService.getUser();

        Anuncio anuncio = Anuncio.builder()
            .aluguel(request.getAluguel())
            .condominio(request.getCondominio())
            .caucao(request.getCaucao())
            .duracaoMinimaContrato(request.getDuracaoMinimaContrato())
            .pausado(false)
            .anunciante(anunciante)
            .imovel(savedImovel)
            .build();

        return anuncioRepository.save(anuncio);
    }

    private Imovel createImovelFromRequest(CreateAnuncioRequest request, List<MultipartFile> fotos) throws IOException {
        Imovel imovel = Imovel.builder()
            .descricao(request.getDescricao())
            .tipo(TipoImovel.valueOf(request.getTipo().toUpperCase()))
            .qtdQuartos(request.getQtdQuartos())
            .qtdBanheiros(request.getQtdBanheiros())
            .area(request.getArea())
            .dataDisponibilidade(request.getDataDisponibilidade())
            .cep(request.getCep())
            .cidade(request.getCidade())
            .estado(Estado.valueOf(request.getEstado().toUpperCase()))
            .logradouro(request.getLogradouro())
            .numero(request.getNumero())
            .bairro(request.getBairro())
            .complemento(request.getComplemento())
            .build();

        // Lógica para converter os arquivos em Base64 e criar as entidades Foto
        if (fotos != null && !fotos.isEmpty()) {
            Set<Foto> fotoSet = new HashSet<>();
            for (MultipartFile file : fotos) {
                String base64Data = Base64.getEncoder().encodeToString(file.getBytes());
                Foto foto = Foto.builder()
                    .dadosBase64(base64Data)
                    .imovel(imovel)
                    .build();
                fotoSet.add(foto);
            }
            imovel.setFotos(fotoSet);
        }

        if (request.getCaracteristicas() != null) {
            imovel.setCaracteristicas(request.getCaracteristicas().stream()
                .map(String::toUpperCase)
                .map(Caracteristica::valueOf)
                .collect(Collectors.toSet()));
        }

        return imovel;
    }

    /**
     * Busca todos os anúncios cadastrados.
     *
     * @return Uma lista de todas as entidades Anuncio.
     */
    @Transactional(readOnly = true)
    public List<Anuncio> findAll() {
        return anuncioRepository.findAllWithImovelAndCaracteristicas();
    }

    /**
     * Busca anúncios com base em critérios de filtro.
     *
     * @param tipoImovel    O tipo do imóvel.
     * @param areaMin       A área mínima do imóvel.
     * @param areaMax       A área máxima do imóvel.
     * @param precoTotalMin O preço total (aluguel + condomínio) mínimo.
     * @param precoTotalMax O preço total (aluguel + condomínio) máximo.
     * @param caracteristicas A lista de características do imóvel.
     * @return Uma lista de anúncios que correspondem aos filtros.
     */
    @Transactional(readOnly = true)
    public List<Anuncio> search(String tipoImovel, Integer areaMin, Integer areaMax, Double precoTotalMin, Double precoTotalMax, List<String> caracteristicas) {
        Specification<Anuncio> spec = Specification.where(null);

        if (tipoImovel != null && !tipoImovel.isBlank()) {
            try {
                spec = spec.and(AnuncioSpecification.porTipo(TipoImovel.valueOf(tipoImovel.toUpperCase())));
            } catch (IllegalArgumentException e) {
                // Ignora tipo inválido 
            }
        }

        if (areaMin != null || areaMax != null) {
            spec = spec.and(AnuncioSpecification.porArea(areaMin, areaMax));
        }

        if (precoTotalMin != null || precoTotalMax != null) {
            spec = spec.and(AnuncioSpecification.porPrecoTotal(precoTotalMin, precoTotalMax));
        }

        if (caracteristicas != null && !caracteristicas.isEmpty()) {
            try {
                List<Caracteristica> caracteristicaEnums = caracteristicas.stream()
                    .map(String::toUpperCase)
                    .map(Caracteristica::valueOf)
                    .collect(Collectors.toList());
                spec = spec.and(AnuncioSpecification.porCaracteristicas(caracteristicaEnums));
            } catch (IllegalArgumentException e) {
                // Ignora características inválidas para não quebrar a busca.
                log.warn("Busca com característica inválida ignorada: {}", e.getMessage());
            }
        }

        return anuncioRepository.findAll(spec);
    }

    /**
     * Busca um anúncio pelo seu ID.
     *
     * @param id O UUID do anúncio.
     * @return A entidade Anuncio encontrada.
     * @throws NotFoundException se nenhum anúncio for encontrado com o ID fornecido.
     */
    @Transactional(readOnly = true)
    public Anuncio findById(UUID id) {
        return anuncioRepository.findByIdWithImovelAndCaracteristicas(id)
            .orElseThrow(() -> new NotFoundException("Anúncio não encontrado com o ID: " + id));
    }

    /**
     * Busca todos os anúncios de um anunciante específico.
     *
     * @param anuncianteId O UUID do anunciante.
     * @return Uma lista de entidades Anuncio do anunciante.
     */
    @Transactional(readOnly = true)
    public List<Anuncio> findAllByAnuncianteId(UUID anuncianteId) {
        return anuncioRepository.findAllByAnuncianteIdWithDetails(anuncianteId);
    }

    /**
     * Busca todos os usuários que favoritaram um anúncio específico e possuem perfil público.
     *
     * @param anuncioId O ID do anúncio.
     * @return Uma lista de usuários que favoritaram o anúncio e têm perfil público.
     */
    @Transactional(readOnly = true)
    public List<User> getUsuariosInteressados(UUID anuncioId) {
        if (!anuncioRepository.existsById(anuncioId)) {
            throw new NotFoundException("Anúncio não encontrado com o ID: " + anuncioId);
        }

        // Chama o método na instância injetada do userRepository
        return userRepository.findUsersByFavoritedAnuncioAndPublicProfile(
            anuncioId, PrivacidadePerfil.PUBLICO
        );
    }

    /**
     * Atualiza os dados de um anúncio existente.
     *
     * @param anuncioId O ID do anúncio a ser atualizado.
     * @param request   O DTO com os novos dados.
     * @return A entidade Anuncio atualizada.
     * @throws NotFoundException  se o anúncio não for encontrado.
     * @throws ForbiddenException se o usuário não for o proprietário ou se tentar
     *                            alterar campos bloqueados em um anúncio ativo.
     */
    @Transactional
    public Anuncio update(UUID anuncioId, UpdateAnuncioRequest request) {
        User currentUser = userService.getUser();
        Anuncio anuncio = anuncioRepository.findByIdWithImovelAndCaracteristicas(anuncioId)
                .orElseThrow(() -> new NotFoundException("Anúncio não encontrado com o ID: " + anuncioId));

        if (!anuncio.getAnunciante().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Você não tem permissão para editar este anúncio.");
        }

        Imovel imovel = anuncio.getImovel();

        // Atualizar os dados que são sempre permitidos
        anuncio.setDuracaoMinimaContrato(request.getDuracaoMinimaContrato());
        imovel.setDescricao(request.getDescricao());
        imovel.setQtdQuartos(request.getQtdQuartos());
        imovel.setQtdBanheiros(request.getQtdBanheiros());
        imovel.setArea(request.getArea());
        imovel.setDataDisponibilidade(request.getDataDisponibilidade());
        if (request.getCaracteristicas() != null) {
            imovel.setCaracteristicas(request.getCaracteristicas().stream()
                    .map(String::toUpperCase)
                    .map(Caracteristica::valueOf)
                    .collect(Collectors.toSet()));
        } else {
            imovel.getCaracteristicas().clear();
        }

        // Se o anúncio está pausado, permite a alteração de preço, tipo e endereço.
        if (anuncio.isPausado()) {
            anuncio.setAluguel(request.getAluguel());
            anuncio.setCondominio(request.getCondominio());
            anuncio.setCaucao(request.getCaucao());

            imovel.setTipo(TipoImovel.valueOf(request.getTipo().toUpperCase()));
            imovel.setCep(request.getCep());
            imovel.setCidade(request.getCidade());
            imovel.setEstado(Estado.valueOf(request.getEstado().toUpperCase()));
            imovel.setLogradouro(request.getLogradouro());
            imovel.setNumero(request.getNumero());
            imovel.setBairro(request.getBairro());
            imovel.setComplemento(request.getComplemento());
        } else {
            // Se o anúncio está ativo, verifica se houve tentativa de alteração.
            if (!Objects.equals(anuncio.getAluguel(), request.getAluguel()) ||
                !Objects.equals(anuncio.getCondominio(), request.getCondominio()) ||
                !Objects.equals(anuncio.getCaucao(), request.getCaucao())) {
                throw new ForbiddenException("O preço (aluguel, condomínio, caução) não pode ser alterado em um anúncio ativo.");
            }

            if (!Objects.equals(imovel.getTipo().name(), request.getTipo().toUpperCase())) {
                throw new ForbiddenException("O tipo do imóvel não pode ser alterado em um anúncio ativo.");
            }
            if (!Objects.equals(imovel.getCep(), request.getCep()) ||
                !Objects.equals(imovel.getCidade(), request.getCidade()) ||
                !Objects.equals(imovel.getEstado().name(), request.getEstado().toUpperCase()) ||
                !Objects.equals(imovel.getLogradouro(), request.getLogradouro()) ||
                !Objects.equals(imovel.getNumero(), request.getNumero()) ||
                !Objects.equals(imovel.getBairro(), request.getBairro())) {
                throw new ForbiddenException("O endereço não pode ser alterado em um anúncio ativo.");
            }
        }

        return anuncioRepository.save(anuncio);
    }

    /**
     * Adiciona novas fotos a um anúncio existente.
     *
     * @param anuncioId O ID do anúncio.
     * @param fotos     A lista de arquivos de imagem a serem adicionados.
     * @return A entidade Anuncio atualizada com as novas fotos.
     * @throws IOException se houver um erro ao processar os arquivos.
     */
    @Transactional(rollbackFor = {IOException.class, Exception.class})
    public Anuncio addFotos(UUID anuncioId, List<MultipartFile> fotos) throws IOException {
        User currentUser = userService.getUser();
        Anuncio anuncio = anuncioRepository.findByIdWithImovelAndCaracteristicas(anuncioId)
                .orElseThrow(() -> new NotFoundException("Anúncio não encontrado com o ID: " + anuncioId));

        if (!anuncio.getAnunciante().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Você não tem permissão para adicionar fotos a este anúncio.");
        }

        if (fotos == null || fotos.isEmpty()) {
            return anuncio; // Nenhuma foto para adicionar
        }

        Imovel imovel = anuncio.getImovel();
        for (MultipartFile file : fotos) {
            String base64Data = Base64.getEncoder().encodeToString(file.getBytes());
            Foto foto = Foto.builder()
                    .dadosBase64(base64Data)
                    .imovel(imovel)
                    .build();
            imovel.getFotos().add(foto);
        }

        return anuncioRepository.save(anuncio);
    }

    /**
     * Deleta uma foto específica de um anúncio.
     *
     * @param anuncioId O ID do anúncio.
     * @param fotoId    O ID da foto a ser deletada.
     */
    @Transactional
    public void deleteFoto(UUID anuncioId, UUID fotoId) {
        User currentUser = userService.getUser();
        Anuncio anuncio = anuncioRepository.findByIdWithImovelAndCaracteristicas(anuncioId)
                .orElseThrow(() -> new NotFoundException("Anúncio não encontrado com o ID: " + anuncioId));

        if (!anuncio.getAnunciante().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Você não tem permissão para deletar fotos deste anúncio.");
        }

        Imovel imovel = anuncio.getImovel();
        boolean removed = imovel.getFotos().removeIf(foto -> foto.getId().equals(fotoId));

        if (!removed) {
            throw new NotFoundException("Foto não encontrada neste anúncio com o ID: " + fotoId);
        }

        // A exclusão da foto acontece automaticamente ao final da transação 
        // por causa da configuração 'orphanRemoval=true' na entidade Imovel.
    }

    /**
     * Alterna o status de 'pausado' de um anúncio.
     * Apenas o proprietário do anúncio pode realizar esta ação.
     *
     * @param anuncioId O ID do anúncio a ser pausado/despausado.
     * @return O novo estado de 'pausado' (true se pausado, false se ativo).
     * @throws NotFoundException se o anúncio não for encontrado.
     * @throws ForbiddenException se o usuário atual não for o proprietário do anúncio.
     */
    @Transactional
    public boolean togglePauseStatus(UUID anuncioId) {
        // 1. Obter o usuário autenticado
        User currentUser = userService.getUser();

        // 2. Encontrar o anúncio ou lançar exceção
        Anuncio anuncio = anuncioRepository.findById(anuncioId)
            .orElseThrow(() -> new NotFoundException("Anúncio não encontrado."));

        // 3. Verificação de autorização
        if (!anuncio.getAnunciante().getId().equals(currentUser.getId())) {
        throw new ForbiddenException("Você não tem permissão para modificar este anúncio.");
        }

        // 4. Alternar o status e salvar
        boolean novoStatus = !anuncio.isPausado();
        anuncio.setPausado(novoStatus);
        anuncioRepository.save(anuncio);

        log.info("Anúncio [{}] teve seu status alterado para: {}", anuncioId, novoStatus ? "PAUSADO" : "ATIVO");

        return novoStatus;
    }
}