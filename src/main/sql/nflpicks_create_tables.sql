create sequence conference_id_sequence;
create table conference (
	id integer primary key default nextval('conference_id_sequence'),
	name varchar(255) not null,
	start_year varchar(4) not null default to_char(current_date, 'yyyy'),
	end_year varchar(4),
	current_name varchar(255) not null
);
create index on conference (name);

create sequence division_id_sequence;
create table division (
	id integer primary key default nextval('division_id_sequence'),
	conference_id integer references conference(id),
	name varchar(255) not null,
	start_year varchar(4) not null default to_char(current_date, 'yyyy'),
	end_year varchar(4),
	current_name varchar(255) not null
);
create index on division (conference_id);
create index on division (name);

create sequence team_id_sequence;
create table team (
	id integer primary key default nextval('team_id_sequence'),
	division_id integer references division(id) not null,
	city varchar(255) not null,
	nickname varchar(255) not null,
	abbreviation varchar(255) not null,
	start_year varchar(4) not null default to_char(current_date, 'yyyy'),
	end_year varchar(4),
	current_abbreviation varchar(255) not null
);
create index on team (division_id);
create index on team (city);
create index on team (nickname);
create index on team (abbreviation);

create sequence season_id_sequence;
create table season (
	id integer primary key default nextval('season_id_sequence'),
	year varchar(4) not null
);
create index on season (year);

create sequence week_id_sequence;
create table week (
	id integer primary key default nextval('week_id_sequence'),
	season_id integer references season(id) not null,
	week_number integer not null,
	label varchar(255) not null
);
create index on week (season_id);
create index on week (week_number);
create index on week (label);

create sequence game_id_sequence;
create table game (
	id integer primary key default nextval('game_id_sequence'),
	week_id integer references week(id) not null,
	home_team_id integer references team(id) not null,
	away_team_id integer references team(id) not null,
	winning_team_id integer
);
create index on game (week_id);
create index on game (home_team_id);
create index on game (away_team_id);
create index on game (winning_team_id);

create sequence player_id_sequence;
create table player (
	id integer primary key default nextval('player_id_sequence'),
	name varchar(255) not null
);
create index on player (name);

create sequence pick_id_sequence;
create table pick (
	id integer primary key default nextval('pick_id_sequence'),
	game_id integer references game(id) not null,
	player_id integer references player(id) not null,
	team_id integer references team(id)
);
create index on pick (game_id);
create index on pick (player_id);
create index on pick (team_id);