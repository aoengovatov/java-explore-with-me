package ru.practicum.events;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    @Autowired
    private final EventService eventService;

    @GetMapping()
    public List<EventDto> getEvents(@RequestParam(value = "text", required = false) String text,
                                    @RequestParam(value = "categories", required = false) List<Long> categories,
                                    @RequestParam(value = "paid", required = false) Boolean paid,
                                    @RequestParam(value = "rangeStart", required = false) LocalDateTime rangeStart,
                                    @RequestParam(value = "rangeEnd", required = false) LocalDateTime rangeEnd,
                                    @RequestParam(value = "onlyAvailable", required = false) Boolean onlyAvailable,
                                    @RequestParam(value = "sort", required = false) EventSort sort,
                                    @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                    @Positive @RequestParam (name = "size", defaultValue = "10") Integer size,
                                    HttpServletRequest request) {
        return eventService.getPublishedWithFilters(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventDto getById(@PathVariable Long id,
                            HttpServletRequest request) {
        return eventService.getPublishedById(id, request);
    }
}