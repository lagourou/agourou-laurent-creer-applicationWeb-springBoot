package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.model.Firestation;
import com.safetynet.service.dataService.DataLoad;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FirestationService {

    private final DataLoad dataLoad;

    public FirestationService(DataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    public List<Firestation> add(List<Firestation> firestations) throws IOException {
        log.info("Ajout de casernes au fichier Json.");
        List<Firestation> existingFirestations = dataLoad.readJsonFile("firestations",
                new TypeReference<Map<String, List<Firestation>>>() {
                });

        for (Firestation newFirestation : firestations) {
            if (existingFirestations.stream()
                    .anyMatch(existing -> existing.getAddress().equalsIgnoreCase(newFirestation.getAddress()))) {

                log.warn("La caserne existe déjà : {}", newFirestation.getAddress());
                continue;
            }
            existingFirestations.add(newFirestation);
        }

        dataLoad.writeJsonFile("firestations", existingFirestations);
        log.info("Ajout de casernes fait");
        return firestations;
    }

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
