package com.lapuja.api.dto.dashboard;

public class DashboardGraficaItemResponse {

    private String etiqueta;
    private Double valor;

    public DashboardGraficaItemResponse() {
    }

    public DashboardGraficaItemResponse(String etiqueta, Double valor) {
        this.etiqueta = etiqueta;
        this.valor = valor;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public Double getValor() {
        return valor;
    }
}