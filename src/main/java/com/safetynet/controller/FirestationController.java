package com.safetynet.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.model.Firestation;
import com.safetynet.service.FirestationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/firestation")
public class FirestationController {

    private final FirestationService firestationService;
    private static final Logger logger = LoggerFactory.getLogger(FirestationController.class);

    public FirestationController(FirestationService firestationService) {
        this.firestationService = firestationService;
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Firestation> creer(@Valid @RequestBody List<Firestation> firestations) throws IOException {

        if (firestations == null || firestations.isEmpty()) {
            logger.warn("La liste des casernes est vide ou nulle !");
            throw new IllegalArgumentException("La liste des casernes ne peut pas être vide.");
        }
        logger.info("Casernes ajoutées avec succès");
        return firestationService.add(firestations);
    }

}
