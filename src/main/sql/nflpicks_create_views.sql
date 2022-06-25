create or replace view team_view as
select c.id as team_conference_id,
	   c.name as conference_name,
	   d.id as team_division_id,
	   d.name as division_name,
	   t.id as team_id,
	   t.city as team_name,
	   t.nickname as nickname,
	   t.abbreviation as team_abbreviation
from team t join team_division d on t.team_division_id = d.id
     join team_conference c on d.team_conference_id = c.id;
	   
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

     
create or replace view record_view as
select pick_totals.player_id,  
     pick_totals.player_name,  
     sum(pick_totals.wins) as wins,  
     sum(pick_totals.losses) as losses, 
     sum(pick_totals.ties) as ties 
from (select pl.id as player_id,  
		   pl.name as player_name, 
		  (case when p.team_id = g.winning_team_id  
		 	    then 1  
		 	    else 0  
		   end) as wins,  
		  (case when g.winning_team_id != -1 and (p.team_id is not null and p.team_id != g.winning_team_id)  
		 	    then 1  
		 	    else 0  
		   end) as losses,  
		  (case when g.winning_team_id = -1  
		 	    then 1  
		 	    else 0 
		   end) as ties 
    from pick p join game g on p.game_id = g.id  
		 join player pl on p.player_id = pl.id 
         join team home_team on g.home_team_id = home_team.id  
		 join team away_team on g.away_team_id = away_team.id 
	) pick_totals  
group by pick_totals.player_id, pick_totals.player_name;