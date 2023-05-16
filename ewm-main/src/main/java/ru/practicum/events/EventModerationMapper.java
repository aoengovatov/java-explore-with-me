package ru.practicum.events;

import lombok.experimental.UtilityClass;
import ru.practicum.events.dto.EventModerationDto;
import ru.practicum.events.model.EventModeration;

@UtilityClass
public class EventModerationMapper {

    public EventModerationDto toDto(EventModeration eventModeration) {
        return new EventModerationDto(eventModeration.getComment(),
                eventModeration.getDateTime());
    }
}