package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.CreateHitDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StatClient extends BaseClient {

    @Autowired
    public StatClient(@Value("${stat-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addHit(CreateHitDto dto) {
        return post("/hit", dto);
    }

    public ResponseEntity<Object> getStat(LocalDateTime start,
                                          LocalDateTime end,
                                          List<String> uris,
                                          Boolean unique) {
        Map<String,Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", String.join(",", uris),
                "unique", unique
        );
        return get("/stats", parameters);
    }
}