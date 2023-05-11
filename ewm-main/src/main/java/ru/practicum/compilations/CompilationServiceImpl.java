package ru.practicum.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.MyPageRequest;
import ru.practicum.compilations.dto.CompilationCreateDto;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationUpdateDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.events.EventRepository;
import ru.practicum.events.model.Event;
import ru.practicum.exception.EntityNotFoundException;

import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto add(CompilationCreateDto dto) {
        Compilation compilation = compilationRepository.save(CompilationMapper.createDtoToCompilation(dto));
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(dto.getEvents());
            compilation.getEvents().addAll(events);
        }
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, CompilationUpdateDto dto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(Compilation.class, compId));
        if (checkNull(dto.getPinned())) {
            compilation.setPinned(dto.getPinned());
        }
        if (checkNullOrBlank(dto.getTitle())) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(dto.getEvents());
            compilation.setEvents(new HashSet<>(events));
        }
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(Compilation.class, compId));
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getWithFilter(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        if (checkNull(pinned)) {
            compilations = compilationRepository.getWithPinned(pinned, new MyPageRequest(from, size, Sort.unsorted()));
        } else {
            compilations = compilationRepository.findAllWithPagable(new MyPageRequest(from, size, Sort.unsorted()));
        }
        return compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(toList());
    }

    private boolean checkNull(Object o) {
        return o != null;
    }

    private boolean checkNullOrBlank(Object o) {
        return o != null && !o.equals("");
    }
}