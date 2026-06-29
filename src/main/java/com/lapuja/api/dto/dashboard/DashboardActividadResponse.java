package com.lapuja.api.dto.dashboard;

import java.time.LocalDateTime;

public class DashboardActividadResponse {

    private String tipo;
    private String titulo;
    private String descripcion;
    private Double monto;
    private LocalDateTime fecha;

    public DashboardActividadResponse() {
    }

    public DashboardActividadResponse(
            String tipo,
            String titulo,
            String descripcion,
            Double monto,
            LocalDateTime fecha
    ) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.monto = monto;
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Double getMonto() {
        return monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}