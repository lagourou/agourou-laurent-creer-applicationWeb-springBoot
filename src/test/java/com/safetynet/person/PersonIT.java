package com.safetynet.person;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.model.Person;

/**
 * Tests d'intégration pour l'endpoint Person.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PersonIT {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        /**
         * Teste l'ajout d'une personne via une requête HTTP POST.
         * Vérifie que le statut de la réponse est HTTP 201 (Created).
         *
         * @throws Exception si une erreur survient lors de l'exécution de la requête.
         */
        @Test
        public void testAddPersons() throws Exception {
                Person person = new Person("Jack", "Will", "1252 Roses St", "Burbank", "95051", "841-874-7589",
                                "jack.will@email.com");

                mockMvc.perform(post("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(person))))
                                .andExpect(status().isCreated());

        }

        /**
         * Teste la mise à jour des informations d'une personne via une requête HTTP
         * PUT.
         * Vérifie que le statut de la réponse est HTTP 200 (OK) après la mise à jour.
         *
         * @throws Exception si une erreur survient lors de l'exécution de la requête.
         */
        @Test
        public void testUpdatePersons() throws Exception {
                Person personExisting = new Person("Jack", "Will", "1252 Roses St", "Burbank", "95051", "841-874-7589",
                                "jack.will@email.com");

                mockMvc.perform(post("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(personExisting))))
                                .andExpect(status().isCreated());

                Person updatePerson = new Person("Jack", "Will", "1478 Spring St", "Inglewood", "97251",
                                "841-989-1234", "jack.update@email.com");
                mockMvc.perform(put("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(updatePerson))))
                                .andExpect(status().isOk());

        }

        /**
         * Teste la suppression d'une personne via une requête HTTP DELETE.
         * Vérifie que le statut de la réponse est HTTP 200 (OK) après la suppression.
         *
         * @throws Exception si une erreur survient lors de l'exécution de la requête.
         */
        @Test
        public void testDeletePersons() throws Exception {
                Person person = new Person("Jack", "Will", "1252 Roses St", "Burbank", "95051", "841-874-7589",
                                "jack.will@email.com");

                mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(person))))
                                .andExpect(status().isCreated());

                mockMvc.perform(delete("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(person))))
                                .andExpect(status().isOk());

        }

}
