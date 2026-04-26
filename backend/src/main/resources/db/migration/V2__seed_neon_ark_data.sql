INSERT INTO habitats (name, zone, capacity) VALUES
('Deep Canopy', 'Sector 3', 12),
('Axiom Vault', 'Zone 1', 6),
('Crimson Dunes', 'Sector 7', 8),
('Abyssal Trench', 'Marine Ring', 10),
('Gloomwood Den', 'Sector 5', 5);

INSERT INTO roles (name) VALUES
('ADMIN'),
('STAFF'),
('OPERATOR'),
('REVIEWER');

INSERT INTO app_users (username, full_name, email, phone, active) VALUES
('kara.vens', 'Kara Vens', 'kara.vens@neonark.org', '555-0101', TRUE),
('mila.sorren', 'Mila Sorren', 'mila.sorren@neonark.org', '555-0102', TRUE),
('renn.tal', 'Renn Tal', 'renn.tal@neonark.org', '555-0103', TRUE),
('vaelis.ruun', 'Vaelis Ruun', 'vaelis.ruun@neonark.org', '555-0104', TRUE),
('orell.archive', 'Archivist Orell', 'orell.archive@neonark.org', '555-0105', FALSE);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM app_users u, roles r
WHERE (u.username = 'kara.vens' AND r.name = 'ADMIN')
   OR (u.username = 'mila.sorren' AND r.name = 'STAFF')
   OR (u.username = 'renn.tal' AND r.name = 'OPERATOR')
   OR (u.username = 'vaelis.ruun' AND r.name = 'REVIEWER')
   OR (u.username = 'orell.archive' AND r.name = 'ADMIN');

INSERT INTO creatures (name, species, status, habitat_id, removed_at) VALUES
('Nyx', 'Void Fox', 'ACTIVE', (SELECT id FROM habitats WHERE name = 'Deep Canopy'), NULL),
('Orellian Mote', 'Memory Mote', 'REMOVED', (SELECT id FROM habitats WHERE name = 'Axiom Vault'), CURRENT_TIMESTAMP),
('Emberfang', 'Lava Serpent', 'ACTIVE', (SELECT id FROM habitats WHERE name = 'Crimson Dunes'), NULL),
('Coralback', 'Tide Crawler', 'ACTIVE', (SELECT id FROM habitats WHERE name = 'Abyssal Trench'), NULL),
('Mossclaw', 'Forest Troll', 'INTAKE', (SELECT id FROM habitats WHERE name = 'Gloomwood Den'), NULL),
('Duskwing', 'Shadow Bat', 'ACTIVE', (SELECT id FROM habitats WHERE name = 'Deep Canopy'), NULL),
('Stonecrest', 'Rock Basilisk', 'ACTIVE', (SELECT id FROM habitats WHERE name = 'Crimson Dunes'), NULL);

INSERT INTO observations (creature_id, author_id, note, observed_at) VALUES
((SELECT id FROM creatures WHERE name = 'Nyx'), (SELECT id FROM app_users WHERE username = 'vaelis.ruun'), 'Bioluminescence increased during low-light cycle.', CURRENT_TIMESTAMP - INTERVAL '3 days'),
((SELECT id FROM creatures WHERE name = 'Nyx'), (SELECT id FROM app_users WHERE username = 'mila.sorren'), 'Responded calmly to revised feeding path.', CURRENT_TIMESTAMP - INTERVAL '1 day'),
((SELECT id FROM creatures WHERE name = 'Emberfang'), (SELECT id FROM app_users WHERE username = 'renn.tal'), 'Heat output rose above normal during shift change.', CURRENT_TIMESTAMP - INTERVAL '2 days'),
((SELECT id FROM creatures WHERE name = 'Orellian Mote'), (SELECT id FROM app_users WHERE username = 'orell.archive'), 'Historical record preserved after transfer from active operations.', CURRENT_TIMESTAMP - INTERVAL '10 days');

INSERT INTO feeding_schedules (creature_id, feeding_time, food, instructions, active) VALUES
((SELECT id FROM creatures WHERE name = 'Nyx'), '08:00', 'Lunar berries', 'Dim the east corridor lights before delivery.', TRUE),
((SELECT id FROM creatures WHERE name = 'Emberfang'), '12:30', 'Basalt mineral blocks', 'Use heat-resistant transfer tongs.', TRUE),
((SELECT id FROM creatures WHERE name = 'Coralback'), '08:00', 'Kelp cubes', 'Keep water gate open for 2 minutes.', TRUE),
((SELECT id FROM creatures WHERE name = 'Mossclaw'), '18:00', 'Cave fungi', 'Intake team must confirm appetite response.', TRUE),
((SELECT id FROM creatures WHERE name = 'Orellian Mote'), '08:00', 'Archive pollen', 'Inactive historical schedule.', FALSE);

