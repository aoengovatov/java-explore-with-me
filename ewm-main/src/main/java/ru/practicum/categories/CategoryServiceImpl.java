package ru.practicum.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryCreateDto;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.model.Category;
import ru.practicum.common.MyPageRequest;
import ru.practicum.events.EventRepository;
import ru.practicum.events.model.Event;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.FieldValidationException;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(Integer from, Integer size) {
        Page<Category> categories = categoryRepository.findAll(new MyPageRequest(from, size, Sort.unsorted()));
        if (categories.isEmpty()) {
            throw new EntityNotFoundException(Category.class);
        }
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long catId) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(Category.class, catId)));
    }

    @Override
    @Transactional
    public CategoryDto add(CategoryCreateDto dto) {
        checkCategoryExists(dto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(dto)));
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        checkCategoryUsed(catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, CategoryDto dto) {
        checkCategoryExists(dto.getName());
        Category categoryUpdate = CategoryMapper.dtoToCategory(getById(catId));
        if (dto.getName() != null && !dto.getName().isEmpty()) {
            categoryUpdate.setName(dto.getName());
        }
        return CategoryMapper.toCategoryDto(categoryUpdate);
    }

    private void checkCategoryExists(String name) {
        Category category = categoryRepository.getByName(name);
        if (category != null) {
            throw new FieldValidationException("Category with name=" + name + " already exists.");
        }
    }

    private void checkCategoryUsed(Long catId) {
        List<Event> events = eventRepository.getAllWithCategoryId(catId);
        if (!events.isEmpty()) {
            throw new FieldValidationException("Category delete error. " +
                    "Category with id=" + catId + " used in " + events.size() + " event(s)");
        }
    }
}