package cl.duoc.SonidoVivo_BackEnd.controller;

import cl.duoc.SonidoVivo_BackEnd.dto.ProductoRequest;
import cl.duoc.SonidoVivo_BackEnd.dto.StockUpdateRequest;
import cl.duoc.SonidoVivo_BackEnd.model.Producto;
import cl.duoc.SonidoVivo_BackEnd.service.ProductoService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {

        return ResponseEntity.ok(
                productoService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(
            @PathVariable String id) {

        Producto producto =
                productoService.buscarPorId(id);

        if (producto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(producto);
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Producto> registrar(
            @Valid @RequestBody ProductoRequest producto) {

        Producto nuevo =
                productoService.registrar(producto);

        return new ResponseEntity<>(
                nuevo,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Producto> actualizar(
            @PathVariable String id,
            @Valid @RequestBody ProductoRequest producto) {
        Producto actualizado = productoService.actualizar(id, producto);

        return ResponseEntity.ok(actualizado);
    }

    @PatchMapping("/{id}/stock")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Producto> actualizarStock(
            @PathVariable String id,
            @Valid @RequestBody StockUpdateRequest request) {
        return ResponseEntity.ok(productoService.actualizarStock(id, request));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> eliminar(
            @PathVariable String id) {

        boolean eliminado =
                productoService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}