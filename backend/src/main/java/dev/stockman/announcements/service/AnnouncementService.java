package dev.stockman.announcements.service;

import dev.stockman.announcements.exception.AnnouncementNotFoundException;
import dev.stockman.announcements.exception.InvalidAnnouncementException;
import dev.stockman.announcements.exception.InvalidPaginationException;
import dev.stockman.announcements.model.*;
import dev.stockman.announcements.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final WebSocketNotificationService notificationService;
    private final Clock clock;

    public AnnouncementService(AnnouncementRepository announcementRepository, WebSocketNotificationService notificationService, Clock clock) {
        this.announcementRepository = announcementRepository;
        this.notificationService = notificationService;
        this.clock = clock;
    }

    @Transactional
    public AnnouncementResponse createAnnouncement(CreateAnnouncementRequest request) {
        // Validate expiration date is in the future
        if (request.expiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidAnnouncementException("Expiration date must be in the future");
        }

        // Validate related announcement exists if provided
        if (request.relatedAnnouncementId() != null) {
            if (!announcementRepository.existsById(request.relatedAnnouncementId())) {
                throw new InvalidAnnouncementException(
                        "Related announcement with id " + request.relatedAnnouncementId() + " does not exist"
                );
            }
        }

        // Create announcement
        Announcement announcement = new Announcement(
                null,
                request.title(),
                request.content(),
                request.type(),
                request.severity(),
                LocalDateTime.now(clock),
                request.expiresAt(),
                request.links(),
                request.relatedAnnouncementId()
        );

        // Save to database
        Announcement saved = announcementRepository.save(announcement);

        // Send WebSocket notification
        notificationService.notifyAnnouncementCreated(saved);

        // Return response
        return AnnouncementResponse.from(saved, clock);
    }

    public AnnouncementResponse getAnnouncementById(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new AnnouncementNotFoundException(id));

        return AnnouncementResponse.from(announcement, clock);
    }

    public PagedAnnouncementResponse getLatestAnnouncements(
            int page,
            int size,
            List<Severity> severities,
            List<String> types) {

        // Validate pagination parameters
        if (page < 0) {
            throw new InvalidPaginationException("Page number cannot be negative");
        }
        if (size <= 0 || size > 100) {
            throw new InvalidPaginationException("Page size must be between 1 and 100");
        }

        // Fetch announcements
        List<Announcement> announcements = announcementRepository.findLatest(page, size, severities, types);

        // Get total count for pagination
        long totalElements = announcementRepository.countLatest(severities, types);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Convert to response DTOs
        List<AnnouncementResponse> content = announcements.stream()
                .map(announcement -> AnnouncementResponse.from(announcement, clock))
                .toList();

        return new PagedAnnouncementResponse(
                content,
                page,
                size,
                totalElements,
                totalPages,
                page < totalPages - 1,
                page > 0
        );
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new AnnouncementNotFoundException(id);
        }

        announcementRepository.deleteById(id);
    }
}
