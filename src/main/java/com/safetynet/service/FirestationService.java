package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.model.Firestation;
import com.safetynet.service.dataService.DataLoad;

import lombok.extern.slf4j.Slf4j;

/**
 * Service permettant de gérer les informatons liées aux casernes de pompiers.
 */
@Slf4j
@Service
public class FirestationService {

    private final DataLoad dataLoad;

    /**
     * Constructeur de la classe FirestationService.
     *
     * @param dataLoad Service pour charger et sauvegarder les données du
     *                 fichier JSON.
     */
    public FirestationService(DataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    /**
     * Ajoute des casernes de pompiers.
     *
     * @param firestations Liste des casernes à ajouter.
     * @return Les casernes ajoutées.
     * @throws IOException En cas d'erreur lors de la lecture ou de la sauvegarde
     *                     des données.
     */
    public List<Firestation> add(List<Firestation> firestations) throws IOException {
        log.info("Ajout de casernes au fichier Json.");
        List<Firestation> existingFirestations = dataLoad.readJsonFile("firestations",
                new TypeReference<Map<String, List<Firestation>>>() {
                });

        for (Firestation newFirestation : firestations) {
            if (existingFirestations.stream()
                    .anyMatch(existing -> existing.getAddress().equalsIgnoreCase(newFirestation.getAddress()))) {

                log.info("La caserne existe déjà : {}", newFirestation.getAddress());
                continue;
            }
            existingFirestations.add(newFirestation);
        }

        dataLoad.writeJsonFile("firestations", existingFirestations);
        log.info("Ajout de casernes fait");
        return firestations;
    }

    /**
     * Met à jour les informations des casernes qui existes.
     *
     * @param firestations Liste des casernes à mettre à jour.
     * @return Les casernes mises à jour.
     * @throws IOException En cas d'erreur lors de la lecture ou de la sauvegarde
     *                     des données.
     */
    public List<Firestation> update(List<Firestation> firestations) throws IOException {
        log.info("Mise à jour des casernes");

        List<Firestation> existingFirestations = dataLoad.readJsonFile("firestations",
                new TypeReference<Map<String, List<Firestation>>>() {
                });

        for (Firestation firestation : firestations) {
            for (Firestation existing : existingFirestations) {
                if (existing.getAddress().equalsIgnoreCase(firestation.getAddress())) {

                    existing.setStation(firestation.getStation());
                    log.info("Numéro de la station mise à jour : {}", existing);
                }

            }
        }
        dataLoad.writeJsonFile("firestations", existingFirestations);
        return existingFirestations;
    }

    /**
     * Supprime les casernes de pompiers.
     *
     * @param firestations Liste des casernes à supprimer.
     * @return Les casernes supprimées.
     * @throws IOException En cas d'erreur lors de la lecture ou de la sauvegarde
     *                     des données.
     */
    public List<Firestation> delete(List<Firestation> firestations) throws IOException {
        log.info("Suppression des casernes");

        List<Firestation> existingFirestations = dataLoad.readJsonFile("firestations",
                new TypeReference<Map<String, List<Firestation>>>() {
                });

        existingFirestations.removeIf(
                f -> firestations.stream()
                        .anyMatch(firestation -> f.getAddress().equalsIgnoreCase(firestation.getAddress()) &&
                                f.getStation() == firestation.getStation()));

        dataLoad.writeJsonFile("firestations", existingFirestations);

        log.info("Caserne supprimée: {}", existingFirestations);
        return firestations;
    }
}
