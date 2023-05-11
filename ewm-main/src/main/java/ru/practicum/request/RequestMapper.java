package ru.practicum.request;

import lombok.experimental.UtilityClass;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;

@UtilityClass
public class RequestMapper {

    public RequestDto requestToRequestDto(Request request) {
        return new RequestDto(request.getId(), request.getEventId(),
                request.getRequesterId(), request.getState(), request.getCreated());
    }
}