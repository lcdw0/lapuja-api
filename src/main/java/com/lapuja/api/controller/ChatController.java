package com.lapuja.api.controller;

import com.lapuja.api.dto.ChatConversacionResponse;
import com.lapuja.api.dto.ChatMensajeRequest;
import com.lapuja.api.dto.ChatMensajeResponse;
import com.lapuja.api.entity.ChatConversacion;
import com.lapuja.api.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/conversaciones")
    public ChatConversacionResponse crearConversacion(
            @RequestParam Long subastaId,
            @RequestParam Long compradorId,
            @RequestParam Long vendedorId
    ) {
        ChatConversacion conversacion = chatService.crearConversacionSiNoExiste(
                subastaId,
                compradorId,
                vendedorId
        );

        return chatService.listarConversaciones(compradorId)
                .stream()
                .filter(c -> c.getId().equals(conversacion.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se pudo obtener la conversación creada"));
    }

    @GetMapping("/conversaciones/{usuarioId}")
    public List<ChatConversacionResponse> listarConversaciones(@PathVariable Long usuarioId) {
        return chatService.listarConversaciones(usuarioId);
    }

    @GetMapping("/conversaciones/{conversacionId}/mensajes")
    public List<ChatMensajeResponse> listarMensajes(@PathVariable Long conversacionId) {
        return chatService.listarMensajes(conversacionId);
    }

    @PostMapping("/conversaciones/{conversacionId}/mensajes")
    public ChatMensajeResponse enviarMensaje(
            @PathVariable Long conversacionId,
            @RequestBody ChatMensajeRequest request
    ) {
        return chatService.enviarMensaje(conversacionId, request);
    }

    @PutMapping("/conversaciones/{conversacionId}/leer")
    public void marcarComoLeidos(
            @PathVariable Long conversacionId,
            @RequestParam Long usuarioId
    ) {
        chatService.marcarMensajesComoLeidos(conversacionId, usuarioId);
    }
}