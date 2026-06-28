package com.lapuja.api.controller;

import com.lapuja.api.entity.MetodoPago;
import com.lapuja.api.repository.MetodoPagoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metodos-pago")
@CrossOrigin(origins = "*")
public class MetodoPagoController {

    private final MetodoPagoRepository metodoPagoRepository;

    public MetodoPagoController(MetodoPagoRepository metodoPagoRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
    }

    @PostMapping
    public Object crear(@RequestBody MetodoPago metodoPago) {

        if (metodoPago.getUsuarioId() == null) {
            return Map.of("ok", false, "mensaje", "El usuario es obligatorio");
        }

        if (metodoPago.getTitular() == null || metodoPago.getTitular().isBlank()) {
            return Map.of("ok", false, "mensaje", "El titular es obligatorio");
        }

        if (metodoPago.getUltimos4() == null || metodoPago.getUltimos4().length() != 4) {
            return Map.of("ok", false, "mensaje", "Los últimos 4 dígitos son inválidos");
        }

        if (metodoPago.getVencimiento() == null || metodoPago.getVencimiento().isBlank()) {
            return Map.of("ok", false, "mensaje", "El vencimiento es obligatorio");
        }

        if (metodoPago.getTipo() == null || metodoPago.getTipo().isBlank()) {
            metodoPago.setTipo("TARJETA");
        }

        if (metodoPago.getMarca() == null || metodoPago.getMarca().isBlank()) {
            metodoPago.setMarca("DESCONOCIDA");
        }

        List<MetodoPago> existentes =
                metodoPagoRepository.findByUsuarioId(metodoPago.getUsuarioId());

        if (existentes.isEmpty()) {
            metodoPago.setPrincipal(true);
        }

        if (Boolean.TRUE.equals(metodoPago.getPrincipal())) {
            List<MetodoPago> metodosUsuario =
                    metodoPagoRepository.findByUsuarioId(metodoPago.getUsuarioId());

            for (MetodoPago metodo : metodosUsuario) {
                metodo.setPrincipal(false);
            }

            metodoPagoRepository.saveAll(metodosUsuario);
        }

        MetodoPago guardado = metodoPagoRepository.save(metodoPago);

        return Map.of(
                "ok", true,
                "mensaje", "Método de pago agregado correctamente",
                "metodoPago", guardado
        );
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<MetodoPago> listarPorUsuario(@PathVariable Long usuarioId) {
        return metodoPagoRepository.findByUsuarioId(usuarioId);
    }

    @DeleteMapping("/{id}")
    public Object eliminar(@PathVariable Long id) {

        if (!metodoPagoRepository.existsById(id)) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Método de pago no encontrado"
            );
        }

        metodoPagoRepository.deleteById(id);

        return Map.of(
                "ok", true,
                "mensaje", "Método de pago eliminado correctamente"
        );
    }

    @PutMapping("/{id}/principal")
    public Object marcarComoPrincipal(@PathVariable Long id) {

        MetodoPago metodoSeleccionado = metodoPagoRepository.findById(id)
                .orElse(null);

        if (metodoSeleccionado == null) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Método de pago no encontrado"
            );
        }

        List<MetodoPago> metodosUsuario =
                metodoPagoRepository.findByUsuarioId(metodoSeleccionado.getUsuarioId());

        for (MetodoPago metodo : metodosUsuario) {
            metodo.setPrincipal(false);
        }

        metodoSeleccionado.setPrincipal(true);

        metodoPagoRepository.saveAll(metodosUsuario);
        metodoPagoRepository.save(metodoSeleccionado);

        return Map.of(
                "ok", true,
                "mensaje", "Método de pago marcado como principal"
        );
    }
}