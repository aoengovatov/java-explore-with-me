package ru.practicum.events;

import lombok.experimental.UtilityClass;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.model.Category;
import ru.practicum.events.dto.EventCreateDto;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.model.Event;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.model.User;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class EventMapper {

    public EventDto eventToEventDto(Event event) {
        UserShortDto user = new UserShortDto();
        user.setId(event.getInitiator().getId());
        user.setName(event.getInitiator().getName());

        CategoryDto category = new CategoryDto();
        category.setId(event.getCategory().getId());
        category.setName(event.getCategory().getName());

        LocationDto location = new LocationDto();
        location.setId(event.getLocation().getId());
        location.setLat(event.getLocation().getLat());
        location.setLon(event.getLocation().getLon());

        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(category);
        dto.setCreatedOn(event.getCreatedOn());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setInitiator(user);
        dto.setLocation(location);
        dto.setPaid(event.isPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setPublishedOn(event.getPublishedOn());
        dto.setRequestModeration(event.isRequestModeration());
        dto.setState(event.getState());
        dto.setTitle(event.getTitle());
        return dto;
    }

    public EventShortDto eventToEventShortDto(Event event) {
        UserShortDto user = new UserShortDto();
        user.setId(event.getInitiator().getId());
        user.setName(event.getInitiator().getName());

        CategoryDto category = new CategoryDto();
        category.setId(event.getCategory().getId());
        category.setName(event.getCategory().getName());

        LocationDto location = new LocationDto();
        location.setId(event.getLocation().getId());
        location.setLat(event.getLocation().getLat());
        location.setLon(event.getLocation().getLon());

        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(category);
        dto.setEventDate(event.getEventDate());
        dto.setInitiator(user);
        dto.setPaid(event.isPaid());
        dto.setTitle(event.getTitle());
        return dto;
    }

    public Event createDtoToEvent(UserDto userDto, CategoryDto categoryDto,
                                  LocationDto locationDto, EventCreateDto eventCreateDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());

        Location location = new Location();
        location.setId(locationDto.getId());
        location.setLat(locationDto.getLat());
        location.setLon(locationDto.getLon());

        Event event = new Event();
        event.setAnnotation(eventCreateDto.getAnnotation());
        event.setCategory(category);
        event.setLocation(location);
        event.setInitiator(user);
        event.setDescription(eventCreateDto.getDescription());
        event.setEventDate(eventCreateDto.getEventDate());
        event.setPaid(eventCreateDto.getPaid());
        event.setParticipantLimit(eventCreateDto.getParticipantLimit());
        event.setRequestModeration(eventCreateDto.getRequestModeration());
        event.setTitle(eventCreateDto.getTitle());
        return event;
    }

    public List<EventShortDto> toEventDto(Set<Event> events) {
        return events.stream()
                .map(EventMapper::eventToEventShortDto)
                .collect(toList());
    }
}