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

    public SubastaFinalizacionService(SubastaRepository subastaRepository) {
        this.subastaRepository = subastaRepository;
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
            }

            subastaRepository.save(subasta);
        }
    }
}