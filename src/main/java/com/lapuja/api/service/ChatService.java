package com.lapuja.api.service;

import com.lapuja.api.dto.ChatConversacionResponse;
import com.lapuja.api.dto.ChatMensajeRequest;
import com.lapuja.api.dto.ChatMensajeResponse;
import com.lapuja.api.entity.ChatConversacion;
import com.lapuja.api.entity.ChatMensaje;
import com.lapuja.api.entity.Subasta;
import com.lapuja.api.entity.Usuario;
import com.lapuja.api.repository.ChatConversacionRepository;
import com.lapuja.api.repository.ChatMensajeRepository;
import com.lapuja.api.repository.SubastaRepository;
import com.lapuja.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import com.lapuja.api.enums.TipoMensaje;

import java.util.List;

@Service
public class ChatService {

    private final ChatConversacionRepository chatConversacionRepository;
    private final ChatMensajeRepository chatMensajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final SubastaRepository subastaRepository;
    private final UsuarioActividadService usuarioActividadService;

    public ChatService(ChatConversacionRepository chatConversacionRepository,
                       ChatMensajeRepository chatMensajeRepository,
                       UsuarioRepository usuarioRepository,
                       SubastaRepository subastaRepository,
                       UsuarioActividadService usuarioActividadService) {
        this.chatConversacionRepository = chatConversacionRepository;
        this.chatMensajeRepository = chatMensajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.subastaRepository = subastaRepository;
        this.usuarioActividadService = usuarioActividadService;
    }

