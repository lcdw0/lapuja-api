package com.lapuja.api.controller;

import com.lapuja.api.dto.RecargaRequest;
import com.lapuja.api.entity.MetodoPago;
import com.lapuja.api.entity.Usuario;
import com.lapuja.api.entity.WalletMovimiento;
import com.lapuja.api.repository.MetodoPagoRepository;
import com.lapuja.api.repository.UsuarioRepository;
import com.lapuja.api.repository.WalletMovimientoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WalletController {

    private final UsuarioRepository usuarioRepository;
    private final WalletMovimientoRepository walletRepository;
    private final MetodoPagoRepository metodoPagoRepository;

    public WalletController(
            UsuarioRepository usuarioRepository,
            WalletMovimientoRepository walletRepository,
            MetodoPagoRepository metodoPagoRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.walletRepository = walletRepository;
        this.metodoPagoRepository = metodoPagoRepository;
    }

    @PostMapping("/{usuarioId}/recargar")
    public Object recargarSaldo(
            @PathVariable Long usuarioId,
            @RequestBody RecargaRequest request
    ) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario == null) {
            return Map.of("ok", false, "mensaje", "Usuario no encontrado");
        }

        if (request.getMonto() == null || request.getMonto() <= 0) {
            return Map.of("ok", false, "mensaje", "El monto debe ser mayor a cero");
        }

        if (request.getMetodoPagoId() == null) {
            return Map.of("ok", false, "mensaje", "Debe seleccionar un método de pago");
        }

        MetodoPago metodoPago = metodoPagoRepository.findById(request.getMetodoPagoId())
                .orElse(null);

        if (metodoPago == null) {
            return Map.of("ok", false, "mensaje", "Método de pago no encontrado");
        }

        if (!metodoPago.getUsuarioId().equals(usuarioId)) {
            return Map.of("ok", false, "mensaje", "El método de pago no pertenece al usuario");
        }

        Double saldoActual = usuario.getSaldo() == null ? 0.0 : usuario.getSaldo();
        usuario.setSaldo(saldoActual + request.getMonto());
        usuarioRepository.save(usuario);

        WalletMovimiento movimiento = new WalletMovimiento();
        movimiento.setUsuarioId(usuarioId);
        movimiento.setTipo("RECARGA");
        movimiento.setMonto(request.getMonto());
        movimiento.setDescripcion(
                "Recarga con " + metodoPago.getMarca() + " ****" + metodoPago.getUltimos4()
        );

        walletRepository.save(movimiento);

        return Map.of(
                "ok", true,
                "mensaje", "Saldo recargado correctamente",
                "saldo", usuario.getSaldo()
        );
    }

    @GetMapping("/{usuarioId}/saldo")
    public Object obtenerSaldo(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario == null) {
            return Map.of("ok", false, "mensaje", "Usuario no encontrado");
        }

        return Map.of(
                "ok", true,
                "saldo", usuario.getSaldo()
        );
    }

    @GetMapping("/{usuarioId}/movimientos")
    public Object obtenerMovimientos(@PathVariable Long usuarioId) {
        return walletRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }
}