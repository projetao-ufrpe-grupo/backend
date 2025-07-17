package com.mewebstudio.javaspringbootboilerplate.controller;

import static com.mewebstudio.javaspringbootboilerplate.util.Constants.SECURITY_SCHEME_NAME;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mewebstudio.javaspringbootboilerplate.dto.request.anuncio.CreateAnuncioRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.anuncio.UpdateAnuncioRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.response.AnuncioMapaResponse;
import com.mewebstudio.javaspringbootboilerplate.dto.response.AnuncioResponse;
import com.mewebstudio.javaspringbootboilerplate.dto.response.ErrorResponse;
import com.mewebstudio.javaspringbootboilerplate.dto.response.user.UserResponse;
import com.mewebstudio.javaspringbootboilerplate.entity.Anuncio;
import com.mewebstudio.javaspringbootboilerplate.entity.TipoImovel;
import com.mewebstudio.javaspringbootboilerplate.service.AnuncioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
    @Transactional(readOnly = true)
    public ResponseEntity<List<AnuncioResponse>> getAllAnuncios() {
        List<AnuncioResponse> anuncios = anuncioService.findAll().stream()
            .map(AnuncioResponse::convert)
            .collect(Collectors.toList());
        return ResponseEntity.ok(anuncios);
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search for announcements with filters",
        description = "Returns a list of announcements based on the provided filter criteria. All filters are optional."
    )
    public ResponseEntity<List<AnuncioResponse>> searchAnuncios(
        @Parameter(description = "Type of property (e.g., APARTAMENTO, CASA)", schema = @Schema(implementation = TipoImovel.class))
        @RequestParam(required = false) String tipo,
        @Parameter(description = "Minimum area in square meters")
        @RequestParam(required = false) Integer areaMin,
        @Parameter(description = "Maximum area in square meters")
        @RequestParam(required = false) Integer areaMax,
        @Parameter(description = "Minimum total monthly price (rent + condo fee)")
        @RequestParam(required = false) Double precoTotalMin,
        @Parameter(description = "Maximum total monthly price (rent + condo fee)")
        @RequestParam(required = false) Double precoTotalMax,
        @Parameter(description = "List of required property characteristics (e.g., PISCINA, GARAGEM)")
        @RequestParam(required = false) List<String> caracteristicas
    ) {
        List<AnuncioResponse> anuncios = anuncioService.search(tipo, areaMin, areaMax, precoTotalMin, precoTotalMax, caracteristicas)
            .stream()
            .map(AnuncioResponse::convert)
            .collect(Collectors.toList());
        return ResponseEntity.ok(anuncios);
    }

    @GetMapping("/mapa")
    @Operation(
        summary = "Get announcements for map view",
        description = "Returns a lightweight list of active announcements with coordinates for map display."
    )
    public ResponseEntity<List<AnuncioMapaResponse>> getAnunciosParaMapa() {
        List<AnuncioMapaResponse> anunciosParaMapa = anuncioService.findAllForMap().stream()
                .map(AnuncioMapaResponse::convert)
                .collect(Collectors.toList());
        return ResponseEntity.ok(anunciosParaMapa);
    }

    @GetMapping("/{id}/favoritos")
    @Operation(
        summary = "List users who favorited an announcement",
        description = "Returns a list of users who favorited the specified announcement and have public profiles.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Announcement not found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<List<UserResponse>> getFavoritingUsers(
        @Parameter(description = "ID of the announcement", required = true)
        @PathVariable UUID id
    ) {
        List<UserResponse> users = anuncioService.getUsuariosInteressados(id).stream()
            .map(UserResponse::convert)
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/anunciante/{anuncianteId}")
    @Operation(
        summary = "Get all announcements from a specific user",
        description = "Returns a list of all announcements registered by a specific user (announcer)."
    )
    public ResponseEntity<List<AnuncioResponse>> getAnunciosByAnunciante(
        @Parameter(description = "ID of the announcer to retrieve announcements from", required = true)
        @PathVariable UUID anuncianteId
    ) {
        List<AnuncioResponse> anuncios = anuncioService.findAllByAnuncianteId(anuncianteId).stream()
            .map(AnuncioResponse::convert)
            .collect(Collectors.toList());
        return ResponseEntity.ok(anuncios);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get announcement details by ID",
        description = "Returns the full details of a specific announcement."
    )
    @Transactional()
    public ResponseEntity<AnuncioResponse> getAnuncioById(
        @Parameter(description = "ID of the announcement to retrieve", required = true)
        @PathVariable("id") UUID anuncioId
    ) {
        Anuncio anuncio = anuncioService.findById(anuncioId);
        return ResponseEntity.ok(AnuncioResponse.convert(anuncio));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update an announcement",
        description = "Updates the details of a specific announcement. Only the owner can perform this action. " +
                      "The property type and address cannot be changed if the announcement is active (not paused).",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<AnuncioResponse> updateAnuncio(
        @Parameter(description = "ID of the announcement to update", required = true)
        @PathVariable("id") UUID anuncioId,
        @RequestBody @Valid UpdateAnuncioRequest request
    ) {
        Anuncio updatedAnuncio = anuncioService.update(anuncioId, request);
        return ResponseEntity.ok(AnuncioResponse.convert(updatedAnuncio));
    }

    @PostMapping(value = "/{id}/fotos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Add photos to an announcement",
        description = "Uploads one or more new photos for a specific announcement. Only the owner can add photos.",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<AnuncioResponse> addFotosToAnuncio(
        @Parameter(description = "ID of the announcement to add photos to", required = true)
        @PathVariable("id") UUID anuncioId,
        @Parameter(description = "List of image files to be uploaded", required = true)
        @RequestParam("fotos") List<MultipartFile> fotos
    ) throws IOException {
        Anuncio updatedAnuncio = anuncioService.addFotos(anuncioId, fotos);
        return ResponseEntity.ok(AnuncioResponse.convert(updatedAnuncio));
    }

    @DeleteMapping("/{anuncioId}/fotos/{fotoId}")
    @Operation(
        summary = "Delete a photo from an announcement",
        description = "Deletes a specific photo from an announcement. Only the owner can delete photos.",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<Void> deleteFotoFromAnuncio(
        @Parameter(description = "ID of the announcement", required = true)
        @PathVariable UUID anuncioId,
        @Parameter(description = "ID of the photo to be deleted", required = true)
        @PathVariable UUID fotoId
    ) {
        anuncioService.deleteFoto(anuncioId, fotoId);
        return ResponseEntity.noContent().build();
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

    @PatchMapping("/{id}/{vagas}")
    @Operation(
        summary = "Update parking spaces in an announcement",
        description = "Updates the number of parking spaces available for a specific announcement. " +
                      "Only the owner can perform this action.",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<AnuncioResponse> updateVagas(
        @Parameter(description = "ID of the announcement to update", required = true)
        @PathVariable("id") UUID anuncioId,
        @Parameter(description = "Number of vacancies to be established", required = true)
        @PathVariable("vagas") Integer vagas
    ) {
        Anuncio updatedAnuncio = anuncioService.updateVagas(anuncioId, vagas);
        return ResponseEntity.ok(AnuncioResponse.convert(updatedAnuncio));
    }

    @GetMapping("/recent")
    @Operation(
        summary = "List recently viewed announcements",
        description = "Returns a list of announcements that the user has viewed recently.",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<List<AnuncioResponse>> getRecentAnuncios() {
        List<AnuncioResponse> recentAnuncios = anuncioService.getRecentAnuncios();
        return ResponseEntity.ok(recentAnuncios);
    }

}