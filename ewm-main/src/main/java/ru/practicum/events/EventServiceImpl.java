package ru.practicum.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.CategoryMapper;
import ru.practicum.categories.CategoryService;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.model.Category;
import ru.practicum.client.StatClient;
import ru.practicum.common.MyPageRequest;
import ru.practicum.dto.CreateHitDto;
import ru.practicum.dto.ViewStatDto;
import ru.practicum.events.dto.EventCreateDto;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventUpdateDto;
import ru.practicum.events.model.Event;
import ru.practicum.exception.CheckStatusException;
import ru.practicum.exception.EventNotFoundException;
import ru.practicum.exception.FieldValidationException;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.LocationService;
import ru.practicum.location.dto.LocationCreateDto;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;
import ru.practicum.users.UserService;
import ru.practicum.users.dto.UserDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    @Autowired
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final StatClient statClient;

    @Override
    @Transactional
    public EventDto add(Long userId, EventCreateDto dto) {
        checkDate(dto.getEventDate());
        UserDto user = userService.getById(userId);
        CategoryDto category = categoryService.getById(dto.getCategory());
        LocationCreateDto locationCreateDto = new LocationCreateDto(
                dto.getLocation().getLat(), dto.getLocation().getLon());
        LocationDto location = locationService.add(locationCreateDto);
        Event event = EventMapper.createDtoToEvent(user, category, location, dto);
        event.setState(EventStatus.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setConfirmedRequests(0);
        eventRepository.save(event);
        EventDto eventDto = EventMapper.eventToEventDto(event);
        eventDto.setViews(0);
        return eventDto;
    }

    @Override
    @Transactional
    public EventDto updateFromUser(Long userId, Long eventId, EventUpdateDto dto) {
        Event event = eventRepository.getByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + "was not found"));
        checkEventStatusBeforeUpdate(event.getState());
        userService.getById(userId);
        updateEventFields(event, dto);
        EventDto eventDto = EventMapper.eventToEventDto(event);
        Map<String,Long> views = getViewsByEvents(List.of(event));
        setViewsByEventsDto(List.of(eventDto), views);
        return eventDto;
    }

    @Override
    @Transactional
    public EventDto updateFromAdmin(Long eventId, EventUpdateDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + "was not found"));
        updateEventFields(event, dto);
        EventDto eventDto = EventMapper.eventToEventDto(event);
        Map<String,Long> views = getViewsByEvents(List.of(event));
        setViewsByEventsDto(List.of(eventDto), views);
        return eventDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getPublishedWithFilters(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort,
                                       Integer from, Integer size, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String url = request.getRequestURI();
        addHit(ip, url);
        List<Event> events = eventRepository.getPublishedEvents(text, categories, paid, rangeStart, rangeEnd,
                new MyPageRequest(from, size, Sort.unsorted()));
        Map<String,Long> views = getViewsByEvents(events);
        List<EventDto> eventDtos = events.stream()
                .map(EventMapper::eventToEventDto)
                .collect(Collectors.toList());
        setViewsByEventsDto(eventDtos, views);
        return eventDtos;
    }


    @Override
    @Transactional(readOnly = true)
    public EventDto getPublishedById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdWithStatus(eventId, EventStatus.PUBLISHED)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + "was not found"));
        String ip = request.getRemoteAddr();
        String url = request.getRequestURI();
        addHit(ip, url);
        EventDto eventDto = EventMapper.eventToEventDto(event);
        Map<String,Long> views = getViewsByEvents(List.of(event));
        setViewsByEventsDto(List.of(eventDto), views);
        return eventDto;
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEventInfoByUser(Long eventId, Long userId) {
        return EventMapper.eventToEventDto(eventRepository.getByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + "was not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getEventsByUser(Long userId) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId);
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        return events.stream()
                .map(EventMapper::eventToEventDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getEventsWithFilters(List<Long> userIds, List<EventStatus> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Integer from, Integer size) {
        List<Event> events = eventRepository.getEventsWithFilters(userIds, states, categories, rangeStart, rangeEnd,
                new MyPageRequest(from, size, Sort.unsorted()));
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        return events.stream()
                .map(EventMapper::eventToEventDto)
                .collect(Collectors.toList());
    }

    private void checkDate(LocalDateTime eventDate) {
        if (!eventDate.isAfter(LocalDateTime.now().plusHours(2))) {
            throw new FieldValidationException("Field: eventDate. Error: должно содержать дату, " +
                    "которая еще не наступила. Value: " + eventDate);
        }
    }

    private boolean checkNull(Object o) {
        return o != null;
    }

    private boolean checkNullOrBlank(Object o) {
        return o != null && !o.equals("");
    }

    private boolean checkEventStatusBeforeUpdate(EventStatus state) {
        if (state.equals(EventStatus.CANCELED) || state.equals(EventStatus.PENDING)) {
            return true;
        } else {
            throw new FieldValidationException("Field: state. Error: изменить можно только отмененные события " +
                    "или события в состоянии ожидания модерации. Value: " + state);
        }
    }

    private void updateEventFields(Event event, EventUpdateDto dto) {
        if (checkNull(dto.getEventDate())) {
            checkDate(dto.getEventDate());
            event.setEventDate(dto.getEventDate());
        }
        if (checkNull(dto.getAnnotation())) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (checkNull(dto.getCategory())) {
            Category category = CategoryMapper.dtoToCategory(categoryService.getById(dto.getCategory()));
            event.setCategory(category);
        }
        if (checkNull(dto.getDescription())) {
            event.setDescription(dto.getDescription());
        }
        if (checkNull(dto.getLocation())) {
            if (dto.getLocation().getLat() != event.getLocation().getLat() ||
                    dto.getLocation().getLon() != event.getLocation().getLon()) {
                LocationCreateDto locationCreateDto = new LocationCreateDto(
                        dto.getLocation().getLat(), dto.getLocation().getLon());
                Location location = LocationMapper.locationDtoToLocation(locationService.add(locationCreateDto));
                event.setLocation(location);
            }
        }
        if (checkNull(dto.getPaid())) {
            event.setPaid(dto.getPaid());
        }
        if (checkNull(dto.getParticipantLimit())) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (checkNull(dto.getRequestModeration())) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        checkEventState(event, dto);
        if (checkNullOrBlank(dto.getTitle())) {
            event.setTitle(dto.getTitle());
        }
    }

    private void checkEventState(Event event, EventUpdateDto dto) {
        if (checkNull(dto.getStateAction())) {
            switch (dto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (event.getState().equals(EventStatus.PUBLISHED) ||
                            event.getState().equals(EventStatus.CANCELED)) {
                        throw new CheckStatusException("событие можно публиковать, только если оно в " +
                                "состоянии ожидания публикации");
                    }
                    if (event.getState().equals(EventStatus.PENDING)) {
                        event.setState(EventStatus.PUBLISHED);
                        event.setPublishedOn(LocalDateTime.now());
                    }
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventStatus.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(EventStatus.PENDING);
                    break;
                case REJECT_EVENT:
                    if (event.getState().equals(EventStatus.PUBLISHED)) {
                        throw new CheckStatusException("событие можно отклонить, только если оно еще не опубликовано");
                    }
                    event.setState(EventStatus.CANCELED);
                    break;
            }
        }
    }

    private Map<String, Long> getViewsByEvents(List<Event> events) {
        List<ViewStatDto> eventViews = getEventsViewList(events);
        Map<String, Long> views =  new HashMap<>();
        for (ViewStatDto eventView : eventViews) {
            views.put(eventView.getUri(), eventView.getHits());
        }
        return views;
    }

    private void setViewsByEventsDto(List<EventDto> dto, Map<String, Long> views) {
        if (!views.isEmpty()) {
            for (EventDto event : dto) {
                Long hits = views.get(String.format("/events/%s", event.getId()));
                if (hits != null) {
                    event.setViews(hits.intValue());
                } else {
                    event.setViews(0);
                }
            }
        }
    }

    private List<ViewStatDto> getEventsViewList(List<Event> events) {
        List<String> uris = events.stream()
                .map(e -> String.format("/events/%s", e.getId()))
                .collect(Collectors.toList());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String start = LocalDateTime.now().minusMonths(3).format(formatter);
        String end = LocalDateTime.now().plusMonths(3).format(formatter);

        return statClient.getStat(start, end, uris, false);
    }

    private void addHit(String ip, String url) {
        log.info("ADD HIT ip: {}, url: {}", ip, url);
        statClient.addHit(new CreateHitDto("ewm-main", url, ip, LocalDateTime.now()));
    }
}