package com.lapuja.api.service;

import com.lapuja.api.dto.dashboard.DashboardResumenResponse;
import com.lapuja.api.repository.MetodoPagoRepository;
import com.lapuja.api.repository.PujaRepository;
import com.lapuja.api.repository.SubastaRepository;
import com.lapuja.api.repository.WalletMovimientoRepository;
import org.springframework.stereotype.Service;

import com.lapuja.api.dto.dashboard.DashboardGraficaItemResponse;
import com.lapuja.api.dto.dashboard.DashboardGraficasResponse;
import com.lapuja.api.entity.Subasta;
import com.lapuja.api.entity.WalletMovimiento;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.lapuja.api.dto.dashboard.DashboardActividadResponse;
import java.util.Comparator;

@Service
public class DashboardService {

    private final SubastaRepository subastaRepository;
    private final PujaRepository pujaRepository;
    private final WalletMovimientoRepository walletMovimientoRepository;
    private final MetodoPagoRepository metodoPagoRepository;

    public DashboardService(
            SubastaRepository subastaRepository,
            PujaRepository pujaRepository,
            WalletMovimientoRepository walletMovimientoRepository,
            MetodoPagoRepository metodoPagoRepository
    ) {
        this.subastaRepository = subastaRepository;
        this.pujaRepository = pujaRepository;
        this.walletMovimientoRepository = walletMovimientoRepository;
        this.metodoPagoRepository = metodoPagoRepository;
    }

    public DashboardResumenResponse obtenerResumenUsuario(Long usuarioId) {

        Long totalSubastasCreadas = subastaRepository.countByUsuarioId(usuarioId);

        Long totalPujasRealizadas = pujaRepository.countByUsuarioId(usuarioId);

        Long subastasGanadas = subastaRepository.countByGanadorIdAndEstado(usuarioId, "FINALIZADA");

        Long subastasPerdidas = pujaRepository.countSubastasPerdidasPorUsuario(usuarioId);

        Long subastasVendidas = subastaRepository
                .countByUsuarioIdAndEstadoAndGanadorIdIsNotNull(usuarioId, "FINALIZADA");

        Long subastasCanceladas = subastaRepository.countByUsuarioIdAndEstado(usuarioId, "CANCELADA");

        Double dineroGastado = walletMovimientoRepository.sumarMontoPorTipo(usuarioId, "PAGO_FINAL");

        Double dineroGanado = walletMovimientoRepository.sumarMontoPorTipo(usuarioId, "VENTA");

        Double totalRecargado = walletMovimientoRepository.sumarMontoPorTipo(usuarioId, "RECARGA");

        Double totalRetirado = walletMovimientoRepository.sumarMontoPorTipo(usuarioId, "RETIRO");

        Long tarjetasRegistradas = metodoPagoRepository.countByUsuarioId(usuarioId);

        Double porcentajeVictorias = calcularPorcentajeVictorias(subastasGanadas, subastasGanadas + subastasPerdidas);

        return new DashboardResumenResponse(
                totalSubastasCreadas,
                totalPujasRealizadas,
                subastasGanadas,
                subastasPerdidas,
                subastasVendidas,
                subastasCanceladas,
                dineroGastado,
                dineroGanado,
                totalRecargado,
                totalRetirado,
                tarjetasRegistradas,
                porcentajeVictorias
        );
    }

