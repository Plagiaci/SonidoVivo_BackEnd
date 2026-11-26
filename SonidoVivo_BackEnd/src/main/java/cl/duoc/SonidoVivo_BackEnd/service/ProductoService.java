package cl.duoc.SonidoVivo_BackEnd.service;

import cl.duoc.SonidoVivo_BackEnd.dto.ProductoRequest;
import cl.duoc.SonidoVivo_BackEnd.dto.StockUpdateRequest;
import cl.duoc.SonidoVivo_BackEnd.model.Categoria;
import cl.duoc.SonidoVivo_BackEnd.model.Producto;
import cl.duoc.SonidoVivo_BackEnd.repository.CategoriaRepository;
import cl.duoc.SonidoVivo_BackEnd.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Producto buscarPorId(String id) {
        return productoRepository.findById(id)
                .orElse(null);
    }

    public Producto registrar(ProductoRequest request) {
        return productoRepository.save(convertir(request, new Producto()));
    }

    public Producto actualizar(String id, ProductoRequest request) {
        if (!id.equals(request.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El código del producto no coincide con la ruta."
            );
        }

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró el producto."
                ));

        return productoRepository.save(convertir(request, producto));
    }

    public Producto actualizarStock(String id, StockUpdateRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró el producto."
                ));
        producto.setStock(request.getStock());
        return productoRepository.save(producto);
    }

    public boolean eliminar(String id) {

        if (!productoRepository.existsById(id)) {
            return false;
        }

        productoRepository.deleteById(id);
        return true;
    }

    private Producto convertir(ProductoRequest request, Producto producto) {
        Categoria categoria = categoriaRepository
                .findByNombreIgnoreCase(request.getCategoriaNombre().trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La categoría indicada no existe."
                ));

        producto.setId(request.getId().trim());
        producto.setNombre(request.getNombre().trim());
        producto.setMarca(request.getMarca().trim());
        producto.setModelo(request.getModelo().trim());
        producto.setStock(request.getStock());
        producto.setPrecio(request.getPrecio());
        producto.setDescripcion(request.getDescripcion());
        producto.setCategoria(categoria);
        return producto;
    }
}