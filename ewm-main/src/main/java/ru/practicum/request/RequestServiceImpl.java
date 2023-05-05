package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.events.EventRepository;
import ru.practicum.events.EventService;
import ru.practicum.events.EventStatus;
import ru.practicum.events.model.Event;
import ru.practicum.exception.FieldValidationException;
import ru.practicum.exception.RequestNotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestUpdateStatusDto;
import ru.practicum.request.dto.RequestUpdateStatusResultDto;
import ru.practicum.request.model.Request;
import ru.practicum.users.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;
    private final EventRepository eventRepository;


    @Override
    @Transactional
    public RequestDto add(Long userId, Long eventId) {
        userService.getById(userId);
        checkDoubleRequest(eventId, userId);
        Event event = eventRepository.findById(eventId).get();
        checkEventStatusAndLimitRequests(event);
        checkRequestFromInitiator(event, userId);
        Request request = new Request();
        request.setEventId(eventId);
        request.setRequesterId(userId);
        if (!event.getRequestModeration()) {
            request.setState(RequestStatus.CONFIRMED);
        } else {
            request.setState(RequestStatus.PENDING);
        }
        request.setCreated(LocalDateTime.now());
        return RequestMapper.requestToRequestDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getRequestsInfoByUser(Long userId) {
        userService.getById(userId);
        List<Request> requests = requestRepository.findAllByRequester(userId);
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        return requests.stream()
                .map(RequestMapper::requestToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getRequestsByUserEvent(Long userId, Long eventId) {
        userService.getById(userId);
        eventService.getEventInfoByUser(eventId, userId);
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        return requests.stream()
                .map(RequestMapper::requestToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDto cancel(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndUserId(userId, requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request with id=" + requestId + "was not found"));
        request.setState(RequestStatus.CANCELED);
        return RequestMapper.requestToRequestDto(request);
    }

    @Override
    @Transactional
    public RequestUpdateStatusResultDto updateRequestStatus(Long userId, Long eventId, RequestUpdateStatusDto dto) {
        Event event = eventRepository.getByIdAndInitiatorId(eventId,userId).get();
        List<Request> requests = requestRepository.findAllById(dto.getRequestIds());
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        checkEventStatusAndLimitRequests(event);
        if (event.getParticipantLimit() > 0 || !event.getRequestModeration()) {
            for (Request request : requests) {
                if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                    switch (dto.getStatus()) {
                        case CONFIRMED:
                            if (request.getState().equals(RequestStatus.PENDING)) {
                                request.setState(dto.getStatus());
                                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                                confirmedRequests.add(request);
                            }
                            break;
                        case REJECTED:
                            if (request.getState().equals(RequestStatus.PENDING)) {
                                request.setState(dto.getStatus());
                                rejectedRequests.add(request);
                            }
                            break;
                    }
                } else {
                    request.setState(RequestStatus.REJECTED);
                    rejectedRequests.add(request);
                }
            }
        }

        return new RequestUpdateStatusResultDto(confirmedRequests.stream().map(RequestMapper::requestToRequestDto)
                .collect(Collectors.toList()),
                rejectedRequests.stream().map(RequestMapper::requestToRequestDto).collect(Collectors.toList()));
    }

    private Integer getConfirmedRequestsByEventId(Long eventId) {
        return requestRepository.confirmedRequestsByEventId(eventId);
    }

    private void checkDoubleRequest(Long eventId, Long userId) {
        Optional<Request> request = requestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (request.isPresent()) {
            throw new FieldValidationException("Double request from User with id=" + userId +
                    " to event with id=" + eventId);
        }
    }

    private void checkRequestFromInitiator(Event event, Long userId) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new FieldValidationException("Request from Initiator with id=" + userId +
                    " to event with id=" + event.getId());
        }
    }

    private void checkEventStatusAndLimitRequests(Event event) {
        if (event.getState() != EventStatus.PUBLISHED) {
            throw new FieldValidationException("Error. Requests for non published event " +
                    "with id=" + event.getId());
        }
        event.setConfirmedRequests(getConfirmedRequestsByEventId(event.getId()));
        if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new FieldValidationException("The event requests limit has been reached " +
                    "with event id=" + event.getId());
        }
    }
}