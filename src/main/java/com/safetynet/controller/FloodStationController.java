package com.safetynet.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.dto.FloodStation;
import com.safetynet.service.FloodStationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("flood/stations")
public class FloodStationController {
    private final FloodStationService floodStationService;

    public FloodStationController(FloodStationService floodStationService) {
        this.floodStationService = floodStationService;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping
    public ResponseEntity<Map<String, List<FloodStation>>> getFloodStation(
            @RequestParam("stations") List<Integer> stationNumbers)
            throws IOException {

        log.info("Requête reçue la liste des numéros de casernes: {}", stationNumbers);

        Map<String, List<FloodStation>> result = floodStationService.getFloodStation(stationNumbers);
        if (result.isEmpty()) {
            log.info("Aucune caserne trouvée: {}", stationNumbers);
            return ResponseEntity.noContent().build();
        }

        log.info("{} Foyer(s) trouvé(s) pour la liste des numéros de casernes {}", result.size(), stationNumbers);
        return ResponseEntity.ok(result);
    }

}