    public ChatConversacion crearConversacionSiNoExiste(Long subastaId, Long compradorId, Long vendedorId) {
        Subasta subasta = subastaRepository.findById(subastaId)
                .orElseThrow(() -> new RuntimeException("Subasta no encontrada"));

        Usuario comprador = usuarioRepository.findById(compradorId)
                .orElseThrow(() -> new RuntimeException("Comprador no encontrado"));

        Usuario vendedor = usuarioRepository.findById(vendedorId)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));

        return chatConversacionRepository
                .findBySubastaAndCompradorAndVendedor(subasta, comprador, vendedor)
                .orElseGet(() -> {
                    ChatConversacion conversacion = new ChatConversacion();
                    conversacion.setSubasta(subasta);
                    conversacion.setComprador(comprador);
                    conversacion.setVendedor(vendedor);
                    conversacion.setActivo(true);
                    return chatConversacionRepository.save(conversacion);
                });
    }

    public List<ChatConversacionResponse> listarConversaciones(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioActividadService.actualizarActividad(usuarioId);

        return chatConversacionRepository
                .findByCompradorOrVendedorOrderByFechaCreacionDesc(usuario, usuario)
                .stream()
                .map(conversacion -> convertirConversacionResponse(conversacion, usuarioId))
                .toList();
    }

    public List<ChatMensajeResponse> listarMensajes(Long conversacionId) {
        ChatConversacion conversacion = chatConversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));

        return chatMensajeRepository
                .findByConversacionOrderByFechaEnvioAsc(conversacion)
                .stream()
                .map(this::convertirMensajeResponse)
                .toList();
    }

    public ChatMensajeResponse enviarMensaje(Long conversacionId, ChatMensajeRequest request) {
        ChatConversacion conversacion = chatConversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));

        Usuario emisor = usuarioRepository.findById(request.getEmisorId())
                .orElseThrow(() -> new RuntimeException("Usuario emisor no encontrado"));

        validarUsuarioPerteneceConversacion(conversacion, emisor.getId());

        usuarioActividadService.actualizarActividad(emisor.getId());

        TipoMensaje tipoMensaje = TipoMensaje.TEXTO;

        if (request.getTipoMensaje() != null && !request.getTipoMensaje().isBlank()) {
            try {
                tipoMensaje = TipoMensaje.valueOf(request.getTipoMensaje().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Tipo de mensaje no válido");
            }
        }

        String contenido = request.getContenido() != null
                ? request.getContenido().trim()
                : null;

        String imagenUrl = request.getImagenUrl() != null
                ? request.getImagenUrl().trim()
                : null;

        if (tipoMensaje == TipoMensaje.TEXTO && (contenido == null || contenido.isBlank())) {
            throw new RuntimeException("El mensaje de texto no puede estar vacío");
        }

        if (tipoMensaje == TipoMensaje.IMAGEN && (imagenUrl == null || imagenUrl.isBlank())) {
            throw new RuntimeException("El mensaje de imagen debe incluir una imagen");
        }

        ChatMensaje mensaje = new ChatMensaje();
        mensaje.setConversacion(conversacion);
        mensaje.setEmisor(emisor);
        mensaje.setContenido(contenido);
        mensaje.setTipoMensaje(tipoMensaje);
        mensaje.setImagenUrl(imagenUrl);
        mensaje.setLeido(false);
        mensaje.setEliminado(false);

        ChatMensaje guardado = chatMensajeRepository.save(mensaje);

        return convertirMensajeResponse(guardado);
    }

    public void marcarMensajesComoLeidos(Long conversacionId, Long usuarioId) {
        ChatConversacion conversacion = chatConversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));

        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        validarUsuarioPerteneceConversacion(conversacion, usuarioId);

        usuarioActividadService.actualizarActividad(usuarioId);

        List<ChatMensaje> mensajes = chatMensajeRepository.findByConversacionOrderByFechaEnvioAsc(conversacion);

        mensajes.forEach(mensaje -> {
            if (!mensaje.getEmisor().getId().equals(usuarioId)) {
                mensaje.setLeido(true);
            }
        });

        chatMensajeRepository.saveAll(mensajes);
    }

    private void validarUsuarioPerteneceConversacion(ChatConversacion conversacion, Long usuarioId) {
        boolean esComprador = conversacion.getComprador().getId().equals(usuarioId);
        boolean esVendedor = conversacion.getVendedor().getId().equals(usuarioId);

        if (!esComprador && !esVendedor) {
            throw new RuntimeException("El usuario no pertenece a esta conversación");
        }
    }

    private ChatConversacionResponse convertirConversacionResponse(ChatConversacion conversacion, Long usuarioId) {

        List<ChatMensaje> mensajes = chatMensajeRepository
                .findByConversacionOrderByFechaEnvioAsc(conversacion);

        ChatMensaje ultimoMensaje = mensajes.isEmpty()
                ? null
                : mensajes.get(mensajes.size() - 1);

        Long mensajesNoLeidos = chatMensajeRepository
                .countByConversacionAndLeidoFalseAndEmisorIdNot(
                        conversacion,
                        usuarioId
                );

        Usuario otroUsuario = conversacion.getComprador().getId().equals(usuarioId)
                ? conversacion.getVendedor()
                : conversacion.getComprador();

        Boolean otroUsuarioEnLinea = usuarioActividadService.estaEnLinea(otroUsuario);

        return new ChatConversacionResponse(
                conversacion.getId(),
                conversacion.getSubasta().getId(),
                conversacion.getSubasta().getNombre(),
                conversacion.getComprador().getId(),
                conversacion.getComprador().getNombre(),
                conversacion.getComprador().getFotoPerfil(),
                conversacion.getVendedor().getId(),
                conversacion.getVendedor().getNombre(),
                conversacion.getVendedor().getFotoPerfil(),
                conversacion.getActivo(),
                conversacion.getFechaCreacion(),
                ultimoMensaje != null ? ultimoMensaje.getContenido() : null,
                ultimoMensaje != null ? ultimoMensaje.getFechaEnvio() : null,
                mensajesNoLeidos,
                otroUsuarioEnLinea,
                otroUsuario.getUltimaActividad(),
                otroUsuario.getFotoPerfil()
        );
    }

    private ChatMensajeResponse convertirMensajeResponse(ChatMensaje mensaje) {
        return new ChatMensajeResponse(
                mensaje.getId(),
                mensaje.getConversacion().getId(),
                mensaje.getEmisor().getId(),
                mensaje.getEmisor().getNombre(),
                mensaje.getContenido(),
                mensaje.getTipoMensaje() != null ? mensaje.getTipoMensaje().name() : "TEXTO",
                mensaje.getImagenUrl(),
                mensaje.getLeido(),
                mensaje.getEliminado(),
                mensaje.getFechaEnvio(),
                mensaje.getFechaEdicion()
        );
    }
}