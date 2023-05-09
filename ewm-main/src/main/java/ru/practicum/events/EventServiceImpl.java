package ru.practicum.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.FieldValidationException;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.LocationService;
import ru.practicum.location.dto.LocationCreateDto;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;
import ru.practicum.request.RequestRepository;
import ru.practicum.request.RequestStatus;
import ru.practicum.request.dto.ConfirmedRequestDto;
import ru.practicum.users.UserService;
import ru.practicum.users.dto.UserDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class EventServiceImpl implements EventService {

    private final String appName;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final StatClient statClient;
    private final RequestRepository requestRepository;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public EventServiceImpl(@Value("${application.name}") String appName,
                            EventRepository eventRepository,
                            UserService userService,
                            CategoryService categoryService,
                            LocationService locationService,
                            StatClient statClient, RequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.locationService = locationService;
        this.statClient = statClient;
        this.appName = appName;
        this.requestRepository = requestRepository;
    }

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
        eventRepository.save(event);
        EventDto eventDto = EventMapper.eventToEventDto(event);
        eventDto.setViews(0);
        eventDto.setConfirmedRequests(0);
        return eventDto;
    }

    @Override
    @Transactional
    public EventDto updateFromUser(Long userId, Long eventId, EventUpdateDto dto) {
        Event event = eventRepository.getByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, eventId));
        checkEventStatusBeforeUpdate(event.getState());
        userService.getById(userId);
        updateEventFields(event, dto);
        EventDto eventDto = EventMapper.eventToEventDto(event);
        setViewsByEventsDto(List.of(eventDto));
        return eventDto;
    }

    @Override
    @Transactional
    public EventDto updateFromAdmin(Long eventId, EventUpdateDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, eventId));
        updateEventFields(event, dto);
        EventDto eventDto = EventMapper.eventToEventDto(event);
        setViewsByEventsDto(List.of(eventDto));
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
        Sort sortEvents = Sort.unsorted();
        if (sort != null && sort.equals(EventSort.EVENT_DATE)) {
            sortEvents = Sort.by(Sort.Direction.DESC, "eventDate");
        }
        List<Event> events = eventRepository.getPublishedEvents(text, categories, paid, rangeStart, rangeEnd,
                new MyPageRequest(from, size, sortEvents));
        List<EventDto> eventDtos = addViewsAndConfirmedRequests(events);
        if (sort != null && sort.equals(EventSort.VIEWS)) {
            eventDtos = eventDtos.stream()
                    .sorted(Comparator.comparing(EventDto::getViews).reversed())
                    .collect(Collectors.toList());
        }
        if (onlyAvailable) {
            eventDtos = eventDtos.stream()
                    .filter(e -> e.getConfirmedRequests() < e.getParticipantLimit())
                    .collect(toList());
        }
        return eventDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getPublishedById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdWithStatus(eventId, EventStatus.PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, eventId));
        String ip = request.getRemoteAddr();
        String url = request.getRequestURI();
        addHit(ip, url);

        return addViewsAndConfirmedRequests(List.of(event)).get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEventInfoByUser(Long eventId, Long userId) {
        return EventMapper.eventToEventDto(eventRepository.getByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, eventId)));
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
                .collect(toList());
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
        return addViewsAndConfirmedRequests(events);
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
            if (!Objects.equals(dto.getLocation().getLat(), event.getLocation().getLat()) ||
                    !Objects.equals(dto.getLocation().getLon(), event.getLocation().getLon())) {
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

    private List<EventDto> addViewsAndConfirmedRequests(List<Event> events) {
        List<EventDto> eventsDto = events.stream()
                .map(EventMapper::eventToEventDto)
                .collect(toList());
        if (!eventsDto.isEmpty()) {
            setViewsByEventsDto(eventsDto);
            setConfirmedRequestsByEventsDto(eventsDto);
        } else {
            return Collections.emptyList();
        }
        return eventsDto;
    }

    private void setViewsByEventsDto(List<EventDto> dto) {
        Map<String,Long> views = getEventsViews(dto);
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

    private  Map<String,Long> getEventsViews(List<EventDto> events) {
        List<String> uris = events.stream()
                .map(e -> String.format("/events/%s", e.getId()))
                .collect(toList());
        String start = LocalDateTime.now().minusMonths(3).format(DATETIME_FORMATTER);
        String end = LocalDateTime.now().format(DATETIME_FORMATTER);
        List<ViewStatDto> eventViews = statClient.getStat(start, end, uris, false);
        Map<String, Long> views = new HashMap<>();
        if (!eventViews.isEmpty()) {
            for (ViewStatDto eventView : eventViews) {
                views.put(eventView.getUri(), eventView.getHits());
            }
        }
        return views;
    }

    private void addHit(String ip, String url) {
        log.info("ADD HIT ip: {}, url: {}", ip, url);
        statClient.addHit(new CreateHitDto(appName, url, ip, LocalDateTime.now()));
    }

    private void setConfirmedRequestsByEventsDto(List<EventDto> dto) {
        List<Long> ids = dto.stream()
                .map(d -> d.getId())
                .collect(toList());
        List<ConfirmedRequestDto> confirmedRequests = requestRepository.getRequestsByEventIdsAndStatus(ids,
                RequestStatus.CONFIRMED);
        Map<Long, Long> eventsConfirmedRequests = new HashMap<>();
        if (!confirmedRequests.isEmpty()) {
            for (ConfirmedRequestDto request : confirmedRequests) {
                eventsConfirmedRequests.put(request.getEventId(), request.getConfirmedRequest());
            }
        }
        if (!eventsConfirmedRequests.isEmpty()) {
            for (EventDto event : dto) {
                int confirmRequests = eventsConfirmedRequests.get(event.getId()).intValue();
                event.setConfirmedRequests(confirmRequests);
            }
        }
    }
}