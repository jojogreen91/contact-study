package com.fastcampus.javaallinone.project3.mycontact.repository;

import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import com.fastcampus.javaallinone.project3.mycontact.domain.dto.Birthday;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

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
    void findByName () {

        givenPeople();
        List<Person> people = personRepository.findByName("jo");

        assertThat(people.size()).isEqualTo(1);

        Person person = people.get(0);
        assertAll (
                () -> assertThat(person.getName()).isEqualTo("jo"),
                () -> assertThat(person.getBirthday()).isEqualTo(Birthday.of(LocalDate.of(1991, 10, 14))),
                () -> assertThat(person.getHobby()).isEqualTo("coding")
        );
    }

    @Test
    void findByNameIfDeleted () {

        givenPeople();
        List<Person> people = personRepository.findByName("kim");

        assertThat(people.size()).isEqualTo(0);
    }

    @Test
    void findByMonthOfBirthday () {

        givenPeople();
        List<Person> people = personRepository.findByMonthOfBirthday(1);

        assertThat(people.size()).isEqualTo(1);

        Person person = people.get(0);
        assertAll(
                () -> assertThat(person.getName()).isEqualTo("park"),
                () -> assertThat(person.getBirthday()).isEqualTo(Birthday.of(LocalDate.of(1993, 1, 11))),
                () -> assertThat(person.getHobby()).isEqualTo("painting")
        );
    }

    @Test
    void findPeopleDeleted () {

        givenPeople();

        List<Person> deletedPeople = personRepository.findPeopleDeleted();

        assertThat(deletedPeople.size()).isEqualTo(1);

        Person person = deletedPeople.get(0);
        assertAll(
                () -> assertThat(person.getName()).isEqualTo("kim"),
                () -> assertThat(person.getBirthday()).isEqualTo(Birthday.of(LocalDate.of(1990, 12, 12))),
                () -> assertThat(person.getHobby()).isEqualTo("game")
        );
    }

    void givenPerson (String name, LocalDate birthday, String hobby) {
        Person person = new Person(name).setBirthday(Birthday.of(birthday)).setHobby(hobby);
        personRepository.save(person);
    }

    void givenDeletedPerson (String name, LocalDate birthday, String hobby) {
        Person person = new Person(name).setBirthday(Birthday.of(birthday)).setHobby(hobby).setDeleted(true);
        personRepository.save(person);
    }

    void givenPeople () {
        givenPerson("jo", LocalDate.of(1991, 10, 14), "coding");
        givenPerson("park", LocalDate.of(1993, 1, 11), "painting");
        givenDeletedPerson("kim", LocalDate.of(1990, 12, 12), "game");
    }
}