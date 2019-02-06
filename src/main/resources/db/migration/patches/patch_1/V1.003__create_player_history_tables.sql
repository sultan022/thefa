create table fa_player_grade_history
(
  history_id int auto_increment primary key,
  player_id varchar(50),
  player_grade varchar(50),
  created_at datetime default CURRENT_TIMESTAMP,
  created_by varchar(255) null,
  FOREIGN KEY (player_id) REFERENCES fa_player(player_id),
  FOREIGN KEY (player_grade) REFERENCES fa_grade(grade)
);

create table fa_player_squad_history
(
  history_id int auto_increment primary key,
  player_id varchar(50) not null,
  squad varchar (50) not null,
  squad_status varchar(100) null,
  assignment varchar(50) not null,
  created_at datetime default CURRENT_TIMESTAMP,
  created_by varchar(255) null,
  FOREIGN KEY (player_id) REFERENCES fa_player(player_id),
  FOREIGN KEY (squad) REFERENCES fa_squad(squad),
  FOREIGN KEY (squad_status) REFERENCES fa_squad_status(status)
);

create table fa_player_position_history
(
  history_id int auto_increment primary key,
  player_id varchar(50) not null,
  position_number int not null,
  position_order int not null,
  assignment varchar(50) not null,
  created_at datetime default CURRENT_TIMESTAMP,
  created_by varchar(255) null,
  FOREIGN KEY (player_id) REFERENCES fa_player(player_id),
  FOREIGN KEY (position_number) REFERENCES fa_position(position_number)
);

create table fa_player_injury_status_history
(
  history_id int auto_increment primary key,
  player_id varchar(50) not null,
  injury_status varchar(50) null,
  created_at datetime default CURRENT_TIMESTAMP,
  created_by varchar(255) null,
  FOREIGN KEY (player_id) REFERENCES fa_player(player_id)
);
