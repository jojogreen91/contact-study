package com.fastcampus.javaallinone.project3.mycontact.domain;

import com.fastcampus.javaallinone.project3.mycontact.controller.dto.PersonDto;
import com.fastcampus.javaallinone.project3.mycontact.domain.dto.Birthday;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted = false")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @NotEmpty
    @Column(nullable = false)
    private String name;

    private String hobby;

    @NonNull
    @NotEmpty
    @Column(nullable = false)
    private String bloodType;

    private String address;


    @Valid
    @Embedded
    private Birthday birthday;

    private String job;

    @ToString.Exclude
    private String phoneNumber;

    @ColumnDefault("0")
    private boolean deleted;

    // {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE} == CascadeType.ALL
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Block block;

    // Entity 를 업데이트 할 때 PersonDto 를 이용해서 수정 할 멤버변수가 존재 하지 않을 때는 업데이트를 안하기 위한 메서드
    public void set (PersonDto personDto) {

        if (!StringUtils.isEmpty(personDto.getHobby())) {
            this.setHobby(personDto.getHobby());
        }

        if (!StringUtils.isEmpty(personDto.getBloodType())) {
            this.setBloodType(personDto.getBloodType());
        }

        if (!StringUtils.isEmpty(personDto.getAddress())) {
            this.setAddress(personDto.getAddress());
        }

        if (!StringUtils.isEmpty(personDto.getJob())) {
            this.setJob(personDto.getJob());
        }

        if (!StringUtils.isEmpty(personDto.getPhoneNumber())) {
            this.setPhoneNumber(personDto.getPhoneNumber());
        }
    }

    public Integer getAge () {
        if (this.birthday != null) {
            return LocalDate.now().getYear() - this.birthday.getYearOfBirthday() + 1;
        }
        else {
            return null;
        }
    }

    public boolean isBirthdayToday () {
        return LocalDate.now().equals(LocalDate.of(this.birthday.getYearOfBirthday(), this.birthday.getMonthOfBirthday(), this.birthday.getDayOfBirthday()));
    }
}
