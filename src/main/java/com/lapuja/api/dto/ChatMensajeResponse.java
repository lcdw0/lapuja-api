package com.lapuja.api.dto;

import java.time.LocalDateTime;

public class ChatMensajeResponse {

    private Long id;
    private Long conversacionId;
    private Long emisorId;
    private String emisorNombre;
    private String contenido;
    private String tipoMensaje;
    private String imagenUrl;
    private Boolean leido;
    private Boolean eliminado;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaEdicion;

    public ChatMensajeResponse(Long id, Long conversacionId, Long emisorId, String emisorNombre,
                               String contenido, String tipoMensaje, String imagenUrl,
                               Boolean leido, Boolean eliminado,
                               LocalDateTime fechaEnvio, LocalDateTime fechaEdicion) {
        this.id = id;
        this.conversacionId = conversacionId;
        this.emisorId = emisorId;
        this.emisorNombre = emisorNombre;
        this.contenido = contenido;
        this.tipoMensaje = tipoMensaje;
        this.imagenUrl = imagenUrl;
        this.leido = leido;
        this.eliminado = eliminado;
        this.fechaEnvio = fechaEnvio;
        this.fechaEdicion = fechaEdicion;
    }

    public Long getId() {
        return id;
    }

    public Long getConversacionId() {
        return conversacionId;
    }

    public Long getEmisorId() {
        return emisorId;
    }

    public String getEmisorNombre() {
        return emisorNombre;
    }

    public String getContenido() {
        return contenido;
    }

    public String getTipoMensaje() {
        return tipoMensaje;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public Boolean getLeido() {
        return leido;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public LocalDateTime getFechaEdicion() {
        return fechaEdicion;
    }
}