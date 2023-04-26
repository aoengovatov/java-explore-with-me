package ru.practicum.service;

import ru.practicum.dto.ViewStatDto;
import lombok.experimental.UtilityClass;
import ru.practicum.models.ViewStat;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ViewStatMapper {
    public List<ViewStatDto> toViewStatDto(List<ViewStat> viewStats) {
        return viewStats.stream()
                .map(viewStat -> new ViewStatDto(viewStat.getApp(), viewStat.getUri(),
                        viewStat.getHits()))
                .collect(Collectors.toList());
    }
}