/* Conferences */
delete from conference;
insert into conference (name) values ('AFC');
insert into conference (name) values ('NFC');

/* Divisions */
delete from division;
insert into division (conference_id, name) values ((select id from conference where name = 'AFC'), 'East');
insert into division (conference_id, name) values ((select id from conference where name = 'AFC'), 'North');
insert into division (conference_id, name) values ((select id from conference where name = 'AFC'), 'South');
insert into division (conference_id, name) values ((select id from conference where name = 'AFC'), 'West');

insert into division (conference_id, name) values ((select id from conference where name = 'NFC'), 'East');
insert into division (conference_id, name) values ((select id from conference where name = 'NFC'), 'North');
insert into division (conference_id, name) values ((select id from conference where name = 'NFC'), 'South');
insert into division (conference_id, name) values ((select id from conference where name = 'NFC'), 'West');

/* Teams */
delete from team;
insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'East'), 'Buffalo Bills', 'Bills', 'BUF');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'East'), 'New England Patriots', 'Patriots', 'NE');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'East'), 'New York Jets', 'Jets', 'NYJ');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'East'), 'Miami Dolphins', 'Dolphins', 'MIA');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'North'), 'Baltimore Ravens', 'Ravens', 'BAL');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'North'), 'Pittsburgh Steelers', 'Steelers', 'PIT');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'North'), 'Cincinnati Bengals', 'Bengals', 'CIN');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'North'), 'Cleveland Browns', 'Browns', 'CLE');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'South'), 'Indianapolis Colts', 'Colts', 'IND');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'South'), 'Jacksonville Jaguars', 'Jaguars', 'JAX');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'South'), 'Tennessee Titans', 'Titans', 'TEN');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'South'), 'Houston Texans', 'Texans', 'HOU');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'West'), 'Oakland Raiders', 'Raiders', 'OAK');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'West'), 'Denver Broncos', 'Broncos', 'DEN');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'West'), 'San Diego Chargers', 'Chargers', 'SD');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'AFC') and d.name = 'West'), 'Kansas City Chiefs', 'Chiefs', 'KC');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'East'), 'New York Giants', 'Giants', 'NYG');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'East'), 'Dallas Cowboys', 'Cowboys', 'DAL');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'East'), 'Philadelphia Eagles', 'Eagles', 'PHI');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'East'), 'Washington Redskins', 'Redskins', 'WASH');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'North'), 'Green Bay Packers', 'Packers', 'GB');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'North'), 'Minnesota Vikings', 'Vikings', 'MIN');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'North'), 'Detroit Lions', 'Lions', 'DET');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'North'), 'Chicago Bears', 'Bears', 'CHI');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'South'), 'Atlanta Falcons', 'Falcons', 'ATL');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'South'), 'Carolina Panthers', 'Panthers', 'CAR');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'South'), 'Tampa Bay Buccaneers', 'Buccaneers', 'TB');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'South'), 'New Orleans Saints', 'Saints', 'NO');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'West'), 'Seattle Seahawks', 'Seahawks', 'SEA');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'West'), 'San Francisco 49ers', '49ers', 'SF');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'West'), 'Arizona Cardinals', 'Cardinals', 'ARZ');

insert into team (division_id, name, nickname, abbreviation) 
values ((select d.id from division d where d.conference_id in (select c.id from conference c where c.name = 'NFC') and d.name = 'West'), 'Los Angeles Rams', 'Rams', 'LA');

/* Players */
insert into player (name) values ('Benny boy');
insert into player (name) values ('Bruce');
insert into player (name) values ('Chance');
insert into player (name) values ('Jonathan');
insert into player (name) values ('Mark');
insert into player (name) values ('Tim');
insert into player (name) values ('Teddy');
