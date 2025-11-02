CREATE TABLE IF NOT EXISTS announcement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(100) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    links VARCHAR(2000),
    related_announcement_id BIGINT,
    CONSTRAINT fk_related_announcement
    FOREIGN KEY (related_announcement_id)
    REFERENCES announcement(id)
);

CREATE INDEX idx_announcement_expires_at ON announcement(expires_at);
CREATE INDEX idx_announcement_created_at ON announcement(created_at);
CREATE INDEX idx_announcement_severity ON announcement(severity);
CREATE INDEX idx_announcement_type ON announcement(type);