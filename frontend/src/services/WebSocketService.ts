import { Client, type IMessage, type IFrame } from '@stomp/stompjs';
import type {WebSocketError} from "../types.ts";

const WS_URL = "ws://localhost:8080/ws";

interface WebSocketConnectionOptions<T> {
    topic: string;
    onMessage: (message: T) => void;
    onError?: (error: WebSocketError) => void;
    onConnect?: () => void;
    onDisconnect?: () => void;
}

class WebSocketService {
    private client: Client;

    constructor() {
        this.client = new Client({
            brokerURL: WS_URL,
            reconnectDelay: 5000,
            debug: (msg) => console.log("[STOMP DEBUG]:", msg),
        });
    }

    connect<T>(options: WebSocketConnectionOptions<T>): void {
        const { topic, onMessage, onError, onConnect, onDisconnect } = options;

        this.client.onConnect = () => {
            console.log("✅ Connected to WebSocket server");
            this.client.subscribe(topic, (message: IMessage) => {
                try {
                    // Parse the message body as the generic type T
                    const parsedMessage: T = JSON.parse(message.body);
                    onMessage(parsedMessage);
                } catch (error) {
                    console.error("❌ Failed to parse WebSocket message", error);
                    console.error("Received message:", message.body);
                }
            });
            onConnect?.();
        };

        this.client.onStompError = (frame: IFrame) => {
            console.error("❌ STOMP error:", frame.headers["message"]);
            console.error("Details:", frame.body);

            if (onError) {
                const customError: WebSocketError = {
                    message: frame.headers["message"] || "Unknown STOMP error",
                    details: frame.body,
                };
                onError(customError);
            }
        };

        this.client.onDisconnect = () => {
            console.log("🔌 Disconnected from WebSocket server");
            onDisconnect?.();
        };

        this.client.activate();
    }

    disconnect(): void {
        if (this.client.active) {
            this.client.deactivate();
        }
    }
}

export default new WebSocketService();