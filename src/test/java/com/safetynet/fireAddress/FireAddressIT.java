package com.safetynet.fireAddress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.dto.FireAddress;
import com.safetynet.service.FireAddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext // Réinitialise le contexte entre chaque test
public class FireAddressIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FireAddressService fireAddressService;

    @Test
    void testGetFireAddress_returnValidResponse() throws Exception {
        // Adresse existante avec habitants dans le fichier JSON
        String address = "1509 Culver St";

        // Exécution de la requête GET
        ResultActions response = mockMvc.perform(get("/fire")
                .param("address", address)
                .contentType(MediaType.APPLICATION_JSON));

        // Vérification du statut HTTP et du contenu de la réponse
        response.andExpect(status().isOk());

        // Désérialisation et validation des résultats
        List<FireAddress> fireAddresses = objectMapper.readValue(
                response.andReturn().getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, FireAddress.class));

        assertThat(fireAddresses).isNotEmpty();
        assertThat(fireAddresses.get(0).name()).isNotEmpty(); // Vérifie que le nom est présent
        assertThat(fireAddresses.get(0).phone()).isNotEmpty(); // Vérifie que le téléphone est présent
    }

    @Test
    void testGetFireAddress_returnNoContent() throws Exception {
        // Adresse valide mais sans habitants dans le fichier JSON
        String address = "unknown address";

        // Exécution de la requête GET
        mockMvc.perform(get("/fire")
                .param("address", address)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Vérifie que le statut est 204
    }

    @Test
    void testGetFireAddress_returnBadRequest() throws Exception {
        // Adresse invalide (vide ou null)
        String invalidAddress = "";

        // Exécution de la requête GET
        mockMvc.perform(get("/fire")
                .param("address", invalidAddress)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Vérifie que le statut est 400
    }

    @Test
    void testGetFireAddress_validateLogic() throws Exception {
        // Adresse existante dans le fichier JSON
        String address = "1509 Culver St";

        // Appel direct au service pour valider les résultats
        List<FireAddress> fireAddresses = fireAddressService.getFireAddress(address);

        // Vérifications
        assertThat(fireAddresses).isNotEmpty();
        assertThat(fireAddresses.get(0).name()).isNotEmpty(); // Vérifie que le nom est présent
        assertThat(fireAddresses.get(0).medications()).isNotNull(); // Vérifie les médicaments
        assertThat(fireAddresses.get(0).allergies()).isNotNull(); // Vérifie les allergies
        assertThat(fireAddresses.get(0).stationNumber()).isGreaterThan(0); // Vérifie le numéro de caserne
    }
}
