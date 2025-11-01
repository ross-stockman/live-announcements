package dev.stockman.announcements.controller;

import dev.stockman.announcements.model.AnnouncementResponse;
import dev.stockman.announcements.model.CreateAnnouncementRequest;
import dev.stockman.announcements.model.PagedAnnouncementResponse;
import dev.stockman.announcements.model.Severity;
import dev.stockman.announcements.service.AnnouncementService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncementController.class);

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping
    public ResponseEntity<AnnouncementResponse> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request) {
        logger.info("Creating announcement: title={}, type={}, severity={}", request.title(), request.type(), request.severity());

        AnnouncementResponse response = announcementService.createAnnouncement(request);

        logger.info("Announcement created successfully: id={}", response.id());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<PagedAnnouncementResponse> getLatestAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<Severity> severity,
            @RequestParam(required = false) List<String> type
    ) {
        logger.info("Fetching latest announcements: page={}, size={}, severity={}, type={}",
                page, size, severity, type);

        PagedAnnouncementResponse response = announcementService.getLatestAnnouncements(
                page, size, severity, type);

        logger.info("Fetched {} announcements (total: {})",
                response.content().size(), response.totalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementResponse> getAnnouncementById(@PathVariable Long id) {
        logger.info("Fetching announcement by id: {}", id);

        AnnouncementResponse response = announcementService.getAnnouncementById(id);

        logger.info("Announcement found: id={}, expired={}", response.id(), response.expired());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        logger.info("Deleting announcement: id={}", id);

        announcementService.deleteAnnouncement(id);

        logger.info("Announcement deleted successfully: id={}", id);

        return ResponseEntity.noContent().build();
    }
}
