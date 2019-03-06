
insert into fa_player(player_id, first_name, middle_Name, last_Name, known_Name, date_of_birth, gender, player_grade, created_At, updated_At, version)
values (1, 'Nayyer', '', 'Kamran', 'SNK',  curdate(), 'M', 'A1', sysdate(), sysdate(),  1);

insert into fa_player_squad (player_id, squad, squad_status) values (1, 'U21', 'MONITOR');
insert into fa_player_squad (player_id, squad, squad_status) values (1, 'SENIORS', 'MONITOR');

insert into fa_player_eligibility (player_id, country_code) values (1, 'AFG');
insert into fa_player_eligibility (player_id, country_code) values (1, 'ENG');

INSERT INTO fa_player_intel (player_id, intel_type, note, archived, created_at) VALUES (1, 'GENERAL', 'He is an awesome player', false, sysdate());
INSERT INTO fa_player_intel (player_id, intel_type, note, archived, created_at) VALUES (1, 'EDUCATION', 'He done a PhD', true, sysdate());

INSERT INTO fa_player_social (player_id, social_media, link, created_at) VALUES (1, 'TWITTER', 'https://twitter.com/jpickford1?lang=en', sysdate());
INSERT INTO fa_player_social (player_id, social_media, link, created_at) VALUES (1, 'WIKIPEDIA', 'https://en.wikipedia.org/wiki/Jordan_Pickford', sysdate());
INSERT INTO fa_player_social (player_id, social_media, link, created_at) VALUES (1, 'TRANSFER_MARKET', 'https://www.transfermarkt.co.uk/jordan-pickford/profil/spieler/130164', sysdate());

INSERT INTO fa_player_position (player_id, position_number, position_order) VALUES (1, 1, 1);

INSERT INTO fa_player_foreign_mapping (player_id, source, foreign_id) VALUES (1, 'OPTA', 'opta1000');
INSERT INTO fa_player_foreign_mapping (player_id, source, foreign_id) VALUES (1, 'FAN', '11');


INSERT INTO fa_player(player_id, first_name, middle_Name, last_Name, known_Name, date_of_birth, gender, player_grade, created_At, updated_At, version)
values (2, 'waleed', '', 'khan', 'wk',  curdate(), 'M', 'A1', sysdate(), sysdate(),  1);

INSERT INTO  fa_player_squad (player_id, squad, squad_status) values (2, 'U21', 'MONITOR');

INSERT INTO  fa_player_eligibility (player_id, country_code) values (2, 'PAK');

INSERT INTO fa_player_intel (player_id, intel_type, note, archived, created_at) VALUES (2, 'GENERAL', 'He is an awesome player', false, sysdate());
INSERT INTO fa_player_intel (player_id, intel_type, note, archived, created_at) VALUES (2, 'EDUCATION', 'He done a PhD', true, sysdate());

INSERT INTO fa_player_social (player_id, social_media, link, created_at) VALUES (2, 'TWITTER', 'https://twitter.com/jpickford1?lang=en', sysdate());
INSERT INTO fa_player_social (player_id, social_media, link, created_at) VALUES (2, 'WIKIPEDIA', 'https://en.wikipedia.org/wiki/Jordan_Pickford', sysdate());
INSERT INTO fa_player_social (player_id, social_media, link, created_at) VALUES (2, 'TRANSFER_MARKET', 'https://www.transfermarkt.co.uk/jordan-pickford/profil/spieler/130164', sysdate());

INSERT INTO fa_player_position (player_id, position_number, position_order) VALUES (2, 1, 1);

INSERT INTO fa_player_foreign_mapping (player_id, source, foreign_id) VALUES (2, 'FAN', 'fapl0001');


INSERT INTO fa_player(player_id, first_name, middle_Name, last_Name, known_Name, date_of_birth, gender, player_grade, created_At, updated_At, version)
values (3, 'Taimour', '', 'Babar', 'NH',  curdate(), 'M', 'A1', sysdate(), sysdate(),  1);

