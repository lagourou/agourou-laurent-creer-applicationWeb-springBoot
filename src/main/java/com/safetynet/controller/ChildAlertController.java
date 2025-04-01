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

import com.safetynet.dto.ChildrenByAddress;
import com.safetynet.service.ChildAlertService;
import com.safetynet.service.MedicalRecordService;
import com.safetynet.service.PersonService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur pour gérer les alertes concernant les enfants.
 * Endpoint pour récupérer les enfants et les autres membres du foyer
 * à partir de l'adresse
 */
@Slf4j
@RestController
@RequestMapping("childAlert")
public class ChildAlertController {

    private final ChildAlertService childAlertService;

    /**
     * Constructeur de la classe ChildAlertController.
     * 
     * @param childAlertService    Service permettant de gérer les alertes pour les
     *                             enfants.
     * @param personService        Service pour gérer les personnes.
     * @param medicalRecordService Service pour gérer les dossiers médicaux.
     */

    public ChildAlertController(ChildAlertService childAlertService, PersonService personService,
            MedicalRecordService medicalRecordService) {
        this.childAlertService = childAlertService;
    }

    /**
     * @param address L'adresse utilisée pour rechercher les enfants et les autres
     *                membres du foyer
     * @return Une ResponseEntity contenant la liste des enfants par adresse, ou un
     *         statut HTTP spécifique
     * @throws IOException Si une erreur survient lors de la récupération des
     *                     données
     */

    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The children and other members were successfully recovered."),
            @ApiResponse(responseCode = "400", description = "Invalid or missing address parameter."),
            @ApiResponse(responseCode = "204", description = "No children found at the provided address.")
    })
    @GetMapping
    public ResponseEntity<List<ChildrenByAddress>> getChildrenByAddress(@RequestParam("address") String address)
            throws IOException {

        log.info("Requête reçue avec l'adresse : {}", address);

        if (address == null || address.isBlank()) {
            log.error("Le paramètre 'address' est manquant ou invalide.");
            return ResponseEntity.badRequest().build();
        }

        List<ChildrenByAddress> childrenByAddresses = childAlertService.getChildrenByAddress(address);
        if (childrenByAddresses.isEmpty()) {
            log.info("Aucun enfant(s) trouvé(s) pour l'adresse: {}", address);
            return ResponseEntity.noContent().build();
        }
        log.info("{} enfant(s) trouvé(s) pour l'adresse {}", childrenByAddresses.size(), address);
        return ResponseEntity.ok(childrenByAddresses);
    }

}
