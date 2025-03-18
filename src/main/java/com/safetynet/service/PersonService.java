package com.safetynet.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PersonService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

    @Value("${data.file.path}")
    private String filePath;

    public List<Person> readJsonFile() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {

            logger.error("Fichier Json n'existe pas: {}", filePath);
            throw new IOException("Fichier Json introuvable");
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            Map<String, List<Person>> data = objectMapper.readValue(inputStream, new TypeReference<>() {
            });

            logger.info("Fichier Json lue avec succès: {}", filePath);
            return data.getOrDefault("persons", List.of());
        }
    }

    public List<Person> add(List<Person> persons) throws IOException {
        logger.info("Ajout de personnes au fichier.");

        List<Person> existingPersons = readJsonFile();
        existingPersons.addAll(persons);

        return persons;
    }
}
