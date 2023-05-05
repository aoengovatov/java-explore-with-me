package ru.practicum.users;

import lombok.experimental.UtilityClass;
import ru.practicum.users.dto.UserCreateDto;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.model.User;

@UtilityClass
public class UserMapper {

    public User createDtoToUser(UserCreateDto dto) {
        return new User(dto.getId(), dto.getName(), dto.getEmail());
    }

    public UserDto userToUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
