package com.lapuja.api.repository;

import com.lapuja.api.entity.ChatConversacion;
import com.lapuja.api.entity.ChatMensaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMensajeRepository extends JpaRepository<ChatMensaje, Long> {

    List<ChatMensaje> findByConversacionOrderByFechaEnvioAsc(
            ChatConversacion conversacion
    );

    Long countByConversacionAndLeidoFalseAndEmisorIdNot(
            ChatConversacion conversacion,
            Long emisorId
    );
}