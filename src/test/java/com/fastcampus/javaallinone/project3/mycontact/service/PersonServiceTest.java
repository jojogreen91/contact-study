package com.fastcampus.javaallinone.project3.mycontact.service;

import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import com.fastcampus.javaallinone.project3.mycontact.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class PersonServiceTest {

    @Autowired
    private PersonService personService;
    @Autowired
    private PersonRepository personRepository;

    @Test
    void getPeopleByName () {
        givenPeople();

        List<Person> result = personService.getPeopleByName("kim");

        result.forEach(f -> System.out.println(f));
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