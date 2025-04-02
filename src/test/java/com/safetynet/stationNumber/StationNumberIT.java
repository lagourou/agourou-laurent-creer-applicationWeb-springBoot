package com.safetynet.stationNumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.dto.FirestationByPerson;
import com.safetynet.service.StationNumberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext

/**
 * Tests d'intégration pour l'endpoint StationNumber.
 */
public class StationNumberIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StationNumberService stationNumberService;

    /**
     * Teste la récupération des informations des personnes pour un numéro de
     * caserne valide.
     * Vérifie que le statut HTTP est 200 et que les informations retournées
     * sont correctes.
     *
     * @throws Exception s'il y a une erreur lors de l'exécution de la requête.
     */
    @Test
    void testReturnValidsPersons() throws Exception {
        int stationNumber = 1;

        ResultActions response = mockMvc.perform(get("/firestation")
                .param("stationNumber", String.valueOf(stationNumber))
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());

        FirestationByPerson firestationByPerson = objectMapper.readValue(
                response.andReturn().getResponse().getContentAsString(),
                FirestationByPerson.class);

        assertThat(firestationByPerson).isNotNull();
        assertThat(firestationByPerson.persons()).isNotEmpty();
        assertThat(firestationByPerson.numberOfAdults()).isGreaterThanOrEqualTo(0);
        assertThat(firestationByPerson.numberOfChildren()).isGreaterThanOrEqualTo(0);
    }

    /**
     * Teste la récupération des informations pour un numéro de caserne valide
     * mais sans données disponibles.
     * Vérifie que le statut HTTP est 204.
     *
     * @throws Exception s'il y a une erreur lors de l'exécution de la requête.
     */
    @Test
    void testNoPerson() throws Exception {
        int stationNumber = 999;

        mockMvc.perform(get("/firestation")
                .param("stationNumber", String.valueOf(stationNumber))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    /**
     * Teste la récupération des informations avec un numéro de caserne invalide.
     * Vérifie que le statut HTTP est 400.
     *
     * @throws Exception s'il y a une erreur lors de l'exécution de la requête.
     */
    @Test
    void testInvalidNumber() throws Exception {
        int invalidStationNumber = -1;

        mockMvc.perform(get("/firestation")
                .param("stationNumber", String.valueOf(invalidStationNumber))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Teste la récupération des informations des personnes
     * pour un numéro de caserne valide.
     * Vérifie que les données retournées sont correctes.
     *
     * @throws Exception s'il y a une erreur lors de l'exécution de la requête.
     */
    @Test
    void testValidNumber() throws Exception {
        int stationNumber = 1;

        FirestationByPerson firestationByPerson = stationNumberService.getPersonByStation(stationNumber);

        assertThat(firestationByPerson).isNotNull();
        assertThat(firestationByPerson.persons()).isNotEmpty();
        assertThat(firestationByPerson.numberOfAdults()).isGreaterThanOrEqualTo(0);
        assertThat(firestationByPerson.numberOfChildren()).isGreaterThanOrEqualTo(0);
    }
}
