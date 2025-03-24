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

import com.safetynet.dto.ChildrenByAddress;
import com.safetynet.service.ChildAlertService;
import com.safetynet.service.MedicalRecordService;
import com.safetynet.service.PersonService;

@RestController
@RequestMapping("childAlert")
public class ChildAlertController {

    private final ChildAlertService childAlertService;

    public ChildAlertController(ChildAlertService childAlertService, PersonService personService,
            MedicalRecordService medicalRecordService) {
        this.childAlertService = childAlertService;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ChildrenByAddress>> getChildrenByAddress(@RequestParam("address") String address)
            throws IOException {

        List<ChildrenByAddress> childrenByAddresses = childAlertService.getchildrenByAddress(address);
        if (childrenByAddresses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(childrenByAddresses);
    }

}
