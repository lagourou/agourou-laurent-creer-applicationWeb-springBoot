package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.FireAddress;
import com.safetynet.model.Firestation;
import com.safetynet.model.MedicalRecord;
import com.safetynet.model.Person;
import com.safetynet.service.dataService.DataLoad;

import lombok.extern.slf4j.Slf4j;

/**
 * Service permettant de récupérer la liste des habitants vivant à l’adresse
 * donnée.
 *
 */
@Slf4j
@Service
public class FireAddressService {
    private final DataLoad dataLoad;
    private final AgeCalculationService ageCalculationService;

    /**
     * Calcul de l'âge à partir de la date de naissance.
     *
     * @param birthdate La date de naissance au format MM/dd/yyyy.
     * @return L'âge.
     */
    public int getAge(String birthdate) {
        return ageCalculationService.calculateAge(birthdate);
    }

    /**
     * Constructeur de la classe FireAddressService.
     *
     * @param dataLoad              Service pour charger les données depuis un
     *                              fichier JSON.
     * @param ageCalculationService Service pour calculer l'âge des habitants.
     */
    public FireAddressService(DataLoad dataLoad, AgeCalculationService ageCalculationService) {
        this.dataLoad = dataLoad;
        this.ageCalculationService = ageCalculationService;
    }

    /**
     * Récupère les informations des habitants associés à une adresse donnée.
     * 
     * @param fireAddress Adresse utilisée pour récupérer les données des habitants.
     * @return Une liste contenant les informations des habitants et le numéro de la
     *         caserne.
     * @throws IOException En cas d'erreur lors de la lecture des fichiers JSON.
     */
    public List<FireAddress> getFireAddress(String fireAddress) throws IOException {

        String fireAddresse = fireAddress.trim();
        List<Person> persons = dataLoad.readJsonFile("persons", new TypeReference<Map<String, List<Person>>>() {
        });
        List<MedicalRecord> medicalRecords = dataLoad.readJsonFile("medicalrecords",
                new TypeReference<Map<String, List<MedicalRecord>>>() {
                });
        List<Firestation> firestations = dataLoad.readJsonFile("firestations",
                new TypeReference<Map<String, List<Firestation>>>() {
                });
        List<FireAddress> fireAddresses = persons.stream()
                .filter(person -> fireAddresse.equals(person.getAddress().trim()))
                .map(person -> {
                    String name = person.getFirstName() + " " + person.getLastName();
                    MedicalRecord record = medicalRecords.stream()
                            .filter(medical -> medical.getFirstName().equals(person.getFirstName())
                                    && medical.getLastName().equals(person.getLastName()))
                            .findFirst().orElse(null);

                    int age = 0;
                    List<String> medications = List.of();
                    List<String> allergies = List.of();

                    if (record != null) {
                        log.info("Dossier médical trouvé pour : {} {}", person.getFirstName(), person.getLastName());

                        age = getAge(record.getBirthdate());
                        medications = record.getMedications();
                        allergies = record.getAllergies();
                    } else {
                        log.info("Dossier médical introuvable pour : {} {}", person.getFirstName(),
                                person.getLastName());
                    }

                    Firestation station = firestations.stream()
                            .filter(fire -> fireAddresse.equalsIgnoreCase(fire.getAddress().trim()))
                            .findFirst().orElse(null);

                    int stationNumber = 0;
                    if (station != null) {
                        log.info("Caserne trouvée pour l'adresse : {} (Numéro : {})", fireAddresse,
                                station.getStation());

                        stationNumber = station.getStation();
                    } else {
                        log.info("Caserne introuvable pour l'adresse : {}", fireAddresse);
                    }

                    return new FireAddress(name, person.getPhone(), age,
                            medications, allergies, stationNumber);

                }).toList();

        log.info("Nombre de personne(s) trouvé(s) pour l'adresse {} : {}", fireAddress, fireAddresses.size());
        return fireAddresses;

    }
}
