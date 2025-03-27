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

@Slf4j
@RestController
@RequestMapping("fire")
public class FireAddressController {
    private final FireAddressService fireAddressService;

    public FireAddressController(FireAddressService fireAddressService) {
        this.fireAddressService = fireAddressService;
    }

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

        List<FireAddress> result = fireAddressService.getFireAddress(fireAddress);
        if (result.isEmpty()) {
            log.info("Aucune donnée trouvée pour l'adresse: {}", fireAddress);
            return ResponseEntity.noContent().build();
        }

        log.info("{} Habitant(s) trouvé(s) pour l'adresse {}", result.size(), fireAddress);
        return ResponseEntity.ok(result);
    }

}
