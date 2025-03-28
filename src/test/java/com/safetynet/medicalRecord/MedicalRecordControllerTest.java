package com.safetynet.medicalRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.controller.MedicalRecordController;
import com.safetynet.exceptions.GenericExceptionHandler;
import com.safetynet.exceptions.IllegalArgumentExceptionHandler;
import com.safetynet.exceptions.MethodArgumentTypeMismatchExceptionHandler;
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

@ExtendWith(MockitoExtension.class)
public class MedicalRecordControllerTest {

        @Mock
        private MedicalRecordService medicalRecordService;

        @InjectMocks
        private MedicalRecordController medicalRecordController;

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;

        @BeforeEach
        @SuppressWarnings(value = { "unused" })
        void setUp() {
                mockMvc = MockMvcBuilders
                                .standaloneSetup(medicalRecordController)
                                .setControllerAdvice(
                                                new IllegalArgumentExceptionHandler(),
                                                new GenericExceptionHandler(),
                                                new MethodArgumentTypeMismatchExceptionHandler())
                                .build();
                objectMapper = new ObjectMapper();
        }

        @Test
        void testCreerMedicalRecords_Success() throws Exception {
                List<MedicalRecord> records = List.of(
                                new MedicalRecord("John", "Doe", "01/01/1980", List.of("med1", "med2"),
                                                List.of("allergy1")));
                when(medicalRecordService.add(records)).thenReturn(records);

                mockMvc.perform(post("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(records)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$[0].firstName").value("John"))
                                .andExpect(jsonPath("$[0].lastName").value("Doe"));

                verify(medicalRecordService).add(records);
        }

        @Test
        void testMettreAJourMedicalRecords_Success() throws Exception {
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

        @Test
        void testSupprimerMedicalRecords_Success() throws Exception {
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

        @Test
        void testCreerMedicalRecords_InvalidRequest() throws Exception {
                // Données invalides : liste vide
                List<MedicalRecord> emptyRecords = List.of();

                // Vérifier que l'exception est levée lorsque la liste est vide
                Exception exception = assertThrows(IllegalArgumentException.class,
                                () -> medicalRecordController.creer(emptyRecords));
                assertEquals("La liste des dossiers médicales ne peut pas être vide.", exception.getMessage());
        }

        @Test
        void testMettreAJourMedicalRecords_InvalidRequest() throws Exception {
                // Données invalides : liste vide
                List<MedicalRecord> emptyRecords = List.of();

                // Vérifier que l'exception est levée lorsque la liste est vide
                Exception exception = assertThrows(IllegalArgumentException.class,
                                () -> medicalRecordController.mettreAJour(emptyRecords));
                assertEquals("La liste des dossiers médicales ne peut pas être vide.", exception.getMessage());
        }

        @Test
        void testSupprimerMedicalRecords_InvalidRequest() throws Exception {
                // Données invalides : liste vide
                List<MedicalRecord> emptyRecords = List.of();

                // Vérifier que l'exception est levée lorsque la liste est vide
                Exception exception = assertThrows(IllegalArgumentException.class,
                                () -> medicalRecordController.supprimer(emptyRecords));
                assertEquals("La liste des dossiers médicales ne peut pas être vide.", exception.getMessage());
        }
}
