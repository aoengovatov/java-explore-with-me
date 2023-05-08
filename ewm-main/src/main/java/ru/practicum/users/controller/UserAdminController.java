package ru.practicum.users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.UserService;
import ru.practicum.users.dto.UserCreateDto;
import ru.practicum.users.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class UserAdminController {

    @Autowired
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(name = "ids", defaultValue = "ALL") List<Long> ids,
                                  @PositiveOrZero @RequestParam (name = "from", defaultValue = "0") Integer from,
                                  @Positive @RequestParam (name = "size", defaultValue = "10") Integer size) {
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody UserCreateDto dto) {
        return userService.add(dto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
