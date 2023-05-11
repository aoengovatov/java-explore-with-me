package ru.practicum.request;

import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestUpdateStatusDto;
import ru.practicum.request.dto.RequestUpdateStatusResultDto;

import java.util.List;

public interface RequestService {
    RequestDto add(Long userId, Long eventId);

    List<RequestDto> getRequestsInfoByUser(Long userId);

    List<RequestDto> getRequestsByUserEvent(Long userId, Long eventId);

    RequestDto cancel(Long userId, Long requestId);

    RequestUpdateStatusResultDto updateRequestStatus(Long userId, Long eventId, RequestUpdateStatusDto dto);
}