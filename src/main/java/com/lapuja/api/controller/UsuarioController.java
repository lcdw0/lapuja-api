package com.lapuja.api.controller;

import com.lapuja.api.dto.LoginRequest;
import com.lapuja.api.dto.UsuarioRegistroRequest;
import com.lapuja.api.dto.UsuarioUpdateRequest;
import com.lapuja.api.entity.EmailToken;
import com.lapuja.api.entity.Usuario;
import com.lapuja.api.enums.EmailTokenType;
import com.lapuja.api.repository.SubastaRepository;
import com.lapuja.api.repository.UsuarioRepository;
import com.lapuja.api.service.EmailService;
import com.lapuja.api.service.EmailTokenService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final SubastaRepository subastaRepository;
    private final EmailService emailService;
    private final EmailTokenService emailTokenService;

    public UsuarioController(
            UsuarioRepository usuarioRepository,
            SubastaRepository subastaRepository,
            EmailService emailService,
            EmailTokenService emailTokenService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.subastaRepository = subastaRepository;
        this.emailService = emailService;
        this.emailTokenService = emailTokenService;
    }

    @PostMapping("/registro")
    public Object registrar(@RequestBody UsuarioRegistroRequest request) {

        if (request.getNombre() == null || request.getNombre().isBlank()) {
            return Map.of("ok", false, "mensaje", "El nombre es obligatorio");
        }

        if (request.getApellidos() == null || request.getApellidos().isBlank()) {
            return Map.of("ok", false, "mensaje", "Los apellidos son obligatorios");
        }

        if (request.getCorreo() == null || request.getCorreo().isBlank()) {
            return Map.of("ok", false, "mensaje", "El correo es obligatorio");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return Map.of("ok", false, "mensaje", "La contraseña es obligatoria");
        }

        if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
            return Map.of("ok", false, "mensaje", "El correo ya está registrado");
        }

        Usuario nuevoUsuario = new Usuario(
                request.getNombre(),
                request.getCorreo(),
                request.getPassword()
        );

        nuevoUsuario.setTelefono(request.getTelefono());
        nuevoUsuario.setCiudad(request.getCiudad());
        nuevoUsuario.setApellidos(request.getApellidos());
        nuevoUsuario.setPais(request.getPais());

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        try {
            EmailToken token = emailTokenService.crearToken(
                    usuarioGuardado,
                    EmailTokenType.VERIFICACION_CORREO,
                    1440
            );

            emailService.enviarCorreoVerificacion(
                    usuarioGuardado,
                    token.getToken()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return respuestaUsuario(
                true,
                "Usuario registrado correctamente. Revisa tu correo para verificar tu cuenta.",
                usuarioGuardado
        );
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequest request) {

        if (request.getCorreo() == null || request.getCorreo().isBlank()) {
            return Map.of("ok", false, "mensaje", "El correo es obligatorio");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return Map.of("ok", false, "mensaje", "La contraseña es obligatoria");
        }

        return usuarioRepository.findByCorreo(request.getCorreo())
                .map(usuarioEncontrado -> {
                    if (!usuarioEncontrado.getPassword().equals(request.getPassword())) {
                        return Map.of("ok", false, "mensaje", "Contraseña incorrecta");
                    }

                    if (!Boolean.TRUE.equals(usuarioEncontrado.getCorreoVerificado())) {
                        return Map.of(
                                "ok", false,
                                "mensaje", "Debes verificar tu correo antes de iniciar sesión"
                        );
                    }

                    return respuestaUsuario(
                            true,
                            "Inicio de sesión correcto",
                            usuarioEncontrado
                    );
                })
                .orElse(Map.of("ok", false, "mensaje", "Usuario no encontrado"));
    }

    @GetMapping("/{id}")
    public Object obtenerUsuario(@PathVariable Long id) {

        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null) {
            return Map.of("ok", false, "mensaje", "Usuario no encontrado");
        }

        return respuestaUsuario(true, "Usuario encontrado", usuario);
    }

    @GetMapping("/{id}/perfil-publico")
    public Object obtenerPerfilPublico(@PathVariable Long id) {

        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null) {
            return Map.of("ok", false, "mensaje", "Usuario no encontrado");
        }

        long subastasActivas = subastaRepository.countByUsuarioIdAndEstado(id, "ACTIVA");
        long subastasFinalizadas = subastaRepository.countByUsuarioIdAndEstado(id, "FINALIZADA");
        long subastasVendidas = subastaRepository.countByUsuarioIdAndEstadoAndGanadorIdIsNotNull(id, "FINALIZADA");
        long compras = subastaRepository.countByGanadorIdAndEstado(id, "FINALIZADA");

        Map<String, Object> respuesta = new HashMap<>();

        respuesta.put("ok", true);
        respuesta.put("mensaje", "Perfil público encontrado");
        respuesta.put("id", usuario.getId());
        respuesta.put("nombre", usuario.getNombre());
        respuesta.put("fotoPerfil", usuario.getFotoPerfil());
        respuesta.put("ciudad", usuario.getCiudad());
        respuesta.put("biografia", usuario.getBiografia());
        respuesta.put("fechaRegistro", usuario.getFechaRegistro());
        respuesta.put("cantidadVentas", subastasVendidas);
        respuesta.put("cantidadCompras", compras);
        respuesta.put("subastasActivas", subastasActivas);
        respuesta.put("subastasFinalizadas", subastasFinalizadas);
        respuesta.put("subastasVendidas", subastasVendidas);
        respuesta.put("reputacion", 0.0);
        respuesta.put("promedioEstrellas", 0.0);
        respuesta.put("apellidos", usuario.getApellidos());
        respuesta.put("pais", usuario.getPais());

        return respuesta;
    }

    @GetMapping("/{id}/subastas/activas")
    public Object obtenerSubastasActivasUsuario(@PathVariable Long id) {

        if (usuarioRepository.findById(id).isEmpty()) {
            return Map.of("ok", false, "mensaje", "Usuario no encontrado");
        }

        return Map.of(
                "ok", true,
                "mensaje", "Subastas activas encontradas",
                "subastas", subastaRepository.findByUsuarioIdAndEstadoOrderByFechaCreacionDesc(id, "ACTIVA")
        );
    }

    @GetMapping("/{id}/subastas/finalizadas")
    public Object obtenerSubastasFinalizadasUsuario(@PathVariable Long id) {

        if (usuarioRepository.findById(id).isEmpty()) {
            return Map.of("ok", false, "mensaje", "Usuario no encontrado");
        }

        return Map.of(
                "ok", true,
                "mensaje", "Subastas finalizadas encontradas",
                "subastas", subastaRepository.findByUsuarioIdAndEstadoOrderByFechaCreacionDesc(id, "FINALIZADA")
        );
    }

    @GetMapping("/{id}/subastas/vendidas")
    public Object obtenerSubastasVendidasUsuario(@PathVariable Long id) {

        if (usuarioRepository.findById(id).isEmpty()) {
            return Map.of("ok", false, "mensaje", "Usuario no encontrado");
        }

        return Map.of(
                "ok", true,
                "mensaje", "Subastas vendidas encontradas",
                "subastas", subastaRepository.findByUsuarioIdAndEstadoAndGanadorIdIsNotNullOrderByFechaCreacionDesc(id, "FINALIZADA")
        );
    }

    @PutMapping("/{id}")
    public Object actualizarUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioUpdateRequest request
    ) {

        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null) {
            return Map.of("ok", false, "mensaje", "Usuario no encontrado");
        }

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            usuario.setNombre(request.getNombre());
        }

        if (request.getApellidos() != null && !request.getApellidos().isBlank()) {
            usuario.setApellidos(request.getApellidos());
        }

        if (request.getPais() != null) {
            usuario.setPais(request.getPais());
        }

        if (request.getCorreo() != null && !request.getCorreo().isBlank()) {
            usuario.setCorreo(request.getCorreo());
        }

        if (request.getFotoPerfil() != null && !request.getFotoPerfil().isBlank()) {
            usuario.setFotoPerfil(request.getFotoPerfil());
        }

        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono());
        }

        if (request.getCiudad() != null) {
            usuario.setCiudad(request.getCiudad());
        }

        if (request.getBiografia() != null) {
            usuario.setBiografia(request.getBiografia());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {

            if (request.getPasswordActual() == null || request.getPasswordActual().isBlank()) {
                return Map.of("ok", false, "mensaje", "Debe ingresar su contraseña actual.");
            }

            if (!usuario.getPassword().equals(request.getPasswordActual())) {
                return Map.of("ok", false, "mensaje", "La contraseña actual es incorrecta.");
            }

            if (request.getConfirmarPassword() == null ||
                    !request.getPassword().equals(request.getConfirmarPassword())) {
                return Map.of("ok", false, "mensaje", "Las nuevas contraseñas no coinciden.");
            }

            if (request.getPassword().length() < 8) {
                return Map.of("ok", false, "mensaje", "La nueva contraseña debe tener al menos 8 caracteres.");
            }

            usuario.setPassword(request.getPassword());
        }

        Usuario actualizado = usuarioRepository.save(usuario);

        return respuestaUsuario(true, "Usuario actualizado correctamente", actualizado);
    }

    private Map<String, Object> respuestaUsuario(
            boolean ok,
            String mensaje,
            Usuario usuario
    ) {
        Map<String, Object> respuesta = new HashMap<>();

        respuesta.put("ok", ok);
        respuesta.put("mensaje", mensaje);
        respuesta.put("id", usuario.getId());
        respuesta.put("nombre", usuario.getNombre());
        respuesta.put("correo", usuario.getCorreo());
        respuesta.put("fotoPerfil", usuario.getFotoPerfil());
        respuesta.put("telefono", usuario.getTelefono());
        respuesta.put("ciudad", usuario.getCiudad());
        respuesta.put("biografia", usuario.getBiografia());
        respuesta.put("fechaRegistro", usuario.getFechaRegistro());
        respuesta.put("saldo", usuario.getSaldo());
        respuesta.put("correoVerificado", usuario.getCorreoVerificado());
        respuesta.put("fechaVerificacionCorreo", usuario.getFechaVerificacionCorreo());
        respuesta.put("apellidos", usuario.getApellidos());
        respuesta.put("pais", usuario.getPais());

        return respuesta;
    }
}