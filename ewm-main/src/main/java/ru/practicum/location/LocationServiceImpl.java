package ru.practicum.location;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.location.dto.LocationCreateDto;
import ru.practicum.location.dto.LocationDto;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    @Autowired
    private final LocationRepository locationRepository;

    @Override
    public LocationDto add(LocationCreateDto dto) {
        return LocationMapper.toLocationDto(locationRepository.save(
                LocationMapper.createDtoToLocation(dto)));
    }
}