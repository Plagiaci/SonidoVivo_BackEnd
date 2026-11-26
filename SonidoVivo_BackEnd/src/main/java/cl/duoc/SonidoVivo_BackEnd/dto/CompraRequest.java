package cl.duoc.SonidoVivo_BackEnd.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CompraRequest {

    @NotEmpty
    @Valid
    private List<CompraItemRequest> items;
}