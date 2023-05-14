package ru.practicum.events;

import ru.practicum.events.dto.EventCreateDto;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventUpdateDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventDto add(Long userId, EventCreateDto dto);

    EventDto updateFromUser(Long userId, Long eventId, EventUpdateDto dto);

    EventDto updateFromAdmin(Long eventId, EventUpdateDto dto);


    List<EventDto> getPublishedWithFilters(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, Integer from,
                                Integer size, HttpServletRequest request);

    EventDto getPublishedById(Long id, HttpServletRequest request);

    EventDto getEventInfoByUser(Long eventId, Long userId);

    List<EventDto> getEventsByUser(Long userId);

    List<EventDto> getEventsWithFilters(List<Long> userIds, List<EventStatus> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Integer from, Integer size);

    List<EventDto> getByModeration();

    EventDto addModerationResolve(Long eventId, EventStateAction state, String comment);
}