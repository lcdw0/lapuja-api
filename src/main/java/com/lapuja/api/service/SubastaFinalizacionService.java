package com.lapuja.api.service;

import com.lapuja.api.entity.Subasta;
import com.lapuja.api.repository.SubastaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubastaFinalizacionService {

    private final SubastaRepository subastaRepository;
    private final NotificacionService notificacionService;

    public SubastaFinalizacionService(
            SubastaRepository subastaRepository,
            NotificacionService notificacionService
    ) {
        this.subastaRepository = subastaRepository;
        this.notificacionService = notificacionService;
    }

    @Scheduled(fixedRate = 5000)
    public void finalizarSubastasVencidas() {
        LocalDateTime ahora = LocalDateTime.now();

        List<Subasta> subastasVencidas =
                subastaRepository.findByEstadoAndFechaFinBefore("ACTIVA", ahora);

        for (Subasta subasta : subastasVencidas) {
            subasta.setEstado("FINALIZADA");

            if (subasta.getGanadorId() == null) {
                subasta.setGanador("Sin ganador");

                notificacionService.crear(
                        subasta.getUsuarioId(),
                        "Subasta finalizada",
                        "Tu subasta " + subasta.getNombre() + " finalizó sin ganador.",
                        "SUBASTA_FINALIZADA",
                        subasta.getId(),
                        "auction_detail"
                );
            } else {
                notificacionService.crear(
                        subasta.getGanadorId(),
                        "Ganaste la subasta",
                        "Ganaste la subasta " + subasta.getNombre() + ". Ya puedes contactar al vendedor.",
                        "GANASTE_SUBASTA",
                        subasta.getId(),
                        "auction_detail"
                );

                notificacionService.crear(
                        subasta.getUsuarioId(),
                        "Vendiste tu subasta",
                        "Tu subasta " + subasta.getNombre() + " fue vendida a " + subasta.getGanador() + ".",
                        "VENDISTE_SUBASTA",
                        subasta.getId(),
                        "auction_detail"
                );
            }

            subastaRepository.save(subasta);
        }
    }
}