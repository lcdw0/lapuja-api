package com.lapuja.api.dto;

import java.time.LocalDateTime;

public class ChatConversacionResponse {

    private Long id;
    private Long subastaId;
    private String subastaTitulo;
    private Long compradorId;
    private String compradorNombre;
    private String compradorFotoPerfil;
    private Long vendedorId;
    private String vendedorNombre;
    private String vendedorFotoPerfil;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private String ultimoMensaje;
    private LocalDateTime fechaUltimoMensaje;
    private Long mensajesNoLeidos;
    private Boolean otroUsuarioEnLinea;
    private LocalDateTime otroUsuarioUltimaActividad;
    private String otroUsuarioFotoPerfil;

    public ChatConversacionResponse(
            Long id,
            Long subastaId,
            String subastaTitulo,
            Long compradorId,
            String compradorNombre,
            String compradorFotoPerfil,
            Long vendedorId,
            String vendedorNombre,
            String vendedorFotoPerfil,
            Boolean activo,
            LocalDateTime fechaCreacion,
            String ultimoMensaje,
            LocalDateTime fechaUltimoMensaje,
            Long mensajesNoLeidos,
            Boolean otroUsuarioEnLinea,
            LocalDateTime otroUsuarioUltimaActividad,
            String otroUsuarioFotoPerfil
    ) {
        this.id = id;
        this.subastaId = subastaId;
        this.subastaTitulo = subastaTitulo;
        this.compradorId = compradorId;
        this.compradorNombre = compradorNombre;
        this.compradorFotoPerfil = compradorFotoPerfil;
        this.vendedorId = vendedorId;
        this.vendedorNombre = vendedorNombre;
        this.vendedorFotoPerfil = vendedorFotoPerfil;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.ultimoMensaje = ultimoMensaje;
        this.fechaUltimoMensaje = fechaUltimoMensaje;
        this.mensajesNoLeidos = mensajesNoLeidos;
        this.otroUsuarioEnLinea = otroUsuarioEnLinea;
        this.otroUsuarioUltimaActividad = otroUsuarioUltimaActividad;
        this.otroUsuarioFotoPerfil = otroUsuarioFotoPerfil;
    }

    public Long getId() { return id; }
    public Long getSubastaId() { return subastaId; }
    public String getSubastaTitulo() { return subastaTitulo; }
    public Long getCompradorId() { return compradorId; }
    public String getCompradorNombre() { return compradorNombre; }
    public String getCompradorFotoPerfil() { return compradorFotoPerfil; }
    public Long getVendedorId() { return vendedorId; }
    public String getVendedorNombre() { return vendedorNombre; }
    public String getVendedorFotoPerfil() { return vendedorFotoPerfil; }
    public Boolean getActivo() { return activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public String getUltimoMensaje() { return ultimoMensaje; }
    public LocalDateTime getFechaUltimoMensaje() { return fechaUltimoMensaje; }
    public Long getMensajesNoLeidos() { return mensajesNoLeidos; }
    public Boolean getOtroUsuarioEnLinea() { return otroUsuarioEnLinea; }
    public LocalDateTime getOtroUsuarioUltimaActividad() { return otroUsuarioUltimaActividad; }
    public String getOtroUsuarioFotoPerfil() { return otroUsuarioFotoPerfil; }
}