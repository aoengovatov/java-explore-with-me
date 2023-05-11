package ru.practicum.compilations.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompilationUpdateDto {

    private Boolean pinned;

    @Size(max = 255)
    private String title;

    private List<Long> events;
}
