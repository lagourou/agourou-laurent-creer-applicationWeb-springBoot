package com.safetynet.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.dto.FirestationByPerson;
import com.safetynet.service.FirestationService;
import com.safetynet.service.MedicalRecordService;
import com.safetynet.service.PersonService;
import com.safetynet.service.StationNumberService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur pour gérer les données des personnes associées à une caserne de
 * pompiers.
 */
@Slf4j
@RestController
@RequestMapping("/firestation")
public class StationNumberController {

    private final StationNumberService stationNumberService;

    /**
     * Constructeur de la classe StationNumberController.
     *
     * @param stationNumberService Service pour gérer les données des personnes par
     *                             caserne.
     * @param personService        Service pour gérer les informations des
     *                             personnes.
     * @param firestationService   Service pour gérer les casernes de pompiers.
     * @param medicalRecordService Service pour gérer les dossiers médicaux.
     */

    public StationNumberController(StationNumberService stationNumberService, PersonService personService,
            FirestationService firestationService, MedicalRecordService medicalRecordService) {
        this.stationNumberService = stationNumberService;
    }

    /**
     * Récupère les informations des personnes associées au numéro de caserne donné.
     *
     * @param stationNumber Numéro de la caserne.
     * @return Les informations des personnes par caserne ou une réponse avec un
     *         code HTTP spécifique.
     * @throws IOException En cas d'erreur lors de la récupération des données.
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Firestation data retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid or missing station number parameter."),
            @ApiResponse(responseCode = "204", description = "No data found for the specified station number.")
    })
    @GetMapping
    public ResponseEntity<FirestationByPerson> getPersonsByStation(@RequestParam("stationNumber") int stationNumber)
            throws IOException {

        log.info("Requête reçue avec le numéro de la caserne: {}", stationNumber);

        if (stationNumber <= 0) {
            log.error("Le paramètre 'stationNumber' est invalide.");
            return ResponseEntity.badRequest().build();
        }

        log.debug("Appel au service 'stationNumberService.getPersonByStation' avec le numéro de caserne : {}",
                stationNumber);
        FirestationByPerson result = stationNumberService.getPersonByStation(stationNumber);

        log.debug("Résultat du service pour la caserne {} : {}", stationNumber, result);
        if (result.persons().isEmpty()) {

            log.info("Aucune personne trouvée pour la caserne numéro: {}", stationNumber);
            return ResponseEntity.noContent().build();
        }

        log.info("Données récupérées pour la station {} : {} personnes, {} adultes, {} enfants",
                stationNumber,
                result.persons().size(),
                result.numberOfAdults(),
                result.numberOfChildren());

        return ResponseEntity.ok(result);

    }

}
