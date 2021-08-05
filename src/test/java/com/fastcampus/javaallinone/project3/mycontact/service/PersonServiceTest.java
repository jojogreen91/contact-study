package com.fastcampus.javaallinone.project3.mycontact.service;

import com.fastcampus.javaallinone.project3.mycontact.controller.dto.PersonDto;
import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import com.fastcampus.javaallinone.project3.mycontact.domain.dto.Birthday;
import com.fastcampus.javaallinone.project3.mycontact.repository.PersonRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Mockito 설정
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @InjectMocks // 테스트의 대상이 되는 클래스
    private PersonService personService;
    @Mock // @Autowired 랑 비슷한 기능
    private PersonRepository personRepository;

    @Test
    void getPeopleByName () {

        // Mockito 사용
        when (personRepository.findByName("jo"))
                .thenReturn(Lists.newArrayList(new Person("jo")));

        List<Person> result = personService.getPeopleByName("jo");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("jo");
    }

    @Test
    void getPerson () {

        // Mockito 사용
        when (personRepository.findById(1L))
                .thenReturn(Optional.of(new Person("jo")));

        Person person = personService.getPerson(1L);

        assertThat(person.getName()).isEqualTo("jo");
    }

    @Test
    void getPersonIfNotFound () {

        // Mockito 사용
        when (personRepository.findById(1L))
                .thenReturn(Optional.empty());

        Person person = personService.getPerson(1L);

        assertThat(person).isNull();
    }

    @Test
    void put () {

        personService.put(mockPersonDto());

        // Mockito 에서의 검증 방식, 리턴 값이 void 일때 사용
        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void modifyIfPersonNotFound () {

        when (personRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Exception 이 발생한다는 것을 확인하는 코드
        assertThrows(RuntimeException.class, () -> personService.modify(1L, mockPersonDto()));
    }

    @Test
    void modifyPersonIfNameIsDifferent () {

        when (personRepository.findById(1L))
                .thenReturn(Optional.of(new Person("kim")));

        // Exception 이 발생한다는 것을 확인하는 코드
        assertThrows(RuntimeException.class, () -> personService.modify(1L, mockPersonDto()));
    }

    @Test
    void modify () {

        when (personRepository.findById(1L))
                .thenReturn(Optional.of(new Person("jo")));

        // Exception 이 발생한다는 것을 확인하는 코드
        personService.modify(1L, mockPersonDto());

        // Mockito 에서의 검증 방식, 리턴 값이 void 일때 사용
//        verify(personRepository, times(1)).save(any(Person.class));
        verify(personRepository, times(1)).save(argThat(new IsPersonWillBeUpdated()));
    }

    private PersonDto mockPersonDto () {
        return PersonDto.of("jo", "coding", "부산", LocalDate.of(1991, 10, 14), "student", "010-5224-1660");
    }


    // modify 할 때 세이브된 Entity 가 제대로 update 가 되었는지 확인하기 위한 클래스, Person 의 set 메서드가 제대로 작동하는지 확인하는 클래스
    private static class IsPersonWillBeUpdated implements ArgumentMatcher<Person> {

        @Override
        public boolean matches(Person person) {
            return equals(person.getName(), "jo")
                    && equals(person.getHobby(), "coding")
                    && equals(person.getAddress(), "부산")
                    && equals(person.getBirthday(), Birthday.of(LocalDate.of(1991, 10, 14)))
                    && equals(person.getJob(), "student")
                    && equals(person.getPhoneNumber(), "010-5224-1660");
        }

        private boolean equals(Object actual, Object expected) {
            return expected.equals(actual);
        }
    }
}