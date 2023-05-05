package ru.practicum.compilations;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.common.MyPageRequest;
import ru.practicum.compilations.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("select c from Compilation c " +
            "where c.pinned = :pinned")
    List<Compilation> getWithPinned(@Param("pinned") Boolean pinned, MyPageRequest myPageRequest);

    @Query("select c from Compilation c")
    List<Compilation> findAllWithPagable(MyPageRequest myPageRequest);

}