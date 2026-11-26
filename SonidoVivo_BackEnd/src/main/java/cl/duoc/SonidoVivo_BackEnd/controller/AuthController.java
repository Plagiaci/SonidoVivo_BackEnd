package cl.duoc.SonidoVivo_BackEnd.controller;

import cl.duoc.SonidoVivo_BackEnd.dto.AuthResponse;
import cl.duoc.SonidoVivo_BackEnd.dto.LoginRequest;
import cl.duoc.SonidoVivo_BackEnd.dto.RegisterRequest;
import cl.duoc.SonidoVivo_BackEnd.service.AuthService;
import cl.duoc.SonidoVivo_BackEnd.dto.RefreshTokenRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import cl.duoc.SonidoVivo_BackEnd.dto.RefreshTokenRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registrar(
            @Valid @RequestBody RegisterRequest datos) {

        authService.registrar(datos);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "mensaje",
                        "Usuario registrado correctamente"
                ));
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest datos) {

        return authService.login(datos);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public Map<String, String> miPerfil(
            @AuthenticationPrincipal Jwt jwt) {

        return Map.of(
                "email", jwt.getSubject(),
                "nombre", jwt.getClaimAsString("nombre"),
                "rol", jwt.getClaimAsString("rol")
        );
    }
    @PostMapping("/refresh")
    public AuthResponse refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        return authService.renovarToken(
                request.getRefreshToken()
        );
    }
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        authService.logout(request.getRefreshToken());

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Sesión cerrada correctamente"
                )
        );
    }

}