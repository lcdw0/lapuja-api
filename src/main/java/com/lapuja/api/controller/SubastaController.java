package com.lapuja.api.controller;

import com.lapuja.api.dto.SubastaDetalleResponse;
import com.lapuja.api.dto.SubastaRequest;
import com.lapuja.api.entity.Subasta;
import com.lapuja.api.entity.SubastaImagen;
import com.lapuja.api.repository.PujaRepository;
import com.lapuja.api.repository.SubastaImagenRepository;
import com.lapuja.api.repository.SubastaRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/subastas")
@CrossOrigin(origins = "*")
public class SubastaController {

    private final SubastaRepository subastaRepository;
    private final PujaRepository pujaRepository;
    private final SubastaImagenRepository subastaImagenRepository;

    public SubastaController(
            SubastaRepository subastaRepository,
            PujaRepository pujaRepository,
            SubastaImagenRepository subastaImagenRepository
    ) {
        this.subastaRepository = subastaRepository;
        this.pujaRepository = pujaRepository;
        this.subastaImagenRepository = subastaImagenRepository;
    }

    @GetMapping
    public List<Subasta> listar() {
        return subastaRepository.findAll();
    }

    @PostMapping
    public Object crear(@RequestBody SubastaRequest request) {

        Object validacion = validarSubastaRequest(request, true);
        if (validacion != null) {
            return validacion;
        }

        Subasta subasta = new Subasta();

        subasta.setNombre(request.getNombre().trim());
        subasta.setDescripcion(request.getDescripcion());
        subasta.setPrecioInicial(request.getPrecioInicial());
        subasta.setCategoria(request.getCategoria());
        subasta.setImagen(request.getImagen());
        subasta.setFechaFin(request.getFechaFin());
        subasta.setUsuarioId(request.getUsuarioId());

        return subastaRepository.save(subasta);
    }

