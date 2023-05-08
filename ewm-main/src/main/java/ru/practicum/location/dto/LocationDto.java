package ru.practicum.location.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {

    private Long id;

    @NotNull
    private Float lat;

    @NotNull
    private Float lon;
}