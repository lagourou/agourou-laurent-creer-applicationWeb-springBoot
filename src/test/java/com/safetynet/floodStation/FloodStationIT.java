package com.safetynet.floodStation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.dto.FloodStation;
import com.safetynet.service.FloodStationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour l'endpoint FloodStation.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class FloodStationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FloodStationService floodStationService;

    /**
     * Teste la récupération des foyers pour une liste valide de casernes avec
     * des résidents.
     *
     * @throws Exception gère l'erreur lors de l'exécution du test.
     */
    @Test
    void testReturnValidHome() throws Exception {
        ResultActions response = mockMvc.perform(get("/flood/stations")
                .param("stations", "1", "2")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());

        Map<String, List<FloodStation>> floodStations = objectMapper.readValue(
                response.andReturn().getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, List.class));

        assertThat(floodStations).isNotEmpty();
        assertThat(floodStations.values().stream().findFirst().orElse(List.of())).isNotEmpty();
    }

    /**
     * Teste la récupération des foyers pour une liste valide de casernes sans
     * résidents.
     *
     * @throws Exception gère l'erreur lors de l'exécution du test.
     */
    @Test
    void testReturnNoHome() throws Exception {
        mockMvc.perform(get("/flood/stations")
                .param("stations", "999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    /**
     * Teste le comportement avec des paramètres invalides pour les casernes.
     * Vérifie qu'une réponse de type "Bad Request" est retournée.
     *
     * @throws Exception gère l'erreur lors de l'exécution du test.
     */
    @Test
    void testInvalidParameter() throws Exception {
        mockMvc.perform(get("/flood/stations")
                .param("stations", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/flood/stations")
                .param("stations", (String) null)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Teste la récupération des foyers pour une liste valide de casernes.
     * Vérifie que les foyers et leurs informations sont correctement retournés.
     *
     * @throws Exception gère l'erreur lors de l'exécution du test.
     */
    @Test
    void testValidParameter() throws Exception {
        List<Integer> stationNumbers = List.of(1);

        Map<String, List<FloodStation>> floodStations = floodStationService.getFloodStation(stationNumbers);

        assertThat(floodStations).isNotEmpty();
        List<FloodStation> firstList = floodStations.values().stream().findFirst().orElse(List.of());
        assertThat(firstList).isNotEmpty();
        if (!firstList.isEmpty()) {
            FloodStation firstEntry = firstList.get(0);
            assertThat(firstEntry.name()).isNotEmpty();
            assertThat(firstEntry.address()).isNotEmpty();
        }
    }
}
