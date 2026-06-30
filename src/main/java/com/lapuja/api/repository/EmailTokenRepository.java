package com.lapuja.api.repository;

import com.lapuja.api.entity.EmailToken;
import com.lapuja.api.entity.Usuario;
import com.lapuja.api.enums.EmailTokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {

    Optional<EmailToken> findByToken(String token);

    List<EmailToken> findByUsuario(Usuario usuario);

    List<EmailToken> findByUsuarioAndTipo(Usuario usuario, EmailTokenType tipo);

    void deleteByFechaExpiracionBefore(LocalDateTime fecha);

}