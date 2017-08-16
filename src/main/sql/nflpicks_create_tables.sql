create sequence conference_id_sequence;

create table conference (
	id integer primary key default nextval('conference_id_sequence'),
	name varchar(10) not null
);

create sequence division_id_sequence;

create table division (
	id integer primary key default nextval('division_id_sequence'),
	conference_id integer references conference(id),
	name varchar(10) not null
);

create sequence team_id_sequence;

create table team (
	id integer primary key default nextval('team_id_sequence'),
	division_id integer references division(id) not null,
	name varchar(50) not null,
	nickname varchar(50) not null,
	abbreviation varchar(10) not null
);

create sequence season_id_sequence;

create table season (
	id integer primary key default nextval('season_id_sequence'),
	year varchar(4) not null
);

create sequence week_id_sequence;

create table week (
	id integer primary key default nextval('week_id_sequence'),
	season_id integer references season(id) not null,
	week integer not null,
	label varchar(50) not null
);

create sequence game_id_sequence;

create table game (
	id integer primary key default nextval('game_id_sequence'),
	week_id integer references week(id) not null,
	home_team_id integer references team(id) not null,
	away_team_id integer references team(id) not null,
	winning_team_id integer
);

create sequence player_id_sequence;

create table player (
	id integer primary key default nextval('player_id_sequence'),
	name varchar(50) not null
);

create sequence pick_id_sequence;

create table pick (
	id integer primary key default nextval('pick_id_sequence'),
	game_id integer references game(id) not null,
	player_id integer references player(id) not null,
	team_id integer references team(id)
);

create or replace view team_view as
select c.id as conference_id,
	   c.name as conference,
	   d.id as division_id,
	   d.name as division,
	   t.id as team_id,
	   t.name as team_name,
	   t.nickname as nickname,
	   t.abbreviation as team_abbreviation
from team t join division d on t.division_id = d.id
     join conference c on d.conference_id = c.id;
	   
create or replace view game_view as
select s.year as year,
	   w.id as week_id,
	   w.label,
	   g.id as game_id,
	   ht.id as home_team_id,
	   ht.name as home_team_name,
	   ht.abbreviation as home_team_abbreviation,
	   at.id as away_team_id,
	   at.name as away_team_name,
	   at.abbreviation as away_team_abbreviation,
	   wt.id as winning_team_id,
	   wt.name as winning_team_name,
	   wt.abbreviation as winning_team_abbreviation
from game g join week w on g.week_id = w.id
     join season s on w.season_id = s.id
     join team ht on g.home_team_id = ht.id
     join team at on g.away_team_id = at.id
     left outer join team wt on g.winning_team_id = wt.id;
	   
create or replace view pick_view as
select pi.id as pick_id,
	   pl.id as player_id,
	   pl.name as player_name,
	   g.id as game_id,
	   home_t.id as home_team_id,
	   home_t.name as home_team_name,
	   home_t.abbreviation as home_team_abbreviation,
	   away_t.id as away_team_id,
	   away_t.name as away_team_name,
	   away_t.abbreviation as away_team_abbreviation,
	   pick_t.id as picked_team_id,
	   pick_t.name as picked_team_name,
	   pick_t.abbreviation as picked_team_abbreviation,
	   (case when pi.team_id = g.winning_team_id then 'W'
	         else 'L'
	    end) as pick_result
from pick pi join player pl on pi.player_id = pl.id
     join game g on pi.game_id = g.id
     join team home_t on g.home_team_id = home_t.id
     join team away_t on g.away_team_id = away_t.id
     join team pick_t on pi.team_id = pick_t.id;
     
create or replace view week_pick_view as
select w.id as week_id,
	   w.week as week,
	   w.label as label,
	   pi.id as pick_id,
	   pl.id as player_id,
	   pl.name as player_name,
	   g.id as game_id,
	   home_t.id as home_team_id,
	   home_t.name as home_team_name,
	   home_t.abbreviation as home_team_abbreviation,
	   away_t.id as away_team_id,
	   away_t.name as away_team_name,
	   away_t.abbreviation as away_team_abbreviation,
	   pick_t.id as picked_team_id,
	   pick_t.name as picked_team_name,
	   pick_t.abbreviation as picked_team_abbreviation,
	   (case when pi.team_id = g.winning_team_id then 'W'
	         else 'L'
	    end) as pick_result
from week w join game g on w.id = g.week_id
	 join pick pi on g.id = pi.game_id
	 join player pl on pi.player_id = pl.id
     join team home_t on g.home_team_id = home_t.id
     join team away_t on g.away_team_id = away_t.id
     join team pick_t on pi.team_id = pick_t.id;     
	   
