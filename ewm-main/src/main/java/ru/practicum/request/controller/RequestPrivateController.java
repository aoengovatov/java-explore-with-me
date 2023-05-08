package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.RequestService;
import ru.practicum.request.dto.RequestDto;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class RequestPrivateController {

    @Autowired
    private final RequestService requestService;

    @GetMapping("/{userId}/requests")
    public List<RequestDto> getOtherRequestsInfoByUser(@PathVariable Long userId) {
        return requestService.getRequestsInfoByUser(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto addRequestByUser(@PathVariable Long userId,
                                       @RequestParam (value = "eventId") Long eventId) {
        return requestService.add(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequestByUser(@PathVariable Long userId,
                                    @PathVariable Long requestId) {
        return requestService.cancel(userId, requestId);
    }
}