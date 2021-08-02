package com.fastcampus.javaallinone.project3.mycontact.controller;

import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import com.fastcampus.javaallinone.project3.mycontact.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PersonControllerTest {

    @Autowired
    private PersonController personController;
    @Autowired
    private PersonRepository personRepository;

    private MockMvc mockMvc;

    @Test
    void getPerson () throws Exception {

        givenPerson("jo", 20, "O");

        mockMvc = MockMvcBuilders.standaloneSetup(personController).build();

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/person/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void postPerson () throws Exception {

        mockMvc = MockMvcBuilders.standaloneSetup(personController).build();

        mockMvc.perform(
                // MockMvcRequestBuilders.post("/api/person?name=jo&age=20&bloodType=O")) -> @RequestParam 했을 때
                MockMvcRequestBuilders.post("/api/person")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content("{\n" +
                            "  \"name\" : \"park\",\n" +
                            "  \"age\" : \"18\",\n" +
                            "  \"bloodType\" : \"O\"\n" +
                            "}"))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void modifyPerson () throws Exception {

        givenPerson("jo", 20, "O");

        mockMvc = MockMvcBuilders.standaloneSetup(personController).build();

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/person/1")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content("{\n" +
                                "  \"name\" : \"jo\",\n" +
                                "  \"age\" : \"18\",\n" +
                                "  \"bloodType\" : \"O\"\n" +
                                "}"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void modifyName () throws Exception {

        givenPerson("jo", 20, "O");

        mockMvc = MockMvcBuilders.standaloneSetup(personController).build();

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/person/1")
                .param("name", "cho"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    void givenPerson (String name, int age, String bloodType) {

        Person person = new Person(name, age, bloodType);
        personRepository.save(person);
    }
}