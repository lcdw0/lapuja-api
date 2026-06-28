package com.lapuja.api.controller;

import com.lapuja.api.dto.PujaRequest;
import com.lapuja.api.entity.Puja;
import com.lapuja.api.entity.Subasta;
import com.lapuja.api.entity.Usuario;
import com.lapuja.api.entity.WalletMovimiento;
import com.lapuja.api.repository.PujaRepository;
import com.lapuja.api.repository.SubastaRepository;
import com.lapuja.api.repository.UsuarioRepository;
import com.lapuja.api.repository.WalletMovimientoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pujas")
@CrossOrigin(origins = "*")
public class PujaController {

    private final PujaRepository pujaRepository;
    private final SubastaRepository subastaRepository;
    private final UsuarioRepository usuarioRepository;
    private final WalletMovimientoRepository walletRepository;

    public PujaController(
            PujaRepository pujaRepository,
            SubastaRepository subastaRepository,
            UsuarioRepository usuarioRepository,
            WalletMovimientoRepository walletRepository
    ) {
        this.pujaRepository = pujaRepository;
        this.subastaRepository = subastaRepository;
        this.usuarioRepository = usuarioRepository;
        this.walletRepository = walletRepository;
    }

    @PostMapping
    public Object crearPuja(@RequestBody PujaRequest request) {

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElse(null);

        if (usuario == null) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Usuario no encontrado"
            );
        }

        Subasta subasta = subastaRepository.findById(request.getSubastaId())
                .orElse(null);

        if (subasta == null) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Subasta no encontrada"
            );
        }

        if (subasta.getUsuarioId() != null &&
                subasta.getUsuarioId().equals(usuario.getId())) {
            return Map.of(
                    "ok", false,
                    "mensaje", "No puedes pujar en tu propia subasta"
            );
        }

        if (!"ACTIVA".equals(subasta.getEstado())) {
            return Map.of(
                    "ok", false,
                    "mensaje", "La subasta no está activa"
            );
        }

        if (request.getMonto() == null || request.getMonto() <= subasta.getPrecioActual()) {
            return Map.of(
                    "ok", false,
                    "mensaje", "La puja debe ser mayor al precio actual"
            );
        }

        Double saldoActual = usuario.getSaldo() == null ? 0.0 : usuario.getSaldo();

        Long ganadorAnteriorId = subasta.getGanadorId();
        Double montoAnterior = subasta.getPrecioActual() == null ? 0.0 : subasta.getPrecioActual();

        boolean mismoGanador =
                ganadorAnteriorId != null && ganadorAnteriorId.equals(usuario.getId());

        Double montoADescontar;

        if (mismoGanador) {
            montoADescontar = request.getMonto() - montoAnterior;
        } else {
            montoADescontar = request.getMonto();
        }

        if (montoADescontar <= 0) {
            return Map.of(
                    "ok", false,
                    "mensaje", "La nueva puja debe ser mayor a la anterior"
            );
        }

        if (saldoActual < montoADescontar) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Saldo insuficiente para realizar esta puja",
                    "saldo", saldoActual
            );
        }

        if (!mismoGanador && ganadorAnteriorId != null) {
            Usuario ganadorAnterior = usuarioRepository.findById(ganadorAnteriorId)
                    .orElse(null);

            if (ganadorAnterior != null) {
                Double saldoGanadorAnterior =
                        ganadorAnterior.getSaldo() == null ? 0.0 : ganadorAnterior.getSaldo();

                ganadorAnterior.setSaldo(saldoGanadorAnterior + montoAnterior);
                usuarioRepository.save(ganadorAnterior);

                WalletMovimiento reembolso = new WalletMovimiento();
                reembolso.setUsuarioId(ganadorAnterior.getId());
                reembolso.setTipo("REEMBOLSO");
                reembolso.setMonto(montoAnterior);
                reembolso.setDescripcion(
                        "Reembolso por puja superada en " + subasta.getNombre()
                );

                walletRepository.save(reembolso);
            }
        }

        usuario.setSaldo(saldoActual - montoADescontar);
        usuarioRepository.save(usuario);

        Puja puja = new Puja();
        puja.setUsuarioId(request.getUsuarioId());
        puja.setSubastaId(request.getSubastaId());
        puja.setMonto(request.getMonto());

        Puja nuevaPuja = pujaRepository.save(puja);

        subasta.setPrecioActual(request.getMonto());
        subasta.setOfertas(subasta.getOfertas() + 1);
        subasta.setGanador(usuario.getNombre());
        subasta.setGanadorId(usuario.getId());

        subastaRepository.save(subasta);

        WalletMovimiento movimiento = new WalletMovimiento();
        movimiento.setUsuarioId(usuario.getId());
        movimiento.setTipo("PUJA");
        movimiento.setMonto(montoADescontar);

        if (mismoGanador) {
            movimiento.setDescripcion(
                    "Aumento de puja en " + subasta.getNombre()
            );
        } else {
            movimiento.setDescripcion(
                    "Puja realizada en " + subasta.getNombre()
            );
        }

        walletRepository.save(movimiento);

        return Map.of(
                "ok", true,
                "mensaje", "Puja realizada correctamente",
                "puja", nuevaPuja,
                "subasta", subasta,
                "saldo", usuario.getSaldo(),
                "montoDescontado", montoADescontar
        );
    }

    @GetMapping
    public List<Puja> listar() {
        return pujaRepository.findAll();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Puja> listarPorUsuario(@PathVariable Long usuarioId) {
        return pujaRepository.findByUsuarioId(usuarioId);
    }

    @GetMapping("/subasta/{subastaId}")
    public List<Puja> listarPorSubasta(@PathVariable Long subastaId) {
        return pujaRepository.findBySubastaId(subastaId);
    }
}