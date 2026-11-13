package cl.duoc.SonidoVivo_BackEnd.repository;

import cl.duoc.SonidoVivo_BackEnd.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository
        extends JpaRepository<Producto, String> {
}