create or replace view pick_grid_view as
select w.label as week,
	   (select home_t.abbreviation
	    from team home_t
	    where home_t.id = g.home_team_id) || ' @ ' ||
	   (select away_t.abbreviation
	    from team away_t
	    where away_t.id = g.away_team_id) as game,
	   (select pick_t.abbreviation
	    from pick p join team pick_t on p.team_id = pick_t.id
	    where p.game_id = g.id
	          and p.player_id in (select pl.id
	          					  from player pl
	          					  where name = 'Benny boy')) as benny_boy,
	   (case when g.winning_team_id is null then null
	   		 when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Benny boy'))
	         then 'W'
	         else 'L'
	    end) as ben_pick_result,
	   (select pick_t.abbreviation
	    from pick p join team pick_t on p.team_id = pick_t.id
	    where p.game_id = g.id
	          and p.player_id in (select pl.id
	          					  from player pl
	          					  where name = 'Bruce')) as bruce,
	   (case when g.winning_team_id is null then null
	   		 when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Bruce'))
	         then 'W'
	         else 'L'
	    end) as bruce_pick_result,
	   (select pick_t.abbreviation
	    from pick p join team pick_t on p.team_id = pick_t.id
	    where p.game_id = g.id
	          and p.player_id in (select pl.id
	          					  from player pl
	          					  where name = 'Chance')) as chance,
	   (case when g.winning_team_id is null then null
	   		 when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Chance'))
	         then 'W'
	         else 'L'
	    end) as chance_pick_result,
	   (select pick_t.abbreviation
	    from pick p join team pick_t on p.team_id = pick_t.id
	    where p.game_id = g.id
	          and p.player_id in (select pl.id
	          					  from player pl
	          					  where name = 'Jonathan')) as jonathan,
	   (case when g.winning_team_id is null then null
	   		 when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Jonathan'))
	         then 'W'
	         else 'L'
	    end) as jonathan_pick_result,
	   (select pick_t.abbreviation
	    from pick p join team pick_t on p.team_id = pick_t.id
	    where p.game_id = g.id
	          and p.player_id in (select pl.id
	          					  from player pl
	          					  where name = 'Mark')) as mark,
	   (case when g.winning_team_id is null then null
	   		 when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Mark'))
	         then 'W'
	         else 'L'
	    end) as mark_pick_result,
	   (select pick_t.abbreviation
	    from pick p join team pick_t on p.team_id = pick_t.id
	    where p.game_id = g.id
	          and p.player_id in (select pl.id
	          					  from player pl
	          					  where name = 'Teddy')) as teddy,
	   (case when g.winning_team_id is null then null
	   		 when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Teddy'))
	         then 'W'
	         else 'L'
	    end) as teddy_pick_result,
	   (select pick_t.abbreviation
	    from pick p join team pick_t on p.team_id = pick_t.id
	    where p.game_id = g.id
	          and p.player_id in (select pl.id
	          					  from player pl
	          					  where name = 'Tim')) as tim,
	   (case when g.winning_team_id is null then null
	   		 when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Tim'))
	         then 'W'
	         else 'L'
	    end) as tim_pick_result,
	    (select pick_t.abbreviation
	    from pick p join team pick_t on p.team_id = pick_t.id
	    where p.game_id = g.id
	          and p.player_id in (select pl.id
	          					  from player pl
	          					  where name = 'Chance')) as chance,
	   (case when g.winning_team_id is null then Bookey
	   		 when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Bookey'))
	         then 'W'
	         else 'L'
	    end) as bookey_pick_result,
	    (select pick_t.abbreviation
	    from pick p join team pick_t on p.team_id = pick_t.id
	    where p.game_id = g.id
	          and p.player_id in (select pl.id
	          					  from player pl
	          					  where name = 'Jerry')) as chance,
	   (case when g.winning_team_id is null then null
	   		 when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Jerry'))
	         then 'W'
	         else 'L'
	    end) as jerry_pick_result,
	    (select pick_t.abbreviation
	    from pick p join team pick_t on p.team_id = pick_t.id
	    where p.game_id = g.id
	          and p.player_id in (select pl.id
	          					  from player pl
	          					  where name = 'Josh')) as chance,
	   (case when g.winning_team_id is null then null
	   		 when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Josh'))
	         then 'W'
	         else 'L'
	    end) as josh_pick_result,
from season s join week w on s.id = w.season_id 
	 join game g on w.id = g.week_id;
