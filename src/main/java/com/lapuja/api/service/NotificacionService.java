package com.lapuja.api.service;

import com.lapuja.api.entity.Notificacion;
import com.lapuja.api.repository.NotificacionRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public Notificacion crear(
            Long usuarioId,
            String titulo,
            String mensaje,
            String tipo,
            Long referenciaId,
            String pantallaDestino
    ) {
        if (usuarioId == null) {
            return null;
        }

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuarioId(usuarioId);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setTipo(tipo);
        notificacion.setReferenciaId(referenciaId);
        notificacion.setPantallaDestino(pantallaDestino);
        notificacion.setLeida(false);

        return notificacionRepository.save(notificacion);
    }
}