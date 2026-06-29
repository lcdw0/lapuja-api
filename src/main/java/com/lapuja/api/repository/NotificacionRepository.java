package com.lapuja.api.repository;

import com.lapuja.api.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    List<Notificacion> findByUsuarioIdAndLeidaFalseOrderByFechaDesc(Long usuarioId);

    long countByUsuarioIdAndLeidaFalse(Long usuarioId);

}