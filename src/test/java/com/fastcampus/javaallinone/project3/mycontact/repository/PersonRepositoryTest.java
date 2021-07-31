package com.fastcampus.javaallinone.project3.mycontact.repository;

import com.fastcampus.javaallinone.project3.mycontact.domain.Block;
import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import com.fastcampus.javaallinone.project3.mycontact.domain.dto.Birthday;
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

    @Test
    void findByBloodType () {
        givenPerson("kim", 12, "A");
        givenPerson("jo", 19, "AB");
        givenPerson("ko", 11, "A");
        givenPerson("park", 10, "O");

        List<Person> result = personRepository.findByBloodType("A");

        result.forEach(f -> System.out.println(f.getName()));
    }

    @Test
    void findByMonthOfBirthday () {

        givenPerson("kim", 12, "A", LocalDate.of(1991, 8, 14));
        givenPerson("jo", 19, "AB", LocalDate.of(1981, 6, 20));
        givenPerson("ko", 11, "A", LocalDate.of(1997, 8, 3));
        givenPerson("park", 10, "O", LocalDate.of(1890, 3, 21));

        List<Person> result = personRepository.findByMonthOfBirthday(8);

        result.forEach(f -> System.out.println(f));
    }

    private void givenPerson(String name, int age, String bloodType) {
        givenPerson(name, age, bloodType, null);
    }

    private void givenPerson(String name, int age, String bloodType, LocalDate birthday) {

        Person person = new Person(name, age, bloodType);

        Birthday bDay = new Birthday(birthday);
        person.setBirthday(bDay);

        personRepository.save(person);
    }
}