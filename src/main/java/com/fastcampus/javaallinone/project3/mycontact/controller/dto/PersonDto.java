package com.fastcampus.javaallinone.project3.mycontact.controller.dto;

import com.fastcampus.javaallinone.project3.mycontact.domain.dto.Birthday;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder
public class PersonDto {

    // @NotEmpty(message = "I need Name!!!") // name 값은 null 이면 안되고 "" 처럼 값이 없어도 안된다
    @NotBlank(message = "I need Name!!!") // name 값은 null 이면 안되고 "", " " 처럼 값이 없거나 비어 있어서도 안된다
    private String name;
    private String hobby;
    private String address;
    private LocalDate birthday;
    private String job;
    private String phoneNumber;
}
