package com.fastcampus.javaallinone.project3.mycontact.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Birthday {

    private Integer yearOfBirthday;
    private Integer monthOfBirthday;
    private Integer dayOfBirthday;

    public Birthday (LocalDate birthday) {
        this.yearOfBirthday = birthday.getYear();
        this.monthOfBirthday = birthday.getMonthValue();
        this.dayOfBirthday = birthday.getDayOfMonth();
    }

    // static Constructor
    public static Birthday of(LocalDate birthday) {
        return new Birthday(birthday);
    }
}