INSERT INTO  fa_player_squad (player_id, squad, squad_status) values (3, 'U21', 'MONITOR');

INSERT INTO  fa_player_eligibility (player_id, country_code) values (3, 'PAK');

INSERT INTO fa_player_intel (player_id, intel_type, note, archived, created_at) VALUES (3, 'GENERAL', 'He is an awesome player', false, sysdate());
INSERT INTO fa_player_intel (player_id, intel_type, note, archived, created_at) VALUES (3, 'EDUCATION', 'He done a PhD', true, sysdate());

INSERT INTO fa_player_social (player_id, social_media, link, created_at) VALUES (3, 'TWITTER', 'https://twitter.com/jpickford1?lang=en', sysdate());
INSERT INTO fa_player_social (player_id, social_media, link, created_at) VALUES (3, 'WIKIPEDIA', 'https://en.wikipedia.org/wiki/Jordan_Pickford', sysdate());
INSERT INTO fa_player_social (player_id, social_media, link, created_at) VALUES (3, 'TRANSFER_MARKET', 'https://www.transfermarkt.co.uk/jordan-pickford/profil/spieler/130164', sysdate());

INSERT INTO fa_player_position (player_id, position_number, position_order) VALUES (3, 1, 1);

INSERT INTO fa_player_foreign_mapping (player_id, source, foreign_id) VALUES (3, 'FAN', 'fapl0002');
INSERT INTO fa_player_foreign_mapping (player_id, source, foreign_id) VALUES (3, 'OPTA', 'opta1001');


INSERT INTO fa_player(player_id, first_name, middle_Name, last_Name, known_Name, date_of_birth, gender, player_grade, created_At, updated_At, version)
values (4, 'Taimour', '', 'Babar', 'NH',  curdate(), 'M', 'A1', sysdate(), sysdate(),  1);

INSERT INTO  fa_player_squad (player_id, squad, squad_status) values (4, 'U21', 'MONITOR');

INSERT INTO  fa_player_eligibility (player_id, country_code) values (4, 'PAK');

INSERT INTO fa_player_intel (player_id, intel_type, note, archived, created_at) VALUES (4, 'GENERAL', 'He is an awesome player', false, sysdate());
INSERT INTO fa_player_intel (player_id, intel_type, note, archived, created_at) VALUES (4, 'EDUCATION', 'He done a PhD', true, sysdate());

INSERT INTO fa_player_position (player_id, position_number, position_order) VALUES (4, 1, 1);

INSERT INTO fa_player_foreign_mapping (player_id, source, foreign_id) VALUES (4, 'FAN', 'fapl0005');

INSERT INTO fa_player(player_id, first_name, middle_Name, last_Name, known_Name, date_of_birth, gender, player_grade, created_At, updated_At, version)
values (5, 'Taimour', '', 'Babar', 'NH',  curdate(), 'M', 'A1', sysdate(), sysdate(),  1);

INSERT INTO  fa_player_squad (player_id, squad, squad_status) values (5, 'U21', 'MONITOR');

INSERT INTO  fa_player_eligibility (player_id, country_code) values (5, 'PAK');

INSERT INTO fa_player_intel (player_id, intel_type, note, archived, created_at) VALUES (5, 'GENERAL', 'He is an awesome player', false, sysdate());
INSERT INTO fa_player_intel (player_id, intel_type, note, archived, created_at) VALUES (5, 'EDUCATION', 'He done a PhD', true, sysdate());

INSERT INTO fa_player_position (player_id, position_number, position_order) VALUES (5, 1, 1);

INSERT INTO fa_player_foreign_mapping (player_id, source, foreign_id) VALUES (5, 'FAN', 'fapl0005');

INSERT INTO fa_player_foreign_mapping (player_id, source, foreign_id) VALUES (5, 'PMA_EXTERNAL', '1084313');





