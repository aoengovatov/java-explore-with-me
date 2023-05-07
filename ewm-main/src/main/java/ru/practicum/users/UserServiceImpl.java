package ru.practicum.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.MyPageRequest;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.FieldValidationException;
import ru.practicum.users.dto.UserCreateDto;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.model.User;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users = userRepository.getUsers(ids, new MyPageRequest(from, size, Sort.unsorted()));
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(UserMapper::userToUserDto)
                .collect(toList());
    }

    @Override
    @Transactional
    public UserDto add(UserCreateDto dto) {
        checkUserNameExists(dto.getName());
        return UserMapper.userToUserDto(userRepository.save(UserMapper.createDtoToUser(dto)));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, userId));
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.userToUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, userId)));
    }

    private void checkUserNameExists(String name) {
        User user = userRepository.getUserByName(name);
        if (user != null) {
            throw new FieldValidationException("User with name=" + name + " already exists.");
        }
    }
}