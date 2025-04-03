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

import com.safetynet.dto.FireAddress;
import com.safetynet.service.FireAddressService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur pour gérer les alertes liées aux adresses des casernes de pompier
 * Endpoint pour récupérer les habitants vivant l'adresse
 * donnée.
 */
@Slf4j
@RestController
@RequestMapping("fire")
public class FireAddressController {
    private final FireAddressService fireAddressService;

    /**
     * Constructeur de la classe FireAddressController.
     * 
     * @param fireAddressService Service permettant de récupérer les informations
     *                           des
     *                           habitants par adresse des casernes de pompier.
     */

    public FireAddressController(FireAddressService fireAddressService) {
        this.fireAddressService = fireAddressService;
    }

    /**
     *
     * @param fireAddress L'adresse utilisée pour rechercher les informations des
     *                    habitants.
     * @return Une ResponseEntity contenant la liste des habitants pour l'adresse
     *         donnée ou un statut HTTP spécifique.
     * @throws IOException Si une erreur survient lors de la récupération
     *                     des
     *                     données.
     */

    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Residents retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid or missing address parameter."),
            @ApiResponse(responseCode = "204", description = "No residents found for the specified address.")
    })
    @GetMapping
    public ResponseEntity<List<FireAddress>> getFireAddress(@RequestParam("address") String fireAddress)
            throws IOException {

        log.info("Requête reçue pour l'adresse: {}", fireAddress);

        if (fireAddress == null || fireAddress.isBlank()) {
            log.error("Le paramètre 'address' est manquant ou invalide.");
            return ResponseEntity.badRequest().build();
        }

        log.debug("Appel au service 'fireAddressService.getFireAddress' avec l'adresse : {}", fireAddress);
        List<FireAddress> result = fireAddressService.getFireAddress(fireAddress);

        log.debug("Résultat du service pour l'adresse {}: {}", fireAddress, result);
        if (result.isEmpty()) {
            log.info("Aucune donnée trouvée pour l'adresse: {}", fireAddress);
            return ResponseEntity.noContent().build();
        }

        log.info("{} Habitant(s) trouvé(s) pour l'adresse {}", result.size(), fireAddress);
        return ResponseEntity.ok(result);
    }

}
