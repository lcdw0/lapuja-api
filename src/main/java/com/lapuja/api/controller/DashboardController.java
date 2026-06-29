package com.lapuja.api.controller;

import com.lapuja.api.dto.dashboard.DashboardResumenResponse;
import com.lapuja.api.service.DashboardService;
import org.springframework.web.bind.annotation.*;
import com.lapuja.api.dto.dashboard.DashboardGraficasResponse;
import com.lapuja.api.dto.dashboard.DashboardActividadResponse;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumen/{usuarioId}")
    public DashboardResumenResponse obtenerResumenUsuario(@PathVariable Long usuarioId) {
        return dashboardService.obtenerResumenUsuario(usuarioId);
    }

    @GetMapping("/graficas/{usuarioId}")
    public DashboardGraficasResponse obtenerGraficasUsuario(@PathVariable Long usuarioId) {
        return dashboardService.obtenerGraficasUsuario(usuarioId);
    }

    @GetMapping("/actividad/{usuarioId}")
    public List<DashboardActividadResponse> obtenerActividadUsuario(@PathVariable Long usuarioId) {
        return dashboardService.obtenerActividadUsuario(usuarioId);
    }
}