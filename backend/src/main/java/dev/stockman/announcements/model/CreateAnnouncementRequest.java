package dev.stockman.announcements.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreateAnnouncementRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Content is required")
        String content,

        @NotBlank(message = "Type is required")
        String type,

        @NotNull(message = "Severity is required")
        Severity severity,

        @NotNull(message = "Expires at is required")
        LocalDateTime expiresAt,

        List<Link> links,

        Long relatedAnnouncementId
) {
}
