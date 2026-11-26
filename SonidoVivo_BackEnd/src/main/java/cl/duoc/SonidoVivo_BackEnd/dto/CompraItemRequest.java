package cl.duoc.SonidoVivo_BackEnd.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompraItemRequest {

    @NotBlank
    private String productoId;

    @Min(1)
    private Integer cantidad;
}