package com.mewebstudio.javaspringbootboilerplate.service;

import java.io.IOException;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mewebstudio.javaspringbootboilerplate.dto.request.anuncio.CreateAnuncioRequest;
import com.mewebstudio.javaspringbootboilerplate.entity.Anuncio;
import com.mewebstudio.javaspringbootboilerplate.entity.Caracteristica;
import com.mewebstudio.javaspringbootboilerplate.entity.Estado;
import com.mewebstudio.javaspringbootboilerplate.entity.Foto;
import com.mewebstudio.javaspringbootboilerplate.entity.Imovel;
import com.mewebstudio.javaspringbootboilerplate.entity.TipoImovel;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.exception.ForbiddenException;
import com.mewebstudio.javaspringbootboilerplate.exception.NotFoundException;
import com.mewebstudio.javaspringbootboilerplate.repository.AnuncioRepository;
import com.mewebstudio.javaspringbootboilerplate.repository.ImovelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnuncioService {

    private final ImovelRepository imovelRepository;
    private final AnuncioRepository anuncioRepository;
    private final UserService userService;

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
