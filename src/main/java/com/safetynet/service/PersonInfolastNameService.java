package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.PersonInfolastName;
import com.safetynet.model.MedicalRecord;
import com.safetynet.model.Person;
import com.safetynet.service.dataService.DataLoad;
import lombok.extern.slf4j.Slf4j;

/**
 * Service pour récupérer les informations des habitants en fonction de leur nom
 * de famille.
 */

@Slf4j
@Service
public class PersonInfolastNameService {
    private final DataLoad dataLoad;
    private final AgeCalculationService ageCalculationService;

    /**
     * Calcule l'âge à partir de la date de naissance.
     *
     * @param birthdate La date de naissance au format MM/dd/yyyy.
     * @return L'âge en années.
     */
    public int getAge(String birthdate) {
        return ageCalculationService.calculateAge(birthdate);
    }

    /**
     * Constructeur de la classe PersonInfolastNameService.
     *
     * @param dataLoad              Service pour charger les données du
     *                              fichier JSON.
     * @param ageCalculationService Service pour calculer l'âge à partir de la date
     *                              de naissance.
     */
    public PersonInfolastNameService(DataLoad dataLoad, AgeCalculationService ageCalculationService) {
        this.dataLoad = dataLoad;
        this.ageCalculationService = ageCalculationService;
    }

    /**
     * Récupère les noms de famille des habitants.
     *
     * @param lastNames Nom de famille.
     * @return Une liste contenant les informations des habitants,
     * @throws IOException En cas d'erreur lors de la lecture des fichiers JSON.
     */
    public List<PersonInfolastName> getPersonInfolastName(String lastNames) throws IOException {
        log.info("Début de la récupération des informations pour le nom de famille : {}", lastNames);

        List<Person> persons;
        List<MedicalRecord> medicalRecords;

        try {
            persons = dataLoad.readJsonFile("persons", new TypeReference<Map<String, List<Person>>>() {
            });
            medicalRecords = dataLoad.readJsonFile("medicalrecords",
                    new TypeReference<Map<String, List<MedicalRecord>>>() {
                    });
            log.debug("Données des habitants chargées : {}", persons);
            log.debug("Données des dossiers médicaux chargées : {}", medicalRecords);
        } catch (IOException e) {
            log.error("Erreur lors de la lecture des fichiers JSON : {}", e.getMessage(), e);
            throw e;
        }

        List<PersonInfolastName> personInfolastNames = persons.stream()
                .filter(person -> person.getLastName().trim().equalsIgnoreCase(lastNames))
                .map(person -> {
                    MedicalRecord record = medicalRecords.stream()
                            .filter(medical -> medical.getFirstName().trim()
                                    .equalsIgnoreCase(person.getFirstName().trim())
                                    && medical.getLastName().trim().equalsIgnoreCase(person.getLastName().trim()))
                            .findFirst().orElse(null);
                    int age = 0;
                    String email = person.getEmail();
                    List<String> medications = List.of();
                    List<String> allergies = List.of();
                    if (record != null) {
                        log.info("Dossier médical trouvé pour : {} {}", person.getFirstName(), person.getLastName());
                        age = getAge(record.getBirthdate());
                        medications = record.getMedications();
                        allergies = record.getAllergies();
                    } else {
                        log.warn("Dossier médical introuvable pour : {} {}", person.getFirstName(),
                                person.getLastName());
                    }

                    return new PersonInfolastName(person.getLastName(), person.getAddress(), age, email, medications,
                            allergies);
                }).toList();

        log.debug("Liste des habitants correspondant au nom de famille '{}': {}", lastNames, personInfolastNames);
        log.info("Nombre total d'habitants correspondant au nom de famille '{}': {}", lastNames,
                personInfolastNames.size());
        return personInfolastNames;
    }

}
