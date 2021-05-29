create or replace view team_view as
select c.id as conference_id,
	   c.name as conference,
	   d.id as division_id,
	   d.name as division,
	   t.id as team_id,
	   t.city as team_name,
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
	   ht.city as home_team_city,
	   ht.abbreviation as home_team_abbreviation,
	   at.id as away_team_id,
	   at.city as away_team_city,
	   at.abbreviation as away_team_abbreviation,
	   wt.id as winning_team_id,
	   wt.city as winning_team_city,
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
	   home_t.city as home_team_city,
	   home_t.abbreviation as home_team_abbreviation,
	   away_t.id as away_team_id,
	   away_t.city as away_team_city,
	   away_t.abbreviation as away_team_abbreviation,
	   pick_t.id as picked_team_id,
	   pick_t.city as picked_team_city,
	   pick_t.abbreviation as picked_team_abbreviation,
	   (case when g.winning_team_id = -1 then 'T'
	   		 when g.winning_team_id = pi.team_id then 'W'
	         else 'L'
	    end) as pick_result
from pick pi join player pl on pi.player_id = pl.id
     join game g on pi.game_id = g.id
     join team home_t on g.home_team_id = home_t.id
     join team away_t on g.away_team_id = away_t.id
     join team pick_t on pi.team_id = pick_t.id;
     
create or replace view week_pick_view as
select w.id as week_id,
	   w.sequence_number as sequence_number,
	   w.label as label,
	   pi.id as pick_id,
	   pl.id as player_id,
	   pl.name as player_name,
	   g.id as game_id,
	   home_t.id as home_team_id,
	   home_t.city as home_team_city,
	   home_t.abbreviation as home_team_abbreviation,
	   away_t.id as away_team_id,
	   away_t.city as away_team_city,
	   away_t.abbreviation as away_team_abbreviation,
	   pick_t.id as picked_team_id,
	   pick_t.city as picked_team_city,
	   pick_t.abbreviation as picked_team_abbreviation,
	   (case when g.winning_team_id is null then ''
	   		 when g.winning_team_id = -1 then 'T'
	   		 when g.winning_team_id = pi.team_id then 'W'
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
	   		 when g.winning_team_id = -1 then 'T'
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
	   		 when g.winning_team_id = -1 then 'T'
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
	   		 when g.winning_team_id = -1 then 'T'
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
	   		 when g.winning_team_id = -1 then 'T'
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
	   		 when g.winning_team_id = -1 then 'T'
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
	   		 when g.winning_team_id = -1 then 'T'
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
	   		 when g.winning_team_id = -1 then 'T'
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
	          					  where name = 'Bookey')) as bookey,
	   (case when g.winning_team_id is null then null
	   		 when g.winning_team_id = -1 then 'T'
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
	          					  where name = 'Jerry')) as jerry,
	   (case when g.winning_team_id is null then null
	   		 when g.winning_team_id = -1 then 'T'
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
	          					  where name = 'Josh')) as josh,
	   (case when g.winning_team_id is null then null
	   		 when g.winning_team_id = -1 then 'T'
	   		 when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Josh'))
	         then 'W'
	         else 'L'
	    end) as josh_pick_result,
	    (select pick_t.abbreviation
	    from pick p join team pick_t on p.team_id = pick_t.id
	    where p.game_id = g.id
	          and p.player_id in (select pl.id
	          					  from player pl
	          					  where name = 'Doodle')) as doodle,
	   (case when g.winning_team_id is null then null
	   		 when g.winning_team_id = -1 then 'T'
	   		 when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Doodle'))
	         then 'W'
	         else 'L'
	    end) as doodle_pick_result,
	    (select pick_t.abbreviation
	    from pick p join team pick_t on p.team_id = pick_t.id
	    where p.game_id = g.id
	          and p.player_id in (select pl.id
	          					  from player pl
	          					  where name = 'Boo')) as boo,
	   (case when g.winning_team_id is null then null
	   		 when g.winning_team_id = -1 then 'T'
	   		 when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Boo'))
	         then 'W'
	         else 'L'
	    end) as boo_pick_result,
	    (select pick_t.abbreviation
	    from pick p join team pick_t on p.team_id = pick_t.id
	    where p.game_id = g.id
	          and p.player_id in (select pl.id
	          					  from player pl
	          					  where name = 'Moe')) as moe,
	    (case when g.winning_team_id is null then null
	    	  when g.winning_team_id = -1 then 'T'
	   		  when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Moe'))
	         then 'W'
	         else 'L'
	    end) as moe_pick_result,
	    (select pick_t.abbreviation
	    from pick p join team pick_t on p.team_id = pick_t.id
	    where p.game_id = g.id
	          and p.player_id in (select pl.id
	          					  from player pl
	          					  where name = 'Var')) as var,
	    (case when g.winning_team_id is null then null
	    	  when g.winning_team_id = -1 then 'T'
	   		  when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Var'))
	         then 'W'
	         else 'L'
	    end) as var_pick_result,
	    (select pick_t.abbreviation
	    from pick p join team pick_t on p.team_id = pick_t.id
	    where p.game_id = g.id
	          and p.player_id in (select pl.id
	          					  from player pl
	          					  where name = 'Scott')) as scott,
	    (case when g.winning_team_id is null then null
	    	  when g.winning_team_id = -1 then 'T'
	   		  when g.winning_team_id in (select p.team_id
	   								    from pick p
	   								    where p.game_id = g.id
	   								          and p.player_id in (select pl.id
	   								          					  from player pl
	   								          					  where pl.name = 'Scott'))
	         then 'W'
	         else 'L'
	    end) as scott_pick_result
from season s join week w on s.id = w.season_id 
	 join game g on w.id = g.week_id;