package com.safetynet.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.dto.FloodStation;
import com.safetynet.service.FloodStationService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur pour gérer les casernes en cas d'inondation.
 * Endpoint pour récupérer les foyers associés à une ou plusieurs casernes
 */
@Slf4j
@RestController
@RequestMapping("flood/stations")
public class FloodStationController {
    private final FloodStationService floodStationService;

    /**
     * Constructeur de la classe FloodStationController.
     *
     * @param floodStationService Service permettant de gérer les données des
     *                            casernes en cas d'inondation.
     */
    public FloodStationController(FloodStationService floodStationService) {
        this.floodStationService = floodStationService;
    }

    /**
     * Récupère les foyers associés aux casernes spécifiées.
     *
     * @param stationNumbers Liste des numéros de casernes.
     * @return Les foyers associés aux casernes ou une réponse avec un code HTTP.
     * @throws IOException En cas d'erreur lors de la récupération des données.
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved flood stations."),
            @ApiResponse(responseCode = "400", description = "Invalid or missing station numbers parameter."),
            @ApiResponse(responseCode = "204", description = "No flood stations found for the provided station numbers.")
    })
    @GetMapping
    public ResponseEntity<Map<String, List<FloodStation>>> getFloodStation(
            @RequestParam(value = "stations", required = false) List<Integer> stationNumbers)
            throws IOException {

        log.info("Requête reçue la liste des numéros de casernes: {}", stationNumbers);

        // Vérification des paramètres invalides
        if (stationNumbers == null || stationNumbers.isEmpty()) {
            log.warn("Paramètre stations invalide: {}", stationNumbers);
            return ResponseEntity.badRequest().build(); // Retourne 400 Bad Request
        }

        Map<String, List<FloodStation>> result = floodStationService.getFloodStation(stationNumbers);
        if (result.isEmpty()) {
            log.info("Aucune caserne(s) trouvée(s): {}", stationNumbers);
            return ResponseEntity.noContent().build();
        }

        log.info("{} Foyer(s) trouvé(s) correspondant à la liste des numéros de casernes {}", result.size(),
                stationNumbers);
        return ResponseEntity.ok(result);
    }
}
