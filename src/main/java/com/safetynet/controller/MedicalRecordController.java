package com.safetynet.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.model.MedicalRecord;
import com.safetynet.service.MedicalRecordService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordController.class);

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<MedicalRecord> creer(@Valid @RequestBody List<MedicalRecord> medicalRecords) throws IOException {

        if (medicalRecords == null || medicalRecords.isEmpty()) {
            logger.warn("La liste des dossiers médicales est vide ou nulle !");
            throw new IllegalArgumentException("La liste des dossiers médicales ne peut pas être vide.");
        }
        logger.info("Dossiers médiacales ajoutées avec succès.");
        return medicalRecordService.add(medicalRecords);
    }

}
