package cl.duoc.SonidoVivo_BackEnd.dto;

import cl.duoc.SonidoVivo_BackEnd.model.EstadoCompra;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CompraResponse {

    private Long id;
    private LocalDateTime fecha;
    private EstadoCompra estado;
    private Integer total;
    private List<DetalleCompraResponse> detalles;
}