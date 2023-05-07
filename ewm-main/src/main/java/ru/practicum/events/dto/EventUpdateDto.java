package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.events.EventStateAction;
import ru.practicum.location.dto.LocationCreateDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateDto {

    private String annotation;

    private Long category;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationCreateDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private EventStateAction stateAction;

    private String title;
}