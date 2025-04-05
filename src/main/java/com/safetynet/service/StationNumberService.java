package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.FirestationByPerson;
import com.safetynet.dto.PersonByStation;
import com.safetynet.model.Firestation;
import com.safetynet.model.MedicalRecord;
import com.safetynet.model.Person;
import com.safetynet.service.dataService.DataLoad;
import com.safetynet.util.AgeCalculatorUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Service pour récupérer les informations des personnes associées à une caserne
 * donnée.
 */
@Slf4j
@Service
public class StationNumberService {
        private final AgeCalculatorUtil ageCalculatorUtil;
        private final DataLoad dataLoad;

        /**
         * Constructeur de la classe StationNumberService.
         *
         * @param ageCalculatorUtil Service pour calculer l'âge à partir de la date
         *                          de naissance.
         * @param dataLoad          Service pour charger les données depuis un
         *                          fichier JSON.
         */
        public StationNumberService(AgeCalculatorUtil ageCalculatorUtil, DataLoad dataLoad) {
                this.ageCalculatorUtil = ageCalculatorUtil;
                this.dataLoad = dataLoad;
        }

        /**
         * Calcule l'âge à partir de la date de naissance.
         *
         * @param birthdate La date de naissance au format MM/dd/yyyy.
         * @return L'âge.
         */

        public int getAge(String birthdate) {
                return ageCalculatorUtil.calculateAge(birthdate);
        }

        /**
         * Récupère les informations des personnes associées à une caserne donnée.
         *
         * @param stationNumber Le numéro de la caserne.
         * @return Une liste des personnes couvertes par la caserne de pompiers
         *         correspondante
         *         et le nombre d'adultes et d'enfants.
         * @throws IOException En cas d'erreur lors de la lecture des fichiers JSON.
         */
        public FirestationByPerson getPersonByStation(int stationNumber) throws IOException {
                log.info("Requête reçue pour getPersonByStation avec le numéro de la caserne: {}", stationNumber);

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
                        log.debug("Données des personnes chargées : {}", persons);
                        log.debug("Données des casernes chargées : {}", firestations);
                        log.debug("Données des dossiers médicaux chargées : {}", medicalRecords);
                } catch (IOException e) {
                        log.error("Erreur lors de la lecture des fichiers JSON pour le numéro de caserne {} : {}",
                                        stationNumber, e.getMessage(), e);
                        throw e;
                }

                List<String> address = firestations.stream()
                                .filter(firestation -> firestation.getStation() == stationNumber)
                                .map(Firestation::getAddress).toList();
                log.debug("Adresses associées à la caserne {} : {}", stationNumber, address);

                List<PersonByStation> filterPersonByStations = persons.stream()
                                .filter(person -> address.contains(person.getAddress()))
                                .map(person -> new PersonByStation(person.getFirstName(), person.getLastName(),
                                                person.getAddress(), person.getPhone()))
                                .toList();
                log.debug("Liste des personnes filtrées pour la caserne {} : {}", stationNumber,
                                filterPersonByStations);

                int numberOfAdults = (int) persons.stream()
                                .filter(person -> address.contains(person.getAddress())
                                                && medicalRecords.stream()
                                                                .filter(record -> record.getFirstName()
                                                                                .equals(person.getFirstName())
                                                                                && record.getLastName().equals(
                                                                                                person.getLastName()))
                                                                .findFirst()
                                                                .map(record -> ageCalculatorUtil.calculateAge(
                                                                                record.getBirthdate()) > 18)
                                                                .orElse(false))
                                .count();
                int numberOfChildren = filterPersonByStations.size() - numberOfAdults;

                log.debug("Nombre d'adultes : {}, Nombre d'enfants : {}", numberOfAdults, numberOfChildren);
                log.info("Réponse retournée pour le numéro de caserne {} : {} adultes, {} enfants, total : {} personnes",
                                stationNumber, numberOfAdults, numberOfChildren, filterPersonByStations.size());

                return new FirestationByPerson(filterPersonByStations, numberOfAdults, numberOfChildren);
        }

}
