package com.lapuja.api.service;

import com.lapuja.api.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.lapuja.api.entity.Subasta;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String correoOrigen;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCorreoVerificacion(Usuario usuario, String token) {
        String asunto = "Verifica tu cuenta en LaPuja";

        String enlace = "lapuja://verificar-correo?token=" + token;

        String contenido = """
            <p>Hola <strong>%s</strong>,</p>
            <p>Gracias por registrarte en <strong>LaPuja</strong>.</p>
            <p>Para activar tu cuenta, presiona el siguiente botón:</p>
            <p style="margin: 24px 0;">
                <a href="%s" style="background-color: #2563eb; color: #ffffff; padding: 12px 18px; text-decoration: none; border-radius: 8px; display: inline-block;">
                    Verificar mi cuenta
                </a>
            </p>
            <p>Este enlace expirará en 24 horas.</p>
            <p>Si no creaste esta cuenta, puedes ignorar este correo.</p>
            """.formatted(usuario.getNombre(), enlace);

        enviarCorreoHtml(
                usuario.getCorreo(),
                asunto,
                plantillaBase("Verificación de cuenta", contenido)
        );
    }

    public void enviarCorreoRecuperacionPassword(Usuario usuario, String token) {
        String asunto = "Recuperación de contraseña - LaPuja";

        String enlace = "lapuja://recuperar-password?token=" + token;

        String contenido = """
            <p>Hola <strong>%s</strong>,</p>
            <p>Recibimos una solicitud para restablecer tu contraseña.</p>
            <p>Presiona el siguiente botón para continuar:</p>
            <p style="margin: 24px 0;">
                <a href="%s" style="background-color: #2563eb; color: #ffffff; padding: 12px 18px; text-decoration: none; border-radius: 8px; display: inline-block;">
                    Restablecer contraseña
                </a>
            </p>
            <p>Este enlace expirará en 30 minutos.</p>
            <p>Si no solicitaste este cambio, puedes ignorar este correo.</p>
            """.formatted(usuario.getNombre(), enlace);

        enviarCorreoHtml(
                usuario.getCorreo(),
                asunto,
                plantillaBase("Recuperación de contraseña", contenido)
        );
    }

    public void enviarCorreoGanador(Usuario usuario, String tituloSubasta) {
        String asunto = "Ganaste una subasta en LaPuja";

        String contenido = """
            <p>Hola <strong>%s</strong>,</p>
            <p>¡Felicidades! Ganaste la siguiente subasta:</p>
            <p style="font-size: 18px;">
                <strong>%s</strong>
            </p>
            <p>Ya puedes revisar los detalles desde la aplicación y contactar al vendedor.</p>
            """.formatted(usuario.getNombre(), tituloSubasta);

        enviarCorreoHtml(
                usuario.getCorreo(),
                asunto,
                plantillaBase("Ganaste la subasta", contenido)
        );
    }

    public void enviarCorreoVendedor(Usuario usuario, String tituloSubasta) {
        String asunto = "Vendiste una subasta en LaPuja";

        String contenido = """
            <p>Hola <strong>%s</strong>,</p>
            <p>Tu subasta fue vendida exitosamente:</p>
            <p style="font-size: 18px;">
                <strong>%s</strong>
            </p>
            <p>Ya puedes revisar los detalles desde la aplicación y coordinar con el comprador.</p>
            """.formatted(usuario.getNombre(), tituloSubasta);

        enviarCorreoHtml(
                usuario.getCorreo(),
                asunto,
                plantillaBase("Vendiste tu subasta", contenido)
        );
    }

    public void enviarCorreoOfertaSuperada(Usuario usuario, String tituloSubasta) {
        String asunto = "Superaron tu oferta en LaPuja";

        String contenido = """
            <p>Hola <strong>%s</strong>,</p>
            <p>Tu oferta fue superada en la siguiente subasta:</p>
            <p style="font-size: 18px;">
                <strong>%s</strong>
            </p>
            <p>El monto retenido fue reembolsado a tu wallet.</p>
            <p>Puedes entrar a la aplicación para ofertar nuevamente.</p>
            """.formatted(usuario.getNombre(), tituloSubasta);

        enviarCorreoHtml(
                usuario.getCorreo(),
                asunto,
                plantillaBase("Oferta superada", contenido)
        );
    }

    public void enviarCorreoRecarga(Usuario usuario, Double monto) {
        String asunto = "Confirmación de recarga - LaPuja";

        String contenido = """
            <p>Hola <strong>%s</strong>,</p>
            <p>Tu recarga fue registrada correctamente.</p>
            <p style="font-size: 18px;">
                Monto recargado: <strong>C$ %.2f</strong>
            </p>
            <p>Ya puedes usar tu saldo disponible dentro de la aplicación.</p>
            """.formatted(usuario.getNombre(), monto);

        enviarCorreoHtml(
                usuario.getCorreo(),
                asunto,
                plantillaBase("Recarga realizada", contenido)
        );
    }
    public void enviarCorreoSubastaPorFinalizar(Usuario usuario, Subasta subasta) {

        String asunto = "Tu subasta está por finalizar";

        Duration restante = Duration.between(
                LocalDateTime.now(),
                subasta.getFechaFin()
        );

        long horas = restante.toHours();
        long minutos = restante.toMinutesPart();

        String tiempoRestante;

        if (horas > 0 && minutos > 0) {
            tiempoRestante = horas + " hora(s) y " + minutos + " minuto(s)";
        } else if (horas > 0) {
            tiempoRestante = horas + " hora(s)";
        } else {
            tiempoRestante = minutos + " minuto(s)";
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String contenido = """
            <p>Hola <strong>%s</strong>,</p>

            <p>Tu subasta está próxima a finalizar.</p>

            <p style="font-size:18px;">
                <strong>%s</strong>
            </p>

            <p>
                Tiempo restante:
                <strong>%s</strong>
            </p>

            <p>
                Fecha de finalización:
                <strong>%s</strong>
            </p>

            <p>
                Si deseas realizar alguna modificación o compartir tu publicación,
                aún estás a tiempo antes de que finalice.
            </p>
            """.formatted(
                usuario.getNombre(),
                subasta.getNombre(),
                tiempoRestante,
                subasta.getFechaFin().format(formatter)
        );

        enviarCorreoHtml(
                usuario.getCorreo(),
                asunto,
                plantillaBase("Subasta próxima a finalizar", contenido)
        );
    }

    private void enviarCorreoHtml(String destino, String asunto, String contenidoHtml) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(correoOrigen);
            helper.setTo(destino);
            helper.setSubject(asunto);
            helper.setText(contenidoHtml, true);

            mailSender.send(mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String plantillaBase(String titulo, String contenido) {
        return """
            <div style="font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 24px;">
                <div style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 12px; padding: 28px;">
                    <h1 style="color: #1f2937; margin-bottom: 8px;">LaPuja</h1>
                    <h2 style="color: #2563eb;">%s</h2>
                    <div style="color: #374151; font-size: 15px; line-height: 1.6;">
                        %s
                    </div>
                    <hr style="border: none; border-top: 1px solid #e5e7eb; margin: 24px 0;">
                    <p style="color: #6b7280; font-size: 12px;">
                        Este correo fue enviado automáticamente por LaPuja.
                    </p>
                </div>
            </div>
            """.formatted(titulo, contenido);
    }
}