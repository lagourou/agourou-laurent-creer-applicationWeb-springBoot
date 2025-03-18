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
        return persons;
    }

    public List<Person> delete(List<Person> persons) throws IOException {
        logger.info("Suppression de personnes.");

        List<Person> existingPersons = readJsonFile();

        existingPersons.removeIf(p -> persons.stream().anyMatch(person -> p.getFirstName().equals(person.getFirstName())
                && p.getLastName().equals(person.getLastName())));

        logger.info("Personne supprimeé: {}", existingPersons);
        return persons;
    }
}
