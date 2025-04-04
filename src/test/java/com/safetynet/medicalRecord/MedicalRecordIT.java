package com.safetynet.medicalRecord;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.model.MedicalRecord;

/**
 * Tests d'intégration pour l'endpoint MedicalRecord.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MedicalRecordIT {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        /**
         * Teste l'ajout d'un nouveau dossier médical.
         *
         * @throws Exception gère l'erreur lors de l'exécution du test.
         */
        @Test
        public void testAddMedicalRecords() throws Exception {
                MedicalRecord medicalRecord = new MedicalRecord("Jack", "Will", "01/01/1990",
                                Arrays.asList("aspirin", "ibuprofen"), Arrays.asList());

                mockMvc.perform(post("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(medicalRecord))))
                                .andExpect(status().isCreated());
        }

        /**
         * Teste la mise à jour d'un dossier médical existant.
         *
         * @throws Exception gère l'erreurs lors de l'exécution du test.
         */
        @Test
        public void testUpdateMedicalRecords() throws Exception {
                MedicalRecord medicalRecordExisting = new MedicalRecord("Jack", "Will", "01/01/1990",
                                Arrays.asList("aspirin", "ibuprofen"), Arrays.asList());
                mockMvc.perform(post("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(medicalRecordExisting))))
                                .andExpect(status().isCreated());

                MedicalRecord medicalRecordUpdated = new MedicalRecord("Jack", "Will", "01/01/1990",
                                Arrays.asList("paracetamol", "antihistamine"), Arrays.asList("peanut"));
                mockMvc.perform(put("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(medicalRecordUpdated))))
                                .andExpect(status().isOk());
        }

        /**
         * Teste la suppression d'un dossier médical existant.
         *
         * @throws Exception gère l'erreur lors de l'exécution du test.
         */
        @Test
        public void testDeleteMedicalRecords() throws Exception {
                MedicalRecord medicalRecord = new MedicalRecord("Jack", "Will", "01/01/1990",
                                Arrays.asList("aspirin", "ibuprofen"), Arrays.asList());
                mockMvc.perform(post("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(medicalRecord))))
                                .andExpect(status().isCreated());

                mockMvc.perform(delete("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(medicalRecord))))
                                .andExpect(status().isOk());
        }
}
