package com.mewebstudio.javaspringbootboilerplate.controller;

import com.mewebstudio.javaspringbootboilerplate.dto.response.EnumResponse;
import com.mewebstudio.javaspringbootboilerplate.entity.Caracteristica;
import com.mewebstudio.javaspringbootboilerplate.entity.InteressesUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enums")
@Tag(name = "004. Enums & Metadata", description = "API para obter listas de valores fixos (enums)")
public class EnumController {

    @GetMapping("/caracteristicas-imovel")
    @Operation(summary = "Get all available property characteristics")
    public ResponseEntity<List<EnumResponse>> getCaracteristicasImovel() {
        List<EnumResponse> caracteristicas = Arrays.stream(Caracteristica.values())
            .map(c -> new EnumResponse(c.name(), c.getDescricao()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(caracteristicas);
    }

    @GetMapping("/interesses-usuario")
    @Operation(summary = "Get all available user interests")
    public ResponseEntity<List<EnumResponse>> getInteressesUsuario() {
        List<EnumResponse> interesses = Arrays.stream(InteressesUsuario.values())
            .map(i -> new EnumResponse(i.name(), i.getDescricao()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(interesses);
    }
}