package com.lapuja.api.controller;

import com.lapuja.api.entity.Subasta;
import com.lapuja.api.repository.SubastaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Subasta crear(@RequestBody Subasta subasta) {
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
    public Subasta finalizar(@PathVariable Long id) {

        Subasta subasta = subastaRepository.findById(id)
                .orElseThrow();

        subasta.setEstado("FINALIZADA");

        return subastaRepository.save(subasta);
    }
}