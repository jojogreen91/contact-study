package com.fastcampus.javaallinone.project3.mycontact.repository;

import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    void crud () {
        Person person = new Person();
        person.setName("jo");
        person.setAge(20);
        person.setBloodType("O");

        personRepository.save(person);

        System.out.println(personRepository.findAll());

        List<Person> people = personRepository.findAll();
        assertThat(people.size()).isEqualTo(1);
        assertThat(people.get(0).getName()).isEqualTo("jo");
        assertThat(people.get(0).getAge()).isEqualTo(20);
        assertThat(people.get(0).getBloodType()).isEqualTo("O");
    }

    @Test
    void equalsAndHashCodeTest () {
        Person person1 = new Person("jo", 20, "O");
        Person person2 = new Person("jo", 20, "O");

        System.out.println(person1.hashCode());
        System.out.println(person2.hashCode());
        System.out.println(person1.equals(person2));

        Map<Person, Integer> testMap = new HashMap<>();
        testMap.put(person1, person1.getAge());

        System.out.println(testMap);
        System.out.println(testMap.get(person2));
    }
}