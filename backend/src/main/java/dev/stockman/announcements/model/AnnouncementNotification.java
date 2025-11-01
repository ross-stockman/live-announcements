package dev.stockman.announcements.model;

import java.time.LocalDateTime;

public record AnnouncementNotification(
        Long relatedAnnouncementId,
        String type,
        Severity severity,
        LocalDateTime timestamp
) {
    public AnnouncementNotification(Announcement announcement) {
        this(announcement.id(), announcement.type(), announcement.severity(), announcement.createdAt());
    }
}
