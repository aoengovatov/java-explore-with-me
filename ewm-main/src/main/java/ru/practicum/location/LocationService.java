package ru.practicum.location;

import ru.practicum.location.dto.LocationCreateDto;
import ru.practicum.location.dto.LocationDto;

public interface LocationService {
    LocationDto add(LocationCreateDto locationCreateDto);

}