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

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext // Réinitialise le contexte entre chaque test
public class FloodStationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FloodStationService floodStationService;

    @Test
    void testGetFloodStation_returnValidResponse() throws Exception {
        // Exécution de la requête GET avec plusieurs valeurs
        ResultActions response = mockMvc.perform(get("/flood/stations")
                .param("stations", "1", "2") // Modification ici : 2 valeurs pour "stations"
                .contentType(MediaType.APPLICATION_JSON));

        // Vérification du statut HTTP et du contenu de la réponse
        response.andExpect(status().isOk());

        // Désérialisation et validation des résultats
        Map<String, List<FloodStation>> floodStations = objectMapper.readValue(
                response.andReturn().getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, List.class));

        assertThat(floodStations).isNotEmpty();
        assertThat(floodStations.values().stream().findFirst().orElse(List.of())).isNotEmpty();
    }

    @Test
    void testGetFloodStation_returnNoContent() throws Exception {
        // Exécution de la requête GET avec un numéro de caserne inexistant
        mockMvc.perform(get("/flood/stations")
                .param("stations", "999") // Caserne inexistante
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Vérifie que le statut est 204 (No Content)
    }

    @Test
    void testGetFloodStation_returnBadRequestForInvalidParams() throws Exception {
        // Test avec des paramètres invalides (vide)
        mockMvc.perform(get("/flood/stations")
                .param("stations", "") // Paramètre vide
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Vérifie que le statut est 400 (Bad Request)

        // Test avec des paramètres null
        mockMvc.perform(get("/flood/stations")
                .param("stations", (String) null) // Paramètre null
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Vérifie que le statut est 400 (Bad Request)
    }

    @Test
    void testGetFloodStation_validateLogic() throws Exception {
        // Liste des numéros de casernes valides
        List<Integer> stationNumbers = List.of(1);

        // Appel direct au service pour valider les résultats
        Map<String, List<FloodStation>> floodStations = floodStationService.getFloodStation(stationNumbers);

        // Vérifications
        assertThat(floodStations).isNotEmpty();
        List<FloodStation> firstList = floodStations.values().stream().findFirst().orElse(List.of());
        assertThat(firstList).isNotEmpty(); // Vérifie qu'il y a au moins un foyer
        if (!firstList.isEmpty()) {
            FloodStation firstEntry = firstList.get(0);
            assertThat(firstEntry.name()).isNotEmpty(); // Vérifie que le nom est renseigné
            assertThat(firstEntry.address()).isNotEmpty(); // Vérifie que l'adresse est renseignée
        }
    }
}
