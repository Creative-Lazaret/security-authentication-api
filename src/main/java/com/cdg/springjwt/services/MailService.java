package com.cdg.springjwt.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void envoyerDemandeAvecPdf(String destinataire, byte[] pdfContent, String nomFichier) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(destinataire);
        helper.setSubject("Nouvelle demande d’assignation de mission");
        helper.setText("Veuillez trouver ci-joint la demande d’assignation pour l’un de vos collaborateurs.", false);

        helper.addAttachment(nomFichier, new ByteArrayResource(pdfContent));

        mailSender.send(message);
    }
}
