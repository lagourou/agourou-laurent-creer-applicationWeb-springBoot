package com.safetynet.dataLoad;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import com.safetynet.service.dataService.DataLoad;

import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Classe de tests unitaires pour le service {@link DataLoad}.
 * Elle vérifie les lectures et écritures dans le fichier JSON de test.
 */
class DataLoadTest {

    private DataLoad dataLoad;
    private ObjectMapper objectMapper;
    private File tempFile;

    /**
     * Initialise l'environnement de test :
     * - crée un fichier temporaire
     * - instancie DataLoad et ObjectMapper
     * - injecte le chemin du fichier via Reflection
     */
    @BeforeEach
    void setUp() throws IOException {
        dataLoad = new DataLoad();
        objectMapper = new ObjectMapper();
        tempFile = Files.createTempFile("test-data", ".json").toFile();
        tempFile.deleteOnExit();
        ReflectionTestUtils.setField(dataLoad, "filePath", tempFile.getAbsolutePath());
    }

    /**
     * Vérifie que la lecture du fichier JSON retourne les bonnes données.
     * On écrit 3 valeurs dans une clé 'records' et on teste la lecture.
     */
    @Test
    void testReadJsonFile_shouldReturnData() throws IOException {
        Map<String, List<String>> data = Map.of("records", List.of("one", "two", "three"));
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            objectMapper.writeValue(fos, data);
        }

        List<String> result = dataLoad.readJsonFile("records", new TypeReference<Map<String, List<String>>>() {
        });
        assertEquals(3, result.size());
        assertTrue(result.contains("two"));
    }

    /**
     * Vérifie que si le fichier JSON n'existe pas, la lecture lève une exception.
     */

    @Test
    void testReadJsonFile_fileNotFound_shouldThrowException() {
        File invalid = new File("nonexistent.json");
        ReflectionTestUtils.setField(dataLoad, "filePath", invalid.getAbsolutePath());

        assertThrows(IOException.class, () -> {
            dataLoad.readJsonFile("data", new TypeReference<Map<String, List<String>>>() {
            });
        });
    }

    /**
     * Vérifie que l’écriture dans un fichier vide fonctionne :
     * - on écrit une clé 'items' avec des fruits
     * - on relit le fichier et on compare les valeurs
     */
    @Test
    void testWriteJsonFile_shouldUpdateJsonFile() throws IOException {
        ReflectionTestUtils.setField(dataLoad, "filePath", tempFile.getAbsolutePath());
        objectMapper.writeValue(tempFile, Map.of());
        dataLoad.writeJsonFile("items", List.of("apple", "banana"));
        assertTrue(tempFile.length() > 0, "Le fichier JSON est vide après écriture");
        try (FileInputStream fis = new FileInputStream(tempFile)) {
            Map<String, List<String>> result = objectMapper.readValue(fis,
                    new TypeReference<Map<String, List<String>>>() {
                    });
            assertEquals(List.of("apple", "banana"), result.get("items"));
        }
    }

    /**
     * Vérifie que si le fichier contient déjà des données,
     * les nouvelles données sont ajoutées sans écraser les anciennes.
     */
    @Test
    void testWriteJsonFile_withExistingContent_shouldMerge() throws IOException {
        Map<String, Object> initial = Map.of("oldKey", List.of("X"));
        objectMapper.writeValue(tempFile, initial);

        dataLoad.writeJsonFile("newKey", List.of("Y"));

        Map<String, Object> result;
        try (FileInputStream fis = new FileInputStream(tempFile)) {
            result = objectMapper.readValue(fis, new TypeReference<>() {
            });
        }

        assertTrue(result.containsKey("oldKey"));
        assertTrue(result.containsKey("newKey"));
    }
}
