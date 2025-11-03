export interface AnnouncementNotification {
    severity: string;
    type: string;
}

export interface WebSocketError {
    message: string;
    details?: string;
}