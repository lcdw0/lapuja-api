package com.lapuja.api.dto;

import com.lapuja.api.entity.Subasta;
import com.lapuja.api.entity.SubastaImagen;

import java.time.LocalDateTime;
import java.util.List;

public class SubastaDetalleResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private Double precioInicial;
    private Double precioActual;
    private String categoria;
    private String imagen;
    private String estado;
    private Integer ofertas;
    private String ganador;
    private Long ganadorId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaFin;
    private Long usuarioId;

    private List<SubastaImagen> imagenes;

    public SubastaDetalleResponse(Subasta subasta, List<SubastaImagen> imagenes) {
        this.id = subasta.getId();
        this.nombre = subasta.getNombre();
        this.descripcion = subasta.getDescripcion();
        this.precioInicial = subasta.getPrecioInicial();
        this.precioActual = subasta.getPrecioActual();
        this.categoria = subasta.getCategoria();
        this.imagen = subasta.getImagen();
        this.estado = subasta.getEstado();
        this.ofertas = subasta.getOfertas();
        this.ganador = subasta.getGanador();
        this.ganadorId = subasta.getGanadorId();
        this.fechaCreacion = subasta.getFechaCreacion();
        this.fechaFin = subasta.getFechaFin();
        this.usuarioId = subasta.getUsuarioId();
        this.imagenes = imagenes;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Double getPrecioInicial() {
        return precioInicial;
    }

    public Double getPrecioActual() {
        return precioActual;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getImagen() {
        return imagen;
    }

    public String getEstado() {
        return estado;
    }

    public Integer getOfertas() {
        return ofertas;
    }

    public String getGanador() {
        return ganador;
    }

    public Long getGanadorId() {
        return ganadorId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public List<SubastaImagen> getImagenes() {
        return imagenes;
    }
}