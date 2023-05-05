package ru.practicum.request.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RequestCreateDto {

    @NotNull
    private Long event;

    @NotNull
    private Long requester;
}