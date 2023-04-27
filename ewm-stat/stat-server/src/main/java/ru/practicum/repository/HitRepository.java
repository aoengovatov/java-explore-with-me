package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.models.Hit;
import ru.practicum.models.ViewStat;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ViewStat(h.uri, h.app, COUNT(h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStat> getViewStat(LocalDateTime start, LocalDateTime end);

    default List<ViewStat> getViewStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            return getViewStatUniqIp(start, end, uris);
        } else {
            return getViewStatNotUniqIp(start, end, uris);
        }
    }

    @Query("SELECT new ViewStat(h.uri, h.app, COUNT(h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "AND h.uri IN ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStat> getViewStatNotUniqIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ViewStat(h.uri, h.app, COUNT(DISTINCT h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "AND h.uri IN ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStat> getViewStatUniqIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}
