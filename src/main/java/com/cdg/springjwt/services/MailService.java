package com.cdg.springjwt.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void envoyerDemandeAvecPdf(String destinataire, byte[] pdfContent, String nomFichier) throws MessagingException {
        log.info("Sending email with PDF attachment to: {} with filename: {}", destinataire, nomFichier);

        if (destinataire == null || destinataire.trim().isEmpty()) {
            log.error("Cannot send email: destinataire is null or empty");
            throw new IllegalArgumentException("Destinataire cannot be null or empty");
        }

        if (pdfContent == null || pdfContent.length == 0) {
            log.error("Cannot send email: PDF content is null or empty");
            throw new IllegalArgumentException("PDF content cannot be null or empty");
        }

        if (nomFichier == null || nomFichier.trim().isEmpty()) {
            log.error("Cannot send email: filename is null or empty");
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(destinataire);
            helper.setSubject("Nouvelle demande d'assignation de mission");
            helper.setText("Veuillez trouver ci-joint la demande d'assignation pour l'un de vos collaborateurs.", false);

            helper.addAttachment(nomFichier, new ByteArrayResource(pdfContent));

            mailSender.send(message);
            
            log.info("Email sent successfully to: {} with attachment: {} (size: {} bytes)", 
                    destinataire, nomFichier, pdfContent.length);

        } catch (MessagingException e) {
            log.error("MessagingException while sending email to: {}. Error: {}", destinataire, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while sending email to: {}. Error: {}", destinataire, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }
}