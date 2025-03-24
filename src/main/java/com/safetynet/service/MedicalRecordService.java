package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.model.MedicalRecord;
import com.safetynet.service.dataService.DataLoad;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MedicalRecordService {

    private final DataLoad dataLoad;

    public MedicalRecordService(DataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    public List<MedicalRecord> add(List<MedicalRecord> medicalRecords) throws IOException {
        log.info("Ajout d'un dossier médical");
        List<MedicalRecord> existingMedicalRecords = dataLoad.readJsonFile("medicalrecords",
                new TypeReference<Map<String, List<MedicalRecord>>>() {
                });

        for (MedicalRecord newMedicalRecord : medicalRecords) {
            if (existingMedicalRecords.stream()
                    .anyMatch(existing -> existing.getFirstName().equalsIgnoreCase(newMedicalRecord.getFirstName())
                            && existing.getLastName().equalsIgnoreCase(newMedicalRecord.getLastName())
                            && existing.getBirthdate().equalsIgnoreCase(newMedicalRecord.getBirthdate()))) {

                log.warn("Le dossier médical existe déjà: {}", newMedicalRecord.getFirstName(),
                        newMedicalRecord.getLastName(), newMedicalRecord.getBirthdate());
                continue;
            }
            existingMedicalRecords.add(newMedicalRecord);
        }
        dataLoad.writeJsonFile("medicalrecords", existingMedicalRecords);
        log.info("Ajout du dossier médical fait");
        return medicalRecords;
    }

    public List<MedicalRecord> update(List<MedicalRecord> medicalRecords) throws IOException {
        log.info("Mise à jour du dossier médical");

        List<MedicalRecord> existingMedicalRecords = dataLoad.readJsonFile("medicalrecords",
                new TypeReference<Map<String, List<MedicalRecord>>>() {
                });

        for (MedicalRecord medicalRecord : medicalRecords) {
            for (MedicalRecord existing : existingMedicalRecords) {
                if (existing.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName())
                        && existing.getLastName().equalsIgnoreCase(medicalRecord.getLastName())) {

                    existing.setMedications(medicalRecord.getMedications());
                    existing.setAllergies(medicalRecord.getAllergies());
                }
            }
        }
        dataLoad.writeJsonFile("medicalrecords", existingMedicalRecords);
        return existingMedicalRecords;
    }

    public List<MedicalRecord> delete(List<MedicalRecord> medicalRecords) throws IOException {
        log.info("Suppression du dossier médical");

        List<MedicalRecord> existingMedicalRecords = dataLoad.readJsonFile("medicalrecords",
                new TypeReference<Map<String, List<MedicalRecord>>>() {
                });

        existingMedicalRecords.removeIf(m -> medicalRecords.stream()
                .anyMatch((medicalRecord -> m.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName()) &&
                        m.getLastName().equalsIgnoreCase(medicalRecord.getLastName()))));

        dataLoad.writeJsonFile("medicalrecords", existingMedicalRecords);

        log.info("Personne supprimeé: {}", existingMedicalRecords);
        return medicalRecords;

    }
}