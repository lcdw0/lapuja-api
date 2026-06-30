package com.lapuja.api.service;

import com.lapuja.api.entity.EmailToken;
import com.lapuja.api.entity.Usuario;
import com.lapuja.api.enums.EmailTokenType;
import com.lapuja.api.repository.EmailTokenRepository;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailTokenService {

    private final EmailTokenRepository emailTokenRepository;

    public EmailTokenService(EmailTokenRepository emailTokenRepository) {
        this.emailTokenRepository = emailTokenRepository;
    }

    public EmailToken crearToken(
            Usuario usuario,
            EmailTokenType tipo,
            int minutosExpiracion
    ) {
        EmailToken emailToken = new EmailToken();

        emailToken.setUsuario(usuario);
        emailToken.setTipo(tipo);
        emailToken.setToken(UUID.randomUUID().toString());
        emailToken.setFechaExpiracion(LocalDateTime.now().plusMinutes(minutosExpiracion));
        emailToken.setUsado(false);

        return emailTokenRepository.save(emailToken);
    }

    public EmailToken validarToken(String token, EmailTokenType tipo) {
        EmailToken emailToken = emailTokenRepository.findByToken(token).orElse(null);

        if (emailToken == null) {
            return null;
        }

        if (!emailToken.getTipo().equals(tipo)) {
            return null;
        }

        if (Boolean.TRUE.equals(emailToken.getUsado())) {
            return null;
        }

        if (emailToken.estaExpirado()) {
            return null;
        }

        return emailToken;
    }

    public void marcarComoUsado(EmailToken emailToken) {
        emailToken.setUsado(true);
        emailTokenRepository.save(emailToken);
    }

    public void eliminarTokensExpirados() {
        emailTokenRepository.deleteByFechaExpiracionBefore(LocalDateTime.now());
    }

    public EmailToken crearCodigoRecuperacion(
            Usuario usuario,
            EmailTokenType tipo,
            int minutosExpiracion
    ) {
        SecureRandom random = new SecureRandom();
        String codigo = String.format("%06d", random.nextInt(1_000_000));

        EmailToken emailToken = new EmailToken();

        emailToken.setUsuario(usuario);
        emailToken.setTipo(tipo);
        emailToken.setToken(codigo);
        emailToken.setFechaExpiracion(LocalDateTime.now().plusMinutes(minutosExpiracion));
        emailToken.setUsado(false);

        return emailTokenRepository.save(emailToken);
    }
}