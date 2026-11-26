package cl.duoc.SonidoVivo_BackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductoVendidoResponse {

    private String productoId;
    private String nombre;
    private Integer cantidadVendida;
}