package ru.practicum.request.dto;

import lombok.*;
import ru.practicum.request.RequestStatus;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RequestUpdateStatusDto {

    private List<Long> requestIds;

    private RequestStatus status;
}