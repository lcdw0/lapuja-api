package com.lapuja.api.repository;

import com.lapuja.api.entity.ChatConversacion;
import com.lapuja.api.entity.Subasta;
import com.lapuja.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatConversacionRepository extends JpaRepository<ChatConversacion, Long> {

    Optional<ChatConversacion> findBySubastaAndCompradorAndVendedor(
            Subasta subasta,
            Usuario comprador,
            Usuario vendedor
    );

    List<ChatConversacion> findByCompradorOrVendedorOrderByFechaCreacionDesc(
            Usuario comprador,
            Usuario vendedor
    );
}