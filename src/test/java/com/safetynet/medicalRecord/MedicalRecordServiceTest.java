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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceTest {

    @Mock
    private DataLoad dataLoad;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

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

    @Test
    void skipExistingMedicalRecord() throws IOException {
        MedicalRecord existingRecord = new MedicalRecord("John", "Doe", "01/01/1980", List.of(), List.of());
        List<MedicalRecord> newRecords = List.of(existingRecord);
        List<MedicalRecord> existingRecords = new ArrayList<>(List.of(existingRecord));

        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class))).thenReturn(existingRecords);

        List<MedicalRecord> result = medicalRecordService.add(newRecords);

        ArgumentCaptor<List<MedicalRecord>> captor = ArgumentCaptor.forClass(List.class);
        verify(dataLoad, times(1)).writeJsonFile(eq("medicalrecords"), captor.capture());

        List<MedicalRecord> writtenRecords = captor.getValue();
        assertThat(writtenRecords).isEqualTo(existingRecords);

        assertThat(result).isEqualTo(newRecords);
    }

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
