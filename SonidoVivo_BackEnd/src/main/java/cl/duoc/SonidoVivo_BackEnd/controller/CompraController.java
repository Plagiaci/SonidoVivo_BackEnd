package cl.duoc.SonidoVivo_BackEnd.controller;

import cl.duoc.SonidoVivo_BackEnd.dto.CompraRequest;
import cl.duoc.SonidoVivo_BackEnd.dto.CompraResponse;
import cl.duoc.SonidoVivo_BackEnd.service.CompraService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import cl.duoc.SonidoVivo_BackEnd.dto.CompraAdminResponse;
import cl.duoc.SonidoVivo_BackEnd.dto.EstadoCompraRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/compras")
@SecurityRequirement(name = "bearerAuth")
public class CompraController {

    private final CompraService compraService;

    public CompraController(
            CompraService compraService) {

        this.compraService = compraService;
    }

    @PostMapping
    public ResponseEntity<CompraResponse> comprar(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CompraRequest request) {

        CompraResponse compra =
                compraService.realizarCompra(
                        jwt.getSubject(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(compra);
    }

    @GetMapping("/mis-compras")
    public ResponseEntity<List<CompraResponse>>
    misCompras(
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(
                compraService.misCompras(
                        jwt.getSubject()
                )
        );
    }
    @GetMapping
    public ResponseEntity<List<CompraAdminResponse>>
    listarTodas() {

        return ResponseEntity.ok(
                compraService.listarTodas()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraAdminResponse>
    buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                compraService.buscarPorId(id)
        );
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CompraAdminResponse>
    cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoCompraRequest request) {

        return ResponseEntity.ok(
                compraService.cambiarEstado(
                        id,
                        request.getEstado()
                )
        );
    }
}