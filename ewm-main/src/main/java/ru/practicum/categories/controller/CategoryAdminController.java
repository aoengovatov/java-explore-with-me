package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.CategoryService;
import ru.practicum.categories.dto.CategoryCreateDto;
import ru.practicum.categories.dto.CategoryDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
public class CategoryAdminController {

    @Autowired
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid CategoryCreateDto dto) {
        return categoryService.add(dto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.delete(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@RequestBody CategoryDto dto,
                                      @PathVariable Long catId) {
        return categoryService.update(catId, dto);
    }
}
