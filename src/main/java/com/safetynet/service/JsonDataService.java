package com.safetynet.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonDataService<MonObjet> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<MonObjet> chargerDonneesJson() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/src/main/resources/data/dataFile.json");
        return objectMapper.readValue(inputStream, new TypeReference<List<MonObjet>>() {
        });
    }

}
