package com.lapuja.api.controller;

import com.lapuja.api.dto.SubastaRequest;
import com.lapuja.api.entity.Subasta;
import com.lapuja.api.repository.SubastaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subastas")
@CrossOrigin(origins = "*")
public class SubastaController {

    private final SubastaRepository subastaRepository;

    public SubastaController(SubastaRepository subastaRepository) {
        this.subastaRepository = subastaRepository;
    }

    @GetMapping
    public List<Subasta> listar() {
        return subastaRepository.findAll();
    }

    @PostMapping
    public Object crear(@RequestBody SubastaRequest request) {

        if (request.getNombre() == null || request.getNombre().isBlank()) {
            return Map.of(
                    "ok", false,
                    "mensaje", "El nombre es obligatorio"
            );
        }

        if (request.getPrecioInicial() == null || request.getPrecioInicial() <= 0) {
            return Map.of(
                    "ok", false,
                    "mensaje", "El precio inicial debe ser mayor que cero"
            );
        }

        Subasta subasta = new Subasta();

        subasta.setNombre(request.getNombre());
        subasta.setDescripcion(request.getDescripcion());
        subasta.setPrecioInicial(request.getPrecioInicial());
        subasta.setCategoria(request.getCategoria());
        subasta.setImagen(request.getImagen());
        subasta.setFechaFin(request.getFechaFin());
        subasta.setUsuarioId(request.getUsuarioId());

        return subastaRepository.save(subasta);
    }

    @GetMapping("/{id}")
    public Subasta obtenerPorId(@PathVariable Long id) {
        return subastaRepository.findById(id)
                .orElseThrow();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Subasta> listarPorUsuario(@PathVariable Long usuarioId) {
        return subastaRepository.findByUsuarioId(usuarioId);
    }

    @GetMapping("/activas")
    public List<Subasta> listarActivas() {
        return subastaRepository.findByEstado("ACTIVA");
    }

    @PutMapping("/{id}/finalizar")
    public Object finalizar(@PathVariable Long id) {

        Subasta subasta = subastaRepository.findById(id)
                .orElseThrow();

        if ("FINALIZADA".equals(subasta.getEstado())) {
            return Map.of(
                    "ok", false,
                    "mensaje", "La subasta ya está finalizada"
            );
        }

        subasta.setEstado("FINALIZADA");

        return subastaRepository.save(subasta);
    }
}