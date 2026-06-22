package com.lapuja.api.controller;

import com.lapuja.api.entity.Usuario;
import com.lapuja.api.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/registro")
    public Object registrar(@RequestBody Usuario usuario) {
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            return Map.of(
                    "ok", false,
                    "mensaje", "El correo ya está registrado"
            );
        }

        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        return Map.of(
                "ok", true,
                "mensaje", "Usuario registrado correctamente",
                "id", nuevoUsuario.getId(),
                "nombre", nuevoUsuario.getNombre(),
                "correo", nuevoUsuario.getCorreo()
        );
    }

    @PostMapping("/login")
    public Object login(@RequestBody Usuario usuario) {
        return usuarioRepository.findByCorreo(usuario.getCorreo())
                .map(usuarioEncontrado -> {
                    if (!usuarioEncontrado.getPassword().equals(usuario.getPassword())) {
                        return Map.of(
                                "ok", false,
                                "mensaje", "Contraseña incorrecta"
                        );
                    }

                    return Map.of(
                            "ok", true,
                            "mensaje", "Inicio de sesión correcto",
                            "id", usuarioEncontrado.getId(),
                            "nombre", usuarioEncontrado.getNombre(),
                            "correo", usuarioEncontrado.getCorreo()
                    );
                })
                .orElse(
                        Map.of(
                                "ok", false,
                                "mensaje", "Usuario no encontrado"
                        )
                );
    }
}