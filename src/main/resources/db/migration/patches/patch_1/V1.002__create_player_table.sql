create table fa_player
(
  player_id varchar(50) primary key,
  first_name varchar(50) not null,
  middle_name varchar(50) null,
  last_name varchar(50) not null,
  known_name varchar(100) null,
  date_of_birth date not null,
  gender char comment 'M for Male, F for Female' not null,
  profile_image varchar(500) null,
  player_grade varchar(50) null,
  club_id varchar(50) null,
  maturation_status varchar(50),
  maturation_date datetime,
  vulnerability_status int null,
  vulnerability_status_week4 int NULL,
  vulnerability_status_week8 int NULL,
  vulnerability_status_week12 int NULL,
  vulnerability_date datetime,
  created_at datetime default CURRENT_TIMESTAMP,
  updated_at datetime default CURRENT_TIMESTAMP,
  created_by varchar(255) null,
  updated_by varchar(255) null,
  version int,
  injury_status varchar(50) null,
  expected_return_date date null,
  thumbnail_image varchar(500) null,
  FOREIGN KEY (gender) REFERENCES fa_gender(gender),
  FOREIGN KEY (player_grade) REFERENCES fa_grade(grade),
  FOREIGN KEY (club_id) REFERENCES fa_club(club_id)
);

create index idx_firstname on fa_player (first_name);
create index idx_lastname on fa_player (last_name);
create index idx_middlename on fa_player (middle_name);
create index idx_dateOfBirth on fa_player (date_of_birth);
create index idx_gender on fa_player (gender);

create table fa_player_foreign_mapping
(
  player_id varchar(50) not null,
  source VARCHAR(50) not null,
  foreign_id varchar(100) not null,
  PRIMARY KEY (player_id, source),
  FOREIGN KEY (player_id) REFERENCES fa_player(player_id),
  FOREIGN KEY (source) REFERENCES fa_source(source)
);

create table fa_player_squad
(
  player_id varchar(50) not null,
  squad varchar (50) not null,
  squad_status varchar(100) null,
  PRIMARY KEY (player_id, squad),
  FOREIGN KEY (player_id) REFERENCES fa_player(player_id),
  FOREIGN KEY (squad) REFERENCES fa_squad(squad),
  FOREIGN KEY (squad_status) REFERENCES fa_squad_status(status)
);

create table fa_player_eligibility
(
  player_id varchar(50) not null,
  country_code varchar(3) not null,
  primary key (player_id, country_code),
  FOREIGN KEY (player_id) REFERENCES fa_player(player_id),
  FOREIGN KEY (country_code) REFERENCES fa_country(country_code)
);

create table fa_player_intel
(
  id int(11) auto_increment primary key,
  player_id varchar(50) not null,
  intel_type  varchar(100) null,
  note  text null,
  archived boolean not null default false,
  created_at datetime default CURRENT_TIMESTAMP,
  updated_at datetime default CURRENT_TIMESTAMP,
  created_by varchar(255) null,
  updated_by varchar(255) null,
  FOREIGN KEY (player_id) REFERENCES fa_player(player_id),
  FOREIGN KEY (intel_type) REFERENCES fa_intel(intel_type)
);

create table fa_player_social
(
  id int(11) auto_increment primary key,
  player_id varchar(50) not null,
  social_media varchar(100),
  link varchar(255) null,
  created_at datetime default CURRENT_TIMESTAMP null,
  FOREIGN KEY (player_id) REFERENCES fa_player(player_id),
  FOREIGN KEY (social_media) REFERENCES fa_social_media(name)
);

create table fa_player_position
(
  player_id varchar(50) not null,
  position_number int not null,
  position_order int null,
  PRIMARY KEY (player_id, position_number),
  FOREIGN KEY (player_id) REFERENCES fa_player(player_id),
  FOREIGN KEY (position_number) REFERENCES fa_position(position_number)
);

create table fa_player_internal_mapping_counter
(
  id int primary key,
  counter int not null
);

INSERT INTO fa_player_internal_mapping_counter (id, counter) VALUES (1, 11000);

CREATE TABLE `fa_player_attachment` (
  `attachment_id` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` varchar(50) NOT NULL,
  `attachment_path` varchar(500) DEFAULT NULL,
  `attachment_type` varchar(50) DEFAULT NULL,
  `camp_date` datetime DEFAULT NULL,
  `uploaded_by` varchar(50) DEFAULT NULL,
  `uploaded_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`attachment_id`),
  KEY `fa_attachments_ibfk_1` (`player_id`),
  CONSTRAINT `fa_attachments_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `fa_player` (`player_id`)
);

