package ru.practicum.compilations;

import lombok.experimental.UtilityClass;
import ru.practicum.compilations.dto.CompilationCreateDto;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.events.EventMapper;

import java.util.HashSet;

@UtilityClass
public class CompilationMapper {

    public Compilation createDtoToCompilation(CompilationCreateDto dto) {
        Compilation compilation = new Compilation();
        compilation.setEvents(new HashSet<>());
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned());

        return compilation;
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(compilation.getId(), compilation.getPinned(),
                compilation.getTitle(), EventMapper.toEventDto(compilation.getEvents()));
    }
}