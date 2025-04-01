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
public class PersonInfolastNameIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonInfolastNameService personInfolastNameService;

    @Test
    void testRetourPersonnesValides() throws Exception {
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

    @Test
    void testAucunePersonne() throws Exception {
        String lastName = "UnknownLastName";

        mockMvc.perform(get("/personInfolastName")
                .param("lastName", lastName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testNomInvalide() throws Exception {
        String invalidLastName = "";

        mockMvc.perform(get("/personInfolastName")
                .param("lastName", invalidLastName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testNomValide() throws Exception {
        String lastName = "Boyd";

        List<PersonInfolastName> personInfolastNames = personInfolastNameService.getPersonInfolastName(lastName);

        assertThat(personInfolastNames).isNotEmpty();
        assertThat(personInfolastNames.get(0).lastName()).isEqualTo(lastName);
        assertThat(personInfolastNames.get(0).medications()).isNotNull();
        assertThat(personInfolastNames.get(0).allergies()).isNotNull();
        assertThat(personInfolastNames.get(0).email()).isNotEmpty();
    }
}
