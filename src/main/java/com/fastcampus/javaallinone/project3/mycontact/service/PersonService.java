package com.fastcampus.javaallinone.project3.mycontact.service;

import com.fastcampus.javaallinone.project3.mycontact.controller.dto.PersonDto;
import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import com.fastcampus.javaallinone.project3.mycontact.exception.PersonNotFoundException;
import com.fastcampus.javaallinone.project3.mycontact.exception.RenameNotPermittedException;
import com.fastcampus.javaallinone.project3.mycontact.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public List<Person> getPeopleByName (String name) {

        return personRepository.findByName(name);
    }

    @Transactional
    public Page<Person> getAll (Pageable pageable) {

        return personRepository.findAll(pageable);
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

        // custom Exception 을 만들어서 적용해 보았다
        Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException());

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

    // 과제 : 오늘이나 내일이 생일인 친구의 목록을 얻는 Service
    @Transactional
    public String birthdayFriends () {

        // 오늘 월, 일 날짜
        int monthNow = LocalDate.now().getMonthValue();
        int dayNow = LocalDate.now().getDayOfMonth();

        // 내일 월, 일 날짜
        int monthNowPlus = LocalDate.now().plusDays(1).getMonthValue();
        int dayNowPlus = LocalDate.now().plusDays(1).getDayOfMonth();

        // 오늘, 내일이 생일인 사람의 List 생성
        ArrayList<Person> birthdayToday = new ArrayList<Person>();
        ArrayList<Person> birthdayTomorrow = new ArrayList<Person>();

        // 모든 Person Entity
        List<Person> allPerson = personRepository.findAll();

        // 오늘의 월, 일 값과 같은 값의 생일을 가지는 Person 찾기
        for (int i = 0; i < allPerson.size(); i++) {
            if (allPerson.get(i).getBirthday().getMonthOfBirthday() == monthNow && allPerson.get(i).getBirthday().getDayOfBirthday() == dayNow) {
                birthdayToday.add(allPerson.get(i));
            }
        }

        // 내일의 월, 일 값과 같은 값의 생일을 가지는 Person 찾기
        for (int i = 0; i < allPerson.size(); i++) {
            if (allPerson.get(i).getBirthday().getMonthOfBirthday() == monthNowPlus && allPerson.get(i).getBirthday().getDayOfBirthday() == dayNowPlus) {
                birthdayTomorrow.add(allPerson.get(i));
            }
        }

        // 오늘, 내일이 생일인 사람의 List 에서 이름만 리스트로 만들기
        List<String> birthdayTodayName = birthdayToday.stream().map(m -> m.getName()).toList();
        List<String> birthdayTomorrowName = birthdayTomorrow.stream().map(m -> m.getName()).toList();

        // 오늘, 내일이 생일인 사람들의 이름 반환
        return "Birthday Today" + birthdayTodayName.toString() + " | "
                + "Birthday Tomorrow" + birthdayTomorrowName.toString();
    }
}
