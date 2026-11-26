package cl.duoc.SonidoVivo_BackEnd.service;

import cl.duoc.SonidoVivo_BackEnd.model.RefreshToken;
import cl.duoc.SonidoVivo_BackEnd.model.Usuario;
import cl.duoc.SonidoVivo_BackEnd.repository.RefreshTokenRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final long expirationDays;

    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository repository,
            @Value("${app.jwt.refresh-expiration-days}")
            long expirationDays) {

        this.repository = repository;
        this.expirationDays = expirationDays;
    }

    public String crear(Usuario usuario) {

        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setTokenHash(hash(token));
        refreshToken.setUsuario(usuario);
        refreshToken.setRevocado(false);

        refreshToken.setFechaExpiracion(
                Instant.now()
                        .plus(expirationDays, ChronoUnit.DAYS)
        );

        repository.save(refreshToken);

        return token;
    }

    public Usuario validar(String token) {

        RefreshToken refreshToken = repository
                .findByTokenHash(hash(token))
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Refresh token inválido"
                        )
                );

        if (refreshToken.isRevocado()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token revocado"
            );
        }

        if (refreshToken.getFechaExpiracion()
                .isBefore(Instant.now())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token expirado"
            );
        }

        return refreshToken.getUsuario();
    }

    @Transactional
    public void revocar(String token) {

        RefreshToken refreshToken = repository
                .findByTokenHash(hash(token))
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Refresh token inválido"
                        )
                );

        refreshToken.setRevocado(true);
        repository.save(refreshToken);
    }

    private String hash(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {

            throw new IllegalStateException(
                    "No se pudo procesar el refresh token"
            );
        }
    }
}