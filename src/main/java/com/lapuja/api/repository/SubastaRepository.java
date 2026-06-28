package com.lapuja.api.repository;

import com.lapuja.api.entity.Subasta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SubastaRepository extends JpaRepository<Subasta, Long> {

    List<Subasta> findByUsuarioId(Long usuarioId);

    List<Subasta> findByEstado(String estado);

    List<Subasta> findByEstadoAndFechaFinBefore(String estado, LocalDateTime fechaActual);
}