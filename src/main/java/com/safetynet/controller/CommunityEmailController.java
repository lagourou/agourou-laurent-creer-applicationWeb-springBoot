package com.safetynet.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.service.CommunityEmailService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.safetynet.dto.CommunityEmail;

@Slf4j
@RestController
@RequestMapping("communityEmail")
public class CommunityEmailController {
    private final CommunityEmailService communityEmailService;

    public CommunityEmailController(CommunityEmailService communityEmailService) {
        this.communityEmailService = communityEmailService;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emails retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid or missing city parameter."),
            @ApiResponse(responseCode = "204", description = "No emails found for the specified city.")
    })
    @GetMapping
    public ResponseEntity<List<CommunityEmail>> getCommunityEmail(@RequestParam("city") String city)
            throws IOException {

        log.info("Requête reçue pour la ville: {}", city);

        if (city == null || city.isBlank()) {
            log.error("Le paramètre 'city' est manquant ou invalide.");
            return ResponseEntity.badRequest().build();
        }

        List<CommunityEmail> result = communityEmailService.getCommunityEmail(city);
        if (result.isEmpty()) {

            log.info("Aucune donnée trouvée pour cette ville: {}", city);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);

    }

}
