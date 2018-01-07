create function create_game(home_team_abbreviation varchar(10), away_team_abbreviation varchar(10), week_number integer, season_year varchar(10)) 
returns integer as $$
	declare
	
	begin
	
		insert into game (week_id, home_team_id, away_team_id)
		values ((select w.id
 				from week w
				where w.week = week_number
	      			  and w.season_id in (select id
				  	  					  from season s
				  						  where s.year = season_year)),
       		   (select t.id
				from team t
				where abbreviation = home_team_abbreviation),
       		   (select t.id
				from team t
				where abbreviation = away_team_abbreviation));
	
	return 1;
				
	end; 

$$ language plpgsql;