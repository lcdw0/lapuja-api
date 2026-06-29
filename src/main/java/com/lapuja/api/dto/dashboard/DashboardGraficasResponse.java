package com.lapuja.api.dto.dashboard;

import java.util.List;

public class DashboardGraficasResponse {

    private List<DashboardGraficaItemResponse> graficaCompras;
    private List<DashboardGraficaItemResponse> graficaVentas;
    private List<DashboardGraficaItemResponse> graficaSemanal;
    private List<DashboardGraficaItemResponse> resumenMensual;

    public DashboardGraficasResponse() {
    }

    public DashboardGraficasResponse(
            List<DashboardGraficaItemResponse> graficaCompras,
            List<DashboardGraficaItemResponse> graficaVentas,
            List<DashboardGraficaItemResponse> graficaSemanal,
            List<DashboardGraficaItemResponse> resumenMensual
    ) {
        this.graficaCompras = graficaCompras;
        this.graficaVentas = graficaVentas;
        this.graficaSemanal = graficaSemanal;
        this.resumenMensual = resumenMensual;
    }

    public List<DashboardGraficaItemResponse> getGraficaCompras() {
        return graficaCompras;
    }

    public List<DashboardGraficaItemResponse> getGraficaVentas() {
        return graficaVentas;
    }

    public List<DashboardGraficaItemResponse> getGraficaSemanal() {
        return graficaSemanal;
    }

    public List<DashboardGraficaItemResponse> getResumenMensual() {
        return resumenMensual;
    }
}