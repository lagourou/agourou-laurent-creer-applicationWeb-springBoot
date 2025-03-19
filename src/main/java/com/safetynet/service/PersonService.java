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
import com.safetynet.model.Person;

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

            logger.info("Fichier Json lu avec succès: {}", filePath);
            return data.getOrDefault("persons", List.of());
        }
    }

    public void writeJsonFile(List<Person> persons) throws IOException {
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

        fullData.put("persons", persons);

        try (OutputStream outputStream = new FileOutputStream(file)) {
            objectMapper.writeValue(outputStream, fullData);
        }

        logger.info("Fichier JSON mis à jour avec succès : {}", filePath);
    }

    public List<Person> add(List<Person> persons) throws IOException {
        logger.info("Ajout de personnes au fichier Json.");
        List<Person> existingPersons = readJsonFile();

        for (Person newPerson : persons) {
            if (existingPersons.stream().anyMatch(existing -> existing.getFirstName().equals(newPerson.getFirstName())
                    && existing.getLastName().equals(newPerson.getLastName()))) {

                logger.warn("La personne existe déjà: {}", newPerson.getFirstName(), newPerson.getLastName());
                continue;
            }
            existingPersons.add(newPerson);

        }
        writeJsonFile(existingPersons);
        logger.info("Ajout de personnes fait");
        return persons;
    }

    public List<Person> update(List<Person> persons) throws IOException {
        logger.info("Mise à jour des personnes.");

        List<Person> existingPersons = readJsonFile();
        for (Person person : persons) {
            for (Person existing : existingPersons) {
                if (existing.getFirstName().equals(person.getFirstName())
                        && existing.getLastName().equals(person.getLastName())) {
                    existing.setAddress(person.getAddress());
                    existing.setCity(person.getCity());
                    existing.setPhone(person.getPhone());
                    existing.setZip(person.getZip());
                    existing.setEmail(person.getEmail());

                    logger.info("Personne mise à jour : {}", existing);
                }
            }
        }
        writeJsonFile(existingPersons);
        return existingPersons;
    }

    public List<Person> delete(List<Person> persons) throws IOException {
        logger.info("Suppression de personnes.");

        List<Person> existingPersons = readJsonFile();

        existingPersons.removeIf(p -> persons.stream().anyMatch(person -> p.getFirstName().equals(person.getFirstName())
                && p.getLastName().equals(person.getLastName())));

        writeJsonFile(existingPersons);

        logger.info("Personne supprimeé: {}", existingPersons);
        return persons;
    }
}
