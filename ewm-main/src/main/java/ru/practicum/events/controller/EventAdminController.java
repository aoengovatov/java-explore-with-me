package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.EventService;
import ru.practicum.events.EventStatus;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private final EventService eventService;

    @GetMapping
    public List<EventDto> searchEvents(@RequestParam(value = "users", required = false) List<Long> userIds,
                                       @RequestParam(value = "states", required = false) List<EventStatus> states,
                                       @RequestParam(value = "categories", required = false) List<Long> categories,
                                       @RequestParam(value = "rangeStart", required = false) LocalDateTime rangeStart,
                                       @RequestParam(value = "rangeEnd", required = false) LocalDateTime rangeEnd,
                                       @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                       @Positive @RequestParam (name = "size", defaultValue = "10") Integer size) {
        return eventService.getEventsWithFilters(userIds, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEventById(@PathVariable Long eventId,
                                    @RequestBody @Valid EventUpdateDto dto) {
        return eventService.updateFromAdmin(eventId, dto);
    }
}