package com.cdg.springjwt.services;

import com.cdg.springjwt.models.Collaborateur;
import com.cdg.springjwt.models.Mission;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGenerationService {

    public byte[] generateDemandePdf(Mission mission, Collaborateur collab) {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("Demande d’assignation de mission", titleFont));
            document.add(new Paragraph(" ", normalFont));

            document.add(new Paragraph("Mission : " + mission.getTitre(), normalFont));
            document.add(new Paragraph("Code mission : " + mission.getCode(), normalFont));
            document.add(new Paragraph("Collaborateur : " + collab.getColabPrenom() + " " + collab.getColabNom(), normalFont));
            document.add(new Paragraph("Matricule : " + collab.getColabMatricule(), normalFont));
            document.add(new Paragraph("Métier / Domaine : " + mission.getMetier() + " / " + mission.getDomaine(), normalFont));
            document.add(new Paragraph("Date de début : " + mission.getDateDebut().format(DateTimeFormatter.ISO_DATE), normalFont));
            document.add(new Paragraph("Date de la demande : " + java.time.LocalDate.now(), normalFont));

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }

        return baos.toByteArray();
    }
}

