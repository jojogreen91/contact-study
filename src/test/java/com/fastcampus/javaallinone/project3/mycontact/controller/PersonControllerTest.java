package com.fastcampus.javaallinone.project3.mycontact.controller;

import com.fastcampus.javaallinone.project3.mycontact.controller.dto.PersonDto;
import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import com.fastcampus.javaallinone.project3.mycontact.domain.dto.Birthday;
import com.fastcampus.javaallinone.project3.mycontact.exception.RenameNotPermittedException;
import com.fastcampus.javaallinone.project3.mycontact.exception.handler.GlobalExceptionHandler;
import com.fastcampus.javaallinone.project3.mycontact.repository.PersonRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class PersonControllerTest {

    @Autowired
    private PersonController personController;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MappingJackson2HttpMessageConverter messageConverter;
    // GlobalExceptionHandler ??? ???????????? ?????? ??????, ???????????? ??? ???
    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    @BeforeEach
    void beforeEach () {
        mockMvc = MockMvcBuilders
                /*
                .standaloneSetup(personController)
                .setMessageConverters(messageConverter)
                // GlobalExceptionHandler ??? ???????????? ?????? ??????, ???????????? ??? ???
                .setControllerAdvice(globalExceptionHandler)
                */
                .webAppContextSetup(wac) // ?????? 3????????? ????????? ?????? ??? ??? ?????? ??????
                .alwaysDo(print())
                .build();
    }

    @Test
    void getAll () throws Exception {
        givenPerson("jo");
        givenPerson("park");
        givenPerson("kim");

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/person")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.totalElements").value(3)) // ?????? Person ??????
                .andExpect(jsonPath("$.numberOfElements").value(1)) // ?????? ???????????? Person ??????
                //.andExpect(jsonPath("$.content.[0].name").value("jo")) // ????????? ?????????
                //.andExpect(jsonPath("$.content.[1].name").value("park")) // ????????? ?????????
                .andExpect(jsonPath("$.content.[0].name").value("kim")); // ????????? ?????????
    }

    @Test
    void getPerson () throws Exception {

        givenPerson("jo");

        // Response ?????? Body ??? ?????? (Entity ??? ??????) ?????? ?????? ?????? .andExpect ??? jason ?????? ???????????????
        // Repository ??? ?????? ??????????????? ??????????????? ???????????? ??????????????? ????????? ????????? ????????? ?????? ????????? ?????????
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/person/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("jo"))
                .andExpect(jsonPath("$.job").isEmpty())
                .andExpect(jsonPath("$.hobby").value("coding"))
                /*.andExpect(jsonPath("$.birthday.yearOfBirthday").value(1991))
                .andExpect(jsonPath("$.birthday.monthOfBirthday").value(10))
                .andExpect(jsonPath("$.birthday.dayOfBirthday").value(14))*/
                .andExpect(jsonPath("$.birthday").value("1991-10-14"))
                .andExpect(jsonPath("$.age").isNumber())
                .andExpect(jsonPath("$.birthdayToday").isBoolean());
    }

    @Test
    void postPerson () throws Exception {

        PersonDto personDto = PersonDto.builder()
                .name("jo")
                .address("??????")
                .birthday(LocalDate.of(1991, 10, 14))
                .hobby("coding")
                .build();

        mockMvc.perform(
                // MockMvcRequestBuilders.post("/api/person?name=jo&age=20&bloodType=O")) -> @RequestParam ?????? ???
                MockMvcRequestBuilders.post("/api/person")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJasonString(personDto)))
                .andExpect(status().isCreated());

        Person result = personRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).get(0);
        assertAll(
                () -> assertThat(result.getName()).isEqualTo("jo"),
                () -> assertThat(result.getAddress()).isEqualTo("??????"),
                () -> assertThat(result.getBirthday()).isEqualTo(Birthday.of(LocalDate.of(1991, 10, 14))),
                () -> assertThat(result.getHobby()).isEqualTo("coding")
        );
    }

    @Test
    void postPersonIfNameIsNull () throws Exception {

        PersonDto personDto = new PersonDto();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJasonString(personDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("I need Name!!!"));
    }

    @Test
    void postPersonIfNameIsEmpty () throws Exception {

        PersonDto personDto = new PersonDto();
        personDto.setName("");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/person")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJasonString(personDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("I need Name!!!"));
    }

    @Test
    void postPersonIfNameIsBlank () throws Exception {

        PersonDto personDto = new PersonDto();
        personDto.setName(" ");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJasonString(personDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("I need Name!!!"));
    }

    @Test
    void modifyPerson () throws Exception {

        givenPerson("jo");

        PersonDto personDto = PersonDto.builder()
                .name("jo")
                .address("??????")
                .birthday(LocalDate.of(1991, 10, 14))
                .hobby("coding")
                .build();

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/person/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJasonString(personDto)))
                .andExpect(status().isOk());

        Person result = personRepository.findById(1L).get();
        assertAll(
                () -> assertThat(result.getName()).isEqualTo("jo"),
                () -> assertThat(result.getAddress()).isEqualTo("??????"),
                () -> assertThat(result.getBirthday()).isEqualTo(Birthday.of(LocalDate.of(1991, 10, 14))),
                () -> assertThat(result.getHobby()).isEqualTo("coding")
        );
    }

    @Test
    void modifyPersonIfNameIsDifferent () throws Exception {

        givenPerson("jo");

        PersonDto personDto = PersonDto.builder()
                .name("park")
                .address("??????")
                .birthday(LocalDate.of(1991, 10, 14))
                .hobby("coding")
                .build();

        // Response ?????? Body ??? ?????? (Handling ??? Exception ??? Response)??? ?????? ?????? ?????? .andExpect ??? jason ?????? ???????????????
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/person/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJasonString(personDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Name modifying is not permitted."));
    }

    @Test
    void modifyPersonIfPersonNotFound () throws Exception {

        //givenPerson("jo");

        PersonDto personDto = PersonDto.builder()
                .name("park")
                .address("??????")
                .birthday(LocalDate.of(1991, 10, 14))
                .hobby("coding")
                .build();

        // Response ?????? Body ??? ?????? (Handling ??? Exception ??? Response)??? ?????? ?????? ?????? .andExpect ??? jason ?????? ???????????????
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/person/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJasonString(personDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Person Entity is not exist."));
    }

    @Test
    void modifyName () throws Exception {

        givenPerson("jo");

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/person/1")
                .param("name", "jojo green"))
                .andExpect(status().isOk());

        assertThat(personRepository.findById(1L).get().getName().equals("jojo green"));
    }

    @Test
    void deletePerson () throws Exception {

        givenPerson("jo");

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/person/1"))
                .andExpect(status().isOk());

        assertTrue(personRepository.findPeopleDeleted().stream().anyMatch(person -> person.getId().equals(1L)));
    }

    // ?????? : ???????????? ????????? ????????? ????????? ????????? ?????? Api Test
    @Test
    void birthdayFriends () throws Exception {

        // data.sql ??? ????????? ????????? Entity ??????, ?????? ????????? 2??? ?????? ????????? 1??? ??????
        givenPerson("?????????", LocalDate.of(1991, LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth()));
        givenPerson("?????????", LocalDate.of(1993, 1, 11));
        givenPerson("?????????", LocalDate.of(1997, LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth()));
        givenPerson("?????????", LocalDate.of(1995, LocalDate.now().plusDays(1).getMonthValue(), LocalDate.now().plusDays(1).getDayOfMonth()));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/person/birthdayFriends"))
                .andExpect(status().isOk());
    }

    void givenPerson (String name) {

        Person person = new Person(name);
        person.setHobby("coding");
        person.setBirthday(Birthday.of(LocalDate.of(1991, 10, 14)));
        personRepository.save(person);
    }

    void givenPerson (String name, LocalDate localDate) {

        Person person = new Person(name);
        person.setBirthday(Birthday.of(localDate));
        personRepository.save(person);
    }

    private String toJasonString (PersonDto personDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(personDto);
    }
}