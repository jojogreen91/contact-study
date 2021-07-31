package com.fastcampus.javaallinone.project3.mycontact.service;

import com.fastcampus.javaallinone.project3.mycontact.domain.Block;
import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import com.fastcampus.javaallinone.project3.mycontact.repository.BlockRepository;
import com.fastcampus.javaallinone.project3.mycontact.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PersonServiceTest {

    @Autowired
    private PersonService personService;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private BlockRepository blockRepository;

    @Test
    void getPeopleExcludeBlocks () {
        givenPeople();

        List<Person> result = personService.getPeopleExcludeBlocks();
        result.stream().forEach(rslt -> System.out.print(rslt.getName() + " "));
    }

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
    void findByBirthdayBetween () {

        givenPerson("kim", 12, "A");
        givenPerson("jo", 19, "AB");
        givenPerson("ko", 11, "A");
        givenPerson("park", 10, "O");

        Person person1 = personRepository.findById(1L).get();
        person1.setBirthday(LocalDate.of(1991, 8, 14));
        personRepository.save(person1);
        Person person2 = personRepository.findById(2L).get();
        person2.setBirthday(LocalDate.of(1991, 9, 10));
        personRepository.save(person2);
        Person person3 = personRepository.findById(3L).get();
        person3.setBirthday(LocalDate.of(1991, 8, 24));
        personRepository.save(person3);
        Person person4 = personRepository.findById(4L).get();
        person4.setBirthday(LocalDate.of(1991, 10, 14));
        personRepository.save(person4);

        List<Person> result = personRepository.findByBirthdayBetween(LocalDate.of(1991, 8, 1), LocalDate.of(1991, 8, 31));

        result.forEach(f -> System.out.println(f.getName()));
    }

    @Test
    void cascadeTest () {
        givenPeople();

        // person 출력
        List<Person> result = personRepository.findAll();
        result.stream().forEach(f -> System.out.println(f));

        System.out.println("--------------------------------");

        // 차단된 person 의 block 객체 update
        Person person = result.get(3);
        person.getBlock().setStartDate(LocalDate.now());
        person.getBlock().setEndDate(LocalDate.now());
        personRepository.save(person);
        personRepository.findAll().stream().forEach(f -> System.out.println(f));

        System.out.println("--------------------------------");

        // person 삭제와 block 삭제 연동
        personRepository.delete(person);
        personRepository.findAll().stream().forEach(f -> System.out.println(f));
        blockRepository.findAll().stream().forEach(f -> System.out.println(f));
    }

    private void givenPeople() {
        givenPerson("jo", 20, "O");
        givenPerson("park", 18, "B");
        givenPerson("kim", 22, "A");
        givenBlockPerson("kim", 28, "AB");
    }

    private void givenPerson(String name, int age, String bloodType) {
        personRepository.save(new Person(name, age, bloodType));
    }

    private void givenBlockPerson(String name, int age, String bloodType) {
        Person person = new Person(name, age, bloodType);

        // person 의 멤버변수 block 의 cascade 영속성
        person.setBlock(new Block(name));

        personRepository.save(person);
    }

    // Person 의 Block 은 @OneToOne(cascade = CascadeType.PERSIST) 이기 때문에 필요 없음
    /*private Block givenBlock(String name) {
        return blockRepository.save(new Block(name));
    }*/
}