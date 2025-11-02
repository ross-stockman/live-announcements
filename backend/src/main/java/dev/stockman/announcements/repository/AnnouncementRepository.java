package dev.stockman.announcements.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.stockman.announcements.model.Announcement;
import dev.stockman.announcements.model.Link;
import dev.stockman.announcements.model.Severity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class AnnouncementRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public AnnouncementRepository(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper, Clock clock) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    private final RowMapper<Announcement> rowMapper = new RowMapper<Announcement>() {
        @Override
        public Announcement mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Announcement(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("type"),
                    Severity.valueOf(rs.getString("severity")),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("expires_at").toLocalDateTime(),
                    parsedLinks(rs.getString("links")),
                    rs.getObject("related_announcement_id", Long.class)
            );
        }
    };

    private List<Link> parsedLinks(String linksJson) {
        if (linksJson == null || linksJson.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(linksJson, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    private String serializeLinks(List<Link> links) {
        if (links == null || links.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(links);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public Announcement save(Announcement announcement) {
        String sql = """
            INSERT INTO announcement (title, content, type, severity, created_at, expires_at, links, related_announcement_id)
            VALUES (:title, :content, :type, :severity, :createdAt, :expiresAt, :links, :relatedAnnouncementId)
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", announcement.title())
                .addValue("content", announcement.content())
                .addValue("type", announcement.type())
                .addValue("severity", announcement.severity().name())
                .addValue("createdAt", Timestamp.valueOf(announcement.createdAt()))
                .addValue("expiresAt", Timestamp.valueOf(announcement.expiresAt()))
                .addValue("links", serializeLinks(announcement.links()))
                .addValue("relatedAnnouncementId", announcement.relatedAnnouncementId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return new Announcement(
                id,
                announcement.title(),
                announcement.content(),
                announcement.type(),
                announcement.severity(),
                announcement.createdAt(),
                announcement.expiresAt(),
                announcement.links(),
                announcement.relatedAnnouncementId()
        );
    }

    public Optional<Announcement> findById(long id) {
        String sql = """
            SELECT id, title, content, type, severity, created_at, expires_at, links, related_announcement_id
            FROM announcement
            WHERE id = :id
            """;

        MapSqlParameterSource params = new MapSqlParameterSource("id", id);

        List<Announcement> results = jdbcTemplate.query(sql, params, rowMapper);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    public List<Announcement> findLatest(int page, int size, List<Severity> severities, List<String> types) {
        StringBuilder sql = new StringBuilder("""
            SELECT id, title, content, type, severity, created_at, expires_at, links, related_announcement_id
            FROM announcement
            WHERE expires_at > :now
            """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("now", Timestamp.valueOf(LocalDateTime.now()));

        if (severities != null && !severities.isEmpty()) {
            sql.append(" AND severity IN (:severities)");
            params.addValue("severities", severities.stream().map(Enum::name).toList());
        }

        if (types != null && !types.isEmpty()) {
            sql.append(" AND type IN (:types)");
            params.addValue("types", types);
        }

        sql.append(" ORDER BY created_at DESC");
        sql.append(" LIMIT :limit OFFSET :offset");

        params.addValue("limit", size);
        params.addValue("offset", page * size);

        return jdbcTemplate.query(sql.toString(), params, rowMapper);
    }

    public long countLatest(List<Severity> severities, List<String> types) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*)
            FROM announcement
            WHERE expires_at > :now
            """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("now", Timestamp.valueOf(LocalDateTime.now(clock)));

        if (severities != null && !severities.isEmpty()) {
            sql.append(" AND severity IN (:severities)");
            params.addValue("severities", severities.stream().map(Enum::name).toList());
        }

        if (types != null && !types.isEmpty()) {
            sql.append(" AND type IN (:types)");
            params.addValue("types", types);
        }

        Long count = jdbcTemplate.queryForObject(sql.toString(), params, Long.class);
        return count != null ? count : 0;
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM announcement WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        Long count = jdbcTemplate.queryForObject(sql, params, Long.class);
        return count != null && count > 0;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM announcement WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, params);
    }
}
