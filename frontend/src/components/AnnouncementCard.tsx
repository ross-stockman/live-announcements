import React from "react";
import type {Announcement} from "../services/AnnouncementService.ts";
import styles from "./AnnouncementCard.module.css"

interface AnnouncementCardProps {
    announcement: Announcement
}

const AnnouncementCard: React.FC<AnnouncementCardProps> = ({ announcement }) => {

    const severityClass = (severity: string) => {
        switch (severity) {
            case "CRITICAL":
                return styles.severityCritical;
            case "WARNING":
                return styles.severityWarning;
            case "INFO":
                return styles.severityInfo;
            default:
                return "";
        }
    }

    return (
        <li className={`${styles.card} ${severityClass(announcement.severity)}`} key={announcement.id}>
            <h2 className={styles.title}>{announcement.title}</h2>
            <p className={styles.content}>{announcement.content}</p>
            <p className={styles.meta}>
                <strong>Type:</strong> {announcement.type} | <strong>Severity:</strong> {announcement.severity}
            </p>
            <p className={styles.meta}>
                <strong>Created:</strong> {new Date(announcement.createdAt).toLocaleString()} | <strong>Expires:</strong> {new Date(announcement.expiresAt).toLocaleString()}
            </p>
            {announcement.links && announcement.links.length > 0 && (
                <ul className={styles.links}>
                    <strong>Related Links:</strong>
                    {announcement.links.map((link, index) => (
                        <li key={index}>
                            <a href={link.url} target="_blank" rel="noopener noreferrer" className={styles.link}>
                                {link.label}
                            </a>
                        </li>
                    ))}
                </ul>
            )}
        </li>
    )
};

export default AnnouncementCard;