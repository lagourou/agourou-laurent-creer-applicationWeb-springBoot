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

import com.safetynet.model.MedicalRecord;
import com.safetynet.service.MedicalRecordService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    private void validMedicalRecords(List<MedicalRecord> medicalRecords) {
        if (medicalRecords.isEmpty()) {
            log.warn("La liste des dossiers médicales est vide ou nulle !");
            throw new IllegalArgumentException("La liste des dossiers médicales ne peut pas être vide.");
        }
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The medical records have been successfully added."),
            @ApiResponse(responseCode = "400", description = "Invalid query: missing or incorrect medical record details.")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<MedicalRecord> creer(@Valid @RequestBody List<MedicalRecord> medicalRecords) throws IOException {

        validMedicalRecords(medicalRecords);
        log.info("Dossiers médiacales ajoutés avec succès.");
        return medicalRecordService.add(medicalRecords);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The medical records have been successfully updated."),
            @ApiResponse(responseCode = "400", description = "Invalid query: missing or incorrect medical record details.")
    })
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<MedicalRecord> mettreAJour(@Valid @RequestBody List<MedicalRecord> medicalRecords) throws IOException {

        validMedicalRecords(medicalRecords);
        log.info("Dossiers médiacales mis à jour avec succès.");
        return medicalRecordService.update(medicalRecords);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The medical records have been successfully deleted."),
            @ApiResponse(responseCode = "400", description = "Invalid query: missing or incorrect medical record details.")
    })
    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<MedicalRecord> supprimer(@Valid @RequestBody List<MedicalRecord> medicalRecords) throws IOException {

        validMedicalRecords(medicalRecords);
        log.info("Dossiers médiacales supprimés avec succès.");
        return medicalRecordService.delete(medicalRecords);
    }
}