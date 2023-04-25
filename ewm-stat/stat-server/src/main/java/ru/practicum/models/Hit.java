package ru.practicum.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hit")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Hit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app", nullable = false)
    private String app;

    @Column(name = "uri", nullable = false)
    private String uri;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
