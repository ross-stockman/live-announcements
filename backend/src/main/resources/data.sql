-- Standalone announcements (active)
INSERT INTO announcement (title, content, type, severity, created_at, expires_at, links, related_announcement_id)
VALUES
    ('New Feature Release: Advanced Analytics',
     'We are excited to announce the release of our new Advanced Analytics dashboard. This feature provides real-time insights into your data with customizable charts and reports.',
     'New Release',
     'INFO',
     CURRENT_TIMESTAMP(),
     DATEADD('DAY', 30, CURRENT_TIMESTAMP()),
     '[{"url":"https://docs.example.com/analytics","label":"Documentation"},{"url":"https://blog.example.com/analytics-release","label":"Release Notes"}]',
     NULL);

INSERT INTO announcement (title, content, type, severity, created_at, expires_at, links, related_announcement_id)
VALUES
    ('Scheduled Maintenance Window',
     'Our systems will undergo scheduled maintenance on Sunday from 2:00 AM to 6:00 AM EST. Some services may be temporarily unavailable.',
     'Maintenance',
     'WARNING',
     CURRENT_TIMESTAMP(),
     DATEADD('DAY', 7, CURRENT_TIMESTAMP()),
     '[{"url":"https://status.example.com","label":"Status Page"}]',
     NULL);

-- Original outage announcement (active)
INSERT INTO announcement (title, content, type, severity, created_at, expires_at, links, related_announcement_id)
VALUES
    ('Database Service Degradation',
     'We are currently experiencing degraded performance with our database services. Our team is actively investigating the issue.',
     'Outage',
     'CRITICAL',
     DATEADD('HOUR', -2, CURRENT_TIMESTAMP()),
     DATEADD('DAY', 1, CURRENT_TIMESTAMP()),
     '[{"url":"https://status.example.com/incident/123","label":"Incident Details"}]',
     NULL);

-- Follow-up announcement (active) - references the outage
INSERT INTO announcement (title, content, type, severity, created_at, expires_at, links, related_announcement_id)
VALUES
    ('Database Service - Issue Resolved',
     'The database performance issues have been fully resolved. All systems are now operating normally. We apologize for any inconvenience.',
     'Outage',
     'INFO',
     DATEADD('HOUR', -1, CURRENT_TIMESTAMP()),
     DATEADD('DAY', 2, CURRENT_TIMESTAMP()),
     '[{"url":"https://status.example.com/incident/123","label":"Incident Report"}]',
     3);

INSERT INTO announcement (title, content, type, severity, created_at, expires_at, links, related_announcement_id)
VALUES
    ('Security Update Required',
     'A critical security patch has been released. All users are required to update their client applications by end of week.',
     'Security',
     'CRITICAL',
     CURRENT_TIMESTAMP(),
     DATEADD('DAY', 5, CURRENT_TIMESTAMP()),
     '[{"url":"https://downloads.example.com/patch","label":"Download Patch"},{"url":"https://docs.example.com/security","label":"Security Advisory"}]',
     NULL);

-- Expired announcements (for testing)
INSERT INTO announcement (title, content, type, severity, created_at, expires_at, links, related_announcement_id)
VALUES
    ('Holiday Schedule - Office Closed',
     'Our offices will be closed for the holiday season from December 24-26.',
     'General',
     'INFO',
     DATEADD('DAY', -10, CURRENT_TIMESTAMP()),
     DATEADD('DAY', -2, CURRENT_TIMESTAMP()),
     NULL,
     NULL);

INSERT INTO announcement (title, content, type, severity, created_at, expires_at, links, related_announcement_id)
VALUES
    ('Emergency Maintenance Complete',
     'Emergency maintenance has been completed. All services are restored.',
     'Maintenance',
     'WARNING',
     DATEADD('DAY', -5, CURRENT_TIMESTAMP()),
     DATEADD('DAY', -1, CURRENT_TIMESTAMP()),
     NULL,
     NULL);