    @GetMapping("/buscar")
    public List<Subasta> buscarSubastas(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            @RequestParam(required = false, defaultValue = "TODAS") String estado,
            @RequestParam(required = false, defaultValue = "recientes") String orden
    ) {
        Specification<Subasta> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (estado != null && !estado.equalsIgnoreCase("TODAS")) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado.toUpperCase()));
            } else {
                predicates.add(root.get("estado").in("ACTIVA", "FINALIZADA"));
            }

            if (texto != null && !texto.isBlank()) {
                String textoBusqueda = "%" + texto.toLowerCase().trim() + "%";

                Predicate nombreLike = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nombre")),
                        textoBusqueda
                );

                Predicate descripcionLike = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("descripcion")),
                        textoBusqueda
                );

                predicates.add(criteriaBuilder.or(nombreLike, descripcionLike));
            }

            if (categoria != null && !categoria.isBlank() && !"Todas".equalsIgnoreCase(categoria)) {
                predicates.add(criteriaBuilder.equal(root.get("categoria"), categoria));
            }

            if (min != null && min >= 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("precioActual"), min));
            }

            if (max != null && max >= 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precioActual"), max));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = switch (orden) {
            case "precio_asc" -> Sort.by(Sort.Direction.ASC, "precioActual");
            case "precio_desc" -> Sort.by(Sort.Direction.DESC, "precioActual");
            case "ofertas_desc" -> Sort.by(Sort.Direction.DESC, "ofertas");
            case "fecha_fin_asc" -> Sort.by(Sort.Direction.ASC, "fechaFin");
            default -> Sort.by(Sort.Direction.DESC, "fechaCreacion");
        };

        return subastaRepository.findAll(spec, sort);
    }

    @GetMapping("/{id}")
    public Object obtenerPorId(@PathVariable Long id) {
        Subasta subasta = subastaRepository.findById(id).orElse(null);

        if (subasta == null) {
            return Map.of(
                    "ok", false,
                    "mensaje", "Subasta no encontrada"
            );
        }

        List<SubastaImagen> imagenes =
                subastaImagenRepository.findBySubastaIdOrderByOrdenAsc(id);

        return new SubastaDetalleResponse(subasta, imagenes);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Subasta> listarPorUsuario(@PathVariable Long usuarioId) {
        return subastaRepository.findByUsuarioId(usuarioId);
    }

    @GetMapping("/activas")
    public List<Subasta> listarActivas() {
        return subastaRepository.findByEstado("ACTIVA");
    }

    @PutMapping("/{id}")
    public Object editar(@PathVariable Long id, @RequestBody SubastaRequest request) {

        Subasta subasta = subastaRepository.findById(id).orElse(null);

        if (subasta == null) {
            return Map.of("ok", false, "mensaje", "Subasta no encontrada");
        }

        if (!"ACTIVA".equals(subasta.getEstado())) {
            return Map.of("ok", false, "mensaje", "Solo se pueden editar subastas activas");
        }

        boolean tienePujas = pujaRepository.existsBySubastaId(id);

        if (tienePujas) {
            return Map.of("ok", false, "mensaje", "No se puede editar una subasta que ya tiene pujas");
        }

        if (request.getUsuarioId() == null || !request.getUsuarioId().equals(subasta.getUsuarioId())) {
            return Map.of("ok", false, "mensaje", "No tienes permiso para editar esta subasta");
        }

        Object validacion = validarSubastaRequest(request, true);
        if (validacion != null) {
            return validacion;
        }

        subasta.setNombre(request.getNombre().trim());
        subasta.setDescripcion(request.getDescripcion());
        subasta.setPrecioInicial(request.getPrecioInicial());
        subasta.setPrecioActual(request.getPrecioInicial());
        subasta.setCategoria(request.getCategoria());
        subasta.setImagen(request.getImagen());
        subasta.setFechaFin(request.getFechaFin());

        return subastaRepository.save(subasta);
    }

    @PutMapping("/{id}/cancelar")
    public Object cancelar(@PathVariable Long id, @RequestBody Map<String, Long> request) {

        Subasta subasta = subastaRepository.findById(id).orElse(null);

        if (subasta == null) {
            return Map.of("ok", false, "mensaje", "Subasta no encontrada");
        }

        Long usuarioId = request.get("usuarioId");

        if (usuarioId == null || !usuarioId.equals(subasta.getUsuarioId())) {
            return Map.of("ok", false, "mensaje", "No tienes permiso para cancelar esta subasta");
        }

        if (!"ACTIVA".equals(subasta.getEstado())) {
            return Map.of("ok", false, "mensaje", "Solo se pueden cancelar subastas activas");
        }

        boolean tienePujas = pujaRepository.existsBySubastaId(id);

        if (tienePujas) {
            return Map.of("ok", false, "mensaje", "No se puede cancelar una subasta que ya tiene pujas");
        }

        subasta.setEstado("CANCELADA");
        subasta.setGanador("Subasta cancelada");
        subasta.setGanadorId(null);

        return subastaRepository.save(subasta);
    }

    @PutMapping("/{id}/finalizar")
    public Object finalizar(@PathVariable Long id) {

        Subasta subasta = subastaRepository.findById(id).orElse(null);

        if (subasta == null) {
            return Map.of("ok", false, "mensaje", "Subasta no encontrada");
        }

        if ("FINALIZADA".equals(subasta.getEstado())) {
            return Map.of("ok", false, "mensaje", "La subasta ya está finalizada");
        }

        if ("CANCELADA".equals(subasta.getEstado())) {
            return Map.of("ok", false, "mensaje", "No se puede finalizar una subasta cancelada");
        }

        subasta.setEstado("FINALIZADA");

        if (subasta.getGanadorId() == null) {
            subasta.setGanador("Sin ganador");
        }

        return subastaRepository.save(subasta);
    }

    private Object validarSubastaRequest(SubastaRequest request, boolean validarFecha) {

        if (request.getNombre() == null || request.getNombre().isBlank()) {
            return Map.of("ok", false, "mensaje", "El nombre es obligatorio");
        }

        if (request.getPrecioInicial() == null || request.getPrecioInicial() <= 0) {
            return Map.of("ok", false, "mensaje", "El precio inicial debe ser mayor que cero");
        }

        if (request.getUsuarioId() == null) {
            return Map.of("ok", false, "mensaje", "El usuario es obligatorio");
        }

        if (request.getFechaFin() == null) {
            return Map.of("ok", false, "mensaje", "La fecha de finalización es obligatoria");
        }

        if (validarFecha && request.getFechaFin().isBefore(LocalDateTime.now())) {
            return Map.of("ok", false, "mensaje", "La fecha de finalización debe ser futura");
        }

        return null;
    }
}