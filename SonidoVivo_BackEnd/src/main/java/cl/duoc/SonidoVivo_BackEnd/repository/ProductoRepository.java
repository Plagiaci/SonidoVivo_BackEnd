package cl.duoc.SonidoVivo_BackEnd.repository;

import cl.duoc.SonidoVivo_BackEnd.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepository
        extends JpaRepository<Producto, String> {

    @Query(value = "select * from productos where id_producto = :id for update",
            nativeQuery = true)
    Optional<Producto> findByIdForUpdate(@Param("id") String id);
}