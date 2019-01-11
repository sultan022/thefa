create table fa_country
(
  country_code varchar(3)      not null primary key,
  country_name varchar(200) not null,
  points      int(4)       null,
  rank        int(3)       null
);

create table fa_gender
(
  gender char not null primary key,
  description varchar(255) null
);

create table fa_source
(
  source varchar(50) not null primary key,
  description varchar(255) null
);

create table fa_squad
(
  squad       varchar(50) not null primary  key,
  description varchar(255) null
);

create table fa_squad_status
(
  status      varchar(100) not null primary key,
  description varchar(255) null
);

create table fa_grade
(
  grade        varchar(50)  primary key,
  description  varchar(255) null
);

create table fa_position
(
  position_number      int(11) not null primary key,
  description          varchar(255) null
);

create table fa_social_media
(
  name varchar(100) primary key ,
  icon varchar(255) null
);

create table fa_intel
(
  intel_type varchar(100) primary key,
  description          varchar(255) null
);

create table fa_club
(
  club_id varchar(50) primary key,
  club_name varchar(255),
  club_name_abbr varchar(3),
  club_nickname varchar(255),
  club_city varchar(255),
  club_stadium varchar(255),
  club_website varchar(500),
  year_founded int(4),
  team_type varchar(100),
  is_active boolean
);
create index idx_clubname on fa_club (club_name);


