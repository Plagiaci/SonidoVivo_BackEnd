package cl.duoc.SonidoVivo_BackEnd.service;

import cl.duoc.SonidoVivo_BackEnd.dto.UsuarioAdminRequest;
import cl.duoc.SonidoVivo_BackEnd.dto.UsuarioResponse;
import cl.duoc.SonidoVivo_BackEnd.model.Usuario;
import cl.duoc.SonidoVivo_BackEnd.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponse> listar() {

        return repository.findAll()
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    public UsuarioResponse buscarPorId(Long id) {

        Usuario usuario = buscarEntidad(id);

        return convertirRespuesta(usuario);
    }

    public UsuarioResponse crear(UsuarioAdminRequest datos) {

        String email = datos.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (repository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya está registrado"
            );
        }

        if (datos.getPassword() == null ||
                datos.getPassword().length() < 8) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos 8 caracteres"
            );
        }

        Usuario usuario = new Usuario(
                null,
                datos.getNombre().trim(),
                email,
                passwordEncoder.encode(datos.getPassword()),
                datos.getRol()
        );

        return convertirRespuesta(
                repository.save(usuario)
        );
    }

    public UsuarioResponse actualizar(
            Long id,
            UsuarioAdminRequest datos) {

        Usuario usuario = buscarEntidad(id);

        String emailNuevo = datos.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (!usuario.getEmail().equalsIgnoreCase(emailNuevo)
                && repository.existsByEmail(emailNuevo)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya está registrado"
            );
        }

        usuario.setNombre(datos.getNombre().trim());
        usuario.setEmail(emailNuevo);
        usuario.setRol(datos.getRol());

        // Solo cambia la contraseña si el admin escribió una nueva
        if (datos.getPassword() != null &&
                !datos.getPassword().isBlank()) {

            if (datos.getPassword().length() < 8) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La contraseña debe tener al menos 8 caracteres"
                );
            }

            usuario.setPassword(
                    passwordEncoder.encode(datos.getPassword())
            );
        }

        return convertirRespuesta(
                repository.save(usuario)
        );
    }

    public void eliminar(Long id) {

        Usuario usuario = buscarEntidad(id);

        repository.delete(usuario);
    }

    private Usuario buscarEntidad(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario no encontrado"
                        )
                );
    }

    private UsuarioResponse convertirRespuesta(
            Usuario usuario) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol()
        );
    }
}