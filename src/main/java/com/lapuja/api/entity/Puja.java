package com.lapuja.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pujas")
public class Puja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;
    private Long subastaId;
    private Double monto;
    private LocalDateTime fecha;

    public Puja() {
    }

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }

    public Long getId() {
        return id;
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

    public LocalDateTime getFecha() {
        return fecha;
    }
}