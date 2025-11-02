import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api/announcements';

export interface Announcement {
    id: number;
    title: string;
    content: string;
    type: string;
    severity: string;
    createdAt: string;
    expiresAt: string;
    links: Array<{ label: string; url: string}>;
    relatedAnnouncementId: number;
}

export interface AnnouncementsResponse {
    content: Announcement[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    hasNext: boolean;
    hasPrevious: boolean;
}

export const fetchLatestAnnouncements = async (): Promise<AnnouncementsResponse> => {
    const response = await axios.get<AnnouncementsResponse>(`${BASE_URL}/latest`);
    return response.data;
}