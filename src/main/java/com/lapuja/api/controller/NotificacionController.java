package com.lapuja.api.controller;

import com.lapuja.api.entity.Notificacion;
import com.lapuja.api.entity.Usuario;
import com.lapuja.api.repository.NotificacionRepository;
import com.lapuja.api.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacionController(
            NotificacionRepository notificacionRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/usuario/{usuarioId}")
    public Object listarPorUsuario(@PathVariable Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario == null) {
            return Map.of("ok", false, "mensaje", "Usuario no encontrado");
        }

        List<Notificacion> notificaciones =
                notificacionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);

        return Map.of(
                "ok", true,
                "mensaje", "Notificaciones encontradas",
                "notificaciones", notificaciones
        );
    }

    @GetMapping("/usuario/{usuarioId}/no-leidas")
    public Object listarNoLeidas(@PathVariable Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario == null) {
            return Map.of("ok", false, "mensaje", "Usuario no encontrado");
        }

        List<Notificacion> notificaciones =
                notificacionRepository.findByUsuarioIdAndLeidaFalseOrderByFechaDesc(usuarioId);

        return Map.of(
                "ok", true,
                "mensaje", "Notificaciones no leídas encontradas",
                "notificaciones", notificaciones
        );
    }

    @GetMapping("/usuario/{usuarioId}/contador")
    public Object contarNoLeidas(@PathVariable Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario == null) {
            return Map.of("ok", false, "mensaje", "Usuario no encontrado");
        }

        long total = notificacionRepository.countByUsuarioIdAndLeidaFalse(usuarioId);

        return Map.of(
                "ok", true,
                "noLeidas", total
        );
    }

    @PutMapping("/{id}/leer")
    public Object marcarComoLeida(@PathVariable Long id) {

        Notificacion notificacion = notificacionRepository.findById(id).orElse(null);

        if (notificacion == null) {
            return Map.of("ok", false, "mensaje", "Notificación no encontrada");
        }

        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);

        return Map.of(
                "ok", true,
                "mensaje", "Notificación marcada como leída",
                "notificacion", notificacion
        );
    }

    @PutMapping("/usuario/{usuarioId}/leer-todas")
    public Object marcarTodasComoLeidas(@PathVariable Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario == null) {
            return Map.of("ok", false, "mensaje", "Usuario no encontrado");
        }

        List<Notificacion> notificaciones =
                notificacionRepository.findByUsuarioIdAndLeidaFalseOrderByFechaDesc(usuarioId);

        for (Notificacion notificacion : notificaciones) {
            notificacion.setLeida(true);
        }

        notificacionRepository.saveAll(notificaciones);

        return Map.of(
                "ok", true,
                "mensaje", "Todas las notificaciones fueron marcadas como leídas",
                "totalActualizadas", notificaciones.size()
        );
    }

    @DeleteMapping("/{id}")
    public Object eliminar(@PathVariable Long id) {

        Notificacion notificacion = notificacionRepository.findById(id).orElse(null);

        if (notificacion == null) {
            return Map.of("ok", false, "mensaje", "Notificación no encontrada");
        }

        notificacionRepository.delete(notificacion);

        return Map.of(
                "ok", true,
                "mensaje", "Notificación eliminada correctamente"
        );
    }

    @PostMapping("/crear-prueba")
    public Object crearNotificacionPrueba(@RequestBody Notificacion request) {

        if (request.getUsuarioId() == null) {
            return Map.of("ok", false, "mensaje", "El usuarioId es obligatorio");
        }

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).orElse(null);

        if (usuario == null) {
            return Map.of("ok", false, "mensaje", "Usuario no encontrado");
        }

        if (request.getTitulo() == null || request.getTitulo().isBlank()) {
            return Map.of("ok", false, "mensaje", "El título es obligatorio");
        }

        if (request.getMensaje() == null || request.getMensaje().isBlank()) {
            return Map.of("ok", false, "mensaje", "El mensaje es obligatorio");
        }

        if (request.getTipo() == null || request.getTipo().isBlank()) {
            return Map.of("ok", false, "mensaje", "El tipo es obligatorio");
        }

        if (request.getPantallaDestino() == null || request.getPantallaDestino().isBlank()) {
            return Map.of("ok", false, "mensaje", "La pantalla destino es obligatoria");
        }

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuarioId(request.getUsuarioId());
        notificacion.setTitulo(request.getTitulo());
        notificacion.setMensaje(request.getMensaje());
        notificacion.setTipo(request.getTipo());
        notificacion.setReferenciaId(request.getReferenciaId());
        notificacion.setPantallaDestino(request.getPantallaDestino());
        notificacion.setLeida(false);

        Notificacion guardada = notificacionRepository.save(notificacion);

        return Map.of(
                "ok", true,
                "mensaje", "Notificación de prueba creada",
                "notificacion", guardada
        );
    }
}