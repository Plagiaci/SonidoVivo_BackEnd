package cl.duoc.SonidoVivo_BackEnd.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {



    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }




    @Bean
    public SecretKey jwtSecretKey(
            @Value("${app.jwt.secret}") String secret) {

        byte[] bytes =
                secret.getBytes(StandardCharsets.UTF_8);

        if (bytes.length < 32) {

            throw new IllegalArgumentException(
                    "JWT_SECRET debe tener al menos 32 bytes"
            );
        }

        return new SecretKeySpec(
                bytes,
                "HmacSHA256"
        );
    }




    @Bean
    public JwtEncoder jwtEncoder(
            SecretKey key) {

        return new NimbusJwtEncoder(
                new ImmutableSecret<>(key)
        );
    }


    // =========================================================
    // Validar JWT
    // =========================================================

    @Bean
    public JwtDecoder jwtDecoder(
            SecretKey key) {

        return NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }




    @Bean
    public JwtAuthenticationConverter
    jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter roles =
                new JwtGrantedAuthoritiesConverter();

        roles.setAuthoritiesClaimName("rol");

        roles.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                roles
        );

        return converter;
    }



    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter converter
    ) throws Exception {

        http

                // Trabajamos con JWT, no con sesión tradicional.
                .csrf(csrf -> csrf.disable())

                // Usa la configuración CORS de abajo.
                .cors(cors -> {
                })

                // Spring no guardará sesión en servidor.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth


                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/logout"
                        ).permitAll()




                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()


                        // =================================================
                        // CATÁLOGO PÚBLICO
                        // =================================================

                        // Cualquier persona puede consultar productos.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/productos/**"
                        ).permitAll()

                        // Cualquier persona puede consultar categorías.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/categorias/**"
                        ).permitAll()


                        // =================================================
                        // ADMINISTRACIÓN DE PRODUCTOS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/productos/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/productos/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/productos/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/productos/**"
                        ).hasRole("ADMINISTRADOR")


                        // =================================================
                        // ADMINISTRACIÓN DE CATEGORÍAS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/categorias/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/categorias/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/categorias/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/categorias/**"
                        ).hasRole("ADMINISTRADOR")




                        .requestMatchers(
                                "/api/v1/admin/usuarios/**"
                        ).hasRole("ADMINISTRADOR")


                        // =================================================
                        // COMPRAS DEL CLIENTE
                        // =================================================

                        // Cliente puede realizar una compra.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/compras"
                        ).hasRole("CLIENTE")


                        // IMPORTANTE:
                        // Esta regla debe estar ANTES de /compras/*
                        //
                        // Cliente puede consultar solamente
                        // sus propias compras.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/compras/mis-compras"
                        ).hasRole("CLIENTE")


                        // =================================================
                        // COMPRAS ADMIN / VENDEDOR
                        // =================================================

                        // Administrador y vendedor pueden
                        // consultar todas las compras.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/compras"
                        ).hasAnyRole(
                                "ADMINISTRADOR",
                                "VENDEDOR"
                        )


                        // Administrador y vendedor pueden
                        // consultar una compra específica.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/compras/*"
                        ).hasAnyRole(
                                "ADMINISTRADOR",
                                "VENDEDOR"
                        )


                        // Solamente administrador puede
                        // cambiar el estado de una compra.
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/compras/*/estado"
                        ).hasRole("ADMINISTRADOR")




                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/admin/dashboard/**"
                        ).hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated()
                )




                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        converter
                                )
                        )
                );

        return http.build();
    }




    @Bean
    public CorsConfigurationSource
    corsConfigurationSource() {

        CorsConfiguration config =
                new CorsConfiguration();

        // React con Vite
        config.setAllowedOrigins(
                List.of(
                        "http://localhost:5173"
                )
        );

        // Métodos permitidos desde React.
        config.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        // Headers que React puede enviar.
        config.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type"
                )
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );

        return source;
    }
}