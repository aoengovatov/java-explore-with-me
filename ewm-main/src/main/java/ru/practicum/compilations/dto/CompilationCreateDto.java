package ru.practicum.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompilationCreateDto {

    @NotNull
    private List<Long> events;

    private boolean pinned;

    @NotBlank
    @Size(max = 255)
    private String title;
}
