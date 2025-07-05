package com.mewebstudio.javaspringbootboilerplate.controller;

import com.mewebstudio.javaspringbootboilerplate.dto.request.anuncio.CreateAnuncioRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.response.AnuncioResponse;
import com.mewebstudio.javaspringbootboilerplate.entity.Anuncio;
import com.mewebstudio.javaspringbootboilerplate.service.AnuncioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.mewebstudio.javaspringbootboilerplate.util.Constants.SECURITY_SCHEME_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/anuncios")
@Tag(name = "003. Anuncios", description = "Anuncios API")
public class AnuncioController {

    private final AnuncioService anuncioService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(
        summary = "Create a new announcement with images",
        description = "Creates a new property (Imovel) and its corresponding announcement (Anuncio).",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<AnuncioResponse> create(
        @RequestPart("request") @Valid CreateAnuncioRequest request,
        @RequestPart(value = "fotos", required = false) List<MultipartFile> fotos
    ) throws IOException {
        Anuncio createdAnuncio = anuncioService.create(request, fotos);

        // Esta lógica agora funciona corretamente
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest() // Pega a URL base: /anuncios
            .path("/{id}")        // Adiciona o path: /{id}
            .buildAndExpand(createdAnuncio.getId())
            .toUri();             // Resulta em: /anuncios/{id-do-novo-anuncio}

        return ResponseEntity.created(location).body(AnuncioResponse.convert(createdAnuncio));
    }

    @GetMapping
    @Operation(
        summary = "Get all announcements",
        description = "Returns a list of all registered announcements."
    )
    public ResponseEntity<List<AnuncioResponse>> getAllAnuncios() {
        List<AnuncioResponse> anuncios = anuncioService.findAll().stream()
            .map(AnuncioResponse::convert)
            .collect(Collectors.toList());
        return ResponseEntity.ok(anuncios);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get announcement details by ID",
        description = "Returns the full details of a specific announcement."
    )
    public ResponseEntity<AnuncioResponse> getAnuncioById(
        @Parameter(description = "ID of the announcement to retrieve", required = true)
        @PathVariable("id") UUID anuncioId
    ) {
        Anuncio anuncio = anuncioService.findById(anuncioId);
        return ResponseEntity.ok(AnuncioResponse.convert(anuncio));
    }

    @PatchMapping("/{id}/toggle-pause")
    @Operation(
        summary = "Pause or unpause an announcement",
        description = "Toggles the 'paused' status of an announcement. Only the owner can perform this action.",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<Map<String, Boolean>> togglePauseStatus(
        @Parameter(description = "ID of the announcement to toggle", required = true)
        @PathVariable("id") UUID anuncioId
    ) {
        boolean isPaused = anuncioService.togglePauseStatus(anuncioId);
        return ResponseEntity.ok(Map.of("paused", isPaused));
    }

}