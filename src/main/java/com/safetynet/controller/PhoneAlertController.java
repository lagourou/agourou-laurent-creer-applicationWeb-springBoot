package com.safetynet.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.dto.PhoneAlert;
import com.safetynet.service.FirestationService;
import com.safetynet.service.PersonService;
import com.safetynet.service.PhoneAlertService;

@RestController
@RequestMapping("phoneAlert")
public class PhoneAlertController {
    private final PhoneAlertService phoneAlertService;

    public PhoneAlertController(PhoneAlertService phoneAlertService, PersonService personService,
            FirestationService firestationService) {
        this.phoneAlertService = phoneAlertService;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PhoneAlert>> getPhoneAlert(@RequestParam("firestation") int firestationNumber)
            throws IOException {

        List<PhoneAlert> result = phoneAlertService.getPhoneAlert(firestationNumber);
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }
}
