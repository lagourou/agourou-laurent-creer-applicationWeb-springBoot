package com.safetynet.service.dataService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

/**
 * Service pour la gestion des fichiers JSON.
 * Permet de lire et de mettre à jour les données dans un fichier JSON.
 */
@Slf4j
@Component
public class DataLoad {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${data.file.path}")
    private String filePath;

    /**
     * Lit le fichier JSON.
     *
     * @param <T>           Le type générique des objets.
     * @param key           La clé pour accéder aux données dans le fichier JSON.
     * @param typeReference La structure des données attendues.
     * @return Une liste des objets correspondant à la clé donnée ou une liste vide
     *         si
     *         aucune donnée n'est trouvée.
     * @throws IOException En cas de problème lors de la lecture du fichier JSON.
     */
    public <T> List<T> readJsonFile(String key, TypeReference<Map<String, List<T>>> typeReference)
            throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {

            log.error("Fichier Json n'existe pas: {}", filePath);
            throw new IOException("Fichier Json introuvable");
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            Map<String, List<T>> data = objectMapper.readValue(inputStream, typeReference);

            log.info("Fichier Json lu avec succès: {}", filePath);
            return data.getOrDefault(key, List.of());
        }
    }

    /**
     * Met à jour le fichier JSON.
     *
     * @param <T>     Le type générique des objets.
     * @param key     La clé pour accéder aux données dans le fichier JSON.
     * @param objects Une liste d'objets à sauvegarder dans le fichier JSON.
     * @throws IOException En cas de problème lors de l'écriture dans le fichier
     *                     JSON.
     */
    public <T> void writeJsonFile(String key, List<T> objects) throws IOException {
        File file = new File(filePath);
        Map<String, Object> fullData;

        if (file.exists()) {
            try (InputStream inputStream = new FileInputStream(file)) {
                fullData = objectMapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {
                });
            }
        } else {
            fullData = new HashMap<>();
        }
        fullData.put(key, objects);

        try (OutputStream outputStream = new FileOutputStream(file)) {
            objectMapper.writeValue(outputStream, fullData);
        }
        log.info("Fichier Json mis à jour avec succès: {}", filePath);
    }
}
