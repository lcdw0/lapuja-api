package com.lapuja.api.repository;

import com.lapuja.api.entity.WalletMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WalletMovimientoRepository extends JpaRepository<WalletMovimiento, Long> {

    List<WalletMovimiento> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    List<WalletMovimiento> findTop5ByUsuarioIdOrderByFechaDesc(Long usuarioId);

    @Query("""
            SELECT COALESCE(SUM(w.monto), 0)
            FROM WalletMovimiento w
            WHERE w.usuarioId = :usuarioId
            AND w.tipo = :tipo
            """)
    Double sumarMontoPorTipo(
            @Param("usuarioId") Long usuarioId,
            @Param("tipo") String tipo
    );

    @Query("""
            SELECT w
            FROM WalletMovimiento w
            WHERE w.usuarioId = :usuarioId
            AND w.tipo = :tipo
            AND w.fecha BETWEEN :inicio AND :fin
            ORDER BY w.fecha ASC
            """)
    List<WalletMovimiento> findByUsuarioIdAndTipoAndFechaBetween(
            @Param("usuarioId") Long usuarioId,
            @Param("tipo") String tipo,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
}