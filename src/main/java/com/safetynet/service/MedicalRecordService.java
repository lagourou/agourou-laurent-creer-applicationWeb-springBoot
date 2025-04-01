package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.model.MedicalRecord;
import com.safetynet.service.dataService.DataLoad;

import lombok.extern.slf4j.Slf4j;

/**
 * Service permettant de gérer informations liées aux dossiers médicaux.
 */
@Slf4j
@Service
public class MedicalRecordService {

    private final DataLoad dataLoad;

    /**
     * Constructeur de la classe MedicalRecordService.
     *
     * @param dataLoad Service pour charger et sauvegarder les données du fichier
     *                 JSON.
     */
    public MedicalRecordService(DataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    /**
     * Ajoute des dossiers médicaux.
     *
     * @param medicalRecords Liste des dossiers médicaux à ajouter.
     * @return Les dossiers médicaux ajoutés.
     * @throws IOException En cas d'erreur lors de la lecture ou de la sauvegarde
     *                     des données.
     */
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

                log.info("Le dossier médical existe déjà: {} {} {}", newMedicalRecord.getFirstName(),
                        newMedicalRecord.getLastName(), newMedicalRecord.getBirthdate());
                continue;
            }
            existingMedicalRecords.add(newMedicalRecord);
        }
        dataLoad.writeJsonFile("medicalrecords", existingMedicalRecords);
        log.info("Ajout du dossier médical fait");
        return medicalRecords;
    }

    /**
     * Met à jour les dossiers médicaux existants.
     *
     * @param medicalRecords Liste des dossiers médicaux à mettre à jour.
     * @return Les dossiers médicaux mis à jour.
     * @throws IOException En cas d'erreur lors de la lecture ou de la sauvegarde
     *                     des données.
     */
    public List<MedicalRecord> update(List<MedicalRecord> medicalRecords) throws IOException {
        log.info("Mise à jour du dossier médical");

        List<MedicalRecord> existingMedicalRecords = dataLoad.readJsonFile("medicalrecords",
                new TypeReference<Map<String, List<MedicalRecord>>>() {
                });

        for (MedicalRecord medicalRecord : medicalRecords) {
            boolean update = false;
            for (MedicalRecord existing : existingMedicalRecords) {
                if (existing.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName())
                        && existing.getLastName().equalsIgnoreCase(medicalRecord.getLastName())) {
                    log.info("Avant mise à jour: {} - Allergies: {}", existing.getFirstName(), existing.getAllergies());
                    existing.setMedications(medicalRecord.getMedications());
                    existing.setAllergies(medicalRecord.getAllergies());
                    log.info("Allergies mises à jour : {}", existing.getAllergies());

                    update = true;
                    log.info("Dossier médical mis à jour pour : {} {}", existing.getFirstName(),
                            existing.getLastName());
                }
            }
            if (!update) {
                log.warn("Aucune correspondance trouvée pour la mise à jour de : {} {}", medicalRecord.getFirstName(),
                        medicalRecord.getLastName());
            }
        }
        dataLoad.writeJsonFile("medicalrecords", existingMedicalRecords);
        log.info("Données médicales après mise à jour: {}", existingMedicalRecords);

        return existingMedicalRecords;
    }

    /**
     * Supprime des dossiers médicaux.
     *
     * @param medicalRecords Liste des dossiers médicaux à supprimer.
     * @return Les dossiers médicaux supprimés.
     * @throws IOException En cas d'erreur lors de la lecture ou de la sauvegarde
     *                     des données.
     */
    public List<MedicalRecord> delete(List<MedicalRecord> medicalRecords) throws IOException {
        log.info("Suppression du dossier médical");

        try {
            List<MedicalRecord> existingMedicalRecords = dataLoad.readJsonFile("medicalrecords",
                    new TypeReference<Map<String, List<MedicalRecord>>>() {
                    });

            if (existingMedicalRecords == null) {
                log.error("Erreur: Les dossiers médicaux n'ont pas pu être chargés !");
                throw new IOException("Données médicales introuvables.");
            }

            List<MedicalRecord> deletedRecords = existingMedicalRecords.stream()
                    .filter(m -> medicalRecords.stream()
                            .anyMatch(
                                    medicalRecord -> m.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName()) &&
                                            m.getLastName().equalsIgnoreCase(medicalRecord.getLastName())))
                    .toList();

            existingMedicalRecords.removeIf(m -> medicalRecords.stream()
                    .anyMatch(medicalRecord -> m.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName()) &&
                            m.getLastName().equalsIgnoreCase(medicalRecord.getLastName())));

            if (!deletedRecords.isEmpty()) {
                log.info("Personnes supprimées: {}", deletedRecords);
            } else {
                log.warn("Aucune personne trouvée à supprimer.");
            }

            dataLoad.writeJsonFile("medicalrecords", existingMedicalRecords);
            return medicalRecords;
        } catch (IOException e) {
            log.error("Erreur lors de la suppression du dossier médical: {}", e.getMessage(), e);
            throw e;
        }
    }

}