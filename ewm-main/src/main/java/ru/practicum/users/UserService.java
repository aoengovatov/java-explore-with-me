package ru.practicum.users;

import ru.practicum.users.dto.UserCreateDto;
import ru.practicum.users.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto add(UserCreateDto dto);

    void delete(Long userId);

    UserDto getById(Long userId);
}