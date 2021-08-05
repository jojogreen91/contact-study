package com.fastcampus.javaallinone.project3.mycontact.service;

import com.fastcampus.javaallinone.project3.mycontact.controller.dto.PersonDto;
import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import com.fastcampus.javaallinone.project3.mycontact.exception.PersonNotFoundException;
import com.fastcampus.javaallinone.project3.mycontact.exception.RenameNotPermittedException;
import com.fastcampus.javaallinone.project3.mycontact.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public List<Person> getPeopleByName (String name) {

        return personRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public Person getPerson (Long id) {

        Person person = personRepository.findById(id).orElse(null);

        log.info("Person : {}", person);

        return person;
    }

    @Transactional
    public void put (PersonDto personDto) {

        Person person = new Person();
        person.set(personDto);
        person.setName(personDto.getName());

        personRepository.save(person);
    }

    @Transactional
    public void modify (Long id, PersonDto personDto) {

        Person person = personRepository.findById(id).orElseThrow(() -> new RuntimeException("아이디가 존재하지 않습니다."));

        // custom Exception 을 만들어서 적용해 보았다
        if (!person.getName().equals(personDto.getName())) {
            throw new RenameNotPermittedException();
        }

        person.set(personDto);

        personRepository.save(person);
    }

    @Transactional
    public void modify (Long id, String name) {

        // custom Exception 을 만들어서 적용해 보았다
        Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException());

        person.setName(name);

        personRepository.save(person);
    }

    @Transactional
    public void delete (Long id) {

        Person person = personRepository.findById(id).orElseThrow(() -> new RuntimeException("아이디가 존재하지 않습니다."));

        //personRepository.delete(person);

        person.setDeleted(true);
        personRepository.save(person);
    }
}
