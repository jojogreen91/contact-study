package com.fastcampus.javaallinone.project3.mycontact.service;

import com.fastcampus.javaallinone.project3.mycontact.domain.Block;
import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import com.fastcampus.javaallinone.project3.mycontact.domain.dto.Birthday;
import com.fastcampus.javaallinone.project3.mycontact.repository.BlockRepository;
import com.fastcampus.javaallinone.project3.mycontact.repository.PersonRepository;
import com.sun.istack.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

    /*@Test
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

        personRepository.deleteAll();
        blockRepository.deleteAll();
    }*/

    private void givenPeople() {
        givenPerson("jo", "O");
        givenPerson("park", "B");
        givenPerson("kim", "A");
        givenBlockPerson("kim", "AB");
    }

    private void givenPerson(String name, String bloodType) {
        personRepository.save(new Person(name, bloodType));
    }

    private void givenBlockPerson(String name, String bloodType) {
        Person person = new Person(name, bloodType);

        // person 의 멤버변수 block 의 cascade 영속성
        person.setBlock(new Block(name));

        personRepository.save(person);
    }

    // Person 의 Block 은 @OneToOne(cascade = CascadeType.PERSIST) 이기 때문에 필요 없음
    /*private Block givenBlock(String name) {
        return blockRepository.save(new Block(name));
    }*/
}