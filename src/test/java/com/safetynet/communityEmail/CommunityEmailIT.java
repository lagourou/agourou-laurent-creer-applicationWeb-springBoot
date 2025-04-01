package com.safetynet.communityEmail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.dto.CommunityEmail;
import com.safetynet.service.CommunityEmailService;
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
@DirtiesContext // Réinitialise le contexte après chaque test
public class CommunityEmailIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommunityEmailService communityEmailService;

    @Test
    void testGetCommunityEmail_returnValidResponse() throws Exception {
        // Ville existante avec des emails dans le fichier JSON
        String city = "Culver";

        // Exécution de la requête GET
        ResultActions response = mockMvc.perform(get("/communityEmail")
                .param("city", city)
                .contentType(MediaType.APPLICATION_JSON));

        // Vérification du statut HTTP et du contenu de la réponse
        response.andExpect(status().isOk());

        // Désérialisation et validation des résultats
        List<CommunityEmail> communityEmails = objectMapper.readValue(
                response.andReturn().getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CommunityEmail.class));

        assertThat(communityEmails).isNotEmpty();
        assertThat(communityEmails.get(0).email()).isNotEmpty(); // Vérifie qu'un email est présent
    }

    @Test
    void testGetCommunityEmail_returnNoContent() throws Exception {
        // Ville existante mais sans emails associés
        String city = "UnknownCity";

        // Exécution de la requête GET
        mockMvc.perform(get("/communityEmail")
                .param("city", city)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Vérifie que le statut est 204
    }

    @Test
    void testGetCommunityEmail_returnBadRequest() throws Exception {
        // Paramètre "city" invalide (vide ou null)
        String invalidCity = "";

        // Exécution de la requête GET
        mockMvc.perform(get("/communityEmail")
                .param("city", invalidCity)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Vérifie que le statut est 400
    }

    @Test
    void testGetCommunityEmail_validateLogic() throws Exception {
        // Ville existante dans le fichier JSON
        String city = "Culver";

        // Appel direct au service pour valider les résultats
        List<CommunityEmail> communityEmails = communityEmailService.getCommunityEmail(city);

        // Vérifications
        assertThat(communityEmails).isNotEmpty();
        assertThat(communityEmails.get(0).email()).isNotEmpty(); // Vérifie qu'un email est présent
    }
}
