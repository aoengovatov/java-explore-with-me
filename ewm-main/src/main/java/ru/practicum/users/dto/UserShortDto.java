package ru.practicum.users.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {

    private Long id;

    private String name;
}