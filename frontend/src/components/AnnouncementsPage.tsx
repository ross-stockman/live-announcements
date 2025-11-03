import React, { useEffect, useState } from 'react';
import {type Announcement, fetchLatestAnnouncements} from "../services/AnnouncementService.ts";
import AnnouncementCard from "./AnnouncementCard.tsx";
import WebSocketService, {type WebSocketError} from "../services/WebSocketService.ts";

export interface AnnouncementNotification {
    id: number;
    relatedAnnouncementId: number;
    type: string;
    severity: string;
    timestamp: string;
}

const AnnouncementsPage: React.FC = () => {

    const [announcements, setAnnouncements] = useState<Announcement[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    const TOPIC = "/topic/announcements";

    useEffect(() => {
        const loadAnnouncements = async () => {
            try {
                setLoading(true);
                const response = await fetchLatestAnnouncements();
                setAnnouncements(response.content);
            } catch (err) {
                console.error('[AnnouncementsPage]: Failed to load announcements', err);
                setError('Failed to fetch announcements. Please try again later.');
            } finally {
                setLoading(false);
            }
        };

        const handleWebSocketMessage = (notification: AnnouncementNotification) => {
            console.log("📢 Announcement received:", notification);
            loadAnnouncements();
        };

        const handleWebSocketError = (error: WebSocketError) => {
            console.error("WebSocket error:", error);
            setError(`${error.message}: ${error.details || "No additional details available."}`);
        };

        loadAnnouncements();

        WebSocketService.connect({
            topic: TOPIC,
            onMessage: handleWebSocketMessage,
            onError: handleWebSocketError,
            onConnect: () => console.log("Connected to announcements topic"),
            onDisconnect: () => console.log("Disconnected from WebSocket"),
        });

        return () => {
            WebSocketService.disconnect();
        };
    }, []);

    return (
        <div>
            <h1>Announcements</h1>
            {loading && <p>Loading announcements...</p>}
            {error && <p style={{color: 'red'}}>{error}</p>}
            <ul>
                {announcements.map((announcement) => (
                    <AnnouncementCard key={announcement.id} announcement={announcement} />
                ))}
            </ul>
            {!loading && announcements.length === 0 && <p>No announcements found.</p>}
        </div>
    )
};

export default AnnouncementsPage;