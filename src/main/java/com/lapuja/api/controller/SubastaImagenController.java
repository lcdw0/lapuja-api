package com.lapuja.api.controller;

import com.lapuja.api.entity.Subasta;
import com.lapuja.api.entity.SubastaImagen;
import com.lapuja.api.repository.SubastaImagenRepository;
import com.lapuja.api.repository.SubastaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subastas")
@CrossOrigin(origins = "*")
public class SubastaImagenController {

    private final SubastaRepository subastaRepository;
    private final SubastaImagenRepository subastaImagenRepository;

    public SubastaImagenController(
            SubastaRepository subastaRepository,
            SubastaImagenRepository subastaImagenRepository
    ) {
        this.subastaRepository = subastaRepository;
        this.subastaImagenRepository = subastaImagenRepository;
    }

    @GetMapping("/{subastaId}/imagenes")
    public Object listarImagenes(@PathVariable Long subastaId) {
        if (!subastaRepository.existsById(subastaId)) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Subasta no encontrada"
            );
        }

        return subastaImagenRepository.findBySubastaIdOrderByOrdenAsc(subastaId);
    }

    @PostMapping("/{subastaId}/imagenes")
    public Object agregarImagen(
            @PathVariable Long subastaId,
            @RequestBody Map<String, String> request
    ) {
        Subasta subasta = subastaRepository.findById(subastaId).orElse(null);

        if (subasta == null) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Subasta no encontrada"
            );
        }

        String url = request.get("url");

        if (url == null || url.isBlank()) {
            return Map.of(
                    "ok", false,
                    "mensaje", "La URL de la imagen es obligatoria"
            );
        }

        long cantidadImagenes = subastaImagenRepository.countBySubastaId(subastaId);

        if (cantidadImagenes >= 10) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Solo se permiten hasta 10 imágenes por subasta"
            );
        }

        SubastaImagen imagen = new SubastaImagen();
        imagen.setSubastaId(subastaId);
        imagen.setUrl(url);
        imagen.setOrden((int) cantidadImagenes + 1);
        imagen.setPrincipal(cantidadImagenes == 0);

        SubastaImagen imagenGuardada = subastaImagenRepository.save(imagen);

        if (Boolean.TRUE.equals(imagenGuardada.getPrincipal())) {
            subasta.setImagen(imagenGuardada.getUrl());
            subastaRepository.save(subasta);
        }

        return imagenGuardada;
    }

    @PutMapping("/{subastaId}/imagenes/{imagenId}/principal")
    public Object marcarPrincipal(
            @PathVariable Long subastaId,
            @PathVariable Long imagenId
    ) {
        Subasta subasta = subastaRepository.findById(subastaId).orElse(null);

        if (subasta == null) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Subasta no encontrada"
            );
        }

        SubastaImagen imagenPrincipal = subastaImagenRepository.findById(imagenId).orElse(null);

        if (imagenPrincipal == null || !imagenPrincipal.getSubastaId().equals(subastaId)) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Imagen no encontrada para esta subasta"
            );
        }

        List<SubastaImagen> imagenes = subastaImagenRepository.findBySubastaId(subastaId);

        for (SubastaImagen imagen : imagenes) {
            imagen.setPrincipal(false);
            subastaImagenRepository.save(imagen);
        }

        imagenPrincipal.setPrincipal(true);
        subastaImagenRepository.save(imagenPrincipal);

        subasta.setImagen(imagenPrincipal.getUrl());
        subastaRepository.save(subasta);

        return imagenPrincipal;
    }

    @DeleteMapping("/{subastaId}/imagenes/{imagenId}")
    public Object eliminarImagen(
            @PathVariable Long subastaId,
            @PathVariable Long imagenId
    ) {
        Subasta subasta = subastaRepository.findById(subastaId).orElse(null);

        if (subasta == null) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Subasta no encontrada"
            );
        }

        SubastaImagen imagen = subastaImagenRepository.findById(imagenId).orElse(null);

        if (imagen == null || !imagen.getSubastaId().equals(subastaId)) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Imagen no encontrada para esta subasta"
            );
        }

        boolean eraPrincipal = Boolean.TRUE.equals(imagen.getPrincipal());

        subastaImagenRepository.delete(imagen);

        List<SubastaImagen> imagenesRestantes =
                subastaImagenRepository.findBySubastaIdOrderByOrdenAsc(subastaId);

        int orden = 1;

        for (SubastaImagen img : imagenesRestantes) {
            img.setOrden(orden++);
            subastaImagenRepository.save(img);
        }

        if (imagenesRestantes.isEmpty()) {
            subasta.setImagen(null);
            subastaRepository.save(subasta);
        } else if (eraPrincipal) {
            SubastaImagen nuevaPrincipal = imagenesRestantes.get(0);
            nuevaPrincipal.setPrincipal(true);
            subastaImagenRepository.save(nuevaPrincipal);

            subasta.setImagen(nuevaPrincipal.getUrl());
            subastaRepository.save(subasta);
        }

        return Map.of(
                "ok", true,
                "mensaje", "Imagen eliminada correctamente"
        );
    }

    @PutMapping("/{subastaId}/imagenes/reordenar")
    public Object reordenarImagenes(
            @PathVariable Long subastaId,
            @RequestBody List<Long> idsOrdenados
    ) {
        if (!subastaRepository.existsById(subastaId)) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Subasta no encontrada"
            );
        }

        if (idsOrdenados == null || idsOrdenados.isEmpty()) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Debe enviar el orden de las imágenes"
            );
        }

        List<SubastaImagen> imagenes = subastaImagenRepository.findBySubastaId(subastaId);

        if (imagenes.size() != idsOrdenados.size()) {
            return Map.of(
                    "ok", false,
                    "mensaje", "La cantidad de imágenes no coincide"
            );
        }

        int orden = 1;

        for (Long imagenId : idsOrdenados) {
            SubastaImagen imagen = subastaImagenRepository.findById(imagenId).orElse(null);

            if (imagen == null || !imagen.getSubastaId().equals(subastaId)) {
                return Map.of(
                        "ok", false,
                        "mensaje", "Una de las imágenes no pertenece a esta subasta"
                );
            }

            imagen.setOrden(orden++);
            subastaImagenRepository.save(imagen);
        }

        return subastaImagenRepository.findBySubastaIdOrderByOrdenAsc(subastaId);
    }
}