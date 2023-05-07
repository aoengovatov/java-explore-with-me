package ru.practicum.location.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationCreateDto {

    private Long id;

    @NotNull
    private float lat;

    @NotNull
    private float lon;

    public LocationCreateDto(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }
}