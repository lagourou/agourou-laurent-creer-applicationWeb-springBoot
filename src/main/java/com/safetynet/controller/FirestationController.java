package com.safetynet.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.model.Firestation;
import com.safetynet.service.FirestationService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur pour gérer les alertes liées aux casernes de
 * pompiers.
 * Endpoint pour ajouter, mettre à jour et supprimer
 * des casernes.
 */
@Slf4j
@RestController
@RequestMapping("/firestation")
public class FirestationController {

    private final FirestationService firestationService;

    /**
     * Constructeur de la classe FirestationController.
     *
     * @param firestationService Service permettant de gérer les alertes liées
     *                           aux
     *                           casernes de pompiers.
     */

    public FirestationController(FirestationService firestationService) {
        this.firestationService = firestationService;
    }

    /**
     * Valide que la liste des casernes n'est ni vide, ni nulle.
     *
     * @param firestations Liste des casernes à valider.
     * @throws IllegalArgumentException Si la liste est vide ou nulle.
     */

    private void validFirestations(List<Firestation> firestations) {
        if (firestations.isEmpty()) {
            log.info("La liste des casernes est vide ou nulle !");
            throw new IllegalArgumentException("La liste des casernes ne peut pas être vide.");
        }
    }

    /**
     * Ajoute des casernes de pompiers.
     *
     * @param firestations Liste des casernes à ajouter.
     * @return Les casernes ajoutées.
     */

    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The firestations have been successfully added."),
            @ApiResponse(responseCode = "400", description = "Invalid query: missing or incorrect firestation details.")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Firestation> creer(@Valid @RequestBody List<Firestation> firestations) throws IOException {

        validFirestations(firestations);
        log.info("Casernes ajoutées avec succès");
        return firestationService.add(firestations);
    }

    /**
     * Met à jour des casernes qui existes.
     *
     * @param firestations Liste des casernes à mettre à jour.
     * @return Les casernes mises à jour.
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The firestations have been successfully updated."),
            @ApiResponse(responseCode = "400", description = "Invalid query: missing or incorrect firestation details.")
    })
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Firestation> mettreAJour(@Valid @RequestBody List<Firestation> firestations) throws IOException {

        validFirestations(firestations);
        log.info("Casernes mis à jour avec succès");
        return firestationService.update(firestations);
    }

    /**
     * Supprime des casernes de pompiers.
     *
     * @param firestations Liste des casernes à supprimer.
     * @return Les casernes supprimées.
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The firestations have been successfully deleted."),
            @ApiResponse(responseCode = "400", description = "Invalid query: missing or incorrect firestation details.")
    })
    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Firestation> supprimer(@Valid @RequestBody List<Firestation> firestations) throws IOException {

        validFirestations(firestations);
        log.info("Casernes suppprimées avec succès");
        return firestationService.delete(firestations);
    }

}
