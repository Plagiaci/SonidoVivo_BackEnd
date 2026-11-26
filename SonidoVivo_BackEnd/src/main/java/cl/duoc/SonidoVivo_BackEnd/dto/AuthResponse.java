package cl.duoc.SonidoVivo_BackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String token;
    private String tipo;
    private String refreshToken;
    private String nombre;
    private String email;
    private String rol;
    private Long id;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String region;
    private String genero;
}