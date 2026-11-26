package cl.duoc.SonidoVivo_BackEnd.dto;

import cl.duoc.SonidoVivo_BackEnd.model.EstadoCompra;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CompraAdminResponse {

    private Long id;
    private LocalDateTime fecha;
    private EstadoCompra estado;
    private Integer total;

    private Long usuarioId;
    private String nombreCliente;
    private String emailCliente;

    private List<DetalleCompraResponse> detalles;
}