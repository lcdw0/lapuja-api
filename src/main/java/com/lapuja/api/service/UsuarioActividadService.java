package com.lapuja.api.service;

import com.lapuja.api.entity.Usuario;
import com.lapuja.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsuarioActividadService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioActividadService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void actualizarActividad(Long usuarioId) {
        if (usuarioId == null) return;

        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            usuario.setUltimaActividad(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });
    }

    public boolean estaEnLinea(Usuario usuario) {
        if (usuario == null || usuario.getUltimaActividad() == null) {
            return false;
        }

        return usuario.getUltimaActividad()
                .isAfter(LocalDateTime.now().minusMinutes(2));
    }
}