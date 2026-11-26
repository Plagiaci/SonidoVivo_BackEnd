package cl.duoc.SonidoVivo_BackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String token;
    private String refreshToken;
    private String tipo;
    private String nombre;
    private String email;
    private String rol;
}