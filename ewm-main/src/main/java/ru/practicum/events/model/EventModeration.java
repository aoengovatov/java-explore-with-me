package ru.practicum.events.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "moderation_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventModeration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "comment", nullable = false)
    private String comment;

    @CreationTimestamp
    @Column(name = "datetime", nullable = false)
    private LocalDateTime dateTime;
}