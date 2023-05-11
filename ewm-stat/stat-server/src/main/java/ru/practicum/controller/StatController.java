package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CreateHitDto;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ViewStatDto;
import ru.practicum.service.HitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatController {

    private final HitService hitService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto addHit(@RequestBody @Valid CreateHitDto dto) {
        return hitService.add(dto);
    }

    @GetMapping("/stats")
    public List<ViewStatDto> getStat(@RequestParam("start") LocalDateTime start,
                               @RequestParam("end") LocalDateTime end,
                               @RequestParam(value = "uris", defaultValue = "") List<String> uris,
                               @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        return hitService.getStat(start, end, uris, unique);
    }
}