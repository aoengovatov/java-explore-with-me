package ru.practicum.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.CategoryService;
import ru.practicum.categories.dto.CategoryCreateDto;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.compilations.CompilationService;
import ru.practicum.compilations.dto.CompilationCreateDto;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationUpdateDto;
import ru.practicum.events.EventService;
import ru.practicum.events.EventStatus;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventUpdateDto;
import ru.practicum.users.UserService;
import ru.practicum.users.dto.UserCreateDto;
import ru.practicum.users.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(name = "ids", defaultValue = "ALL") List<Long> ids,
                                  @PositiveOrZero @RequestParam (name = "from", defaultValue = "0") Integer from,
                                  @Positive @RequestParam (name = "size", defaultValue = "10") Integer size) {
        return userService.getUsers(ids, from, size);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody UserCreateDto dto) {
        return userService.add(dto);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid CompilationCreateDto dto) {
        return compilationService.add(dto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.delete(compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody CompilationUpdateDto dto) {
        return compilationService.update(compId, dto);
    }

    @GetMapping("/events")
    public List<EventDto> searchEvents(@RequestParam(value = "users", required = false) List<Long> userIds,
                                       @RequestParam(value = "states", required = false) List<EventStatus> states,
                                       @RequestParam(value = "categories", required = false) List<Long> categories,
                                       @RequestParam(value = "rangeStart", required = false) LocalDateTime rangeStart,
                                       @RequestParam(value = "rangeEnd", required = false) LocalDateTime rangeEnd,
                                       @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                       @Positive @RequestParam (name = "size", defaultValue = "10") Integer size) {
        return eventService.getEventsWithFilters(userIds, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventDto updateEventById(@PathVariable Long eventId,
                                    @RequestBody EventUpdateDto dto) {
        return eventService.updateFromAdmin(eventId, dto);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid CategoryCreateDto dto) {
        return categoryService.add(dto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.delete(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@RequestBody CategoryDto dto,
                                      @PathVariable Long catId) {
        return categoryService.update(catId, dto);
    }
}