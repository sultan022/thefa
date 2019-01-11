create table fa_player_grade_history
(
  history_id int auto_increment primary key,
  fan_Id int,
  player_grade varchar(50),
  created_at datetime default CURRENT_TIMESTAMP,
  created_by varchar(255) null,
  FOREIGN KEY (player_grade) REFERENCES fa_grade(grade)
);

create table fa_player_squad_history
(
  history_id int auto_increment primary key,
  fan_id int not null,
  squad varchar (50) not null,
  squad_status varchar(100) null,
  assignment varchar(50) not null,
  created_at datetime default CURRENT_TIMESTAMP,
  created_by varchar(255) null,
  FOREIGN KEY (fan_id) REFERENCES fa_player(fan_id),
  FOREIGN KEY (squad) REFERENCES fa_squad(squad),
  FOREIGN KEY (squad_status) REFERENCES fa_squad_status(status)
);

create table fa_player_position_history
(
  history_id int auto_increment primary key,
  fan_id int not null,
  position_number int not null,
  position_order int not null,
  assignment varchar(50) not null,
  created_at datetime default CURRENT_TIMESTAMP,
  created_by varchar(255) null,
  FOREIGN KEY (fan_id) REFERENCES fa_player(fan_id),
  FOREIGN KEY (position_number) REFERENCES fa_position(position_number)
);

