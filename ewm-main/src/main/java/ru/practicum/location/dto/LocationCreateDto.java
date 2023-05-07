package ru.practicum.location.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationCreateDto {

    private Long id;

    @NotNull
    private Float lat;

    @NotNull
    private Float lon;

    public LocationCreateDto(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }
}