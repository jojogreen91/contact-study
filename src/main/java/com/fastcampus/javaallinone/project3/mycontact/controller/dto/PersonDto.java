package com.fastcampus.javaallinone.project3.mycontact.controller.dto;

import com.fastcampus.javaallinone.project3.mycontact.domain.dto.Birthday;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
public class PersonDto {

    private String name;
    private String hobby;
    private String address;
    private LocalDate birthday;
    private String job;
    private String phoneNumber;
}
