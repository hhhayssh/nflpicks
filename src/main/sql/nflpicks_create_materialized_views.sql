-- Used by the pick accuracy query to make it faster.
create materialized view all_team_all_player_view as 
select pl.id as player_id,
       pl.name as player_name,
       t.id as team_id,
       t.team_division_id as team_division_id,
       t.city as city,
       t.nickname as nickname,
       t.abbreviation as abbreviation,
       t.start_year as start_year,
       t.end_year as end_year,
       t.current_abbreviation
FROM player pl CROSS JOIN team t;