package cl.duoc.SonidoVivo_BackEnd.repository;

import cl.duoc.SonidoVivo_BackEnd.model.DetalleCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleCompraRepository
        extends JpaRepository<DetalleCompra, Long> {
}