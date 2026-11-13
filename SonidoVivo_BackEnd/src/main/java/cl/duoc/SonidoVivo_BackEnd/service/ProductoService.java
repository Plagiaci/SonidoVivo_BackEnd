package cl.duoc.SonidoVivo_BackEnd.service;

import cl.duoc.SonidoVivo_BackEnd.model.Producto;
import cl.duoc.SonidoVivo_BackEnd.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Producto buscarPorId(String id) {
        return productoRepository.findById(id)
                .orElse(null);
    }

    public Producto registrar(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizar(Producto producto) {
        return productoRepository.save(producto);
    }

    public boolean eliminar(String id) {

        if (!productoRepository.existsById(id)) {
            return false;
        }

        productoRepository.deleteById(id);
        return true;
    }
}