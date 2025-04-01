package com.safetynet.personInfolastName;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.dto.PersonInfolastName;
import com.safetynet.service.PersonInfolastNameService;
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
public class PersonInfolastNameIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonInfolastNameService personInfolastNameService;

    @Test
    void testGetPersonInfolastName_returnValidResponse() throws Exception {
        // Nom de famille existant dans le fichier JSON
        String lastName = "Boyd";

        // Exécution de la requête GET
        ResultActions response = mockMvc.perform(get("/personInfolastName")
                .param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON));

        // Vérification du statut HTTP et du contenu de la réponse
        response.andExpect(status().isOk());

        // Désérialisation et validation des résultats
        List<PersonInfolastName> personInfolastNames = objectMapper.readValue(
                response.andReturn().getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, PersonInfolastName.class));

        assertThat(personInfolastNames).isNotEmpty();
        assertThat(personInfolastNames.get(0).lastName()).isEqualTo(lastName); // Vérifie que le nom est correct
    }

    @Test
    void testGetPersonInfolastName_returnNoContent() throws Exception {
        // Nom de famille inexistant dans le fichier JSON
        String lastName = "UnknownLastName";

        // Exécution de la requête GET
        mockMvc.perform(get("/personInfolastName")
                .param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Vérifie que le statut est 204
    }

    @Test
    void testGetPersonInfolastName_returnBadRequest() throws Exception {
        // Paramètre "lastName" invalide (vide ou null)
        String invalidLastName = "";

        // Exécution de la requête GET
        mockMvc.perform(get("/personInfolastName")
                .param("lastName", invalidLastName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Vérifie que le statut est 400
    }

    @Test
    void testGetPersonInfolastName_validateLogic() throws Exception {
        // Nom de famille existant dans le fichier JSON
        String lastName = "Boyd";

        // Appel direct au service pour valider les résultats
        List<PersonInfolastName> personInfolastNames = personInfolastNameService.getPersonInfolastName(lastName);

        // Vérifications
        assertThat(personInfolastNames).isNotEmpty();
        assertThat(personInfolastNames.get(0).lastName()).isEqualTo(lastName); // Vérifie que le nom est correct
        assertThat(personInfolastNames.get(0).medications()).isNotNull(); // Vérifie les médicaments
        assertThat(personInfolastNames.get(0).allergies()).isNotNull(); // Vérifie les allergies
        assertThat(personInfolastNames.get(0).email()).isNotEmpty(); // Vérifie que l'email est renseigné
    }
}
