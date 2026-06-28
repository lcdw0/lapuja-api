package com.lapuja.api.controller;

import com.lapuja.api.dto.FavoritoRequest;
import com.lapuja.api.entity.Favorito;
import com.lapuja.api.repository.FavoritoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favoritos")
@CrossOrigin(origins = "*")
public class FavoritoController {

    private final FavoritoRepository favoritoRepository;

    public FavoritoController(FavoritoRepository favoritoRepository) {
        this.favoritoRepository = favoritoRepository;
    }

    @PostMapping
    public Object agregar(@RequestBody FavoritoRequest request) {

        if (favoritoRepository
                .findByUsuarioIdAndSubastaId(request.getUsuarioId(), request.getSubastaId())
                .isPresent()) {
            return Map.of(
                    "ok", false,
                    "mensaje", "La subasta ya está en favoritos"
            );
        }

        Favorito favorito = new Favorito();
        favorito.setUsuarioId(request.getUsuarioId());
        favorito.setSubastaId(request.getSubastaId());

        Favorito guardado = favoritoRepository.save(favorito);

        return Map.of(
                "ok", true,
                "mensaje", "Favorito agregado correctamente",
                "favorito", guardado
        );
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Favorito> listarPorUsuario(@PathVariable Long usuarioId) {
        return favoritoRepository.findByUsuarioId(usuarioId);
    }

    @DeleteMapping("/{id}")
    public Object eliminar(@PathVariable Long id) {

        if (!favoritoRepository.existsById(id)) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Favorito no encontrado"
            );
        }

        favoritoRepository.deleteById(id);

        return Map.of(
                "ok", true,
                "mensaje", "Favorito eliminado correctamente"
        );
    }
}