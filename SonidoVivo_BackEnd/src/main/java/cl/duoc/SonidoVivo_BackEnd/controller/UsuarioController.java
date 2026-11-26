package cl.duoc.SonidoVivo_BackEnd.controller;

import cl.duoc.SonidoVivo_BackEnd.dto.UsuarioAdminRequest;
import cl.duoc.SonidoVivo_BackEnd.dto.UsuarioResponse;
import cl.duoc.SonidoVivo_BackEnd.service.UsuarioService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(
            UsuarioService usuarioService) {

        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {

        return ResponseEntity.ok(
                usuarioService.listar()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                usuarioService.buscarPorId(id)
        );
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(
            @Valid @RequestBody UsuarioAdminRequest datos) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.crear(datos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioAdminRequest datos) {

        return ResponseEntity.ok(
                usuarioService.actualizar(id, datos)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        usuarioService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}