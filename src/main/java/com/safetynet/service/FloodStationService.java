package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.FloodStation;
import com.safetynet.model.Firestation;
import com.safetynet.model.MedicalRecord;
import com.safetynet.model.Person;
import com.safetynet.service.dataService.DataLoad;
import com.safetynet.util.AgeCalculatorUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Service permettant de récupérer les informations des foyers associés aux
 * casernes de pompiers en cas d'inondation.
 */
@Slf4j
@Service
public class FloodStationService {
        private final DataLoad dataLoad;
        private final AgeCalculatorUtil ageCalculatorUtil;

        /**
         * Constructeur de la classe FloodStationService.
         *
         * @param dataLoad          Service pour charger les données depuis un
         *                          fichier JSON.
         * @param AgeCalculatorUtil Service pour calculer l'âge des habitants à
         *                          partir de leur date de naissance.
         */
        public int getAge(String birthdate) {
                return ageCalculatorUtil.calculateAge(birthdate);
        }

        /**
         * Calcule l'âge à partir de la date de naissance.
         *
         * @param birthdate La date de naissance au format MM/dd/yyyy.
         * @return L'âge.
         */
        public FloodStationService(DataLoad dataLoad, AgeCalculatorUtil ageCalculatorUtil) {
                this.dataLoad = dataLoad;
                this.ageCalculatorUtil = ageCalculatorUtil;
        }

        /**
         * Récupère les informations des foyers associés aux casernes spécifiées.
         *
         * @param stationNumbers Liste des numéros de casernes.
         * @return Une map regroupant les foyers par adresse, contenant les informations
         *         des habitants.
         * @throws IOException En cas d'erreur lors de la lecture des fichiers JSON.
         */
        public Map<String, List<FloodStation>> getFloodStation(List<Integer> stationNumbers) throws IOException {
                log.info("Début de la récupération des foyers pour les casernes : {}", stationNumbers);

                List<Person> persons;
                List<Firestation> firestations;
                List<MedicalRecord> medicalRecords;

                try {
                        persons = dataLoad.readJsonFile("persons", new TypeReference<Map<String, List<Person>>>() {
                        });
                        firestations = dataLoad.readJsonFile("firestations",
                                        new TypeReference<Map<String, List<Firestation>>>() {
                                        });
                        medicalRecords = dataLoad.readJsonFile("medicalrecords",
                                        new TypeReference<Map<String, List<MedicalRecord>>>() {
                                        });
                } catch (IOException e) {
                        log.error("Erreur lors de la lecture des fichiers JSON : {}", e.getMessage());
                        throw e;
                }

                log.debug("Données des personnes chargées : {}", persons);
                log.debug("Données des casernes chargées : {}", firestations);
                log.debug("Données des dossiers médicaux chargées : {}", medicalRecords);

                List<FloodStation> floodStations = persons.stream()
                                .filter(person -> {
                                        List<Integer> stationNumber = firestations.stream()
                                                        .filter(firestation -> firestation.getAddress().trim()
                                                                        .equalsIgnoreCase(person.getAddress().trim()))
                                                        .map(Firestation::getStation).collect(Collectors.toList());

                                        return stationNumber.stream().anyMatch(stationNumbers::contains);
                                })
                                .map(person -> {
                                        String name = person.getFirstName() + " " + person.getLastName();
                                        MedicalRecord record = medicalRecords.stream()
                                                        .filter(medical -> medical.getFirstName().trim()
                                                                        .equalsIgnoreCase(person.getFirstName().trim())
                                                                        && medical.getLastName().trim()
                                                                                        .equalsIgnoreCase(person
                                                                                                        .getLastName()
                                                                                                        .trim()))
                                                        .findFirst().orElse(null);

                                        int age = 0;
                                        List<String> medications = List.of();
                                        List<String> allergies = List.of();

                                        if (record != null) {
                                                log.info("Dossier médical trouvé pour : {} {}", person.getFirstName(),
                                                                person.getLastName());
                                                age = getAge(record.getBirthdate());
                                                medications = record.getMedications();
                                                allergies = record.getAllergies();
                                        } else {
                                                log.warn("Dossier médical introuvable pour : {} {}",
                                                                person.getFirstName(), person.getLastName());
                                        }

                                        return new FloodStation(name, person.getPhone(), age, medications, allergies,
                                                        person.getAddress());
                                }).collect(Collectors.toList());

                Map<String, List<FloodStation>> groupedFloodStations = floodStations.stream()
                                .collect(Collectors.groupingBy(FloodStation::address));

                log.debug("Foyers regroupés par adresse : {}", groupedFloodStations);
                log.info("Nombre de foyers récupérés : {}", groupedFloodStations.size());

                return groupedFloodStations;
        }

}
