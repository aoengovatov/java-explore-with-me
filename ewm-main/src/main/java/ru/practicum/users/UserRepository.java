package ru.practicum.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.common.MyPageRequest;
import ru.practicum.users.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u " +
    "where (:ids is null or u.id in :ids)")
    List<User> getUsers(@Param("ids") List<Long> ids, MyPageRequest myPageRequest);

    @Query("select u from User u " +
    "where u.name = :name")
    User getUserByName(@Param("name") String name);
}