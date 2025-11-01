package dev.stockman.announcements.model;

import java.time.LocalDateTime;
import java.util.List;

public record Announcement(
        Long id,
        String title,
        String content,
        String type,
        Severity severity,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        List<Link> links,
        Long relatedAnnouncementId
) {
}
