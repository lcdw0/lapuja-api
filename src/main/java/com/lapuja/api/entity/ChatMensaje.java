package com.lapuja.api.entity;

import com.lapuja.api.enums.TipoMensaje;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_mensajes")
public class ChatMensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversacion_id", nullable = false)
    private ChatConversacion conversacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emisor_id", nullable = false)
    private Usuario emisor;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMensaje tipoMensaje = TipoMensaje.TEXTO;

    @Column(columnDefinition = "TEXT")
    private String imagenUrl;

    @Column(nullable = false)
    private Boolean leido = false;

    @Column(nullable = false)
    private Boolean eliminado = false;

    private LocalDateTime fechaEnvio;

    private LocalDateTime fechaEdicion;

    @PrePersist
    public void prePersist() {
        if (fechaEnvio == null) {
            fechaEnvio = LocalDateTime.now();
        }

        if (tipoMensaje == null) {
            tipoMensaje = TipoMensaje.TEXTO;
        }

        if (leido == null) {
            leido = false;
        }

        if (eliminado == null) {
            eliminado = false;
        }
    }

    public ChatMensaje() {
    }

    public Long getId() {
        return id;
    }

    public ChatConversacion getConversacion() {
        return conversacion;
    }

    public void setConversacion(ChatConversacion conversacion) {
        this.conversacion = conversacion;
    }

    public Usuario getEmisor() {
        return emisor;
    }

    public void setEmisor(Usuario emisor) {
        this.emisor = emisor;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public TipoMensaje getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(TipoMensaje tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Boolean getLeido() {
        return leido;
    }

    public void setLeido(Boolean leido) {
        this.leido = leido;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public LocalDateTime getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(LocalDateTime fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }
}