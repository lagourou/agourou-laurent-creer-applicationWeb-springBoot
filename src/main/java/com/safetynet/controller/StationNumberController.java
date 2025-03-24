package com.safetynet.controller;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.safetynet.dto.FirestationByPerson;
import com.safetynet.service.FirestationService;
import com.safetynet.service.MedicalRecordService;
import com.safetynet.service.PersonService;
import com.safetynet.service.StationNumberService;

@RestController
@RequestMapping("/firestation")
public class StationNumberController {

    private final StationNumberService stationNumberService;

    public StationNumberController(StationNumberService stationNumberService, PersonService personService,
            FirestationService firestationService, MedicalRecordService medicalRecordService) {
        this.stationNumberService = stationNumberService;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FirestationByPerson> getPersonsByStation(@RequestParam("stationNumber") int stationNumber)
            throws IOException {

        FirestationByPerson result = stationNumberService.getPersonByStation(stationNumber);

        return ResponseEntity.ok(result);

    }

}
