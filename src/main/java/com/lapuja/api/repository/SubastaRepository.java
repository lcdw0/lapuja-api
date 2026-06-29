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
}