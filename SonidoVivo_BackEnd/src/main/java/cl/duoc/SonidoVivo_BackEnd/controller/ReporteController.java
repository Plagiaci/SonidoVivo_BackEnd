package cl.duoc.SonidoVivo_BackEnd.controller;

import cl.duoc.SonidoVivo_BackEnd.dto.DashboardResponse;
import cl.duoc.SonidoVivo_BackEnd.model.PeriodoReporte;
import cl.duoc.SonidoVivo_BackEnd.service.ReporteService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@SecurityRequirement(name = "bearerAuth")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(
            ReporteService reporteService) {

        this.reporteService = reporteService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse>
    obtenerDashboard(

            @RequestParam(
                    defaultValue = "MES"
            )
            PeriodoReporte periodo) {

        return ResponseEntity.ok(
                reporteService.obtenerDashboard(
                        periodo
                )
        );
    }
}