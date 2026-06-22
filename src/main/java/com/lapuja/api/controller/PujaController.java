package com.lapuja.api.controller;

import com.lapuja.api.entity.Puja;
import com.lapuja.api.entity.Subasta;
import com.lapuja.api.entity.Usuario;
import com.lapuja.api.repository.PujaRepository;
import com.lapuja.api.repository.SubastaRepository;
import com.lapuja.api.repository.UsuarioRepository;
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

    public PujaController(
            PujaRepository pujaRepository,
            SubastaRepository subastaRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.pujaRepository = pujaRepository;
        this.subastaRepository = subastaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public Object crearPuja(@RequestBody Puja puja) {
        Usuario usuario = usuarioRepository.findById(puja.getUsuarioId())
                .orElseThrow();

        Subasta subasta = subastaRepository.findById(puja.getSubastaId())
                .orElseThrow();

        if (!"ACTIVA".equals(subasta.getEstado())) {
            return Map.of(
                    "ok", false,
                    "mensaje", "La subasta no está activa"
            );
        }

        if (puja.getMonto() <= subasta.getPrecioActual()) {
            return Map.of(
                    "ok", false,
                    "mensaje", "La puja debe ser mayor al precio actual"
            );
        }

        Puja nuevaPuja = pujaRepository.save(puja);

        subasta.setPrecioActual(puja.getMonto());
        subasta.setOfertas(subasta.getOfertas() + 1);
        subasta.setGanador(usuario.getNombre());

        subastaRepository.save(subasta);

        return Map.of(
                "ok", true,
                "mensaje", "Puja realizada correctamente",
                "puja", nuevaPuja,
                "subasta", subasta
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