package com.lapuja.api.repository;

import com.lapuja.api.entity.WalletMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletMovimientoRepository extends JpaRepository<WalletMovimiento, Long> {

    List<WalletMovimiento> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
}