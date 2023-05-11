package ru.practicum.categories;

import lombok.experimental.UtilityClass;
import ru.practicum.categories.dto.CategoryCreateDto;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.model.Category;

@UtilityClass
public class CategoryMapper {

    public CategoryDto toCategoryDto(Category dto) {
        return new CategoryDto(dto.getId(), dto.getName());
    }

    public Category toCategory(CategoryCreateDto dto) {
        return new Category(dto.getId(), dto.getName());
    }

    public Category dtoToCategory(CategoryDto dto) {
        return new Category(dto.getId(), dto.getName());
    }
}
