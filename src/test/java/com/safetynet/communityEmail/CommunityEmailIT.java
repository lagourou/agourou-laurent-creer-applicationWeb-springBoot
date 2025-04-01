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
@DirtiesContext
public class CommunityEmailIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommunityEmailService communityEmailService;

    @Test
    void testEmailsValides() throws Exception {
        String city = "Culver";

        ResultActions response = mockMvc.perform(get("/communityEmail")
                .param("city", city)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());

        List<CommunityEmail> communityEmails = objectMapper.readValue(
                response.andReturn().getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CommunityEmail.class));

        assertThat(communityEmails).isNotEmpty();
        assertThat(communityEmails.get(0).email()).isNotEmpty();
    }

    @Test
    void testAucunEmail() throws Exception {
        String city = "UnknownCity";

        mockMvc.perform(get("/communityEmail")
                .param("city", city)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testParametreInvalide() throws Exception {
        String invalidCity = "";

        mockMvc.perform(get("/communityEmail")
                .param("city", invalidCity)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testParametrevalide() throws Exception {
        String city = "Culver";

        List<CommunityEmail> communityEmails = communityEmailService.getCommunityEmail(city);

        assertThat(communityEmails).isNotEmpty();
        assertThat(communityEmails.get(0).email()).isNotEmpty();
    }
}
