package ru.practicum.compilations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/compilations")
public class CompilationController {

    @Autowired
    private CompilationService compilationService;

    @GetMapping()
    public List<CompilationDto> getWithFilter(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                              @PositiveOrZero @RequestParam(value = "from", defaultValue = "0")
                                              Integer from,
                                              @Positive @RequestParam(value = "size", defaultValue = "10")
                                                  Integer size) {
        return compilationService.getWithFilter(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable Long compId) {
        return compilationService.getById(compId);
    }
}