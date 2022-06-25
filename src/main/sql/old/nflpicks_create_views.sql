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