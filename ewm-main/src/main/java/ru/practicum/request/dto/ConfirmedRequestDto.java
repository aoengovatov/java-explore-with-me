package ru.practicum.request.dto;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmedRequestDto {
    @Id
    private Long eventId;

    private Long confirmedRequest;
}