package ru.practicum.location;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.location.dto.LocationCreateDto;
import ru.practicum.location.dto.LocationDto;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public LocationDto add(LocationCreateDto dto) {
        return LocationMapper.toLocationDto(locationRepository.save(
                LocationMapper.createDtoToLocation(dto)));
    }
}