create table fa_player
(
  fan_Id int primary key,
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
  vulnerability_status varchar(50),
  vulnerability_date datetime,
  created_at datetime default CURRENT_TIMESTAMP,
  updated_at datetime default CURRENT_TIMESTAMP,
  created_by varchar(255) null,
  updated_by varchar(255) null,
  version int,
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
  fan_id int not null,
  source VARCHAR(50) not null,
  foreign_id varchar(100) not null,
  PRIMARY KEY (fan_id, source),
  FOREIGN KEY (fan_id) REFERENCES fa_player(fan_id),
  FOREIGN KEY (source) REFERENCES fa_source(source)
);

create table fa_player_squad
(
  fan_id int not null,
  squad varchar (50) not null,
  squad_status varchar(100) null,
  PRIMARY KEY (fan_id, squad),
  FOREIGN KEY (fan_id) REFERENCES fa_player(fan_id),
  FOREIGN KEY (squad) REFERENCES fa_squad(squad),
  FOREIGN KEY (squad_status) REFERENCES fa_squad_status(status)
);

create table fa_player_eligibility
(
  fan_id int not null,
  country_code varchar(3) not null,
  primary key (fan_id, country_code),
  FOREIGN KEY (fan_id) REFERENCES fa_player(fan_id),
  FOREIGN KEY (country_code) REFERENCES fa_country(country_code)
);

create table fa_player_intel
(
  id int(11) auto_increment primary key,
  fan_id int not null,
  intel_type  varchar(100) null,
  note  text null,
  archived boolean not null default false,
  created_at datetime default CURRENT_TIMESTAMP,
  updated_at datetime default CURRENT_TIMESTAMP,
  created_by varchar(255) null,
  updated_by varchar(255) null,
  FOREIGN KEY (fan_id) REFERENCES fa_player(fan_id),
  FOREIGN KEY (intel_type) REFERENCES fa_intel(intel_type)
);

create table fa_player_social
(
  id int(11) auto_increment primary key,
  fan_id int not null,
  social_media varchar(100),
  link varchar(255) null,
  created_at datetime default CURRENT_TIMESTAMP null,
  FOREIGN KEY (fan_id) REFERENCES fa_player(fan_id),
  FOREIGN KEY (social_media) REFERENCES fa_social_media(name)
);

create table fa_player_position
(
  fan_id int not null,
  position_number int not null,
  position_order int null,
  PRIMARY KEY (fan_id, position_number),
  FOREIGN KEY (fan_id) REFERENCES fa_player(fan_id),
  FOREIGN KEY (position_number) REFERENCES fa_position(position_number)
);

create table fa_player_internal_mapping_counter
(
  id int primary key,
  counter int not null
);

INSERT INTO fa_player_internal_mapping_counter (id, counter) VALUES (1, 11000);

