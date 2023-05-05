package ru.practicum.categories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.categories.model.Category;
import ru.practicum.common.MyPageRequest;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select c from Category c")
    List<Category> getAll(MyPageRequest name);

    @Query("select c from Category c " +
    "where c.name = :name")
    Category getByName(@Param("name") String name);
}
