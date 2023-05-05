package ru.practicum.compilations;

import ru.practicum.compilations.dto.CompilationCreateDto;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {
    CompilationDto add(CompilationCreateDto dto);

    void delete(Long compId);

    CompilationDto update(Long compId, CompilationUpdateDto dto);

    CompilationDto getById(Long compId);

    List<CompilationDto> getWithFilter(Boolean pinned, Integer from, Integer size);

}