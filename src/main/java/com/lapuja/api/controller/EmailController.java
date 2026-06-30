package com.lapuja.api.controller;

import com.lapuja.api.service.EmailCuentaService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailController {

    private final EmailCuentaService emailCuentaService;

    public EmailController(EmailCuentaService emailCuentaService) {
        this.emailCuentaService = emailCuentaService;
    }

    @PostMapping("/reenviar-verificacion/{usuarioId}")
    public Object reenviarVerificacion(@PathVariable Long usuarioId) {
        return emailCuentaService.reenviarVerificacion(usuarioId);
    }

    @GetMapping("/verificar")
    public Object verificarCorreo(@RequestParam String token) {
        return emailCuentaService.verificarCorreo(token);
    }

    @PostMapping("/solicitar-recuperacion")
    public Object solicitarRecuperacion(@RequestBody Map<String, String> request) {
        return emailCuentaService.solicitarRecuperacion(request);
    }

    @PostMapping("/restablecer-password")
    public Object restablecerPassword(@RequestBody Map<String, String> request) {
        return emailCuentaService.restablecerPassword(request);
    }
}