package cl.duoc.SonidoVivo_BackEnd.dto;

import cl.duoc.SonidoVivo_BackEnd.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioAdminRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    @Email
    private String email;

    // En edición puede venir vacío para conservar la contraseña actual
    private String password;

    @NotNull
    private Rol rol;
}