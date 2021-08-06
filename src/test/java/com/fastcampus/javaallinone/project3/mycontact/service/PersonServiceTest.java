package com.fastcampus.javaallinone.project3.mycontact.service;

import com.fastcampus.javaallinone.project3.mycontact.controller.dto.PersonDto;
import com.fastcampus.javaallinone.project3.mycontact.domain.Person;
import com.fastcampus.javaallinone.project3.mycontact.domain.dto.Birthday;
import com.fastcampus.javaallinone.project3.mycontact.exception.PersonNotFoundException;
import com.fastcampus.javaallinone.project3.mycontact.exception.RenameNotPermittedException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
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
    void getAll () {

        when (personRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Lists.newArrayList(new Person("jo"), new Person("park"), new Person("kim"))));

        Page<Person> result = personService.getAll(PageRequest.of(0, 3));

        assertThat(result.getNumberOfElements()).isEqualTo(3);
        assertThat(result.getContent().get(0).getName()).isEqualTo("jo");
        assertThat(result.getContent().get(1).getName()).isEqualTo("park");
        assertThat(result.getContent().get(2).getName()).isEqualTo("kim");
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
//        verify(personRepository, times(1)).save(any(Person.class));
        verify(personRepository, times(1)).save(argThat(new IsPersonWillBeUpdated()));
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
        // custom Exception 을 만들어서 사용해 보았다
        assertThrows(RenameNotPermittedException.class, () -> personService.modify(1L, mockPersonDto()));
    }

    @Test
    void modify () {

        when (personRepository.findById(1L))
                .thenReturn(Optional.of(new Person("jo")));

        personService.modify(1L, mockPersonDto());

        // Mockito 에서의 검증 방식, 리턴 값이 void 일때 사용, 제대로 update 되었는지 IsPersonWillBeUpdated 클래스로 확인
//        verify(personRepository, times(1)).save(any(Person.class));
        verify(personRepository, times(1)).save(argThat(new IsPersonWillBeUpdated()));
    }

    @Test
    void modifyByNameIfPersonNotFound () {

        when (personRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Exception 이 발생한다는 것을 확인하는 코드
        // custom Exception 을 만들어서 사용해 보았다
        assertThrows(PersonNotFoundException.class, () -> personService.modify(1L, "park"));
    }

    @Test
    void modifyByName () {

        when (personRepository.findById(1L))
                .thenReturn(Optional.of(new Person("jo")));

        personService.modify(1L, "park");

        // Mockito 에서의 검증 방식, 리턴 값이 void 일때 사용, 제대로 update 되었는지 IsPersonWillBeUpdated 클래스로 확인
        verify(personRepository, times(1)).save(argThat(new IsNameWillBeUpdated()));
    }

    @Test
    void deleteIfPersonNotFound () {

        when (personRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> personService.delete(1L));
    }

    @Test
    void delete () {

        when (personRepository.findById(1L))
                .thenReturn(Optional.of(new Person("jo")));

        personService.delete(1L);

        verify(personRepository, times(1)).save(argThat(new IsPersonWillBeDeleted()));
    }

    // 과제 : 오늘이나 내일이 생일인 친구의 목록을 얻는 Service Test
    @Test
    void birthdayFriends () {

        // Mockito 사용
        when (personRepository.findAll())
                .thenReturn(givenPeopleList());

        // 메서드 실행
        String result = personService.birthdayFriends();

        // 반환되는 String 검증
        assertThat(result).isEqualTo("Birthday Today[조한석, 김가연] | Birthday Tomorrow[이수진]");
    }

    // 과제용 Person List 생성
    ArrayList<Person> givenPeopleList () {
        ArrayList<Person> people = new ArrayList<Person>();

        Person person1 = Person.builder()
                .name("조한석")
                .birthday(Birthday.of(LocalDate.of(1991, LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth())))
                .build();
        Person person2 = Person.builder()
                .name("박지연")
                .birthday(Birthday.of(LocalDate.of(1993, 1, 11)))
                .build();
        Person person3 = Person.builder()
                .name("김가연")
                .birthday(Birthday.of(LocalDate.of(1997, LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth())))
                .build();
        Person person4 = Person.builder()
                .name("이수진")
                .birthday(Birthday.of(LocalDate.of(1995, LocalDate.now().plusDays(1).getMonthValue(), LocalDate.now().plusDays(1).getDayOfMonth())))
                .build();

        people.add(person1);
        people.add(person2);
        people.add(person3);
        people.add(person4);

        return people;
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

    private static class IsNameWillBeUpdated implements ArgumentMatcher<Person> {

        @Override
        public boolean matches(Person person) {
            return person.getName().equals("park");
        }
    }

    private static class IsPersonWillBeDeleted implements ArgumentMatcher<Person> {

        @Override
        public boolean matches(Person person) {
            return person.getName().equals("jo")
                    && person.isDeleted(); // Entity 의 boolean 멤버변수(arg)는 get~ 이 아닌 is~ 메서드로 값을 불러올 수 있다.
        }
    }
}