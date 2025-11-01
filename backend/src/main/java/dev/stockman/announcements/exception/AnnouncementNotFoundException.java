package dev.stockman.announcements.exception;

public class AnnouncementNotFoundException extends RuntimeException {
    public AnnouncementNotFoundException(Long id) {
        super("Announcement with id " + id + " not found");
    }
}
