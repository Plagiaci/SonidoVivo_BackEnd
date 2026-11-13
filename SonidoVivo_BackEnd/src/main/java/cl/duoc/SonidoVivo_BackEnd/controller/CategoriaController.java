package cl.duoc.SonidoVivo_BackEnd.controller;

import cl.duoc.SonidoVivo_BackEnd.model.Categoria;
import cl.duoc.SonidoVivo_BackEnd.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        return ResponseEntity.ok(
                categoriaService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarPorId(
            @PathVariable Long id) {

        Categoria categoria =
                categoriaService.buscarPorId(id);

        if (categoria == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    public ResponseEntity<Categoria> registrar(
            @RequestBody Categoria categoria) {

        Categoria nueva =
                categoriaService.registrar(categoria);

        return new ResponseEntity<>(
                nueva,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(
            @PathVariable Long id,
            @RequestBody Categoria categoria) {

        Categoria existente =
                categoriaService.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        categoria.setId(id);

        Categoria actualizada =
                categoriaService.actualizar(categoria);

        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        boolean eliminado =
                categoriaService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}