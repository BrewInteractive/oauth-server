DELETE FROM AUTHORIZATION_CODES;
DELETE FROM REDIRECT_URIS;
DELETE FROM CLIENTS_GRANTS;
DELETE FROM CLIENTS;
DELETE FROM GRANTS;

INSERT INTO CLIENTS
(id, name, client_id, client_secret,  token_expires_in_minutes, refresh_token_expires_in_days, created_at, updated_at)
VALUES('12345678-1234-1234-1234-123456780123', 'test', 'i7GDRmtPPishVmCc5sHY42hppBUYIh3S', 'uCiM-E4wQF2Rb7TB9GtNG-rnoM49kiIeRNBQm7rkbCf7HApF', 1, 1,'2023-03-21 23:05:26.740','2023-03-21 23:05:26.740');

INSERT INTO GRANTS
(id, name, response_type, grant_type)
VALUES(1, 'test', 'code', 'authorization_code');

INSERT INTO CLIENTS_GRANTS
(id, client_id, grant_id, audience, created_at, updated_at)
VALUES('87654321-1234-1234-1234-123456780123', '12345678-1234-1234-1234-123456780123', 1,'https://www.audience.com','2023-03-21 23:05:26.740','2023-03-21 23:05:26.740');

INSERT INTO REDIRECT_URIS
(id, client_id, redirect_uri, created_at, updated_at)
VALUES('43218765-1234-1234-1234-123456780123', '12345678-1234-1234-1234-123456780123', 'http://www.redirect_uri.com','2023-03-21 23:05:26.740','2023-03-21 23:05:26.740');