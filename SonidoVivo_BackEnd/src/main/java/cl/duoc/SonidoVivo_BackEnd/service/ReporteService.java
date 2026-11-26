package cl.duoc.SonidoVivo_BackEnd.service;

import cl.duoc.SonidoVivo_BackEnd.dto.DashboardResponse;
import cl.duoc.SonidoVivo_BackEnd.dto.ProductoVendidoResponse;
import cl.duoc.SonidoVivo_BackEnd.model.Compra;
import cl.duoc.SonidoVivo_BackEnd.model.DetalleCompra;
import cl.duoc.SonidoVivo_BackEnd.model.EstadoCompra;
import cl.duoc.SonidoVivo_BackEnd.model.PeriodoReporte;
import cl.duoc.SonidoVivo_BackEnd.repository.CompraRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    private final CompraRepository compraRepository;

    public ReporteService(
            CompraRepository compraRepository) {

        this.compraRepository = compraRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse obtenerDashboard(
            PeriodoReporte periodo) {

        LocalDateTime fin =
                LocalDateTime.now();

        LocalDateTime inicio =
                calcularInicio(periodo);

        List<Compra> compras =
                compraRepository
                        .findByFechaBetweenOrderByFechaDesc(
                                inicio,
                                fin
                        )
                        .stream()

                        // Las compras canceladas
                        // no cuentan como ventas.
                        .filter(compra ->
                                compra.getEstado()
                                        != EstadoCompra.CANCELADA
                        )
                        .toList();

        long totalOperaciones =
                compras.size();

        long totalVendido =
                compras.stream()
                        .mapToLong(Compra::getTotal)
                        .sum();

        long productosVendidos = 0;

        Map<String, Integer> cantidades =
                new HashMap<>();

        Map<String, String> nombres =
                new HashMap<>();

        for (Compra compra : compras) {

            for (DetalleCompra detalle :
                    compra.getDetalles()) {

                productosVendidos +=
                        detalle.getCantidad();

                String productoId =
                        detalle.getProducto().getId();

                nombres.put(
                        productoId,
                        detalle.getProducto().getNombre()
                );

                cantidades.merge(
                        productoId,
                        detalle.getCantidad(),
                        Integer::sum
                );
            }
        }

        List<ProductoVendidoResponse>
                productosMasVendidos = cantidades
                .entrySet()
                .stream()

                .sorted(
                        Map.Entry
                                .<String, Integer>
                                        comparingByValue()
                                .reversed()
                )

                // Top 5
                .limit(5)

                .map(entry ->
                        new ProductoVendidoResponse(
                                entry.getKey(),
                                nombres.get(entry.getKey()),
                                entry.getValue()
                        )
                )

                .toList();

        return new DashboardResponse(
                periodo.name(),
                totalOperaciones,
                totalVendido,
                productosVendidos,
                productosMasVendidos
        );
    }

    private LocalDateTime calcularInicio(
            PeriodoReporte periodo) {

        LocalDate hoy =
                LocalDate.now();

        return switch (periodo) {

            case SEMANA ->
                    hoy.with(
                            DayOfWeek.MONDAY
                    ).atStartOfDay();

            case MES ->
                    hoy.withDayOfMonth(1)
                            .atStartOfDay();

            case ANIO ->
                    hoy.withDayOfYear(1)
                            .atStartOfDay();
        };
    }
}