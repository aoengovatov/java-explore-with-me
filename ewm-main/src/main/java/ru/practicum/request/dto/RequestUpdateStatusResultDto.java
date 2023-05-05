package ru.practicum.request.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RequestUpdateStatusResultDto {

    private List<RequestDto> confirmedRequests;

    private List<RequestDto> rejectedRequests;

}