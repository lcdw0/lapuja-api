package com.lapuja.api.controller;

import com.lapuja.api.dto.LoginRequest;
import com.lapuja.api.dto.UsuarioRegistroRequest;
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
    public Object registrar(@RequestBody UsuarioRegistroRequest request) {

        if (request.getNombre() == null || request.getNombre().isBlank()) {
            return Map.of(
                    "ok", false,
                    "mensaje", "El nombre es obligatorio"
            );
        }

        if (request.getCorreo() == null || request.getCorreo().isBlank()) {
            return Map.of(
                    "ok", false,
                    "mensaje", "El correo es obligatorio"
            );
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return Map.of(
                    "ok", false,
                    "mensaje", "La contraseña es obligatoria"
            );
        }

        if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
            return Map.of(
                    "ok", false,
                    "mensaje", "El correo ya está registrado"
            );
        }

        Usuario nuevoUsuario = new Usuario(
                request.getNombre(),
                request.getCorreo(),
                request.getPassword()
        );

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        return Map.of(
                "ok", true,
                "mensaje", "Usuario registrado correctamente",
                "id", usuarioGuardado.getId(),
                "nombre", usuarioGuardado.getNombre(),
                "correo", usuarioGuardado.getCorreo()
        );
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequest request) {

        if (request.getCorreo() == null || request.getCorreo().isBlank()) {
            return Map.of(
                    "ok", false,
                    "mensaje", "El correo es obligatorio"
            );
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return Map.of(
                    "ok", false,
                    "mensaje", "La contraseña es obligatoria"
            );
        }

        return usuarioRepository.findByCorreo(request.getCorreo())
                .map(usuarioEncontrado -> {

                    if (!usuarioEncontrado.getPassword().equals(request.getPassword())) {
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