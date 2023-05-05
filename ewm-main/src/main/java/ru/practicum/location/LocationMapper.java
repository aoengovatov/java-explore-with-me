package ru.practicum.location;

import lombok.experimental.UtilityClass;
import ru.practicum.location.dto.LocationCreateDto;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;

@UtilityClass
public class LocationMapper {

    public Location createDtoToLocation(LocationCreateDto dto) {
        return new Location(dto.getId(), dto.getLat(), dto.getLon());
    }

    public LocationDto toLocationDto(Location location) {
        return new LocationDto(location.getId(), location.getLat(), location.getLon());
    }

    public Location locationDtoToLocation(LocationDto dto) {
        return new Location(dto.getId(), dto.getLat(), dto.getLon());
    }
}