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

import com.safetynet.dto.PersonInfolastName;
import com.safetynet.service.PersonInfolastNameService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/personInfolastName")
public class PersonInfolastNameController {
    private final PersonInfolastNameService personInfolastNameService;

    public PersonInfolastNameController(PersonInfolastNameService personInfolastNameService) {
        this.personInfolastNameService = personInfolastNameService;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person information retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid or missing lastName parameter."),
            @ApiResponse(responseCode = "204", description = "No persons found with the specified last name.")
    })
    @GetMapping
    public ResponseEntity<List<PersonInfolastName>> getPersonInfolastName(@RequestParam("lastName") String lastNames)
            throws IOException {

        log.info("Requête reçue pour le nom de famille: {}", lastNames);

        if (lastNames == null || lastNames.isBlank()) {
            log.error("Le paramètre 'lastName' est manquant ou invalide.");
            return ResponseEntity.badRequest().build();
        }

        List<PersonInfolastName> result = personInfolastNameService.getPersonInfolastName(lastNames);
        if (result.isEmpty()) {
            log.info("Aucune personne trouvée portant le nom de famille: {}", lastNames);
            return ResponseEntity.noContent().build();
        }

        log.info("{} Habitant(s) trouvé(s) portant le nom de famille {}", result.size(), lastNames);
        return ResponseEntity.ok(result);
    }

}
