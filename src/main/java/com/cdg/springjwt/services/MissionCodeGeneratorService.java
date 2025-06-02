package com.cdg.springjwt.services;


import com.cdg.springjwt.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class MissionCodeGeneratorService {

    @Autowired
    private MissionRepository missionRepository;

    public String generateMissionCode() {
        String prefix = "MIS";
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));

        // Find the next available sequential number for this year
        String basePattern = prefix + year;
        Long count = missionRepository.countByCodeStartingWith(basePattern);

        // Generate code with 3-digit sequential number
        String sequentialNumber = String.format("%03d", count + 1);
        String generatedCode = basePattern + sequentialNumber;

        // Ensure uniqueness (in case of concurrent creation)
        while (missionRepository.existsByCode(generatedCode)) {
            count++;
            sequentialNumber = String.format("%03d", count + 1);
            generatedCode = basePattern + sequentialNumber;
        }

        return generatedCode;
    }
}
