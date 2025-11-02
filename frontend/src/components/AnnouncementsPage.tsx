import React, { useEffect, useState } from 'react';
import {type Announcement, fetchLatestAnnouncements} from "../services/AnnouncementService.ts";
import AnnouncementCard from "./AnnouncementCard.tsx";
import {Client} from '@stomp/stompjs';

const AnnouncementsPage: React.FC = () => {

    const [announcements, setAnnouncements] = useState<Announcement[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    const WS_URL = "ws://localhost:8080/ws";
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
        const client = new Client({
            brokerURL: WS_URL,
            reconnectDelay: 5000,
            debug: (msg) => console.log("[STOMP DEBUG]:", msg),
            onConnect: () => {
                console.log("✅ Connected to WebSocket server");
                client.subscribe(TOPIC, (message) => {
                    console.log("📢 Announcement received:", message.body);
                    loadAnnouncements();
                });
            },
            onStompError: (frame) => {
                console.error("❌ STOMP error:", frame.headers["message"]);
                console.error("Details:", frame.body);
                setError('WebSocket connection error. Please try again later.');
            },
            onDisconnect: () => {
                console.log("🔌 Disconnected from WebSocket server");
            },
        });
        loadAnnouncements();
        client.activate();

        return () => {
            client.deactivate();
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