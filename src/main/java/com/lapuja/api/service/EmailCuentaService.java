package com.lapuja.api.service;

import com.lapuja.api.entity.EmailToken;
import com.lapuja.api.entity.Usuario;
import com.lapuja.api.enums.EmailTokenType;
import com.lapuja.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class EmailCuentaService {

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final EmailTokenService emailTokenService;

    public EmailCuentaService(
            UsuarioRepository usuarioRepository,
            EmailService emailService,
            EmailTokenService emailTokenService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.emailTokenService = emailTokenService;
    }

    public Object reenviarVerificacion(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario == null) {
            return Map.of("ok", false, "mensaje", "Usuario no encontrado");
        }

        if (Boolean.TRUE.equals(usuario.getCorreoVerificado())) {
            return Map.of("ok", false, "mensaje", "Este correo ya está verificado");
        }

        EmailToken token = emailTokenService.crearToken(
                usuario,
                EmailTokenType.VERIFICACION_CORREO,
                1440
        );

        emailService.enviarCorreoVerificacion(usuario, token.getToken());

        return Map.of(
                "ok", true,
                "mensaje", "Correo de verificación enviado correctamente"
        );
    }

    public Object verificarCorreo(String token) {

        EmailToken emailToken = emailTokenService.validarToken(
                token,
                EmailTokenType.VERIFICACION_CORREO
        );

        if (emailToken == null) {
            return Map.of("ok", false, "mensaje", "Token inválido, expirado o ya utilizado");
        }

        Usuario usuario = emailToken.getUsuario();
        usuario.setCorreoVerificado(true);
        usuario.setFechaVerificacionCorreo(LocalDateTime.now());
        usuarioRepository.save(usuario);

        emailTokenService.marcarComoUsado(emailToken);

        return Map.of(
                "ok", true,
                "mensaje", "Correo verificado correctamente"
        );
    }

    public Object solicitarRecuperacion(Map<String, String> request) {

        String correo = request.get("correo");

        if (correo == null || correo.isBlank()) {
            return Map.of("ok", false, "mensaje", "El correo es obligatorio");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario == null) {
            return Map.of("ok", false, "mensaje", "No existe una cuenta con ese correo");
        }

        EmailToken token = emailTokenService.crearToken(
                usuario,
                EmailTokenType.RECUPERACION_PASSWORD,
                30
        );

        emailService.enviarCorreoRecuperacionPassword(usuario, token.getToken());

        return Map.of(
                "ok", true,
                "mensaje", "Correo de recuperación enviado correctamente"
        );
    }

    public Object restablecerPassword(Map<String, String> request) {

        String token = request.get("token");
        String nuevaPassword = request.get("nuevaPassword");
        String confirmarPassword = request.get("confirmarPassword");

        if (token == null || token.isBlank()) {
            return Map.of("ok", false, "mensaje", "El token es obligatorio");
        }

        if (nuevaPassword == null || nuevaPassword.isBlank()) {
            return Map.of("ok", false, "mensaje", "La nueva contraseña es obligatoria");
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            return Map.of("ok", false, "mensaje", "Las contraseñas no coinciden");
        }

        if (nuevaPassword.length() < 8) {
            return Map.of("ok", false, "mensaje", "La contraseña debe tener al menos 8 caracteres");
        }

        EmailToken emailToken = emailTokenService.validarToken(
                token,
                EmailTokenType.RECUPERACION_PASSWORD
        );

        if (emailToken == null) {
            return Map.of("ok", false, "mensaje", "Token inválido, expirado o ya utilizado");
        }

        Usuario usuario = emailToken.getUsuario();
        usuario.setPassword(nuevaPassword);
        usuarioRepository.save(usuario);

        emailTokenService.marcarComoUsado(emailToken);

        return Map.of(
                "ok", true,
                "mensaje", "Contraseña restablecida correctamente"
        );
    }
}