package com.safetynet.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.dto.PhoneAlert;
import com.safetynet.service.FirestationService;
import com.safetynet.service.PersonService;
import com.safetynet.service.PhoneAlertService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur pour récupérer les numéros de téléphone des habitants
 * en fonction du numéro de caserne de pompiers.
 */
@Slf4j
@RestController
@RequestMapping("phoneAlert")
public class PhoneAlertController {
    private final PhoneAlertService phoneAlertService;

    /**
     * Constructeur de la classe PhoneAlertController.
     *
     * @param phoneAlertService  Service pour gérer les numéros de téléphone.
     * @param personService      Service pour gérer les personnes.
     * @param firestationService Service pour gérer les casernes de pompiers.
     */
    public PhoneAlertController(PhoneAlertService phoneAlertService, PersonService personService,
            FirestationService firestationService) {
        this.phoneAlertService = phoneAlertService;
    }

    /**
     * Récupère les numéros de téléphone associés à une caserne de pompiers.
     *
     * @param firestationNumber Numéro de la caserne.
     * @return Liste des numéros de téléphone ou une réponse avec un code HTTP
     *         spécifique.
     * @throws IOException En cas d'erreur lors de la récupération des données.
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Phone numbers retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid or missing firestation number."),
            @ApiResponse(responseCode = "204", description = "No phone numbers found for the specified firestation.")
    })
    @GetMapping
    public ResponseEntity<List<PhoneAlert>> getPhoneAlert(@RequestParam("firestation") int firestationNumber)
            throws IOException {

        log.info("Requête reçue avec le numéro de la caserne: {}", firestationNumber);

        if (firestationNumber <= 0) {
            log.error("Le paramètre 'firestation' est invalide.");
            return ResponseEntity.badRequest().build();
        }

        List<PhoneAlert> result = phoneAlertService.getPhoneAlert(firestationNumber);
        if (result.isEmpty()) {
            log.info("Aucun numéro de téléphone trouvé pour la caserne {}", firestationNumber);
            return ResponseEntity.noContent().build();
        }

        log.info("{} numéro(s) de téléphone trouvé(s) pour la caserne {}", result.size(), firestationNumber);
        return ResponseEntity.ok(result);
    }
}
