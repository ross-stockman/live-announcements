package dev.stockman.announcements.model;

import java.util.List;

public record PagedAnnouncementResponse(
        List<AnnouncementResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
}
