package ru.practicum.compilations.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompilationCreateDto {

    private List<Long> events;

    @NotNull
    private Boolean pinned;

    @NotBlank
    private String title;
}
