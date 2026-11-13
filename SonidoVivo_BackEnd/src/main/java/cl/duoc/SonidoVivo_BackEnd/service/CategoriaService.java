package cl.duoc.SonidoVivo_BackEnd.service;

import cl.duoc.SonidoVivo_BackEnd.model.Categoria;
import cl.duoc.SonidoVivo_BackEnd.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElse(null);
    }

    public Categoria registrar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public boolean eliminar(Long id) {

        if (!categoriaRepository.existsById(id)) {
            return false;
        }

        categoriaRepository.deleteById(id);
        return true;
    }
}