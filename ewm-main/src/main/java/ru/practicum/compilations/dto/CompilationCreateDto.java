package ru.practicum.compilations.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompilationCreateDto {

    private List<Long> events;

    private boolean pinned;

    @NotBlank
    @Size(max = 255)
    private String title;
}