    public DashboardGraficasResponse obtenerGraficasUsuario(Long usuarioId) {

        LocalDate hoy = LocalDate.now();

        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

        LocalDateTime inicioMesFecha = inicioMes.atStartOfDay();
        LocalDateTime finMesFecha = finMes.atTime(23, 59, 59);

        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = hoy.with(DayOfWeek.SUNDAY);

        LocalDateTime inicioSemanaFecha = inicioSemana.atStartOfDay();
        LocalDateTime finSemanaFecha = finSemana.atTime(23, 59, 59);

        List<DashboardGraficaItemResponse> graficaCompras = construirGraficaMovimientosMensual(
                usuarioId,
                "PAGO_FINAL",
                inicioMesFecha,
                finMesFecha
        );

        List<DashboardGraficaItemResponse> graficaVentas = construirGraficaMovimientosMensual(
                usuarioId,
                "VENTA",
                inicioMesFecha,
                finMesFecha
        );

        List<DashboardGraficaItemResponse> graficaSemanal = construirGraficaSemanalSubastas(
                usuarioId,
                inicioSemanaFecha,
                finSemanaFecha
        );

        List<DashboardGraficaItemResponse> resumenMensual = construirResumenMensual(
                usuarioId,
                inicioMesFecha,
                finMesFecha
        );

        return new DashboardGraficasResponse(
                graficaCompras,
                graficaVentas,
                graficaSemanal,
                resumenMensual
        );
    }

    private List<DashboardGraficaItemResponse> construirGraficaMovimientosMensual(
            Long usuarioId,
            String tipo,
            LocalDateTime inicio,
            LocalDateTime fin
    ) {
        List<WalletMovimiento> movimientos = walletMovimientoRepository
                .findByUsuarioIdAndTipoAndFechaBetween(usuarioId, tipo, inicio, fin);

        List<DashboardGraficaItemResponse> datos = new ArrayList<>();

        LocalDate fechaInicio = inicio.toLocalDate();
        LocalDate fechaFin = fin.toLocalDate();

        LocalDate fechaActual = fechaInicio;

        while (!fechaActual.isAfter(fechaFin)) {
            double totalDia = 0.0;

            for (WalletMovimiento movimiento : movimientos) {
                if (movimiento.getFecha() != null && movimiento.getFecha().toLocalDate().equals(fechaActual)) {
                    totalDia += movimiento.getMonto();
                }
            }

            datos.add(new DashboardGraficaItemResponse(
                    String.valueOf(fechaActual.getDayOfMonth()),
                    totalDia
            ));

            fechaActual = fechaActual.plusDays(1);
        }

        return datos;
    }

    private List<DashboardGraficaItemResponse> construirGraficaSemanalSubastas(
            Long usuarioId,
            LocalDateTime inicio,
            LocalDateTime fin
    ) {
        List<Subasta> subastas = subastaRepository
                .findByUsuarioIdAndFechaCreacionBetweenOrderByFechaCreacionAsc(usuarioId, inicio, fin);

        List<DashboardGraficaItemResponse> datos = new ArrayList<>();

        LocalDate fechaInicio = inicio.toLocalDate();
        LocalDate fechaFin = fin.toLocalDate();

        LocalDate fechaActual = fechaInicio;

        Locale locale = new Locale("es", "ES");

        while (!fechaActual.isAfter(fechaFin)) {
            double totalDia = 0.0;

            for (Subasta subasta : subastas) {
                if (subasta.getFechaCreacion() != null && subasta.getFechaCreacion().toLocalDate().equals(fechaActual)) {
                    totalDia++;
                }
            }

            String dia = fechaActual.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, locale)
                    .replace(".", "");

            datos.add(new DashboardGraficaItemResponse(
                    dia,
                    totalDia
            ));

            fechaActual = fechaActual.plusDays(1);
        }

