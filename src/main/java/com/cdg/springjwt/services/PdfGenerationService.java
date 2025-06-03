package com.cdg.springjwt.services;

import com.cdg.springjwt.models.Collaborateur;
import com.cdg.springjwt.models.Mission;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class PdfGenerationService {

    public byte[] generateDemandePdf(Mission mission, Collaborateur collab) {
        log.info("Starting PDF generation for mission: {} and collaborateur: {}", 
                mission != null ? mission.getCode() : "null", 
                collab != null ? collab.getColabMatricule() : "null");

        if (mission == null) {
            log.error("Cannot generate PDF: Mission is null");
            throw new IllegalArgumentException("Mission cannot be null");
        }

        if (collab == null) {
            log.error("Cannot generate PDF: Collaborateur is null");
            throw new IllegalArgumentException("Collaborateur cannot be null");
        }

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("Demande d'assignation de mission", titleFont));
            document.add(new Paragraph(" ", normalFont));

            // Log mission details being added
            log.debug("Adding mission details to PDF - Mission: {}, Code: {}", 
                     mission.getTitre(), mission.getCode());

            document.add(new Paragraph("Mission : " + (mission.getTitre() != null ? mission.getTitre() : "N/A"), normalFont));
            document.add(new Paragraph("Code mission : " + (mission.getCode() != null ? mission.getCode() : "N/A"), normalFont));
            document.add(new Paragraph("Collaborateur : " + 
                                     (collab.getColabPrenom() != null ? collab.getColabPrenom() : "N/A") + " " + 
                                     (collab.getColabNom() != null ? collab.getColabNom() : "N/A"), normalFont));
            document.add(new Paragraph("Matricule : " + (collab.getColabMatricule() != null ? collab.getColabMatricule() : "N/A"), normalFont));
            document.add(new Paragraph("Métier / Domaine : " + 
                                     (mission.getMetier() != null ? mission.getMetier() : "N/A") + " / " + 
                                     (mission.getDomaine() != null ? mission.getDomaine() : "N/A"), normalFont));

            // Handle potential null date
            String dateDebut = "N/A";
            if (mission.getDateDebut() != null) {
                try {
                    dateDebut = mission.getDateDebut().format(DateTimeFormatter.ISO_DATE);
                } catch (Exception e) {
                    log.warn("Error formatting date debut for mission {}: {}", mission.getCode(), e.getMessage());
                    dateDebut = mission.getDateDebut().toString();
                }
            } else {
                log.warn("Date debut is null for mission: {}", mission.getCode());
            }

            document.add(new Paragraph("Date de début : " + dateDebut, normalFont));
            document.add(new Paragraph("Date de la demande : " + java.time.LocalDate.now(), normalFont));

            document.close();
            
            log.info("PDF generated successfully for mission: {} and collaborateur: {}. Size: {} bytes", 
                    mission.getCode(), collab.getColabMatricule(), baos.size());

        } catch (DocumentException e) {
            log.error("Document creation error while generating PDF for mission: {} and collaborateur: {}. Error: {}", 
                     mission.getCode(), collab.getColabMatricule(), e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la création du document PDF", e);
        } catch (Exception e) {
            log.error("Unexpected error while generating PDF for mission: {} and collaborateur: {}. Error: {}", 
                     mission.getCode(), collab.getColabMatricule(), e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        } finally {
            try {
                if (document.isOpen()) {
                    document.close();
                    log.debug("Document closed successfully");
                }
            } catch (Exception e) {
                log.warn("Error closing document: {}", e.getMessage());
            }
        }

        return baos.toByteArray();
    }
}