package ru.practicum.request;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.request.dto.ConfirmedRequestDto;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("select r from Request r " +
    "where r.requesterId = :userId")
    List<Request> findAllByRequester(@Param("userId") Long userId);

    @Query("select r from Request r " +
    "where r.eventId = :eventId")
    List<Request> findAllByEventId(@Param("eventId") Long eventId);

    @Query("select r from Request r " +
           "where r.id = :id " +
           "and r.requesterId = :requesterId")
    Optional<Request> findByIdAndUserId(@Param("requesterId") Long userId,
                                        @Param("id") Long requestId);

    @Query("select r from Request r " +
            "where r.eventId = :eventId " +
            "and r.requesterId = :requesterId")
    Optional<Request> findByEventIdAndRequesterId(@Param("eventId") Long eventId,
                                                  @Param("requesterId") Long userId);

    @Query("select count(r.id) from Request r " +
    "where r.eventId = :eventId " +
    "and r.state = 'CONFIRMED'")
    Integer getConfirmedRequestsByEventId(@Param("eventId") Long eventId);

    @Query("select new ConfirmedRequestDto(r.eventId, count(r.id)) " +
            "from Request r " +
            "where r.state = :status " +
            "and r.eventId in :ids " +
            "group by r.eventId ")
    List<ConfirmedRequestDto> getRequestsByEventIdsAndStatus(@Param("ids") List<Long> eventIds,
                                                             @Param("status") RequestStatus status);
}