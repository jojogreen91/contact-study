package com.fastcampus.javaallinone.project3.mycontact.service;

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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @InjectMocks // 테스트의 대상이 되는 클래스
    private PersonService personService;
    @Mock // @Autowired 랑 비슷한 기능
    private PersonRepository personRepository;

    @Test
    void getPeopleByName () {

        givenPeople();

        when (personRepository.findByName("jo"))
                .thenReturn(Lists.newArrayList(new Person("jo")));

        List<Person> result = personRepository.findByName("jo");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("jo");
    }

    @Test
    void getPerson () {
        givenPeople();

        Person person = personService.getPerson(4L);

        System.out.println(person);
    }

    private void givenPeople() {
        givenPerson("jo");
        givenPerson("park");
        givenPerson("kim");
    }

    private void givenPerson(String name) {
        personRepository.save(new Person(name));
    }
}