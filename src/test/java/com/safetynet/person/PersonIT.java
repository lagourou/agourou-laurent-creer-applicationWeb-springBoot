package com.safetynet.person;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.model.Person;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonIT {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        public void testAjoutPersonnes() throws Exception {
                Person person = new Person("Jack", "Will", "1252 Roses St", "Burbank", "95051", "841-874-7589",
                                "jack.will@email.com");

                mockMvc.perform(post("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(person))))
                                .andExpect(status().isCreated());
        }

        @Test
        public void testMiseAJourPersonnes() throws Exception {
                Person person = new Person("Jack", "Will", "1252 Roses St", "Burbank", "95051", "841-874-7589",
                                "jack.will@email.com");

                mockMvc.perform(put("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(person))))
                                .andExpect(status().isOk());
        }

        @Test
        public void testSuppressionPersonnes() throws Exception {
                Person person = new Person("Jack", "Will", "1252 Roses St", "Burbank", "95051", "841-874-7589",
                                "jack.will@email.com");

                mockMvc.perform(delete("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(person))))
                                .andExpect(status().isOk());
        }
}
