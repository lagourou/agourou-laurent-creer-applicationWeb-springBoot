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
@DirtiesContext // Réinitialise le contexte entre chaque test
public class StationNumberIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StationNumberService stationNumberService;

    @Test
    void testGetPersonsByStation_returnValidResponse() throws Exception {
        // Numéro de caserne existant dans le fichier JSON
        int stationNumber = 1;

        // Exécution de la requête GET
        ResultActions response = mockMvc.perform(get("/firestation")
                .param("stationNumber", String.valueOf(stationNumber))
                .contentType(MediaType.APPLICATION_JSON));

        // Vérification du statut HTTP et du contenu de la réponse
        response.andExpect(status().isOk());

        // Désérialisation et validation des résultats
        FirestationByPerson firestationByPerson = objectMapper.readValue(
                response.andReturn().getResponse().getContentAsString(),
                FirestationByPerson.class);

        assertThat(firestationByPerson).isNotNull();
        assertThat(firestationByPerson.persons()).isNotEmpty(); // Vérifie que des personnes sont présentes
        assertThat(firestationByPerson.numberOfAdults()).isGreaterThanOrEqualTo(0); // Vérifie le nombre d'adultes
        assertThat(firestationByPerson.numberOfChildren()).isGreaterThanOrEqualTo(0); // Vérifie le nombre d'enfants
    }

    @Test
    void testGetPersonsByStation_returnNoContent() throws Exception {
        // Numéro de caserne inexistant dans le fichier JSON
        int stationNumber = 999;

        // Exécution de la requête GET
        mockMvc.perform(get("/firestation")
                .param("stationNumber", String.valueOf(stationNumber))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Vérifie que le statut est 204
    }

    @Test
    void testGetPersonsByStation_returnBadRequest() throws Exception {
        // Paramètre "stationNumber" invalide (zéro ou négatif)
        int invalidStationNumber = -1;

        // Exécution de la requête GET
        mockMvc.perform(get("/firestation")
                .param("stationNumber", String.valueOf(invalidStationNumber))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Vérifie que le statut est 400
    }

    @Test
    void testGetPersonsByStation_validateLogic() throws Exception {
        // Numéro de caserne existant dans le fichier JSON
        int stationNumber = 1;

        // Appel direct au service pour valider les résultats
        FirestationByPerson firestationByPerson = stationNumberService.getPersonByStation(stationNumber);

        // Vérifications
        assertThat(firestationByPerson).isNotNull();
        assertThat(firestationByPerson.persons()).isNotEmpty(); // Vérifie que des personnes sont présentes
        assertThat(firestationByPerson.numberOfAdults()).isGreaterThanOrEqualTo(0); // Vérifie le nombre d'adultes
        assertThat(firestationByPerson.numberOfChildren()).isGreaterThanOrEqualTo(0); // Vérifie le nombre d'enfants
    }
}
