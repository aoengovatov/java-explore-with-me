package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateHitDto {

    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotBlank
    private String ip;

    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String timestamp;
}
