package com.fastcampus.javaallinone.project3.mycontact.service;

import com.fastcampus.javaallinone.project3.mycontact.domain.Block;
import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import com.fastcampus.javaallinone.project3.mycontact.repository.BlockRepository;
import com.fastcampus.javaallinone.project3.mycontact.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PersonService {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private BlockRepository blockRepository;

    public List<Person> getPeopleExcludeBlocks () {
        /*List<Person> people = personRepository.findAll();

//        List<Block> blocks = blockRepository.findAll();
//        List<String> blockNames = blocks.stream().map(b -> b.getName()).collect(Collectors.toList());
//        return people.stream().filter(person -> !blockNames.contains(person.getName())).collect(Collectors.toList());

        return people.stream().filter(f -> f.getBlock() == null).collect(Collectors.toList());*/

        return personRepository.findByBlockIsNull();
    }

    public List<Person> getPeopleByName (String name) {

        return personRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public Person getPerson (Long id) {

        Person person = personRepository.getById(id);

        log.info("Person : {}", person);

        return person;
    }
}
