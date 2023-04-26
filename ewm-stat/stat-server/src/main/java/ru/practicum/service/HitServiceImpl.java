package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CreateHitDto;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ViewStatDto;
import ru.practicum.models.Hit;
import ru.practicum.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;

    @Override
    @Transactional
    public HitDto add(CreateHitDto dto) {
        Hit hit = HitMapper.toCreateHit(dto);
        return HitMapper.toHitDto(hitRepository.save(hit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<ViewStatDto> result;
        if (uris.isEmpty()) {
            result = ViewStatMapper.toViewStatDto(hitRepository.getViewStat(start, end));
        } else {
            result = ViewStatMapper.toViewStatDto(hitRepository.getViewStat(start, end, uris, unique));
        }
        return result;
    }
}
