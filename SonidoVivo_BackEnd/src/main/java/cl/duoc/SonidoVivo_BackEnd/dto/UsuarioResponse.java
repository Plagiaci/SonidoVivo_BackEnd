package cl.duoc.SonidoVivo_BackEnd.dto;

import cl.duoc.SonidoVivo_BackEnd.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String email;
    private Rol rol;
}