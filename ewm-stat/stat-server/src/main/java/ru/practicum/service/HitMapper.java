package ru.practicum.service;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.CreateHitDto;
import ru.practicum.dto.HitDto;
import ru.practicum.models.Hit;

@UtilityClass
public class HitMapper {

    public HitDto toHitDto(Hit hit) {
        return new HitDto(hit.getId(), hit.getApp(), hit.getUri(),
                hit.getIp(), hit.getTimestamp());
    }

    public Hit toCreateHit(CreateHitDto dto) {
        Hit hit = new Hit();
        hit.setApp(dto.getApp());
        hit.setUri(dto.getUri());
        hit.setIp(dto.getIp());
        hit.setTimestamp(dto.getTimestamp());
        return hit;
    }
}