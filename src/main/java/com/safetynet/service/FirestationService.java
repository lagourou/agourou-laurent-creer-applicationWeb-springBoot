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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.model.Firestation;

@Service
public class FirestationService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(FirestationService.class);

    @Value("${data.file.path}")
    private String filePath;

    public List<Firestation> readJsonFile() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {

            logger.error("Fichier Json n'existe pas: {}", filePath);
            throw new IOException("Fichier Json introuvable");
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            Map<String, List<Firestation>> data = objectMapper.readValue(inputStream, new TypeReference<>() {
            });

            logger.info("Fichier Json lue avec succès: {}", filePath);
            return data.getOrDefault("firestations", List.of());
        }
    }

    public void writeJsonFile(List<Firestation> firestations) throws IOException {
        File file = new File(filePath);
        Map<String, Object> fullData;

        if (file.exists()) {
            try (InputStream inputStream = new FileInputStream(file)) {
                fullData = objectMapper.readValue(inputStream, new TypeReference<>() {
                });
            }
        } else {
            fullData = Map.of();
        }

        fullData.put("firestations", firestations);

        try (OutputStream outputStream = new FileOutputStream(file)) {
            objectMapper.writeValue(outputStream, fullData);
        }

        logger.info("Fichier JSON mis à jour avec succès : {}", filePath);
    }

    public List<Firestation> add(List<Firestation> firestations) throws IOException {
        logger.info("Ajout de casernes au fichier Json.");
        List<Firestation> existingFirestations = readJsonFile();

        for (Firestation newFirestation : firestations) {
            if (existingFirestations.stream()
                    .anyMatch(existing -> existing.getAddress().equals(newFirestation.getAddress()))) {
                logger.warn("La caserne existe déjà : {}", newFirestation.getAddress());
            }
            existingFirestations.add(newFirestation);
        }

        writeJsonFile(existingFirestations);
        logger.info("Ajout de casernes fait");
        return firestations;
    }

}
