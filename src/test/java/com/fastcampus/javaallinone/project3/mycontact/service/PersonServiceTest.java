package com.fastcampus.javaallinone.project3.mycontact.service;

import com.fastcampus.javaallinone.project3.mycontact.controller.dto.PersonDto;
import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import com.fastcampus.javaallinone.project3.mycontact.repository.PersonRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Mockito 설정
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @InjectMocks // 테스트의 대상이 되는 클래스
    private PersonService personService;
    @Mock // @Autowired 랑 비슷한 기능
    private PersonRepository personRepository;

    @Test
    void getPeopleByName () {

        // Mockito 사용
        when (personRepository.findByName("jo"))
                .thenReturn(Lists.newArrayList(new Person("jo")));

        List<Person> result = personService.getPeopleByName("jo");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("jo");
    }

    @Test
    void getPerson () {

        // Mockito 사용
        when (personRepository.findById(1L))
                .thenReturn(Optional.of(new Person("jo")));

        Person person = personService.getPerson(1L);

        assertThat(person.getName()).isEqualTo("jo");
    }

    @Test
    void getPersonIfNotFound () {

        // Mockito 사용
        when (personRepository.findById(1L))
                .thenReturn(Optional.empty());

        Person person = personService.getPerson(1L);

        assertThat(person).isNull();
    }

    @Test
    void put () {

        PersonDto personDto = PersonDto.of("jo", "coding", "부산", LocalDate.of(1991, 10, 14), "student", "010-5224-1660");

        personService.put(personDto);

        // Mockito 에서의 검증 방식
        verify(personRepository, times(1)).save(any(Person.class));
    }
}