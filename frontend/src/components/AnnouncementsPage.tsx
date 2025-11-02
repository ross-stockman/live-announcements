import React, { useEffect, useState, useRef } from 'react';
import {type Announcement, fetchLatestAnnouncements} from "../services/AnnouncementService.ts";
import AnnouncementCard from "./AnnouncementCard.tsx";
import {Client} from '@stomp/stompjs';

const AnnouncementsPage: React.FC = () => {

    const [announcements, setAnnouncements] = useState<Announcement[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    const WS_URL = "ws://localhost:8080/ws";
    const TOPIC = "/topic/announcements"; // Subscribed topic

    useEffect(() => {
        // Function to fetch announcements from the API
        const loadAnnouncements = async () => {
            try {
                setLoading(true);
                const response = await fetchLatestAnnouncements();
                setAnnouncements(response.content); // Set the fetched announcements
            } catch (err) {
                console.error('[AnnouncementsPage]: Failed to load announcements', err);
                setError('Failed to fetch announcements. Please try again later.');
            } finally {
                setLoading(false); // Ensure loading state is turned off
            }
        };

        // Initialize WebSocket client for real-time updates
        const client = new Client({
            brokerURL: WS_URL,
            reconnectDelay: 5000, // Reconnect every 5 seconds if disconnected
            debug: (msg) => console.log("[STOMP DEBUG]:", msg), // Debug logs
            onConnect: () => {
                console.log("✅ Connected to WebSocket server");

                // Subscribe to the announcements topic
                client.subscribe(TOPIC, (message) => {
                    console.log("📢 Announcement received:", message.body);
                    loadAnnouncements(); // Fetch the latest announcements when notified
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

        // Trigger both API fetch and WebSocket connection
        loadAnnouncements(); // Fetch announcements
        client.activate(); // Activate the WebSocket connection

        // Cleanup on component unmount
        return () => {
            client.deactivate(); // Deactivate WebSocket client
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