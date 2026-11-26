package cl.duoc.SonidoVivo_BackEnd.dto;

import cl.duoc.SonidoVivo_BackEnd.model.EstadoCompra;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstadoCompraRequest {

    @NotNull
    private EstadoCompra estado;
}