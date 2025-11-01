package dev.stockman.announcements.model;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public record AnnouncementResponse(
        Long id,
        String title,
        String content,
        String type,
        Severity severity,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        List<Link> links,
        Long relatedAnnouncementId,
        boolean expired
) {
    public static AnnouncementResponse from(Announcement announcement, Clock clock) {
        return new AnnouncementResponse(
                announcement.id(),
                announcement.title(),
                announcement.content(),
                announcement.type(),
                announcement.severity(),
                announcement.createdAt(),
                announcement.expiresAt(),
                announcement.links(),
                announcement.relatedAnnouncementId(),
                announcement.expiresAt().isBefore(LocalDateTime.now(clock))
        );
    }
}
