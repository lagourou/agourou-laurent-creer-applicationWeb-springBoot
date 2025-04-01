package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.ChildrenByAddress;
import com.safetynet.model.MedicalRecord;
import com.safetynet.model.Person;
import com.safetynet.service.dataService.DataLoad;

import lombok.extern.slf4j.Slf4j;

/**
 * Service permettant de gérer les alertes liées aux enfants vivant à une
 * adresse donnée.
 *
 * Service traitant les données des personnes et des dossiers médicaux
 * pour identifier les enfants vivant à une adresse spécifique,
 * ainsi que les autres membres de leur foyer.
 */
@Slf4j
@Service
public class ChildAlertService {

        private final AgeCalculationService ageCalculationService;
        private final DataLoad dataLoad;

        /**
         * Constructeur de la classe ChildAlertService.
         *
         * @param ageCalculationService Service pour calculer l'âge à partir de la date
         *                              de naissance.
         * @param dataLoad              Service pour charger les données depuis un
         *                              fichier JSON.
         */
        public ChildAlertService(AgeCalculationService ageCalculationService, DataLoad dataLoad) {
                this.ageCalculationService = ageCalculationService;
                this.dataLoad = dataLoad;
        }

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
         * Récupère la liste des enfants vivant à une adresse donnée.
         * 
         * @param address L'adresse pour rechercher les enfants.
         * @return Une liste des enfants et des autres membres du foyer.
         * @throws IOException En cas d'erreur lors de la lecture des fichiers JSON.
         */
        public List<ChildrenByAddress> getChildrenByAddress(String address) throws IOException {
                String childAddress = address.trim();
                List<Person> persons = dataLoad.readJsonFile("persons", new TypeReference<Map<String, List<Person>>>() {
                });
                List<MedicalRecord> medicalRecords = dataLoad.readJsonFile("medicalrecords",
                                new TypeReference<Map<String, List<MedicalRecord>>>() {
                                });

                List<ChildrenByAddress> filterChildrenByAddress = persons.stream()
                                .filter(person -> childAddress.equalsIgnoreCase(person.getAddress()))
                                .filter(person -> {
                                        MedicalRecord record = medicalRecords.stream()
                                                        .filter(medical -> medical.getFirstName()
                                                                        .equals(person.getFirstName())
                                                                        && medical.getLastName()
                                                                                        .equals(person.getLastName()))
                                                        .findFirst()
                                                        .orElse(null);

                                        if (record != null) {
                                                int age = getAge(record.getBirthdate());
                                                log.info("Dossier médical trouvé pour : {} {}", person.getFirstName(),
                                                                person.getLastName());
                                                return age <= 18;
                                        } else {
                                                log.info("Dossier médical introuvable pour : {} {}",
                                                                person.getFirstName(), person.getLastName());
                                                return false;
                                        }
                                })
                                .map(person -> {
                                        // Trouver les autres membres du foyer
                                        List<Map<String, String>> otherMembers = persons.stream()
                                                        .filter(member -> childAddress
                                                                        .equalsIgnoreCase(member.getAddress()))
                                                        .filter(member -> !(member.getFirstName()
                                                                        .equals(person.getFirstName())
                                                                        && member.getLastName()
                                                                                        .equals(person.getLastName())))
                                                        .map(member -> Map.of("firstName", member.getFirstName(),
                                                                        "lastName", member.getLastName()))
                                                        .toList();

                                        MedicalRecord record = medicalRecords.stream()
                                                        .filter(medical -> medical.getFirstName()
                                                                        .equals(person.getFirstName())
                                                                        && medical.getLastName()
                                                                                        .equals(person.getLastName()))
                                                        .findFirst()
                                                        .orElse(null);

                                        int age = 0;
                                        if (record != null) {
                                                age = getAge(record.getBirthdate());
                                        }

                                        return new ChildrenByAddress(person.getFirstName(), person.getLastName(), age,
                                                        otherMembers);
                                })
                                .toList();

                return filterChildrenByAddress;
        }
}
