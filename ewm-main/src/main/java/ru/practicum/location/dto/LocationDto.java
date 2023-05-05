package ru.practicum.location.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {

    private Long id;

    private float lat;

    private float lon;
}