package com.lapuja.api.dto.dashboard;

public class DashboardResumenResponse {

    private Long totalSubastasCreadas;
    private Long totalPujasRealizadas;
    private Long subastasGanadas;
    private Long subastasPerdidas;
    private Long subastasVendidas;
    private Long subastasCanceladas;
    private Double dineroGastado;
    private Double dineroGanado;
    private Double totalRecargado;
    private Double totalRetirado;
    private Long tarjetasRegistradas;
    private Double porcentajeVictorias;

    public DashboardResumenResponse() {
    }

    public DashboardResumenResponse(
            Long totalSubastasCreadas,
            Long totalPujasRealizadas,
            Long subastasGanadas,
            Long subastasPerdidas,
            Long subastasVendidas,
            Long subastasCanceladas,
            Double dineroGastado,
            Double dineroGanado,
            Double totalRecargado,
            Double totalRetirado,
            Long tarjetasRegistradas,
            Double porcentajeVictorias
    ) {
        this.totalSubastasCreadas = totalSubastasCreadas;
        this.totalPujasRealizadas = totalPujasRealizadas;
        this.subastasGanadas = subastasGanadas;
        this.subastasPerdidas = subastasPerdidas;
        this.subastasVendidas = subastasVendidas;
        this.subastasCanceladas = subastasCanceladas;
        this.dineroGastado = dineroGastado;
        this.dineroGanado = dineroGanado;
        this.totalRecargado = totalRecargado;
        this.totalRetirado = totalRetirado;
        this.tarjetasRegistradas = tarjetasRegistradas;
        this.porcentajeVictorias = porcentajeVictorias;
    }

    public Long getTotalSubastasCreadas() { return totalSubastasCreadas; }
    public Long getTotalPujasRealizadas() { return totalPujasRealizadas; }
    public Long getSubastasGanadas() { return subastasGanadas; }
    public Long getSubastasPerdidas() { return subastasPerdidas; }
    public Long getSubastasVendidas() { return subastasVendidas; }
    public Long getSubastasCanceladas() { return subastasCanceladas; }
    public Double getDineroGastado() { return dineroGastado; }
    public Double getDineroGanado() { return dineroGanado; }
    public Double getTotalRecargado() { return totalRecargado; }
    public Double getTotalRetirado() { return totalRetirado; }
    public Long getTarjetasRegistradas() { return tarjetasRegistradas; }
    public Double getPorcentajeVictorias() { return porcentajeVictorias; }
}