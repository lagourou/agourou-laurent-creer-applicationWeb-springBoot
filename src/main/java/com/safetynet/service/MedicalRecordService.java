package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.model.MedicalRecord;
import com.safetynet.model.Person;
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
     * Vérifie si un dossier médical est associé à une personne existante dans le
     * fichier "persons".
     *
     * <p>
     * Cette méthode lit les données des personnes enregistrées depuis le fichier
     * JSON "persons"
     * et vérifie si les informations du dossier médical donné (prénom et nom de
     * famille)
     * correspondent à une personne existante.
     * </p>
     *
     * @param medicalRecord Le dossier médical contenant les informations du prénom
     *                      et du nom à vérifier.
     * @return {@code true} si une correspondance est trouvée entre le prénom et le
     *         nom dans "persons",
     *         {@code false} sinon.
     * @throws IOException Si une erreur survient lors de la lecture du fichier
     *                     JSON.
     */

    /**
     * Vérifie si un dossier médical est associé à une personne existante
     *
     * @param medicalRecord Le dossier médical contenant les informations de la
     *                      personne
     * @return true si une correspondance est trouvée, false sinon.
     * @throws IOException Si une erreur survient lors de la lecture du fichier
     *                     JSON.
     */

    public boolean personAssociate(MedicalRecord medicalRecord) throws IOException {
        List<Person> existingPersons;
        try {
            existingPersons = dataLoad.readJsonFile("persons",
                    new TypeReference<Map<String, List<Person>>>() {
                    });
        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier JSON : {}", e.getMessage());
            throw e;
        }

        return existingPersons.stream()
                .anyMatch(person -> person.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName()) &&
                        person.getLastName().equalsIgnoreCase(medicalRecord.getLastName()));
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

        List<MedicalRecord> existingMedicalRecords;

        try {
            existingMedicalRecords = dataLoad.readJsonFile("medicalrecords",
                    new TypeReference<Map<String, List<MedicalRecord>>>() {
                    });
            log.debug("Dossiers médicaux existants avant ajout : {}", existingMedicalRecords);
        } catch (IOException e) {
            log.error("Erreur lors de la lecture des fichiers JSON pour les dossiers médicaux : {}", e.getMessage(), e);
            throw e;
        }

        for (MedicalRecord newMedicalRecord : medicalRecords) {
            if (existingMedicalRecords.stream()
                    .anyMatch(existing -> existing.getFirstName().equalsIgnoreCase(newMedicalRecord.getFirstName())
                            && existing.getLastName().equalsIgnoreCase(newMedicalRecord.getLastName())
                            && existing.getBirthdate().equalsIgnoreCase(newMedicalRecord.getBirthdate()))) {

                throw new IllegalArgumentException("Le dossier médical existe déjà :" + " " +
                        newMedicalRecord.getFirstName() + " " + newMedicalRecord.getLastName());
            }
            existingMedicalRecords.add(newMedicalRecord);
        }

        try {
            dataLoad.writeJsonFile("medicalrecords", existingMedicalRecords);
        } catch (IOException e) {
            log.error("Erreur lors de l'écriture dans le fichier JSON des dossiers médicaux : {}", e.getMessage(), e);
            throw e;
        }

        log.info("Ajout du dossier médical terminé");
        log.debug("Dossiers médicaux après ajout : {}", existingMedicalRecords);

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
        log.info("Mise à jour des dossiers médicaux");

        List<MedicalRecord> existingMedicalRecords;

        try {
            existingMedicalRecords = dataLoad.readJsonFile("medicalrecords",
                    new TypeReference<Map<String, List<MedicalRecord>>>() {
                    });
            log.debug("Dossiers médicaux existants avant mise à jour : {}", existingMedicalRecords);
        } catch (IOException e) {
            log.error("Erreur lors de la lecture des fichiers JSON pour les dossiers médicaux : {}", e.getMessage(), e);
            throw e;
        }

        for (MedicalRecord medicalRecord : medicalRecords) {
            boolean updated = false;
            for (MedicalRecord existing : existingMedicalRecords) {
                if (existing.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName())
                        && existing.getLastName().equalsIgnoreCase(medicalRecord.getLastName())) {
                    log.info("Mise à jour des informations pour : {} {}", existing.getFirstName(),
                            existing.getLastName());
                    existing.setMedications(medicalRecord.getMedications());
                    existing.setAllergies(medicalRecord.getAllergies());
                    updated = true;
                    log.info("Dossier médical mis à jour : {}", existing);
                }
            }
            if (!updated) {
                log.warn("Aucune correspondance trouvée pour le dossier médical : {} {}",
                        medicalRecord.getFirstName(), medicalRecord.getLastName());
            }
        }

        try {
            dataLoad.writeJsonFile("medicalrecords", existingMedicalRecords);
        } catch (IOException e) {
            log.error("Erreur lors de l'écriture dans le fichier JSON des dossiers médicaux : {}", e.getMessage(), e);
            throw e;
        }

        log.info("Mise à jour des dossiers médicaux terminée");
        log.debug("Dossiers médicaux après mise à jour : {}", existingMedicalRecords);

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
        log.info("Suppression des dossiers médicaux");

        List<MedicalRecord> existingMedicalRecords;

        try {
            existingMedicalRecords = dataLoad.readJsonFile("medicalrecords",
                    new TypeReference<Map<String, List<MedicalRecord>>>() {
                    });
            log.debug("Dossiers médicaux existants avant suppression : {}", existingMedicalRecords);
        } catch (IOException e) {
            log.error("Erreur lors de la lecture des fichiers JSON pour les dossiers médicaux : {}", e.getMessage(), e);
            throw e;
        }

        List<MedicalRecord> deletedRecords = existingMedicalRecords.stream()
                .filter(existing -> medicalRecords.stream()
                        .anyMatch(
                                medicalRecord -> existing.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName())
                                        && existing.getLastName().equalsIgnoreCase(medicalRecord.getLastName())))
                .toList();

        existingMedicalRecords.removeIf(existing -> medicalRecords.stream()
                .anyMatch(medicalRecord -> existing.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName())
                        && existing.getLastName().equalsIgnoreCase(medicalRecord.getLastName())));

        if (!deletedRecords.isEmpty()) {
            log.info("Dossiers médicaux supprimés : {}", deletedRecords);
        } else {
            log.warn("Aucun dossier médical trouvé pour suppression.");
        }

        try {
            dataLoad.writeJsonFile("medicalrecords", existingMedicalRecords);
        } catch (IOException e) {
            log.error("Erreur lors de l'écriture dans le fichier JSON des dossiers médicaux : {}", e.getMessage(), e);
            throw e;
        }

        log.info("Suppression des dossiers médicaux terminée");
        log.debug("Dossiers médicaux restants après suppression : {}", existingMedicalRecords);

        return medicalRecords;
    }

}