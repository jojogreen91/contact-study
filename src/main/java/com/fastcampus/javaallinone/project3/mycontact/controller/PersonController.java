package com.fastcampus.javaallinone.project3.mycontact.controller;

import com.fastcampus.javaallinone.project3.mycontact.controller.dto.PersonDto;
import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import com.fastcampus.javaallinone.project3.mycontact.exception.PersonNotFoundException;
import com.fastcampus.javaallinone.project3.mycontact.exception.RenameNotPermittedException;
import com.fastcampus.javaallinone.project3.mycontact.exception.dto.ErrorResponse;
import com.fastcampus.javaallinone.project3.mycontact.repository.PersonRepository;
import com.fastcampus.javaallinone.project3.mycontact.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/person")
@Slf4j
public class PersonController {

    @Autowired
    private PersonService personService;
    @Autowired
    private PersonRepository personRepository;

    @GetMapping
    public Page<Person> getAll (@PageableDefault Pageable pageable) {
        return personService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public Person getPerson (@PathVariable Long id) {

        return personService.getPerson(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void postPerson (@RequestBody @Valid PersonDto personDto) {

        personService.put(personDto);

        log.info("person -> {}", personRepository.findAll());
    }

    @PutMapping("/{id}")
    public void modifyPerson (@PathVariable Long id, @RequestBody PersonDto person) {

        personService.modify(id, person);

        log.info("person -> {}", personRepository.findAll());
    }

    @PatchMapping("/{id}") // 일부 요소만 업데이트 한다는 Annotation
    public void modifyPerson (@PathVariable Long id, String name) {

        personService.modify(id, name);

        log.info("person -> {}", personRepository.findAll());
    }

    @DeleteMapping("/{id}")
    public void deletePerson (@PathVariable Long id) {

        personService.delete(id);

        log.info("person -> {}", personRepository.findAll());
        log.info("deleted -> {}", personRepository.findPeopleDeleted());

        // 이런 boolean 리턴 값을 활용해서 제대로 삭제 되었는지 확인 할 수도 있다.
        // return personRepository.findPeopleDeleted().stream().anyMatch(person -> person.getId().equals(id));
    }
}
