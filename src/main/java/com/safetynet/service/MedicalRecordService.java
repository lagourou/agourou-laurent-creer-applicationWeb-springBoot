package com.safetynet.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.model.MedicalRecord;

import org.springframework.beans.factory.annotation.Value;

@Service
public class MedicalRecordService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

    @Value("${data.file.path}")
    private String filepath;

    public List<MedicalRecord> readJsonFile() throws IOException {
        File file = new File(filepath);
        if (!file.exists()) {
            logger.error("Fichier Json n'existe pas: {}", filepath);
            throw new IOException("Fichier Json introuvable");
        }
        try (InputStream inputStream = new FileInputStream(file)) {

            Map<String, List<MedicalRecord>> data = objectMapper.readValue(inputStream, new TypeReference<>() {
            });

            logger.info("Fichier Json lu avec succès: {}", filepath);
            return data.getOrDefault("medicalrecords", List.of());
        }
    }

    public void writeJsonFile(List<MedicalRecord> medicalRecords) throws IOException {
        File file = new File(filepath);
        Map<String, Object> fullData;

        if (file.exists()) {
            try (InputStream inputStream = new FileInputStream(file)) {
                fullData = objectMapper.readValue(inputStream, new TypeReference<>() {
                });
            }
        } else {
            fullData = Map.of();
        }
        fullData.put("medicalrecords", medicalRecords);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            objectMapper.writeValue(outputStream, fullData);
        }
        logger.info("Fichier JSON mis à jour avec succès: {}", filepath);
    }

    public List<MedicalRecord> add(List<MedicalRecord> medicalRecords) throws IOException {
        logger.info("Ajout d'un dossier médical");
        List<MedicalRecord> existingMedicalRecords = readJsonFile();

        for (MedicalRecord newMedicalRecord : medicalRecords) {
            if (existingMedicalRecords.stream()
                    .anyMatch(existing -> existing.getFirstName().equals(newMedicalRecord.getFirstName())
                            && existing.getLastName().equals(newMedicalRecord.getLastName())
                            && existing.getBirthdate().equals(newMedicalRecord.getBirthdate()))) {

                logger.warn("Le dossier médical existe déjà: {}", newMedicalRecord.getFirstName(),
                        newMedicalRecord.getLastName(), newMedicalRecord.getBirthdate());
                continue;
            }
            existingMedicalRecords.add(newMedicalRecord);
        }
        writeJsonFile(existingMedicalRecords);
        logger.info("Ajout du dossier médical fait");
        return medicalRecords;
    }
}