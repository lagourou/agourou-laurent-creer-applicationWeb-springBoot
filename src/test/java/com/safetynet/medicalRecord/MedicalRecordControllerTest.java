package com.safetynet.medicalRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.controller.MedicalRecordController;
import com.safetynet.model.MedicalRecord;
import com.safetynet.service.MedicalRecordService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;

/**
 * Tests pour le contrôleur MedicalRecord.
 */
@ExtendWith(MockitoExtension.class)
public class MedicalRecordControllerTest {
        private MockMvc mockMvc;

        @Mock
        private MedicalRecordService medicalRecordService;

        @InjectMocks
        private MedicalRecordController medicalRecordController;

        private ObjectMapper objectMapper;

        @BeforeEach
        @SuppressWarnings("unused")
        void setUp() {
                // Initialise MockMvc avec le contrôleur et les mocks
                mockMvc = MockMvcBuilders.standaloneSetup(medicalRecordController).build();
                objectMapper = new ObjectMapper();
        }

        /**
         * Teste la mise à jour des dossiers médicaux avec succès.
         *
         * @throws Exception gère l'erreur lors de l'exécution du test.
         */
        @Test
        void testUpdateMedicalRecords_Success() throws Exception {
                // Données simulées pour la mise à jour
                List<MedicalRecord> records = List.of(
                                new MedicalRecord("Jane", "Doe", "02/02/1990", List.of("med-updated"),
                                                List.of("allergy-updated")));
                when(medicalRecordService.update(records)).thenReturn(records);

                // Appeler l'endpoint avec une requête PUT
                mockMvc.perform(put("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(records)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].medications[0]").value("med-updated"))
                                .andExpect(jsonPath("$[0].allergies[0]").value("allergy-updated"));

                // Vérifier que le service a été appelé
                verify(medicalRecordService).update(records);
        }

        /**
         * Teste la suppression de dossiers médicaux avec succès.
         *
         * @throws Exception gère l'erreur lors de l'exécution du test.
         */
        @Test
        void testDeleteMedicalRecords_Success() throws Exception {
                // Données simulées pour la suppression
                List<MedicalRecord> records = List.of(
                                new MedicalRecord("Jack", "Smith", "03/03/2000", List.of(), List.of()));
                when(medicalRecordService.delete(records)).thenReturn(records);

                // Appeler l'endpoint avec une requête DELETE
                mockMvc.perform(delete("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(records)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].firstName").value("Jack"))
                                .andExpect(jsonPath("$[0].lastName").value("Smith"));

                // Vérifier que le service a été appelé
                verify(medicalRecordService).delete(records);
        }

        /**
         * Teste la création de dossiers médicaux avec une liste vide.
         *
         * @throws Exception gère l'erreur lors de l'exécution du test.
         */
        @Test
        void testCreateMedicalRecords_InvalidRequest() throws Exception {
                // Données invalides : liste vide
                List<MedicalRecord> emptyRecords = List.of();

                // Vérifier que l'exception est levée lorsque la liste est vide
                Exception exception = assertThrows(IllegalArgumentException.class,
                                () -> medicalRecordController.creer(emptyRecords));
                assertEquals("La liste des dossiers médicales ne peut pas être vide.", exception.getMessage());
        }

        /**
         * Teste la mise à jour de dossiers médicaux avec une liste vide.
         *
         * @throws Exception gère l'erreur lors de l'exécution du test.
         */
        @Test
        void testUpdateMedicalRecords_InvalidRequest() throws Exception {
                List<MedicalRecord> emptyRecords = List.of();

                Exception exception = assertThrows(IllegalArgumentException.class,
                                () -> medicalRecordController.mettreAJour(emptyRecords));
                assertEquals("La liste des dossiers médicales ne peut pas être vide.", exception.getMessage());
        }

        /**
         * Teste la suppression de dossiers médicaux avec une liste vide.
         *
         * @throws Exception gère l'erreur lors de l'exécution du test.
         */
        @Test
        void testDeleteMedicalRecords_InvalidRequest() throws Exception {
                List<MedicalRecord> emptyRecords = List.of();

                Exception exception = assertThrows(IllegalArgumentException.class,
                                () -> medicalRecordController.supprimer(emptyRecords));
                assertEquals("La liste des dossiers médicales ne peut pas être vide.", exception.getMessage());
        }
}
