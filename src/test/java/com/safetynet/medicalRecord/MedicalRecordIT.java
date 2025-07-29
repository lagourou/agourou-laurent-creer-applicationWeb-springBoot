package com.safetynet.medicalRecord;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.model.MedicalRecord;

/**
 * Classe de tests d'intégration pour les opérations REST sur les dossiers
 * médicaux.
 * Elle utilise {@code MockMvc} pour simuler les requêtes HTTP sur le
 * contrôleur.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MedicalRecordIT {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        /**
         * Exemple d'objet MedicalRecord utilisé pour les tests de suppression.
         */
        private final MedicalRecord sampleRecord = new MedicalRecord(
                        "Jack",
                        "Will",
                        "01/01/1990",
                        Arrays.asList("aspirin", "ibuprofen"),
                        Arrays.asList());

        /**
         * Teste l'ajout d'un dossier médical.
         * Vérifie que l'envoi d'une requête POST avec une liste provoque une erreur.
         * (Car le contrôleur attend probablement un objet unique, pas une liste.)
         */
        @Test
        public void testAddMedicalRecords() throws Exception {
                MedicalRecord medicalRecord = new MedicalRecord(
                                "Jack",
                                "Will",
                                "01/01/1990",
                                Arrays.asList("aspirin", "ibuprofen"),
                                Arrays.asList());

                List<MedicalRecord> records = List.of(medicalRecord);

                mockMvc.perform(post("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(records)))
                                .andExpect(status().isBadRequest());
        }

        /**
         * Teste la mise à jour d'un dossier médical existant.
         * Envoie une requête PUT avec un enregistrement mis à jour.
         * Vérifie que la réponse est un succès (code HTTP 200).
         */
        @Test
        public void testUpdateMedicalRecords() throws Exception {
                MedicalRecord updatedRecord = new MedicalRecord(
                                "Jack",
                                "Will",
                                "01/01/1990",
                                Arrays.asList("paracetamol"),
                                Arrays.asList("peanut"));

                List<MedicalRecord> records = List.of(updatedRecord);

                mockMvc.perform(put("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(records)))
                                .andExpect(status().isOk());
        }

        /**
         * Teste la suppression d'un dossier médical.
         * Envoie une requête DELETE avec l'enregistrement à supprimer.
         * Vérifie que la suppression réussit (code HTTP 200).
         */
        @Test
        public void testDeleteMedicalRecords() throws Exception {
                List<MedicalRecord> records = List.of(sampleRecord);

                mockMvc.perform(delete("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(records)))
                                .andExpect(status().isOk());
        }
}
