package ru.practicum.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.common.MyPageRequest;
import ru.practicum.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>  {

    @Query("select e from Event e " +
    "where e.id = :eventId " +
    "and e.initiator.id = :userId")
    Optional<Event> getByIdAndInitiatorId(@Param("eventId") Long eventId,
                                          @Param("userId") Long userId);

    @Query("select e from Event e " +
            "where e.id = :eventId " +
            "and e.state = :state")
    Optional<Event> findByIdWithStatus(@Param("eventId") Long eventId,
                                       @Param("state") EventStatus state);

    @Query("select e from Event e " +
    "where e.initiator.id = :userId")
    List<Event> findAllByInitiatorId(@Param("userId") Long userId);


    @Query("select e from Event e " +
            "where e.state = 'PUBLISHED' " +
            "and (:text is null or lower(e.description) like concat('%', lower(:text), '%') " +
            "or lower(e.annotation) like concat('%', lower(:text), '%')) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:pinned is null or e.paid = :pinned) " +
            "and ((coalesce(:rangeStart, :rangeEnd) is null and e.eventDate > now()) " +
            "or e.eventDate between coalesce(:rangeStart, e.eventDate) and coalesce(:rangeEnd, e.eventDate))")
    List<Event> getPublishedEvents(@Param("text") String text,
                                   @Param("categories") List<Long> categories,
                                   @Param("pinned") Boolean paid,
                                   @Param("rangeStart") LocalDateTime rangeStart,
                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                   MyPageRequest myPageRequest);

    @Query("select e from Event e " +
            "where (:ids is null or e.initiator.id in :ids) " +
            "and (:states is null or e.state in :states) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (coalesce(:rangeStart, :rangeEnd) is null " +
            "or e.eventDate between coalesce(:rangeStart, e.eventDate) and coalesce(:rangeEnd, e.eventDate))")
    List<Event> getEventsWithFilters(@Param("ids") List<Long> userIds,
                                     @Param("states") List<EventStatus> states,
                                     @Param("categories") List<Long> categories,
                                     @Param("rangeStart") LocalDateTime rangeStart,
                                     @Param("rangeEnd") LocalDateTime rangeEnd,
                                     MyPageRequest myPageRequest);

    @Query("select e from Event e " +
    "where e.category.id = :catId")
    List<Event> getAllWithCategoryId(@Param("catId") Long catId);

    Event findEventByOrderByCreatedOnAsc(MyPageRequest myPageRequest);

    List<Event> findEventByStateOrderByLastUpdateAsc(EventStatus state);
}
