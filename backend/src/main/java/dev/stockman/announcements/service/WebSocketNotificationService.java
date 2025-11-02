package dev.stockman.announcements.service;

import dev.stockman.announcements.model.Announcement;
import dev.stockman.announcements.model.AnnouncementNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketNotificationService.class);
    private static final String ANNOUNCEMENT_TOPIC = "/topic/announcements";

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyAnnouncementCreated(Announcement announcement) {
        AnnouncementNotification notification = new AnnouncementNotification(announcement);
        logger.info("Publishing WebSocket notification for announcement: type={}, severity={}", announcement.type(), announcement.severity());
        messagingTemplate.convertAndSend(ANNOUNCEMENT_TOPIC, notification);
    }
}
