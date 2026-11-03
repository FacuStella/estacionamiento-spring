package com.estacionamiento.services;

import com.estacionamiento.models.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Manda los mails a los usuarios. Si no hay servidor de mail configurado (spring.mail.host),
 * no se manda nada: el contenido se escribe en la consola del backend, para poder probar en desarrollo.
 */
@Slf4j
@Service
public class MailService {

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String remitente;

    public MailService(ObjectProvider<JavaMailSender> mailSender,
                       @Value("${app.mail.remitente:estacionamiento@localhost}") String remitente) {
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    /** Si el mail no se puede enviar lanza MailException, y la operacion que lo pidio se deshace. */
    public void enviarClaveTemporal(Usuario usuario, String clave) {
        String asunto = "Tu acceso al estacionamiento";
        String texto = "Hola " + usuario.getUsername() + ",\n\n"
                + "Se creó (o se restableció) tu acceso al sistema del estacionamiento.\n\n"
                + "Usuario: " + usuario.getUsername() + "\n"
                + "Contraseña temporal: " + clave + "\n\n"
                + "Al ingresar por primera vez vas a tener que elegir una contraseña nueva.\n";

        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            log.warn("No hay servidor de mail configurado (spring.mail.host). Mail que se hubiera enviado a {}:\n{}",
                    usuario.getMail(), texto);
            return;
        }
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(usuario.getMail());
        mensaje.setSubject(asunto);
        mensaje.setText(texto);
        sender.send(mensaje);
    }
}
