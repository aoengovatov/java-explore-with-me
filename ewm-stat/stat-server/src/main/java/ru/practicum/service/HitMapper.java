package ru.practicum.service;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.CreateHitDto;
import ru.practicum.dto.HitDto;
import ru.practicum.models.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class HitMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static HitDto toHitDto(Hit hit) {
        return new HitDto(hit.getId(), hit.getApp(), hit.getUri(),
                hit.getIp(), hit.getTimestamp());
    }

    public static Hit toCreateHit(CreateHitDto dto) {
        Hit hit = new Hit();
        hit.setApp(dto.getApp());
        hit.setUri(dto.getUri());
        hit.setIp(dto.getIp());
        hit.setTimestamp(LocalDateTime.parse(dto.getTimestamp(), DATE_TIME_FORMATTER));
        return hit;
    }
}