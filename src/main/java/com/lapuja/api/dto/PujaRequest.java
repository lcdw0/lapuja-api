package com.lapuja.api.dto;

public class PujaRequest {

    private Long usuarioId;
    private Long subastaId;
    private Double monto;

    public PujaRequest() {
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getSubastaId() {
        return subastaId;
    }

    public void setSubastaId(Long subastaId) {
        this.subastaId = subastaId;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }
}