package ru.practicum.events;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.events.model.EventModeration;

import java.util.List;

public interface EventModerationRepository extends JpaRepository<EventModeration, Long> {

    List<EventModeration> findAllByEventIdIn(List<Long> ids);
}