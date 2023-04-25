package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CreateHitDto;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ViewStatDto;
import ru.practicum.models.Hit;
import ru.practicum.repository.HitRepository;

import javax.transaction.Transactional;
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
    @Transactional
    public List<ViewStatDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris.isEmpty()) {
            return ViewStatMapper.toViewStatDto(hitRepository.getViewStat(start, end));
        }
        return ViewStatMapper.toViewStatDto(hitRepository.getViewStat(start, end, uris, unique));
    }
}
