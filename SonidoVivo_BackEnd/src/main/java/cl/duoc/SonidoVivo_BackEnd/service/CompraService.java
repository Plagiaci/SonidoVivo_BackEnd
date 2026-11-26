package cl.duoc.SonidoVivo_BackEnd.service;

import cl.duoc.SonidoVivo_BackEnd.dto.*;
import cl.duoc.SonidoVivo_BackEnd.model.*;
import cl.duoc.SonidoVivo_BackEnd.repository.CompraRepository;
import cl.duoc.SonidoVivo_BackEnd.repository.ProductoRepository;
import cl.duoc.SonidoVivo_BackEnd.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public CompraService(
            CompraRepository compraRepository,
            ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository) {

        this.compraRepository = compraRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public CompraResponse realizarCompra(
            String email,
            CompraRequest request) {

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario no encontrado"
                        )
                );

        Compra compra = new Compra();

        compra.setFecha(LocalDateTime.now());
        compra.setEstado(EstadoCompra.CONFIRMADA);
        compra.setUsuario(usuario);

        List<DetalleCompra> detalles =
                new ArrayList<>();

        int total = 0;

        for (CompraItemRequest item : request.getItems()) {

            Producto producto = productoRepository
                    .findById(item.getProductoId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Producto no encontrado: "
                                            + item.getProductoId()
                            )
                    );

            if (producto.getStock() < item.getCantidad()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Stock insuficiente para "
                                + producto.getNombre()
                );
            }

            int precioUnitario = producto.getPrecio();

            int subtotal =
                    precioUnitario * item.getCantidad();

            total += subtotal;

            // Descontar stock
            producto.setStock(
                    producto.getStock()
                            - item.getCantidad()
            );

            productoRepository.save(producto);

            DetalleCompra detalle =
                    new DetalleCompra();

            detalle.setCompra(compra);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotal);

            detalles.add(detalle);
        }

        compra.setTotal(total);
        compra.setDetalles(detalles);

        Compra compraGuardada =
                compraRepository.save(compra);

        return convertirRespuesta(compraGuardada);
    }

    @Transactional(readOnly = true)
    public List<CompraResponse> misCompras(
            String email) {

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario no encontrado"
                        )
                );

        return compraRepository
                .findByUsuarioIdOrderByFechaDesc(
                        usuario.getId()
                )
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    private CompraResponse convertirRespuesta(
            Compra compra) {

        List<DetalleCompraResponse> detalles =
                compra.getDetalles()
                        .stream()
                        .map(detalle ->
                                new DetalleCompraResponse(
                                        detalle.getProducto().getId(),
                                        detalle.getProducto().getNombre(),
                                        detalle.getCantidad(),
                                        detalle.getPrecioUnitario(),
                                        detalle.getSubtotal()
                                )
                        )
                        .toList();

        return new CompraResponse(
                compra.getId(),
                compra.getFecha(),
                compra.getEstado(),
                compra.getTotal(),
                detalles
        );
    }
    @Transactional(readOnly = true)
    public List<CompraAdminResponse> listarTodas() {

        return compraRepository
                .findAllByOrderByFechaDesc()
                .stream()
                .map(this::convertirRespuestaAdmin)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompraAdminResponse buscarPorId(Long id) {

        Compra compra = compraRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Compra no encontrada"
                        )
                );

        return convertirRespuestaAdmin(compra);
    }

    @Transactional
    public CompraAdminResponse cambiarEstado(
            Long id,
            EstadoCompra nuevoEstado) {

        Compra compra = compraRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Compra no encontrada"
                        )
                );

        compra.setEstado(nuevoEstado);

        Compra actualizada =
                compraRepository.save(compra);

        return convertirRespuestaAdmin(actualizada);
    }

    private CompraAdminResponse convertirRespuestaAdmin(
            Compra compra) {

        List<DetalleCompraResponse> detalles =
                compra.getDetalles()
                        .stream()
                        .map(detalle ->
                                new DetalleCompraResponse(
                                        detalle.getProducto().getId(),
                                        detalle.getProducto().getNombre(),
                                        detalle.getCantidad(),
                                        detalle.getPrecioUnitario(),
                                        detalle.getSubtotal()
                                )
                        )
                        .toList();

        return new CompraAdminResponse(
                compra.getId(),
                compra.getFecha(),
                compra.getEstado(),
                compra.getTotal(),

                compra.getUsuario().getId(),
                compra.getUsuario().getNombre(),
                compra.getUsuario().getEmail(),

                detalles
        );
    }
}