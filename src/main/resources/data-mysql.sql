USE discord;

INSERT INTO user(usr_email, usr_password, usr_lastname, usr_firstname)
VALUES ('pierre.martin@message.fr', '$2a$10$7wEts30./7J7HvkJ/rIYOeYX8weaGPV6ybZCO73dZj7RrJedbrE0u', 'MARTIN', 'Pierre');
INSERT INTO user(usr_email, usr_password, usr_lastname, usr_firstname)
VALUES ('julie.dupont@message.fr',
        '$2a$10$7wEts30./7J7HvkJ/rIYOeYX8weaGPV6ybZCO73dZj7RrJedbrE0u',
        'DUPONT', 'Julie');
INSERT INTO user(usr_email, usr_password, usr_lastname, usr_firstname)
VALUES ('jean.dufour@message.fr', 'test', 'DUFOUR', 'Jean');

INSERT INTO role(rol_name)
VALUES (0),
       (1);

INSERT INTO has_role(usr_id, rol_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (3, 1);

INSERT INTO channel(cha_name, cha_visibility)
VALUES ('DEVLOG Java', 0);
INSERT INTO channel(cha_name, cha_visibility)
VALUES ('DEVLOG C#', 1);

INSERT INTO is_allowed_in(cha_id, usr_id)
VALUES (1, 1);
INSERT INTO is_allowed_in(cha_id, usr_id)
VALUES (2, 1);
INSERT INTO is_allowed_in(cha_id, usr_id)
VALUES (1, 2);

INSERT INTO subject(dtype, sub_sent_at, sub_channel)
VALUES ('Message', NOW(), (SELECT cha_id FROM channel WHERE cha_name = 'DEVLOG Java')),
       ('Message', NOW(), (SELECT cha_id FROM channel WHERE cha_name = 'DEVLOG Java')),
       ('Meeting', NOW(), (SELECT cha_id FROM channel WHERE cha_name = 'DEVLOG Java'));

INSERT INTO message(sub_id, msg_content, msg_sender, msg_file)
VALUES (1, 'Message de test', (SELECT usr_id FROM user WHERE usr_email = 'pierre.martin@message.fr'),
        null),
       (2, 'Message 2', (SELECT usr_id FROM user WHERE usr_email = 'julie.dupont@message.fr'),
        null);

INSERT INTO notification(not_id, not_seen_at, not_receiver, not_subject)
VALUES (1, null, 2, 1);

INSERT INTO meeting(mee_datetime, mee_duration, mee_name, sub_id, mee_user, sub_channel)
VALUES (NOW(), 60, 'Test meeting', 3, 2, 1);

INSERT INTO user_group(gro_id, gro_name)
VALUES (1, 'CDA');

INSERT INTO is_member_of(gro_id, usr_id)
VALUES (1, 1),
       (1, 2);
