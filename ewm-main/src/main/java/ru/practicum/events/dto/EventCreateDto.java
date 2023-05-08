package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.events.EventStateAction;
import ru.practicum.location.dto.LocationDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventCreateDto {

    @NotBlank
    @Size(max = 1000)
    private String annotation;

    @NotNull
    private Long category;

    @NotNull
    private LocationDto location;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private Boolean paid;

    @NotNull
    private Integer participantLimit;

    @NotNull
    private Boolean requestModeration;

    @NotBlank
    @Size(max = 255)
    private String title;

    private EventStateAction state;
}
