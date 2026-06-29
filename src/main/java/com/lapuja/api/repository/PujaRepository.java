package com.lapuja.api.repository;

import com.lapuja.api.entity.Puja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PujaRepository extends JpaRepository<Puja, Long> {

    List<Puja> findByUsuarioId(Long usuarioId);

    List<Puja> findBySubastaId(Long subastaId);

    boolean existsBySubastaId(Long subastaId);

    long countByUsuarioId(Long usuarioId);

    @Query("""
            SELECT COUNT(DISTINCT p.subastaId)
            FROM Puja p
            JOIN Subasta s ON s.id = p.subastaId
            WHERE p.usuarioId = :usuarioId
            AND s.estado = 'FINALIZADA'
            AND s.ganadorId IS NOT NULL
            AND s.ganadorId <> :usuarioId
            """)
    long countSubastasPerdidasPorUsuario(@Param("usuarioId") Long usuarioId);
}