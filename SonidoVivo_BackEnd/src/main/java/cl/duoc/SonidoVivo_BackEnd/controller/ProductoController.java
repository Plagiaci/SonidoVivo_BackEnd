package cl.duoc.SonidoVivo_BackEnd.controller;

import cl.duoc.SonidoVivo_BackEnd.model.Producto;
import cl.duoc.SonidoVivo_BackEnd.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@CrossOrigin(origins = "http://localhost:5173")
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
    public ResponseEntity<Producto> registrar(
            @RequestBody Producto producto) {

        Producto nuevo =
                productoService.registrar(producto);

        return new ResponseEntity<>(
                nuevo,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
            @PathVariable String id,
            @RequestBody Producto producto) {

        Producto existente =
                productoService.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        producto.setId(id);

        Producto actualizado =
                productoService.actualizar(producto);

        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
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