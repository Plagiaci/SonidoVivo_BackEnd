package cl.duoc.SonidoVivo_BackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DetalleCompraResponse {

    private String productoId;
    private String nombreProducto;
    private Integer cantidad;
    private Integer precioUnitario;
    private Integer subtotal;
}