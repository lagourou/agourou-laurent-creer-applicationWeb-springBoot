package com.safetynet.medicalRecord;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.model.MedicalRecord;
import com.safetynet.service.MedicalRecordService;
import com.safetynet.service.dataService.DataLoad;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests pour le service MedicalRecord.
 */
@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceTest {

    @Mock
    private DataLoad dataLoad;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    /**
     * Teste l'ajout d'un nouveau dossier médical à la liste existante.
     *
     * @throws IOException en cas d'erreur de lecture/écriture des fichiers JSON.
     */
    @Test
    void addMedicalRecord() throws IOException {
        List<MedicalRecord> newRecords = List.of(
                new MedicalRecord("John", "Doe", "01/01/1980", List.of("med1", "med2"), List.of("allergy1")));
        List<MedicalRecord> existingRecords = new ArrayList<>();

        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class)))
                .thenReturn(existingRecords);

        List<MedicalRecord> result = medicalRecordService.add(newRecords);

        ArgumentCaptor<List<MedicalRecord>> captor = ArgumentCaptor.forClass(List.class);
        verify(dataLoad).writeJsonFile(eq("medicalrecords"), captor.capture());

        assertThat(captor.getValue()).containsAll(newRecords);
        assertThat(result).isEqualTo(newRecords);
    }

    /**
     * Teste l'ajout d'un dossier médical déjà existant.
     * Le dossier médical existant doit être ignoré pour éviter les doublons.
     *
     * @throws IOException en cas d'erreur de lecture/écriture des fichiers JSON.
     */
    @Test
    void skipExistingMedicalRecord_shouldThrowException() throws IOException {
        MedicalRecord existingRecord = new MedicalRecord("John", "Doe", "01/01/1980", List.of(), List.of());
        List<MedicalRecord> newRecords = List.of(existingRecord);
        List<MedicalRecord> existingRecords = List.of(existingRecord);

        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class))).thenReturn(existingRecords);

        assertThatThrownBy(() -> medicalRecordService.add(newRecords))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Le dossier médical existe déjà");
    }

    /**
     * Teste la mise à jour d'un dossier médical existant.
     * Les informations du dossier doivent être modifiées selon les données
     * fournies.
     *
     * @throws IOException en cas d'erreur de lecture/écriture des fichiers JSON.
     */
    @Test
    void updateMedicalRecord() throws IOException {
        MedicalRecord existingRecord = new MedicalRecord("Jane", "Doe", "02/02/1990", List.of(), List.of());
        MedicalRecord updatedRecord = new MedicalRecord("Jane", "Doe", "02/02/1990", List.of("med1"),
                List.of("allergy1"));
        List<MedicalRecord> existingRecords = new ArrayList<>(List.of(existingRecord));
        List<MedicalRecord> updateRecords = List.of(updatedRecord);

        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class)))
                .thenReturn(existingRecords);

        List<MedicalRecord> result = medicalRecordService.update(updateRecords);

        ArgumentCaptor<List<MedicalRecord>> captor = ArgumentCaptor.forClass(List.class);
        verify(dataLoad).writeJsonFile(eq("medicalrecords"), captor.capture());

        assertThat(captor.getValue().get(0).getMedications()).isEqualTo(updatedRecord.getMedications());
        assertThat(captor.getValue().get(0).getAllergies()).isEqualTo(updatedRecord.getAllergies());
        assertThat(result).isEqualTo(existingRecords);
    }

    /**
     * Teste la suppression d'un dossier médical existant.
     * Le dossier médical supprimé ne doit plus être présent dans la liste finale.
     *
     * @throws IOException en cas d'erreur de lecture/écriture des fichiers JSON.
     */
    @Test
    void deleteMedicalRecord() throws IOException {
        MedicalRecord recordToDelete = new MedicalRecord("Jack", "Smith", "03/03/2000", List.of(), List.of());
        List<MedicalRecord> existingRecords = new ArrayList<>(List.of(recordToDelete));
        List<MedicalRecord> deleteRecords = List.of(recordToDelete);

        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class)))
                .thenReturn(existingRecords);

        List<MedicalRecord> result = medicalRecordService.delete(deleteRecords);

        ArgumentCaptor<List<MedicalRecord>> captor = ArgumentCaptor.forClass(List.class);
        verify(dataLoad).writeJsonFile(eq("medicalrecords"), captor.capture());

        assertThat(captor.getValue()).doesNotContain(recordToDelete);
        assertThat(result).isEqualTo(deleteRecords);
    }
}
