package com.lapuja.api.service;

import com.lapuja.api.entity.Subasta;
import com.lapuja.api.entity.Usuario;
import com.lapuja.api.repository.SubastaRepository;
import com.lapuja.api.repository.UsuarioRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubastaFinalizacionService {

    private final SubastaRepository subastaRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;
    private final EmailService emailService;

    public SubastaFinalizacionService(
            SubastaRepository subastaRepository,
            UsuarioRepository usuarioRepository,
            NotificacionService notificacionService,
            EmailService emailService
    ) {
        this.subastaRepository = subastaRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
        this.emailService = emailService;
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
                Usuario ganador = usuarioRepository.findById(subasta.getGanadorId()).orElse(null);
                Usuario vendedor = usuarioRepository.findById(subasta.getUsuarioId()).orElse(null);

                notificacionService.crear(
                        subasta.getGanadorId(),
                        "Ganaste la subasta",
                        "Ganaste la subasta " + subasta.getNombre() + ". Ya puedes contactar al vendedor.",
                        "GANASTE_SUBASTA",
                        subasta.getId(),
                        "auction_detail"
                );

                if (ganador != null) {
                    emailService.enviarCorreoGanador(
                            ganador,
                            subasta.getNombre()
                    );
                }

                notificacionService.crear(
                        subasta.getUsuarioId(),
                        "Vendiste tu subasta",
                        "Tu subasta " + subasta.getNombre() + " fue vendida a " + subasta.getGanador() + ".",
                        "VENDISTE_SUBASTA",
                        subasta.getId(),
                        "auction_detail"
                );

                if (vendedor != null) {
                    emailService.enviarCorreoVendedor(
                            vendedor,
                            subasta.getNombre()
                    );
                }
            }

            subastaRepository.save(subasta);
        }
    }

    @Scheduled(fixedRate = 60000)
    public void enviarAvisosFinalizacion() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime unaHoraDespues = ahora.plusHours(1);

        List<Subasta> subastasPorFinalizar =
                subastaRepository.findByEstadoAndFechaFinBetweenAndAvisoFinalizacionEnviadoFalse(
                        "ACTIVA",
                        ahora,
                        unaHoraDespues
                );

        for (Subasta subasta : subastasPorFinalizar) {
            Usuario vendedor = usuarioRepository.findById(subasta.getUsuarioId()).orElse(null);

            if (vendedor != null) {
                emailService.enviarCorreoSubastaPorFinalizar(
                        vendedor,
                        subasta
                );
            }

            subasta.setAvisoFinalizacionEnviado(true);
            subastaRepository.save(subasta);
        }
    }
}