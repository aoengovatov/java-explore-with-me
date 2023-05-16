package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.EventService;
import ru.practicum.events.dto.EventCreateDto;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventUpdateDto;
import ru.practicum.request.RequestService;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestUpdateStatusDto;
import ru.practicum.request.dto.RequestUpdateStatusResultDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class EventPrivateController {

    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping("/{userId}/events")
    public List<EventDto> getEventsByUser(@PathVariable Long userId) {
        return eventService.getEventsByUser(userId);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto addEvent(@PathVariable Long userId,
                              @RequestBody @Valid EventCreateDto dto) {
        return eventService.add(userId, dto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventDto getEventInfo(@PathVariable Long userId,
                                 @PathVariable Long eventId) {
        return eventService.getEventInfoByUser(eventId, userId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventDto updateEvent(@PathVariable Long userId,
                                @PathVariable Long eventId,
                                @RequestBody @Valid EventUpdateDto dto) {
        return eventService.updateFromUser(userId, eventId,dto);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<RequestDto> getRequestsInfoByUser(@PathVariable Long userId,
                                                  @PathVariable Long eventId) {
        return requestService.getRequestsByUserEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public RequestUpdateStatusResultDto updateRequestStatus(@PathVariable Long userId,
                                                            @PathVariable Long eventId,
                                                            @RequestBody RequestUpdateStatusDto dto) {
        return requestService.updateRequestStatus(userId, eventId, dto);
    }
}