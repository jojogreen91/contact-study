package com.fastcampus.javaallinone.project3.mycontact.repository;

import com.fastcampus.javaallinone.project3.mycontact.domain.Block;
import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import com.fastcampus.javaallinone.project3.mycontact.domain.dto.Birthday;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    void crud () {
        Person person = new Person();
        person.setName("jo");
        person.setBloodType("O");
        person.setBirthday(new Birthday(LocalDate.of(1991, 10, 14)));

        personRepository.save(person);

        System.out.println(personRepository.findAll());

        List<Person> people = personRepository.findAll();
        assertThat(people.size()).isEqualTo(1);
        assertThat(people.get(0).getName()).isEqualTo("jo");
        assertThat(people.get(0).getAge()).isEqualTo(31);
        assertThat(people.get(0).getBloodType()).isEqualTo("O");

        personRepository.deleteAll();
    }

    /*@Test
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
    }*/

    @Test
    void findByBloodType () {
        givenPerson("kim", "A");
        givenPerson("jo", "AB");
        givenPerson("ko", "A");
        givenPerson("park", "O");

        List<Person> result = personRepository.findByBloodType("A");

        result.forEach(f -> System.out.println(f.getName()));

        personRepository.deleteAll();
    }

    @Test
    void findByMonthOfBirthday () {

        givenPerson("kim", "A", LocalDate.of(1991, 8, 14));
        givenPerson("jo", "AB", LocalDate.of(1981, 6, 20));
        givenPerson("ko", "A", LocalDate.of(1997, 8, 3));
        givenPerson("park", "O", LocalDate.of(1890, 3, 21));

        List<Person> result = personRepository.findByMonthOfBirthday(8);

        result.forEach(f -> System.out.println(f));

        personRepository.deleteAll();
    }

    private void givenPerson(String name, String bloodType) {
        Person person = new Person(name, bloodType);

        personRepository.save(person);
    }

    private void givenPerson(String name, String bloodType, LocalDate birthday) {
        Person person = new Person(name, bloodType);

        Birthday bDay = new Birthday(birthday);
        person.setBirthday(bDay);

        personRepository.save(person);
    }
}