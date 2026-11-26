package cl.duoc.SonidoVivo_BackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DashboardResponse {

    private String periodo;

    private Long totalOperaciones;

    private Long totalVendido;

    private Long productosVendidos;

    private List<ProductoVendidoResponse> productosMasVendidos;
}