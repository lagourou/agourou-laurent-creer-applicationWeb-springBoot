package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.PhoneAlert;
import com.safetynet.model.Firestation;
import com.safetynet.model.Person;
import com.safetynet.service.dataService.DataLoad;
import lombok.extern.slf4j.Slf4j;

/**
 * Service permettant de récupérer les numéros de téléphone des habitants
 * liés à une caserne de pompiers.
 */
@Slf4j
@Service
public class PhoneAlertService {
    private final DataLoad dataLoad;

    /**
     * Constructeur de la classe PhoneAlertService.
     *
     * @param dataLoad Service pour charger et sauvegarder les données depuis/vers
     *                 un fichier JSON.
     */
    public PhoneAlertService(DataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    /**
     * Récupère les numéros de téléphone des résidents desservis
     * par la caserne de pompiers
     *
     * @param firestationNumber Le numéro de la caserne.
     * @return Une liste contenant les numéros de téléphone des habitants
     *         correspondant.
     * @throws IOException En cas d'erreur lors de la lecture des fichiers JSON.
     */
    public List<PhoneAlert> getPhoneAlert(int firestationNumber) throws IOException {
        log.info("Début de la récupération des numéros de téléphone pour la caserne : {}", firestationNumber);

        List<Person> persons;
        List<Firestation> firestations;

        try {
            persons = dataLoad.readJsonFile("persons", new TypeReference<Map<String, List<Person>>>() {
            });
            firestations = dataLoad.readJsonFile("firestations", new TypeReference<Map<String, List<Firestation>>>() {
            });
            log.debug("Données des casernes chargées : {}", firestations);
            log.debug("Données des habitants chargées : {}", persons);
        } catch (IOException e) {
            log.error("Erreur lors de la lecture des fichiers JSON : {}", e.getMessage(), e);
            throw e;
        }

        List<Firestation> matchingStation = firestations.stream()
                .filter(firestation -> firestation.getStation() == firestationNumber).toList();

        log.debug("Caserne(s) correspondante(s) au numéro {} : {}", firestationNumber, matchingStation);

        List<PhoneAlert> filterPhoneAlert = persons.stream()
                .filter(person -> matchingStation.stream()
                        .anyMatch(firestation -> firestation.getAddress().equals(person.getAddress())))
                .map(person -> new PhoneAlert(person.getPhone())).toList();

        log.debug("Numéros de téléphone filtrés pour la caserne {} : {}", firestationNumber, filterPhoneAlert);
        log.info("Nombre de numéro(s) de téléphone retourné(s) pour la caserne {} : {}", firestationNumber,
                filterPhoneAlert.size());

        return filterPhoneAlert;
    }

}