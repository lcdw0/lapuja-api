package com.lapuja.api.repository;

import com.lapuja.api.entity.Subasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface SubastaRepository extends JpaRepository<Subasta, Long>, JpaSpecificationExecutor<Subasta> {

    List<Subasta> findByUsuarioId(Long usuarioId);

    List<Subasta> findByEstado(String estado);

    List<Subasta> findByEstadoAndFechaFinBefore(String estado, LocalDateTime fechaActual);

    List<Subasta> findByEstadoAndGanadorId(String estado, Long ganadorId);

    long countByUsuarioId(Long usuarioId);

    long countByUsuarioIdAndEstado(Long usuarioId, String estado);

    long countByGanadorIdAndEstado(Long ganadorId, String estado);

    long countByUsuarioIdAndEstadoAndGanadorIdIsNotNull(Long usuarioId, String estado);

    List<Subasta> findTop5ByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    List<Subasta> findTop5ByGanadorIdOrderByFechaFinDesc(Long ganadorId);

    List<Subasta> findByUsuarioIdAndFechaCreacionBetweenOrderByFechaCreacionAsc(
            Long usuarioId,
            LocalDateTime inicio,
            LocalDateTime fin
    );
}