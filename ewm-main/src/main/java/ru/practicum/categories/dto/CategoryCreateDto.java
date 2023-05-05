package ru.practicum.categories.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateDto {

    private Long id;

    @NotBlank
    private String name;
}
