package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.dto.request.anuncio.CreateAnuncioRequest;
import com.mewebstudio.javaspringbootboilerplate.entity.*;
import com.mewebstudio.javaspringbootboilerplate.repository.AnuncioRepository;
import com.mewebstudio.javaspringbootboilerplate.repository.ImovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
            .cep(request.getCep())
            .cidade(request.getCidade())
            .estado(Estado.valueOf(request.getEstado().toUpperCase()))
            .logradouro(request.getLogradouro())
            .numero(request.getNumero())
            .bairro(request.getBairro())
            .complemento(request.getComplemento())
            .fotos(new ArrayList<>())
            .caracteristicas(new ArrayList<>())
            .build();

        // Lógica para converter os arquivos em Base64 e criar as entidades Foto
        if (fotos != null && !fotos.isEmpty()) {
            for (MultipartFile file : fotos) {
                String base64Data = Base64.getEncoder().encodeToString(file.getBytes());
                Foto foto = Foto.builder()
                    .dadosBase64(base64Data)
                    .imovel(imovel)
                    .build();
                imovel.getFotos().add(foto);
            }
        }

        if (request.getCaracteristicas() != null) {
            imovel.setCaracteristicas(request.getCaracteristicas().stream()
                .map(String::toUpperCase)
                .map(Caracteristica::valueOf)
                .collect(Collectors.toList()));
        }

        return imovel;
    }
}
