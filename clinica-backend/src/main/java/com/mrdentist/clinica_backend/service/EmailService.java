package com.mrdentist.clinica_backend.service;

import com.mrdentist.clinica_backend.entity.Cita;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void enviarCorreoCancelacion(Cita cita, String motivoCancelacion) {
        String correoDestinatario = cita.getPaciente().getCorreo();
        if (correoDestinatario == null || correoDestinatario.trim().isEmpty()) {
            System.out.println("[EmailService] No se puede enviar correo de cancelación: el paciente no tiene dirección de correo registrada.");
            return;
        }

        String nombrePaciente = cita.getPaciente().getNombres() + " " + cita.getPaciente().getApellidos();
        String nombreMedico = cita.getMedico().getNombres() + " " + cita.getMedico().getApellidos();
        String especialidad = cita.getMedico().getEspecialidad().getNombre();
        String fechaHoraStr = cita.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        String asunto = "Cita Cancelada - Clínica Mr. Dentist";
        String contenidoHtml = "<html>" +
                "<body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #fcfcfc;'>" +
                "<h2 style='color: #d9534f; border-bottom: 2px solid #d9534f; padding-bottom: 10px;'>Aviso de Cancelación de Cita</h2>" +
                "<p>Estimado(a) <strong>" + nombrePaciente + "</strong>,</p>" +
                "<p>Le informamos que su cita médica programada en nuestra clínica ha sido cancelada.</p>" +
                "<div style='background-color: #f9f9f9; padding: 15px; border-left: 4px solid #d9534f; margin: 15px 0;'>" +
                "<p style='margin: 5px 0;'><strong>Médico:</strong> " + nombreMedico + "</p>" +
                "<p style='margin: 5px 0;'><strong>Especialidad:</strong> " + especialidad + "</p>" +
                "<p style='margin: 5px 0;'><strong>Fecha y Hora:</strong> " + fechaHoraStr + "</p>" +
                "<p style='margin: 5px 0;'><strong>Motivo de Cancelación:</strong> " + motivoCancelacion + "</p>" +
                "</div>" +
                "<p>Si desea reprogramar su cita, por favor ingrese al sistema o comuníquese con atención al cliente.</p>" +
                "<p style='margin-top: 30px; font-size: 0.9em; color: #777;'>Atentamente,<br><strong>Equipo de Clínica Mr. Dentist</strong></p>" +
                "</div>" +
                "</body>" +
                "</html>";

        // Imprimir por consola para depuración y verificación
        System.out.println("======================================================================");
        System.out.println("ENVIANDO CORREO DE CANCELACIÓN:");
        System.out.println("Destinatario: " + correoDestinatario);
        System.out.println("Asunto: " + asunto);
        System.out.println("Detalles:\n" + contenidoHtml.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " "));
        System.out.println("======================================================================");

        if (mailSender == null) {
            System.out.println("[EmailService] JavaMailSender no está configurado o inicializado. Correo guardado únicamente en log.");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(correoDestinatario);
            helper.setSubject(asunto);
            helper.setText(contenidoHtml, true);
            mailSender.send(message);
            System.out.println("[EmailService] Correo enviado exitosamente vía SMTP.");
        } catch (Exception e) {
            System.out.println("[EmailService] Error al enviar el correo vía SMTP: " + e.getMessage());
            System.out.println("[EmailService] Nota: El sistema continuará funcionando correctamente usando el log de consola.");
        }
    }
}
