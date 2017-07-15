/* Seasons */

insert into season (year) values ('2016');
insert into season (year) values ('2017');

/* Weeks */
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 1, 'Week 1');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 2, 'Week 2');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 3, 'Week 3');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 4, 'Week 4');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 5, 'Week 5');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 6, 'Week 6');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 7, 'Week 7');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 8, 'Week 8');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 9, 'Week 9');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 10, 'Week 10');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 11, 'Week 11');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 12, 'Week 12');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 13, 'Week 13');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 14, 'Week 14');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 15, 'Week 15');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 16, 'Week 16');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 17, 'Week 17');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 18, 'Playoffs - Wild Card');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 19, 'Playoffs - Divisional');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 20, 'Playoffs - Conference Championship');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 21, 'Playoffs - Super Bowl');

insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 1, 'Week 1');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 2, 'Week 2');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 3, 'Week 3');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 4, 'Week 4');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 5, 'Week 5');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 6, 'Week 6');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 7, 'Week 7');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 8, 'Week 8');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 9, 'Week 9');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 10, 'Week 10');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 11, 'Week 11');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 12, 'Week 12');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 13, 'Week 13');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 14, 'Week 14');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 15, 'Week 15');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 16, 'Week 16');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 17, 'Week 17');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 18, 'Playoffs - Wild Card');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 19, 'Playoffs - Divisional');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 20, 'Playoffs - Conference Championship');
insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2017'), 21, 'Playoffs - Super Bowl');
