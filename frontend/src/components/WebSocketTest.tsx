import React, { useEffect, useRef, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const WS_URL = "http://localhost:8080/ws"; // adjust if needed
const TOPIC = "/topic/announcements";

interface Announcement {
    type?: string;
    severity?: string;
    [key: string]: any;
}

export const WebSocketTest: React.FC = () => {
    const [status, setStatus] = useState<string>("Connecting...");
    const [messages, setMessages] = useState<string[]>([]);
    // const stompClientRef = useRef<Client | null>(null);

    useEffect(() => {
        const client = new Client({
            webSocketFactory: () => new SockJS(WS_URL),
            reconnectDelay: 5000,
            debug: (str) => console.log(str),
            onConnect: (frame) => {
                console.log("on connect")
                setStatus("✅ Connected to WebSocket");
                logMessage("Connected to server");

                client.subscribe(TOPIC, (msg: any) => {
                    try {
                        const body: Announcement = JSON.parse(msg.body);
                        logMessage(`📢 Announcement received: ${JSON.stringify(body)}`);
                    } catch (err) {
                        logMessage(`⚠️ Error parsing message: ${msg.body}`);
                    }
                });
            },
            onWebSocketClose: (frame) => {
                console.log("on socket close " + frame)
                console.log("on socket close ", frame)
            },
            onHeartbeatLost: (frame) => {
                console.log("on heartbeat lost")
            },
            onStompError: (frame) => {
                console.log("on stomp error")
                setStatus("❌ STOMP error");
                logMessage("Broker error: " + frame.headers["message"]);
            },
            onWebSocketError: (event) => {
                console.log("on socket error")
                setStatus("❌ WebSocket error");
                logMessage("WebSocket error: " + JSON.stringify(event));
            },
            onDisconnect: () => {
                console.log("on disconnect")
                setStatus("🔌 Disconnected");
                logMessage("Connection closed");
            },
        });

        // stompClientRef.current = client;
        client.activate();

        return () => {
            client.deactivate();
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    function logMessage(message: string) {
        setMessages((prev) => [
            ...prev,
            `[${new Date().toLocaleTimeString()}] ${message}`,
        ]);
    }

    return (
        <div style={styles.container}>
            <h2>WebSocket Announcement Test</h2>
            <div style={styles.status}>{status}</div>
            <div style={styles.messages}>
                {messages.map((msg, index) => (
                    <div key={index} style={styles.message}>
                        {msg}
                    </div>
                ))}
            </div>
        </div>
    );
};

const styles: Record<string, React.CSSProperties> = {
    container: {
        fontFamily: "Arial, sans-serif",
        margin: "2rem",
    },
    status: {
        marginBottom: "1rem",
        fontWeight: "bold",
    },
    messages: {
        border: "1px solid #ccc",
        padding: "1rem",
        height: "300px",
        overflowY: "auto",
        backgroundColor: "#f9f9f9",
    },
    message: {
        marginBottom: "0.5rem",
    },
};

export default WebSocketTest;
