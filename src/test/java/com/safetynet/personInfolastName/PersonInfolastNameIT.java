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
@DirtiesContext

/**
 * Tests d'intégration pour l'endpoint PersonInfolastName.
 */
public class PersonInfolastNameIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonInfolastNameService personInfolastNameService;

    /**
     * Teste la recherche des informations d'une personne par un nom de famille
     * valide.
     * Vérifie que le statut HTTP de la réponse est 200.
     *
     * @throws Exception s'il y a une erreur lors de l'exécution de la requête.
     */
    @Test
    void testReturnValidPerson() throws Exception {
        String lastName = "Boyd";

        ResultActions response = mockMvc.perform(get("/personInfolastName")
                .param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());

        List<PersonInfolastName> personInfolastNames = objectMapper.readValue(
                response.andReturn().getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, PersonInfolastName.class));

        assertThat(personInfolastNames).isNotEmpty();
        assertThat(personInfolastNames.get(0).lastName()).isEqualTo(lastName);
    }

    /**
     * Teste la recherche des informations d'une personne par un nom de famille
     * inconnu.
     * Vérifie que le statut HTTP de la réponse est 204.
     *
     * @throws Exception s'il y a une erreur lors de l'exécution de la requête.
     */
    @Test
    void testNoPerson() throws Exception {
        String lastName = "UnknownLastName";

        mockMvc.perform(get("/personInfolastName")
                .param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    /**
     * Teste la recherche des informations d'une personne par un nom de famille
     * invalide (chaîne vide).
     * Vérifie que le statut HTTP de la réponse est 400.
     *
     * @throws Exception s'il y a une erreur lors de l'exécution de la requête.
     */
    @Test
    void testInvalidName() throws Exception {
        String invalidLastName = "";

        mockMvc.perform(get("/personInfolastName")
                .param("lastName", invalidLastName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Teste la recherche des informations d'une personne par un nom de famille
     * valide.
     * Vérifie que les données retournées sont correctes.
     *
     * @throws Exception s'il y a une erreur lors de l'exécution de la requête.
     */
    @Test
    void testValidName() throws Exception {
        String lastName = "Boyd";

        List<PersonInfolastName> personInfolastNames = personInfolastNameService.getPersonInfolastName(lastName);

        assertThat(personInfolastNames).isNotEmpty();
        assertThat(personInfolastNames.get(0).lastName()).isEqualTo(lastName);
        assertThat(personInfolastNames.get(0).medications()).isNotNull();
        assertThat(personInfolastNames.get(0).allergies()).isNotNull();
        assertThat(personInfolastNames.get(0).email()).isNotEmpty();
    }
}
