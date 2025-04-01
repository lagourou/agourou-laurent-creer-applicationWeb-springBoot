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
public class StationNumberIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StationNumberService stationNumberService;

    @Test
    void testRetourPersonnesValides() throws Exception {
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

    @Test
    void testAucunePersonne() throws Exception {
        int stationNumber = 999;

        mockMvc.perform(get("/firestation")
                .param("stationNumber", String.valueOf(stationNumber))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testNumeroInvalide() throws Exception {
        int invalidStationNumber = -1;

        mockMvc.perform(get("/firestation")
                .param("stationNumber", String.valueOf(invalidStationNumber))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testNumeroValide() throws Exception {
        int stationNumber = 1;

        FirestationByPerson firestationByPerson = stationNumberService.getPersonByStation(stationNumber);

        assertThat(firestationByPerson).isNotNull();
        assertThat(firestationByPerson.persons()).isNotEmpty();
        assertThat(firestationByPerson.numberOfAdults()).isGreaterThanOrEqualTo(0);
        assertThat(firestationByPerson.numberOfChildren()).isGreaterThanOrEqualTo(0);
    }
}
