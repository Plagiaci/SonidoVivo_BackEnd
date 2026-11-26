package cl.duoc.SonidoVivo_BackEnd.service;

import cl.duoc.SonidoVivo_BackEnd.dto.AuthResponse;
import cl.duoc.SonidoVivo_BackEnd.dto.LoginRequest;
import cl.duoc.SonidoVivo_BackEnd.dto.RegisterRequest;
import cl.duoc.SonidoVivo_BackEnd.model.Rol;
import cl.duoc.SonidoVivo_BackEnd.model.Usuario;
import cl.duoc.SonidoVivo_BackEnd.repository.UsuarioRepository;
import cl.duoc.SonidoVivo_BackEnd.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public void registrar(RegisterRequest datos) {

        String email = datos.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (repository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya está registrado"
            );
        }

        Usuario usuario = new Usuario(
                null,
                datos.getNombre().trim(),
                email,
                passwordEncoder.encode(datos.getPassword()),
                Rol.CLIENTE
        );

        repository.save(usuario);
    }

    public AuthResponse login(LoginRequest datos) {

        String email = datos.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales incorrectas"
                ));

        if (!passwordEncoder.matches(
                datos.getPassword(),
                usuario.getPassword())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales incorrectas"
            );
        }

        String token = jwtService.generarToken(usuario);

        String refreshToken =
                refreshTokenService.crear(usuario);

        return new AuthResponse(
                token,
                refreshToken,
                "Bearer",
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().name()
        );
    }

    public AuthResponse renovarToken(String refreshToken) {

        Usuario usuario =
                refreshTokenService.validar(refreshToken);

        // El refresh token anterior deja de funcionar
        refreshTokenService.revocar(refreshToken);

        String nuevoToken =
                jwtService.generarToken(usuario);

        String nuevoRefreshToken =
                refreshTokenService.crear(usuario);

        return new AuthResponse(
                nuevoToken,
                nuevoRefreshToken,
                "Bearer",
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().name()
        );
    }
    public void logout(String refreshToken) {

        refreshTokenService.revocar(refreshToken);
    }
}