        return datos;
    }

    private List<DashboardGraficaItemResponse> construirResumenMensual(
            Long usuarioId,
            LocalDateTime inicio,
            LocalDateTime fin
    ) {
        Double compras = sumarMovimientosEnRango(usuarioId, "PAGO_FINAL", inicio, fin);
        Double ventas = sumarMovimientosEnRango(usuarioId, "VENTA", inicio, fin);
        Double recargas = sumarMovimientosEnRango(usuarioId, "RECARGA", inicio, fin);
        Double retiros = sumarMovimientosEnRango(usuarioId, "RETIRO", inicio, fin);

        List<DashboardGraficaItemResponse> resumen = new ArrayList<>();

        resumen.add(new DashboardGraficaItemResponse("Compras", compras));
        resumen.add(new DashboardGraficaItemResponse("Ventas", ventas));
        resumen.add(new DashboardGraficaItemResponse("Recargas", recargas));
        resumen.add(new DashboardGraficaItemResponse("Retiros", retiros));

        return resumen;
    }

    private Double sumarMovimientosEnRango(
            Long usuarioId,
            String tipo,
            LocalDateTime inicio,
            LocalDateTime fin
    ) {
        List<WalletMovimiento> movimientos = walletMovimientoRepository
                .findByUsuarioIdAndTipoAndFechaBetween(usuarioId, tipo, inicio, fin);

        double total = 0.0;

        for (WalletMovimiento movimiento : movimientos) {
            if (movimiento.getMonto() != null) {
                total += movimiento.getMonto();
            }
        }

        return total;
    }

    private Double calcularPorcentajeVictorias(Long ganadas, Long totalFinalizadasParticipadas) {
        if (totalFinalizadasParticipadas == null || totalFinalizadasParticipadas == 0) {
            return 0.0;
        }

        double porcentaje = (ganadas * 100.0) / totalFinalizadasParticipadas;

        return Math.round(porcentaje * 100.0) / 100.0;
    }

    public List<DashboardActividadResponse> obtenerActividadUsuario(Long usuarioId) {

        List<DashboardActividadResponse> actividad = new ArrayList<>();

        List<WalletMovimiento> movimientos = walletMovimientoRepository
                .findTop5ByUsuarioIdOrderByFechaDesc(usuarioId);

        for (WalletMovimiento movimiento : movimientos) {
            actividad.add(new DashboardActividadResponse(
                    movimiento.getTipo(),
                    construirTituloMovimiento(movimiento.getTipo()),
                    movimiento.getDescripcion(),
                    movimiento.getMonto(),
                    movimiento.getFecha()
            ));
        }

        List<Subasta> subastasCreadas = subastaRepository
                .findTop5ByUsuarioIdOrderByFechaCreacionDesc(usuarioId);

        for (Subasta subasta : subastasCreadas) {
            actividad.add(new DashboardActividadResponse(
                    "SUBASTA",
                    construirTituloSubasta(subasta),
                    subasta.getNombre(),
                    subasta.getPrecioActual(),
                    subasta.getFechaCreacion()
            ));
        }

        List<Subasta> subastasGanadas = subastaRepository
                .findTop5ByGanadorIdOrderByFechaFinDesc(usuarioId);

        for (Subasta subasta : subastasGanadas) {
            actividad.add(new DashboardActividadResponse(
                    "GANADA",
                    "Ganaste una subasta",
                    subasta.getNombre(),
                    subasta.getPrecioActual(),
                    subasta.getFechaFin()
            ));
        }

        actividad.sort(Comparator.comparing(DashboardActividadResponse::getFecha).reversed());

        if (actividad.size() > 10) {
            return actividad.subList(0, 10);
        }

        return actividad;
    }

    private String construirTituloMovimiento(String tipo) {
        if (tipo == null) {
            return "Movimiento de wallet";
        }

        return switch (tipo) {
            case "RECARGA" -> "Recargaste saldo";
            case "REEMBOLSO" -> "Recibiste un reembolso";
            case "PUJA" -> "Realizaste una puja";
            case "VENTA" -> "Recibiste una venta";
            case "PAGO_FINAL" -> "Pagaste una subasta";
            case "RETIRO" -> "Retiraste saldo";
            default -> "Movimiento de wallet";
        };
    }

    private String construirTituloSubasta(Subasta subasta) {
        if (subasta.getEstado() == null) {
            return "Creaste una subasta";
        }

        return switch (subasta.getEstado()) {
            case "ACTIVA" -> "Creaste una subasta activa";
            case "FINALIZADA" -> "Finalizó una subasta";
            case "CANCELADA" -> "Cancelaste una subasta";
            default -> "Actualizaste una subasta";
        };
    }
}