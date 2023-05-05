package ru.practicum.compilations.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompilationUpdateDto {

    private Boolean pinned;

    private String title;

    private List<Long> events;
}
