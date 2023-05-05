package ru.practicum.categories;

import ru.practicum.categories.dto.CategoryCreateDto;
import ru.practicum.categories.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto getById(Long catId);

    CategoryDto add(CategoryCreateDto dto);

    void delete(Long catId);

    CategoryDto update(Long catId, CategoryDto dto);

}
