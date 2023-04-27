package ru.practicum.service;

import ru.practicum.dto.CreateHitDto;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ViewStatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    HitDto add(CreateHitDto dto);

    List<ViewStatDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
