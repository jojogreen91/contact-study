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
    // GlobalExceptionHandler 를 적용하기 위한 설정, 테스트를 할 때
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
                // GlobalExceptionHandler 를 적용하기 위한 설정, 테스트를 할 때
                .setControllerAdvice(globalExceptionHandler)
                */
                .webAppContextSetup(wac) // 위의 3가지를 한번에 설정 할 수 있는 기능
                .alwaysDo(print())
                .build();
    }

    @Test
    void getPerson () throws Exception {

        givenPerson("jo");

        // Response 되는 Body 의 내용 (Entity 의 요소) 점검 하기 위해 .andExpect 로 jason 값을 검증해준다
        // Repository 에 새로 추가되거나 수정되거나 삭제되는 변경사항이 있는게 아니라 조회만 하는 것이기 때문에
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
                .address("부산")
                .birthday(LocalDate.of(1991, 10, 14))
                .hobby("coding")
                .build();

        mockMvc.perform(
                // MockMvcRequestBuilders.post("/api/person?name=jo&age=20&bloodType=O")) -> @RequestParam 했을 때
                MockMvcRequestBuilders.post("/api/person")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJasonString(personDto)))
                .andExpect(status().isCreated());

        Person result = personRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).get(0);
        assertAll(
                () -> assertThat(result.getName()).isEqualTo("jo"),
                () -> assertThat(result.getAddress()).isEqualTo("부산"),
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
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Server Error!!!"));
    }

    @Test
    void modifyPerson () throws Exception {

        givenPerson("jo");

        PersonDto personDto = PersonDto.builder()
                .name("jo")
                .address("부산")
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
                () -> assertThat(result.getAddress()).isEqualTo("부산"),
                () -> assertThat(result.getBirthday()).isEqualTo(Birthday.of(LocalDate.of(1991, 10, 14))),
                () -> assertThat(result.getHobby()).isEqualTo("coding")
        );
    }

    @Test
    void modifyPersonIfNameIsDifferent () throws Exception {

        givenPerson("jo");

        PersonDto personDto = PersonDto.builder()
                .name("park")
                .address("부산")
                .birthday(LocalDate.of(1991, 10, 14))
                .hobby("coding")
                .build();

        // Response 되는 Body 의 내용 (Handling 된 Exception 의 Response)을 점검 하기 위해 .andExpect 로 jason 값을 검증해준다
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
                .address("부산")
                .birthday(LocalDate.of(1991, 10, 14))
                .hobby("coding")
                .build();

        // Response 되는 Body 의 내용 (Handling 된 Exception 의 Response)을 점검 하기 위해 .andExpect 로 jason 값을 검증해준다
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

    void givenPerson (String name) {

        Person person = new Person(name);
        person.setHobby("coding");
        person.setBirthday(Birthday.of(LocalDate.of(1991, 10, 14)));
        personRepository.save(person);
    }

    private String toJasonString (PersonDto personDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(personDto);
    }
}