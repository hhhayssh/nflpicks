package nflpicks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import nflpicks.model.CompactPick;
import nflpicks.model.CompactPlayerPick;
import nflpicks.model.Division;
import nflpicks.model.DivisionRecord;
import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.PickSplit;
import nflpicks.model.Player;
import nflpicks.model.PlayerDivision;
import nflpicks.model.Record;
import nflpicks.model.Season;
import nflpicks.model.Team;
import nflpicks.model.TeamConference;
import nflpicks.model.TeamDivision;
import nflpicks.model.Week;
import nflpicks.model.stats.Championship;
import nflpicks.model.stats.ChampionshipsForPlayer;
import nflpicks.model.stats.CompactPickAccuracyContainer;
import nflpicks.model.stats.DivisionTitle;
import nflpicks.model.stats.DivisionTitlesForPlayer;
import nflpicks.model.stats.PickAccuracySummary;
import nflpicks.model.stats.SeasonRecordForPlayer;
import nflpicks.model.stats.WeekRecord;
import nflpicks.model.stats.WeekRecordForPlayer;
import nflpicks.model.stats.WeekRecordForPlayers;
import nflpicks.model.stats.WeekRecordsForPlayer;

/**
 * 
 * This class is the main one for dealing with the actual data in the database.
 * Instead of calling it a "dao", I chose to call it a "data service" because this
 * is my project and I'm doing it how I want.  It will get everything from the
 * database and save everything to it so no other class has to worry about that.
 * 
 * This is the only class that should know anything about what the tables or columns
 * in the database are.
 * 
 * @author albundy
 *
 */
public class OldNFLPicksDataService {
	
	private static final Logger log = Logger.getLogger(OldNFLPicksDataService.class);

	/**
	 * 
	 * The object that does the talking to the database.
	 * 
	 */
	protected DataSource dataSource;
	
	//Statements for dealing with the team_conference table.
	protected static final String SELECT_TEAM_CONFERENCE = "select id, " +
													  "name, " +
													  "start_year, " + 
													  "end_year, " + 
													  "current_name " +
													  "from team_conference ";
	
	protected static final String INSERT_TEAM_CONFERENCE = "insert into team_conference (name, start_year, end_year, current_name) values (?, ?, ?, ?) ";
	
	protected static final String UPDATE_TEAM_CONFERENCE = "update team_conference " + 
													  "set name = ?, " + 
													  	  "start_year = ?, " + 
													  	  "end_year = ?, " + 
													  	  "current_name = ? " + 
													  "where id = ? ";

	//Statements for working with the team_division table.
	protected static final String SELECT_TEAM_DIVISION = "select id, " +
													"team_conference_id, " + 
													"name, " +
													"start_year, " + 
													"end_year, " + 
													"current_name " +
													"from team_division ";
	
	protected static final String INSERT_TEAM_DIVISION = "insert into team_division (team_conference_id, name, start_year, end_year, current_name) values (?, ?, ?, ?, ?) ";
	
	protected static final String UPDATE_TEAM_DIVISION = "update team_division " + 
												 	"set team_conference_id = ?, " + 
												 		"name = ?, " + 
												 		"start_year = ?, " + 
												 		"end_year = ?, " + 
												 		"current_name = ? " +
												 	"where id = ? ";
	
	//Statements for dealing with the team table.
	protected static final String SELECT_TEAM = "select team_division_id, " +
												"id, " +
												"city, " + 
												"nickname, " +
												"abbreviation, " +
												"start_year, " + 
												"end_year, " + 
												"current_abbreviation " + 
												"from team ";
	
	protected static final String INSERT_TEAM = "insert into team (team_division_id, city, nickname, abbreviation, start_year, end_year, current_abbreviation) values (?, ?, ?, ?, ?, ?, ?) ";
	
	protected static final String UPDATE_TEAM = "update team " + 
												"set team_division_id = ?, " + 
													"city = ?, " + 
													"nickname = ?, " + 
													"abbreviation = ?, " + 
													"start_year = ?, " + 
													"end_year = ?, " + 
													"current_abbreviation = ? " + 
												"where id = ? ";
	
	//Statements for dealing with the season table.
	protected static final String SELECT_SEASON = "select id, " +
												  "year " +
												  "from season ";
	
	protected static final String INSERT_SEASON = "insert into season (year) values (?) ";
	
	protected static final String UPDATE_SEASON = "update season " + 
												  "set year = ? " +
												  "where id = ? ";
	
	//Statements for dealing with the week table.
	protected static final String SELECT_WEEK = "select id, " +
												"season_id, " +
												"sequence_number, " + 
												"type, " +
												"key, " +
												"label " + 
												"from week ";
	
	protected static final String INSERT_WEEK = "insert into week (season_id, sequence_number, type, key, label) values (?, ?, ?, ?, ?) ";
	
	protected static final String UPDATE_WEEK = "update week " + 
												"set season_id = ?, " + 
												"sequence_number = ?, " +
												"type = ?, " +
												"key = ?, " +
												"label = ? " + 
												"where id = ? ";
	
	//Statements for dealing with the games that the teams play.
	protected static final String SELECT_GAME = "select id, " +
											    "week_id, " +
											    "home_team_id, " + 
											    "away_team_id, " + 
											    "winning_team_id " +
											    "from game ";
	
	protected static final String INSERT_GAME = "insert into game (week_id, home_team_id, away_team_id, winning_team_id) values (?, ?, ?, ?) ";
	
	protected static final String UPDATE_GAME = "update game " + 
												"set week_id = ?, " + 
												"home_team_id = ?, " + 
												"away_team_id = ?, " + 
												"winning_team_id = ? " + 
												"where id = ? ";
	
	//Statements for dealing with picks that people make.
	protected static final String SELECT_PICK = "select id, " +
												"game_id, " + 
												"player_id, " + 
												"team_id " + 
												"from pick ";
	
	protected static final String INSERT_PICK = "insert into pick (game_id, player_id, team_id) values (?, ?, ?) ";
	
	protected static final String UPDATE_PICK = "update pick " +
												"set game_id = ?, " +
												"player_id = ?, " +
												"team_id = ? " +
												"where id = ? ";
	
	/**
	 * 
	 * For the case where we want to remove a pick that somebody made.
	 * 
	 */
	protected static final String DELETE_PICK = "delete from pick " +
												"where game_id = ? " + 
												" and player_id = ? " +
												" and id = ? ";
	
	//Statements for dealing with the people... still don't quite like that people have ids.
	protected static final String SELECT_PLAYER = "select id, name from player ";
	
	protected static final String INSERT_PLAYER = "insert into player (name) values (?) ";
	
	protected static final String UPDATE_PLAYER = "update player " +
											  	  "set name = ? " + 
											  	  "where id = ? ";
	
	
	protected static final String SELECT_DIVISION = "select id, name, abbreviation from division ";
	
	protected static final String INSERT_DIVISION = "insert into division (name, abbreviation) values (?, ?) ";
	
	protected static final String UPDATE_DIVISION = "update division " +
											  	  	"set name = ?, " +
											  	  	    "abbreviation = ? " + 
											  	  	"where id = ? ";
	
	protected static final String SELECT_PLAYER_DIVISION = "select id, division_id, player_id, season_id from player_division ";
	
	protected static final String INSERT_PLAYER_DIVISION = "insert into player_division (division_id, player_id, season_id) values (?, ?, ?) ";
	
	protected static final String UPDATE_PLAYER_DIVISION = "update player_division " +
											  	  		   "set division_id = ?, " +
											  	  		       "player_id = ?, " +
											  	  		       "season_id = ? " +
											  	  		   "where id = ? ";

	/*
	
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
group by pick_totals.player_id, pick_totals.player_name ;
	
	 */
	//A statement that gets the records for people so that we do it all in one query instead of doing a lot of 
	//queries to pull that info out.  Here because it's faster to do that work on the database side than to
	//pull all the crap out and do it in java.
	protected static final String SELECT_RECORD = "select pick_totals.player_id, " + 
													     "pick_totals.player_name, " + 
													     "sum(pick_totals.wins) as wins, " + 
													     "sum(pick_totals.losses) as losses, " +
													     "sum(pick_totals.ties) as ties " +
												  "from (select pl.id as player_id, " + 
												 			   "pl.name as player_name, " +
												 			  //Count the game as a win if the picked team is the team that won the game.
												 			  "(case when p.team_id = g.winning_team_id " + 
												 			 	    "then 1 " + 
												 			 	    "else 0 " + 
												 			   "end) as wins, " + 
												 			  //Only count the pick as a loss if there was a pick.  If there wasn't (p.team_id = null), then
												 			  //don't count that as a loss.
												 			  "(case when g.winning_team_id != -1 and (p.team_id is not null and p.team_id != g.winning_team_id) " + 
												 			 	    "then 1 " + 
												 			 	    "else 0 " + 
												 			   "end) as losses, " + 
												 			  //If it was a tie, we don't care what they picked.
												 			  "(case when g.winning_team_id = -1 " + 
												 			 	    "then 1 " + 
												 			 	    "else 0 " +
												 			   "end) as ties " +
												 	    "from pick p join game g on p.game_id = g.id " + 
												 			 "join player pl on p.player_id = pl.id " +
												 	         "join team home_team on g.home_team_id = home_team.id " + 
												 			 "join team away_team on g.away_team_id = away_team.id " +
												 			  	   //This will be inserted later so that we only get the records we need.  Makes it
												 			  	   //so we can restrict on stuff like the season, the player, ....
												 			  	   "%s " + 
												 		") pick_totals " + 
												"group by pick_totals.player_id, pick_totals.player_name ";
	
	/**
	 * 
	 * This query will get the records that the players have in a week.  It'll let you put in the criteria
	 * for selecting the weeks and then group the records by their weeks in the select.  
	 * 
	 */
	protected static final String SELECT_WEEK_RECORDS = "select pick_totals.season_id, " + 
													 		   "pick_totals.year, " + 
													 		   "pick_totals.player_id, " + 
													 		   "pick_totals.player_name, " + 
													 		   "pick_totals.week_id, " + 
													 		   "pick_totals.sequence_number, " +
													 		   "pick_totals.week_type, " +
													 		   "pick_totals.week_key, " +
													 		   "pick_totals.week_label, " + 
													 		   "sum(pick_totals.wins) as wins, " + 
													 		   "sum(pick_totals.losses) as losses, " + 
													 		   "sum(pick_totals.ties) as ties " + 
													    "from (select pl.id as player_id, " + 
													 		 	  	 "pl.name as player_name, " + 
													 		 	  	 "s.id as season_id, " + 
													 		 	  	 "s.year as year, " + 
													 		 	  	 "w.id as week_id, " + 
													 		 	  	 "w.sequence_number as sequence_number, " +
													 		 	  	 "w.type as week_type, " + 
													 		 	  	 "w.key as week_key, " + 
													 		 	  	 "w.label as week_label, " + 
													 		 	  	 "(case when p.team_id = g.winning_team_id " + 
													 		 	  	       "then 1 " + 
													 		 	  	       "else 0 " + 
													 		 	  	 "end) as wins, " + 
													 		 	  	 "(case when g.winning_team_id != -1 and (p.team_id is not null and p.team_id != g.winning_team_id) " + 
													 		 	  	       "then 1 " + 
													 		 	  	       "else 0 " + 
													 		 	  	 "end) as losses, " + 
													 		 	  	 "(case when g.winning_team_id = -1 " + 
													 		 	  	       "then 1 " + 
													 		 	  	       "else 0 " + 
													 		 	     "end) as ties " + 
													 		  "from pick p join game g on p.game_id = g.id " + 
													 	           "join player pl on p.player_id = pl.id " + 
													 	           "join week w on g.week_id = w.id " + 
													 	           "join season s on w.season_id = s.id " + 
													 	           " %s " + 
													 	     ") pick_totals " + 
													    "group by pick_totals.season_id, year, pick_totals.player_id, pick_totals.player_name, pick_totals.week_id, pick_totals.sequence_number, pick_totals.week_type, pick_totals.week_key, pick_totals.week_label " + 
													    "order by year, sequence_number, player_name ";
	
	/**
	 * 
	 * A "base" query for selecting the records for weeks.  Doesn't do any ordering and expects to be ran as part of another query.
	 * 
	 */
	protected static final String SELECT_WEEK_RECORDS_BASE = "select pick_totals.season_id, " + 
															 		"pick_totals.year, " + 
															 		"pick_totals.player_id, " + 
															 		"pick_totals.player_name, " + 
															 		"pick_totals.week_id, " + 
															 		"pick_totals.sequence_number, " +
															 		"pick_totals.week_type, " + 
															 		"pick_totals.week_key, " +
															 		"pick_totals.week_label, " + 
															 		"sum(pick_totals.wins) as wins, " + 
															 		"sum(pick_totals.losses) as losses, " + 
															 		"sum(pick_totals.ties) as ties " + 
															 "from (select pl.id as player_id, " + 
															 		 	  "pl.name as player_name, " + 
															 		 	  "s.id as season_id, " + 
															 		 	  "s.year as year, " + 
															 		 	  "w.id as week_id, " + 
															 		 	  "w.sequence_number as sequence_number, " +
															 		 	  "w.type as week_type, " + 
															 		 	  "w.key as week_key, " +
															 		 	  "w.label as week_label, " + 
															 		 	  "(case when p.team_id = g.winning_team_id " + 
															 		 	  	    "then 1 " + 
															 		 	  	    "else 0 " + 
															 		 	  "end) as wins, " + 
															 		 	  "(case when g.winning_team_id != -1 and (p.team_id is not null and p.team_id != g.winning_team_id) " + 
															 		 	  	    "then 1 " + 
															 		 	  	    "else 0 " + 
															 		 	  "end) as losses, " + 
															 		 	  "(case when g.winning_team_id = -1 " + 
															 		 	  	    "then 1 " + 
															 		 	  	    "else 0 " + 
															 		 	  "end) as ties " + 
															 	   "from pick p join game g on p.game_id = g.id " + 
															 	        "join player pl on p.player_id = pl.id " + 
															 	        "join week w on g.week_id = w.id " + 
															 	        "join season s on w.season_id = s.id " + 
															 	        " %s " + 
															 	   ") pick_totals " + 
															"group by pick_totals.season_id, pick_totals.year, pick_totals.player_id, pick_totals.player_name, pick_totals.week_id, pick_totals.sequence_number, pick_totals.week_type, pick_totals.week_key, pick_totals.week_label ";
	
	/**
	 * 
	 * Gets the week records and orders them so that we can easily go through and pick out the best records for 
	 * a given week.
	 * 
	 */
	protected static final String SELECT_WEEK_RECORDS_ORDER_BY_WEEK_AND_RECORD = SELECT_WEEK_RECORDS_BASE + " order by year asc, sequence_number asc, wins desc, losses asc, player_name asc ";
	
	/**
	 * 
	 * This query will get the best weeks that we have for a group of players, weeks, years... or whatever.  It will calculate
	 * the win percentage for each week and use the wins in case of a tie.
	 * 
	 * First, it gets every pick and marks a 1 for each win or loss.  Then, it groups them by player and week and adds up the totals
	 * for each "mark" it made for each win or loss.  Then, gets the win percentage of each week and sorts by that.  So, it's 3 queries
	 * wrapped into one.
	 * 
	 */
	protected static final String SELECT_BEST_WEEKS = "select best_weeks.season_id, " + 
															 "best_weeks.year, " + 
															 "best_weeks.player_id, " + 
															 "best_weeks.player_name, " + 
															 "best_weeks.week_id, " +
															 "best_weeks.sequence_number, " +
															 "best_weeks.week_type, " + 
															 "best_weeks.week_key, " + 
															 "best_weeks.week_label, " + 
															 "best_weeks.wins, " + 
															 "best_weeks.losses, " + 
															 "best_weeks.ties, " + 
															 "best_weeks.number_of_games_in_week, " +
														     "round(cast(best_weeks.wins as decimal) / cast(best_weeks.number_of_picks as decimal), 3) as win_percentage, " +
														     "(case when number_of_games_in_week < 10 then round(cast(best_weeks.wins as decimal) / cast(best_weeks.number_of_games_in_week * 10 as decimal), 3) " +
														    	   "when number_of_picks < 8 then round(cast(best_weeks.wins as decimal) / cast(best_weeks.number_of_games_in_week * 20 as decimal), 3) " + 
														    	   "else round(cast(best_weeks.wins as decimal) / cast(best_weeks.number_of_picks as decimal), 3) " + 
														      "end) as xrank " +
															//Add up all the wins, losses and ties, so we have totals for the time period.
															"from (select pick_totals.season_id, " + 
																	     "pick_totals.year, " + 
																	     "pick_totals.player_id, " + 
																	     "pick_totals.player_name, " + 
																	     "pick_totals.week_id, " + 
																	     "pick_totals.sequence_number, " +
																	     "pick_totals.week_type, " + 
																	     "pick_totals.week_key, " +
																	     "pick_totals.week_label, " + 
																	     "sum(pick_totals.wins) as wins, " + 
																	     "sum(pick_totals.losses) as losses, " + 
																	     "sum(pick_totals.ties) as ties, " + 
																	     "(case when sum(pick_totals.wins) + sum(pick_totals.losses) = 0 then 1 " + 
																		       "else sum(pick_totals.wins) + sum(pick_totals.losses) " +
																	     "end) as number_of_picks, " +
																	     "(select count(id) from game gx where gx.week_id = pick_totals.week_id) as number_of_games_in_week " +
																 //The "base" query.  "Mark" each pick as a win or loss so the outer query can sum them up and
																 //get totals.
																 "from (select pl.id as player_id, " + 
																 	 		  "pl.name as player_name, " + 
																 	 		  "s.id as season_id, " + 
																 	 		  "s.year as year, " + 
																 	 		  "w.id as week_id, " + 
																 	 		  "w.sequence_number as sequence_number, " +
																 	 		  "w.type as week_type, " +
																 	 		  "w.key as week_key, " +
																 	 		  "w.label as week_label, " + 
																 	 		  "(case when p.team_id = g.winning_team_id " + 
																 	 		  "then 1 " + 
																 	 		  "else 0 " + 
																 	 		  "end) as wins, " + 
																 	 		  "(case when g.winning_team_id != -1 and (p.team_id is not null and p.team_id != g.winning_team_id) " + 
																 	 		  "then 1 " + 
																 	 		  "else 0 " + 
																 	 		  "end) as losses, " + 
																 	 		  "(case when g.winning_team_id = -1 " + 
																 	 		  "then 1 " + 
																 	 		  "else 0 " + 
																 	 		  "end) as ties " + 
																 	  "from pick p join game g on p.game_id = g.id " + 
																 	  	   "join player pl on p.player_id = pl.id " + 
																 	  	   "join week w on g.week_id = w.id " + 
																 	  	   "join season s on w.season_id = s.id " +
																 	  	   " %s " + 
																 	  ") pick_totals " + 
																"group by pick_totals.season_id, pick_totals.year, pick_totals.player_id, pick_totals.player_name, pick_totals.week_id, pick_totals.sequence_number, pick_totals.week_type, pick_totals.week_key, pick_totals.week_label " + 
													") best_weeks " + 
													"order by xrank desc, wins desc, losses desc, player_name asc ";
	
	/**
	 * 
	 * Gets the records for a period of time and orders them so that it's easy to pick out the person who
	 * has the best record for that period of time.  It marks each win or loss with a 1 and then adds up each
	 * to get the total number of wins or losses.  Then, it orders by year, wins, and player, so that the person
	 * with the best record for a period of time will be at the top of that "period" of time.
	 * 
	 * Like if the "period of time" is a year, then we can get the first person for a year and then skip to the next year to get
	 * the person for the next year.  
	 * 
	 * I made it so we could, hopefully, answer the question "who has the best record for period 'X'?" in a fast way.  I thought
	 * about doing this only in sql, but then I think I would have had to use the "rank" feature and that seems like it might not
	 * be "database independent" and might tie this to postgres.  There's probably a way to do it all in "ansi" sql, but I think
	 * doing it this way and then getting the best one for each period in java seems ok. 
	 * 
	 * 
	 */
	protected static final String SELECT_ORDERED_BEST_RECORDS = "select pick_totals.season_id, " + 
																   	   "pick_totals.year, " + 
																   	   "pick_totals.player_id, " + 
																   	   "pick_totals.player_name, " + 
																   	   "sum(pick_totals.wins) as wins, " + 
																   	   "sum(pick_totals.losses) as losses, " + 
																   	   "sum(pick_totals.ties) as ties " + 
														         "from (select pl.id as player_id, " + 
																		 	  "pl.name as player_name, " + 
																		 	  "s.id as season_id, " + 
																		 	  "s.year as year, " + 
																		 	  "w.id as week_id, " + 
																		 	  "w.sequence_number as sequence_number, " + 
																		 	  "w.key as week_key, " +
																		 	  "w.label as week_label, " + 
																		 	  "(case when p.team_id = g.winning_team_id " + 
																		 	  "then 1 " + 
																		 	  "else 0 " + 
																		 	  "end) as wins, " + 
																		 	  "(case when g.winning_team_id != -1 and (p.team_id is not null and p.team_id != g.winning_team_id) " + 
																		 	  "then 1 " + 
																		 	  "else 0 " + 
																		 	  "end) as losses, " + 
																		 	  "(case when g.winning_team_id = -1 " + 
																		 	  "then 1 " + 
																		 	  "else 0 " + 
																		 	  "end) as ties " + 
																	  "from pick p join game g on p.game_id = g.id " + 
																 	  	   "join player pl on p.player_id = pl.id " + 
																 	  	   "join week w on g.week_id = w.id " + 
																 	  	   "join season s on w.season_id = s.id " + 
																 	  " %s " +
																      ") pick_totals " + 
															    "group by season_id, year, pick_totals.player_id, pick_totals.player_name " + 
															    "order by year desc, wins desc, player_id ";
	
	
	
	/**
	 * 
	 * The same thing as getting the championships, but with the divisions included.
	 * 
	 */
	protected static final String SELECT_ORDERED_BEST_DIVISION_RECORDS = 
			"select pick_totals.season_id,  " + 
				   "pick_totals.year,  " + 
				   "pick_totals.division_id as division_id," + 
				   "pick_totals.division_name as division_name," + 
				   "pick_totals.division_abbreviation as division_abbreviation," + 
				   "pick_totals.player_id,  " + 
				   "pick_totals.player_name,  " + 
				   "sum(pick_totals.wins) as wins,  " + 
				   "sum(pick_totals.losses) as losses,  " + 
				   "sum(pick_totals.ties) as ties  " + 
			"from (select d.id as division_id," + 
				 		 "d.name as division_name," + 
				 		 "d.abbreviation as division_abbreviation," + 
				 		 "pl.id as player_id,  " + 
				 		 "pl.name as player_name,  " + 
				 		 "s.id as season_id,  " + 
				 		 "s.year as year,  " + 
				 		 "w.id as week_id,  " + 
				 		 "w.sequence_number as sequence_number,  " + 
				 		 "w.key as week_key, " + 
				 		 "w.label as week_label,  " + 
				 		 "(case when p.team_id = g.winning_team_id  " + 
				 		       "then 1  " + 
				 		       "else 0  " + 
				 		 "end) as wins,  " + 
				 		 "(case when g.winning_team_id != -1 and (p.team_id is not null and p.team_id != g.winning_team_id)  " + 
				 		 	   "then 1  " + 
				 		 	   "else 0  " + 
				 		 "end) as losses,  " + 
				 		 "(case when g.winning_team_id = -1  " + 
				 		       "then 1  " + 
				 		       "else 0  " + 
				 		 "end) as ties  " + 
				 "from pick p join game g on p.game_id = g.id  " + 
				      "join player pl on p.player_id = pl.id  " + 
				      "join week w on g.week_id = w.id  " + 
				      "join season s on w.season_id = s.id  " + 
				      "join player_division pd on pl.id = pd.player_id and s.id = pd.season_id " + 
				      "join division d on pd.division_id = d.id " +
				      " %s " +
			     ") pick_totals  " + 
			"group by season_id, year, pick_totals.division_id, pick_totals.division_name, pick_totals.division_abbreviation, " + 
				     "pick_totals.player_id, pick_totals.player_name  " + 
			"order by year desc, pick_totals.division_id, wins desc, player_id ";
	
	/**
	 * 
	 * This query will get the records for an entire season's worth of picks.  It'll order them by the total number of wins
	 * and by player name if there's a tie in the wins and losses.
	 * 
	 */
	protected static final String SELECT_SEASON_RECORDS = "select pick_totals.season_id, " + 
														  		 "pick_totals.year, " + 
														  		 "pick_totals.player_id, " + 
														  		 "pick_totals.player_name, " + 
														  		 "sum(pick_totals.wins) as wins, " + 
														  		 "sum(pick_totals.losses) as losses, " + 
														  		 "sum(pick_totals.ties) as ties " + 
														  "from (select pl.id as player_id, " + 
														  			   "pl.name as player_name, " + 
														  			   "s.id as season_id, " + 
														  			   "s.year as year, " + 
														  			   "w.id as week_id, " + 
														  			   "w.sequence_number as sequence_number, " + 
														  			   "w.key as week_key, " +
														  			   "w.label as week_label, " + 
														  			   "(case when p.team_id = g.winning_team_id " + 
														  			         "then 1 " + 
														  			         "else 0 " + 
														  			    "end) as wins, " + 
														  			   "(case when g.winning_team_id != -1 and (p.team_id is not null and p.team_id != g.winning_team_id) " + 
														  			   		 "then 1 " + 
														  			   		 "else 0 " + 
														  			   	"end) as losses, " + 
														  			   "(case when g.winning_team_id = -1 " + 
														  			   		 "then 1 " + 
														  			   		 "else 0 " + 
														  			    "end) as ties " + 
														  	    "from pick p join game g on p.game_id = g.id " + 
														  			 "join player pl on p.player_id = pl.id " + 
														  			 "join week w on g.week_id = w.id " + 
														  			 "join season s on w.season_id = s.id " + 
														  	    " %s " +
														  	") pick_totals " + 
														  	"group by season_id, year, pick_totals.player_id, pick_totals.player_name " + 
														  	"order by wins desc, losses asc, player_name asc ";
	
	/**
	 * 
	 * This monster of a query gets how accurate a player's picks were for a given team over a given time period.
	 * 
	 * First, it does a "cross join" of teams, players, and seasons so that we have something like this:
	 * 
	 * 		Jonathan	BUF		2016
	 * 		Tim			IND		2015
	 * 		...
	 * 
	 * So, basically, we get the set of all possible player, year, and team combinations, for a given "time period".  Like we can say "oh, only
	 * when the year is 2015, or only when the team is buffalo, or only when the player is Benny boy.  Then, it just goes through and "picks"
	 * out the the individual picks for each team, player and year from the "game" and "pick" tables using the player, team,
	 * and year in the set.
	 * 
	 * So, for example, if the player was Jonathan, the team Buffalo, and the year 2016, we'd want to find out these things:
	 * 
	 * 		1. How many wins did the Bills have in 2016?
	 * 		2. How many losses?
	 * 		3. How many times did Jonathan predict they would win?
	 * 		4. How many times did he predict they would lose?
	 * 		5. How many times was he right when picking they would win?
	 *		6. How many times was he wrong when picking they would win?
	 *		7. How many times was he right when picking they would lose?
	 *		8. How many times was he wrong when picking they would lose?
	 *
	 *		... Spoiler alert, he was never wrong and they never lost.
	 *
	 * To find out each one, we just have to look at the pick and game tables.  We can take the team and season and figure out
	 * how many times a team won or lost from the game table.  We can take the team, season, and player, go to the game
	 * and pick tables, and figure out how many times a player picked a team in a given season and whether that team won or 
	 * lost the game they picked.
	 * 
	 * It's almost like there are two steps:
	 * 
	 * 		1. Get the set of all possible "coordinates" (team, player, and year).
	 * 		2. Use each combination of coordinates to figure out the specific counts by going
	 * 		   into the pick and games tables with the ids of the coordinates (team id, player id, season id).
	 * 
	 */
	protected static final String OLD_AND_SLOW_SELECT_PICK_ACCURACY_SUMMARY = "select pick_accuracy_summary.player_id as player_id, " + 
																 	    "pick_accuracy_summary.player_name as player_name, " + 
																 	    "pick_accuracy_summary.team_id as team_id, " +
																 	    "pick_accuracy_summary.team_division_id as team_division_id, " +
																 	    "pick_accuracy_summary.team_city as team_city, " +
																 	    "pick_accuracy_summary.team_nickname as team_nickname, " +
																 	    "pick_accuracy_summary.team_abbreviation as team_abbreviation, " +
																 	    "sum(pick_accuracy_summary.actual_wins) as actual_wins, " + 
																 	    "sum(pick_accuracy_summary.actual_losses) as actual_losses, " +
																 	    "sum(pick_accuracy_summary.actual_ties) as actual_ties, " + 
																 	    "sum(pick_accuracy_summary.predicted_wins) as predicted_wins, " + 
																 	    "sum(pick_accuracy_summary.predicted_losses) as predicted_losses, " + 
																 	    "sum(pick_accuracy_summary.times_right) as times_right, " + 
																 	    "sum(pick_accuracy_summary.times_wrong) as times_wrong, " + 
																 	    "sum(pick_accuracy_summary.times_picked_to_win_right) as times_picked_to_win_right, " + 
																 	    "sum(pick_accuracy_summary.times_picked_to_win_wrong) as times_picked_to_win_wrong, " + 
																 	    "sum(pick_accuracy_summary.times_picked_to_lose_right) as times_picked_to_lose_right, " + 
																 	    "sum(pick_accuracy_summary.times_picked_to_lose_wrong) as times_picked_to_lose_wrong " + 
																 "from (select s.year, " + 
																 			  "pl.id as player_id, " + 
																 			  "pl.name as player_name, " + 
																 			  "t.id as team_id, " + 
																 			  "t.team_division_id as team_division_id, " +
																 			  "t.city as team_city, " + 
																 			  "t.nickname as team_nickname, " +
																 			  "t.abbreviation as team_abbreviation, " + 
																 			  //The number of wins for the team is the number of times the team with their id
																 			  //was the winning time in weeks that were in the season that we're on.
																 			  "(select count(*) " + 
																 			   "from game g " + 
																 			   "where (g.home_team_id = t.id or " + 
																 			    	  "g.away_team_id = t.id) " + 
																 			    	  "and (g.winning_team_id = t.id and g.winning_team_id != -1) " + 
																 			    	  "and g.week_id in (select w.id " + 
																 			    	  					"from week w " + 
																 			    	  					"where w.season_id = s.id %s ) " + 
																 			  ") as actual_wins, " + 
																 			  //Same deal with losses, except we need to count when it wasn't them and when
																 			  //it wasn't a tie (team id = -1).
																 			  "(select count(*) " + 
																 			   "from game g " + 
																 			   "where (g.home_team_id = t.id or " + 
																 			   	 	  "g.away_team_id = t.id) " + 
																 			   	 	  "and (g.winning_team_id != t.id and g.winning_team_id != -1) " + 
																 			   	 	  "and g.week_id in (select w.id " + 
																 			   	 	  					"from week w " + 
																 			   	 	  					"where w.season_id = s.id %s ) " + 
																 			  ") as actual_losses, " + 
																 			  //With ties, it's just the number of times they were in a game where the winning team was "-1".
																 			  "(select count(*) " + 
																 			   "from game g " + 
																 			   "where (g.home_team_id = t.id or " + 
																 			   	      "g.away_team_id = t.id) " + 
																 			   	      "and g.winning_team_id = -1 " + 
																 			   	      "and g.week_id in (select w.id " + 
																 			   	      				    "from week w " + 
																 			   	      				    "where w.season_id = s.id %s ) " + 
																 			  ") as actual_ties, " + 
																 			  //To get the predicted wins, we just have to look at the picks and see
																 			  //how many times the player we're on picked the team we're on to win in 
																 			  //a week that's in the season we're on.
																 			  //
																 			  //We have 3 "coordinates" at this point: team id, player id, season id.
																 			  //We just have to go and do a normal query to see how many times the player
																 			  //picked the team to win in the season.
																 			  //If the game ended in a tie, we don't want to count that since that doesn't go
																 			  //as a win or loss.
																 			  "(select count(*)  " + 
																 			   "from pick p join game g on p.game_id = g.id " + 
																 			   "where p.player_id = pl.id " + 
																 			   		 "and p.team_id = t.id " + 
																 			   		 "and g.winning_team_id != -1 " +
																 			   		 "and g.week_id in (select w.id " + 
																 			   		 				   "from week w " + 
																 			   		 				   "where w.season_id = s.id %s ) " + 
																 			  ") as predicted_wins, " + 
																 			  //With losses, they didn't pick the team, so we can't use the team id to go
																 			  //directly into the pick table.  Instead, we have to go through the game
																 			  //table and get the game that involves the team they picked (whether they're the
																 			  //home or away team), and then go to the pick table with that game.
																 			  //If the game ended in a tie, we don't want to count that since that doesn't go
																 			  //as a win or loss.
																 			  "(select count(*)  " + 
																 			   "from pick p join game g on p.game_id = g.id " + 
																 			   "where p.player_id = pl.id " + 
																 			   		 "and p.team_id != t.id " + 
																 			   		 "and (g.home_team_id = t.id or g.away_team_id = t.id) " +
																 			   		 "and g.winning_team_id != -1 " +
																 			   		 "and g.week_id in (select w.id " + 
																 			   		 				   "from week w " + 
																 			   		 				   "where w.season_id = s.id %s ) " + 
																 			  ") as predicted_losses, " +
																 			  //The number of times they were right is the number of times the game involved the team
																 			  //we're on, they picked that team, and the week is in the season we're on.
																 			  //If the game ended in a tie, we don't want to count that since that doesn't go
																 			  //as a win or loss.
																 			  "(select count(*)  " + 
																 			   "from pick p join game g on p.game_id = g.id " + 
																 			   "where p.player_id = pl.id " + 
																 			   		 "and (g.home_team_id = t.id or g.away_team_id = t.id) " + 
																 			   		 "and g.winning_team_id = p.team_id " +
																 			   		 "and g.winning_team_id != -1 " +
																 			   		 "and g.week_id in (select w.id " + 
																 			   		 				   "from week w " + 
																 			   		 				   "where w.season_id = s.id %s ) " + 
																 			  ") as times_right, " + 
																 			  //The number of times they were wrong is the number of times the game involved the 
																 			  //team we're on, the winning team isn't the team they picked, and the game is in
																 			  //a week that's in the season we're on.
																 			  //If the game ended in a tie, we don't want to count that since that doesn't go
																 			  //as a win or loss.
																 			  "(select count(*)  " + 
																 			   "from pick p join game g on p.game_id = g.id " + 
																 			   "where p.player_id = pl.id " + 
																 			   		 "and (g.home_team_id = t.id or g.away_team_id = t.id) " + 
																 			   		 "and g.winning_team_id != p.team_id " +
																 			   		 "and g.winning_team_id != -1 " + 
																 			   		 "and g.week_id in (select w.id " + 
																 			   		 				   "from week w " + 
																 			   		 				   "where w.season_id = s.id %s ) " + 
																 			  ") as times_wrong, " + 
																 			  //The number of times they picked a team to win and they were right is when
																 			  //they picked the team, the team won the game, and the week is in the season we're on.
																 			  //If the game ended in a tie, we don't want to count that since that doesn't go
																 			  //as a win or loss.
																 			  "(select count(*)  " + 
																 			   "from pick p join game g on p.game_id = g.id " + 
																 			   "where p.player_id = pl.id " + 
																 			   		 "and p.team_id = t.id " +
																 			   		 "and g.winning_team_id != -1 " +
																 			   		 "and g.week_id in (select w.id " + 
																 			   		 				   "from week w " + 
																 			   		 				   "where w.season_id = s.id %s ) " + 
																 			   		 "and g.winning_team_id = p.team_id " + 
																 			  ") as times_picked_to_win_right, " + 
																 			  //The number of times they picked a team to win and they were wrong is when they
																 			  //picked the team, the team didn't win the game, it wasn't a tie, and the game 
																 			  //is in the season we're on.
																 			  //If the game ended in a tie, we don't want to count that since that doesn't go
																 			  //as a win or loss.
																 			  "(select count(*)  " + 
																 			   "from pick p join game g on p.game_id = g.id " + 
																 			   "where p.player_id = pl.id " + 
																 			   	     "and p.team_id = t.id " +
																 			   	     "and g.week_id in (select w.id " + 
																 			   	     				   "from week w " + 
																 			   	     				   "where w.season_id = s.id %s ) " + 
																 			   	     "and g.winning_team_id != p.team_id " + 
																 			   	     "and g.winning_team_id != -1 " +
																 			  ") as times_picked_to_win_wrong, " +
																 			  //The number of times they picked a team to lose and were right is when the game
																 			  //involves the team we're on, their pick for the game wasn't the team we're on,
																 			  //and the winning team isn't the team we're on (and it wasn't a tie).
																 			  //If the game ended in a tie, we don't want to count that since that doesn't go
																 			  //as a win or loss.
																 			  "(select count(*)  " + 
																 			   "from pick p join game g on p.game_id = g.id " + 
																 			   "where p.player_id = pl.id " + 
																 			     	 "and p.team_id != t.id " + 
																 			     	 "and (g.home_team_id = t.id or g.away_team_id = t.id) " + 
																 			     	 "and g.week_id in (select w.id " + 
																 			     	 				   "from week w " + 
																 			     	 				   "where w.season_id = s.id  %s ) " + 
																 			     	 "and g.winning_team_id != t.id " +
																 			     	 "and g.winning_team_id != -1 " +
																 			  ") as times_picked_to_lose_right, " + 
																 			  //The number of times they picked a team to lose and were wrong is when the game
																 			  //involves the team we're on, they didn't pick that team to win, the team won anyway,
																 			  //and the week is in the season we're on.
																 			  //If the game ended in a tie, we don't want to count that since that doesn't go
																 			  //as a win or loss.
																 			  "(select count(*)  " + 
																 			   "from pick p join game g on p.game_id = g.id " + 
																 			   "where p.player_id = pl.id " + 
																 			   	 	 "and p.team_id != t.id " + 
																 			   	 	 "and g.winning_team_id != -1 " +
																 			   	 	 "and (g.home_team_id = t.id or g.away_team_id = t.id) " + 
																 			   	 	 "and g.week_id in (select w.id " + 
																 			   	 	 				   "from week w " + 
																 			   	 	 				   "where w.season_id = s.id %s ) " + 
																 			   	 	 "and g.winning_team_id = t.id " + 
																 			   ") as times_picked_to_lose_wrong " + 
																 	    //These "cross joins" give us the "cartesian product" of all the players, teams, and season.
																 		//This basically gives us the set of coordinates and we just have to take each "coordinate"
																 	    //(each team, player, and season) and use it to do the counts.
																 	    "from team t cross join player pl cross join season s " +
																 	    //We only want teams paired with the years that they were active in.
																 	    "where (t.end_year is null or t.end_year > s.year) " +
																 	    //This is so we can add in a filter for only a certain teams, players, or seasons.
																 	    " %s " + 
																		") pick_accuracy_summary " +
																 //And we want to filter out teams on a bye week (we can do that by requiring them to have at least 1 
																 //win or loss.
																 "where (pick_accuracy_summary.actual_wins > 0 or pick_accuracy_summary.actual_losses > 0) " + 
																 //We want everything per player and team, so make sure we group by that so that we get counts
															     //for a player's picks for a particular team.
															     "group by player_id, player_name, team_id, team_city, team_nickname, team_abbreviation, team_division_id ";

	/**
	 * 
	 * THIS is the NEW AND IMPROVED version of the pick accuracy query.  The goal is to 
	 * try to figure out how "accurate" each person's picks for a particular team were.  Like, I guess
	 * it shows up in the standings (because that's the whole point), but I figured it would be kind
	 * of neat to see whether somebody had a particular team's number or not.  Like, was somebody on the
	 * money when picking the saints to lose or houston to win? ... Stuff like this...
	 * 
	 * Imagine player was Jonathan, the team Buffalo, and the year 2016, we'd want to find out these things:
	 * 
	 * 		1. How many wins did the Bills have in 2016?
	 * 		2. How many losses?
	 * 		3. How many times did Jonathan predict they would win?
	 * 		4. How many times did he predict they would lose?
	 * 		5. How many times was he right when picking they would win?
	 *		6. How many times was he wrong when picking they would win?
	 *		7. How many times was he right when picking they would lose?
	 *		8. How many times was he wrong when picking they would lose?
	 *
	 *		... Spoiler alert, he was never wrong and they never lost.
	 * 
	 * This query will get all of the numbers we need to show that stuff ... and do it faster than we've done
	 * it before.  It's an improvment over the original in two places:
	 * 
	 * 		1. It just does a "cross join" of the team and player instead of the team, player, and season.
	 * 		2. It does that "cross join" through a materialized view instead of doing it every time we run the query.
	 * 
	 * So, basically, we get a combination of every team and player through a "materialized view" which was created
	 * by "cross joining" all the teams and all the players.  Then, we just use each combination of team and player (each
	 * row in that view) to answer the questions above by just going down into joins of the picks and games tables to
	 * figure out each one.  It tries to use the same idea as the original query (get "coordinates" and use those to get
	 * individual couns), but with an "improved strategy" for getting the "coordinates" (materialized view of just the team
	 * and player instead of a cross join of the team, player, and season tables).
	 * 
	 */
	protected static final String PICK_ACCURACY_SUMMARY_BETTER2 = 
			//These are the numbers we want to pick out for every person and team.
			"select pick_accuracy_summary.player_id as player_id, " + 
				  " pick_accuracy_summary.player_name as player_name, " + 
				  " pick_accuracy_summary.team_id as team_id, " + 
				  " pick_accuracy_summary.team_division_id as team_division_id, " + 
				  " pick_accuracy_summary.team_name as team_city, " + 
				  " pick_accuracy_summary.team_nickname as team_nickname, " + 
				  " pick_accuracy_summary.team_abbreviation as team_abbreviation, " +
				  " sum(pick_accuracy_summary.actual_wins) as actual_wins, " + 
				  " sum(pick_accuracy_summary.actual_losses) as actual_losses, " + 
				  " sum(pick_accuracy_summary.actual_ties) as actual_ties, " + 
				  " sum(pick_accuracy_summary.predicted_wins) as predicted_wins, " + 
				  " sum(pick_accuracy_summary.predicted_losses) as predicted_losses, " + 
				  " sum(pick_accuracy_summary.times_right) as times_right, " + 
				  " sum(pick_accuracy_summary.times_wrong) as times_wrong, " + 
				  " sum(pick_accuracy_summary.times_picked_to_win_right) as times_picked_to_win_right, " + 
				  " sum(pick_accuracy_summary.times_picked_to_win_wrong) as times_picked_to_win_wrong, " + 
				  " sum(pick_accuracy_summary.times_picked_to_lose_right) as times_picked_to_lose_right, " + 
				  " sum(pick_accuracy_summary.times_picked_to_lose_wrong) as times_picked_to_lose_wrong " +
					//The "atapv" is "all_team_all_player_view".  The "materialized view" that was made by cross joining
					//the team and player tables.
			"from (select atapv.player_id as player_id, " + 
					    " atapv.player_name as player_name, " + 
					    " atapv.team_id as team_id, " + 
					    " atapv.team_division_id as team_division_id, " + 
					    " atapv.city as team_name, " + 
					    " atapv.nickname as team_nickname, " + 
					    " atapv.abbreviation as team_abbreviation, " +
						//The number of times a team won is how many times they were the winning team
						//in games that the person picked somebody.
						//I don't use all the team's games here so that the "actual" only includes
						//games where they made a pick.  That way the "actual" vs predicted use the
						//same games.
						" (select count(*) " + 
						 //Join on pick to make sure the player made a pick in the game.  If they didn't, we don't
						 //want to count it.
						 " from game g join pick p on g.id = p.game_id " + 
						 " where p.player_id = atapv.player_id " +
						 	   //The current team was involved in the game.
						       " and (g.home_team_id = atapv.team_id or g.away_team_id = atapv.team_id) " +
						 	   //The current team won the game.
						       " and g.winning_team_id = atapv.team_id " +
						       //The "week and season where clause" depends on the years and weeks they picked.
						       //This will restrict it to just games in those weeks and seasons.
						       " and g.week_id in (select w.id from week w ${WEEK_AND_SEASON_WHERE_CLAUSE}) " + 
					    " ) as actual_wins, " + 
					    //Same thing for the losses.
					    " (select count(*) " + 
					     " from game g join pick p on g.id = p.game_id " + 
					     " where p.player_id = atapv.player_id " +
					     	   //The current team was involved in the game.
					           " and (g.home_team_id = atapv.team_id or g.away_team_id = atapv.team_id) " +
					     	   //The current team didn't win the game and it wasn't a tie.
					           " and (g.winning_team_id != atapv.team_id and g.winning_team_id != -1) " + 
					           " and g.week_id in (select w.id from week w ${WEEK_AND_SEASON_WHERE_CLAUSE}) " + 
					    " ) as actual_losses, " + 
					    //And the ties.
					    " (select count(*) " + 
					     " from game g join pick p on g.id = p.game_id " + 
					     " where p.player_id = atapv.player_id " + 
					     	   //The current team was involved in the game.
					           " and (g.home_team_id = atapv.team_id or g.away_team_id = atapv.team_id) " +
					     	   //It was a tie.
					           " and g.winning_team_id = -1 " + 
					           " and g.week_id in (select w.id from week w ${WEEK_AND_SEASON_WHERE_CLAUSE}) " + 
					    " ) as actual_ties, " +
					    //The predicted wins are the number of times the player picked the team, whether they
					    //won or lost, so we don't look at the winning team id here.
						" (select count(*) " + 
						 " from pick p join game g on p.game_id = g.id " + 
						 " where p.player_id = atapv.player_id " +
						 	   //The "current" team is the team they picked.
						       " and p.team_id = atapv.team_id " + 
						       " and g.week_id in (select w.id from week w ${WEEK_AND_SEASON_WHERE_CLAUSE}) " + 
					    " ) as predicted_wins, " + 
					    //The predicted losses are the number of times the player picked the other team in a game
					    //where the current team (atapv.team_id) was involved.
					    " (select count(*) " + 
					     " from pick p join game g on p.game_id = g.id " + 
					     " where p.player_id = atapv.player_id " + 
					     	   //The "current team" was involved in the game.
					     	   " and (g.home_team_id = atapv.team_id or g.away_team_id = atapv.team_id) " +
					     	   //They picked the other team.
					     	   " and p.team_id != atapv.team_id " + 
					           " and g.week_id in (select w.id from week w ${WEEK_AND_SEASON_WHERE_CLAUSE}) " + 
					    " ) as predicted_losses, " + 
					    //The number of times they were right comes from the number of times they picked
					    //the current team to win a game and that team won.
						" (select count(*) " + 
						 " from pick p join game g on p.game_id = g.id " + 
						 " where p.player_id = atapv.player_id " +
						 	   //The "current" team was involved in the game.
						       " and (g.home_team_id = atapv.team_id or g.away_team_id = atapv.team_id) " +
						 	   //The winning team was the one they picked.
						       " and g.winning_team_id = p.team_id " + 
						       " and g.week_id in (select w.id from week w ${WEEK_AND_SEASON_WHERE_CLAUSE}) " + 
						 " ) as times_right, " +
						 //The number of times they were wrong comes from the number of times they picked
						 //the current team to win a game and that team lost.
						 " (select count(*) " + 
						  " from pick p join game g on p.game_id = g.id " + 
						  " where p.player_id = atapv.player_id " +
						  		//The "current" team was involved in the game.
						        " and (g.home_team_id = atapv.team_id or g.away_team_id = atapv.team_id) " +
						  		//The winning team wasn't the team they picked.
						        " and g.winning_team_id != p.team_id " + 
						        //...And there wasn't a tie.
						        " and g.winning_team_id != -1 " + 
						        " and g.week_id in (select w.id from week w ${WEEK_AND_SEASON_WHERE_CLAUSE}) " + 
						 " ) as times_wrong, " +
						 //The number of times they picked the current team to win and they were right is the number
						 //of times the current team was their pick and also the winning team for the game.
						 " (select count(*) " + 
						  " from pick p join game g on p.game_id = g.id " + 
						  " where p.player_id = atapv.player_id " + 
						  		//They picked the current team.
						        " and p.team_id = atapv.team_id " +
						  		//That team won the game.
						        " and g.winning_team_id = p.team_id " +
						        " and g.week_id in (select w.id from week w ${WEEK_AND_SEASON_WHERE_CLAUSE}) " +
						 " ) as times_picked_to_win_right, " +
						 //The number of times they picked the current team to win and were wrong is the number of
						 //times the current team was their pick and the winning team wasn't that team (and it wasn't a tie).
						 " (select count(*) " + 
						  " from pick p join game g on p.game_id = g.id " + 
						  " where p.player_id = atapv.player_id " + 
						  		//They picked the current team to win.
						        " and p.team_id = atapv.team_id " + 
						  		//The other team won.
						        " and g.winning_team_id != p.team_id " +
						  		//It wasn't a tie.
						        " and g.winning_team_id != -1 " + 
						        " and g.week_id in (select w.id from week w ${WEEK_AND_SEASON_WHERE_CLAUSE}) " +
						 " ) as times_picked_to_win_wrong, " +
						 //The number of times they picked the current team to lose and were right is the number of times
						 //the current team was involved in the game, they picked the other team, the current team didn't
						 //win and it wasn't a tie.
						 " (select count(*) " + 
						  " from pick p join game g on p.game_id = g.id " + 
						  " where p.player_id = atapv.player_id " + 
						  		//The current team was involved in the game.
						  	    " and (g.home_team_id = atapv.team_id or g.away_team_id = atapv.team_id) " +
						  		//They picked the other team.
						  		" and p.team_id != atapv.team_id " +
						  		//The current team didn't win.
						  	    " and g.winning_team_id != atapv.team_id " +
						  		//The game didn't end in a tie.
						  	    " and g.winning_team_id != -1 " +
						  	    " and g.week_id in (select w.id from week w ${WEEK_AND_SEASON_WHERE_CLAUSE}) " + 
						 " ) as times_picked_to_lose_right, " +
						 //The number of times they picked a team to lose and were wrong is the number of times
						 //the current team was involved in the game, they picked the other team, and the current team won.
						 " (select count(*) " + 
						  " from pick p join game g on p.game_id = g.id " + 
						  " where p.player_id = atapv.player_id " +
						  		//The current team was involved in the game.
						  	    " and (g.home_team_id = atapv.team_id or g.away_team_id = atapv.team_id) " +
						  		//They picked the other team.
						        " and p.team_id != atapv.team_id " +
						  		//The current team won.
						        " and g.winning_team_id = atapv.team_id " +
						        " and g.week_id in (select w.id from week w ${WEEK_AND_SEASON_WHERE_CLAUSE}) " +
						 " ) as times_picked_to_lose_wrong " +
				    //This is the "materialized view" that has all the combinations of the teams and players.  This way
				    //we just have to do that query once (when we're creating the views at the beginning of the season
				    //and we can reuse it the whole time.  It will automatically use the indexes just like it would if
				    //we were querying the tables themselves.  Pretty neat.
					" from all_team_all_player_view atapv " +
				    //And we might want to restrict the teams an players based on what they picked (just like we do the weeks
					//and seasons.
					" ${PLAYER_AND_TEAM_WHERE_CLAUSE} " + 
					" ) pick_accuracy_summary " +
					//And we want the totals by player and team, so group by all the columns we want to show for them.
					"group by player_id, player_name, team_id, team_name, team_nickname, team_abbreviation, team_division_id ";
	
	/**
	 * 
	 * This query will get the "pick splits" so that we can see which people picked which teams 
	 * and then show the splits.  The grouping will be done by the function that runs the query because I
	 * didn't want to do some "listagg" thing that might link it to some specific function in the database
	 * that might not work in another type of database, so that's why the grouping is done in java instead
	 * of in the query.
	 * 
	 */
	protected static final String SELECT_PICK_SPLIT_BASE = "select s.year as year, " + 
																  "w.sequence_number as sequence_number, " +
																  "w.type as week_type, " + 
																  "w.key as week_key, " +
																  "w.label as week_label, " + 
																  "g.id as game_id, " +
																  "g.winning_team_id as winning_team_id, " +
																  "home_team.abbreviation as home_team, " + 
																  "away_team.abbreviation as away_team, " + 
																  "winning_team.abbreviation as winning_team, " +
																  "pl.id as player_id, " + 
																  "pl.name as player, " + 
																  "pick_team.abbreviation as pick_team " + 
														  "from pick p join game g on p.game_id = g.id " + 
														  	   "join week w on g.week_id = w.id " + 
														  	   "join season s on w.season_id = s.id " + 
														  	   "join player pl on p.player_id = pl.id " + 
														  	   "join team home_team on g.home_team_id = home_team.id " + 
														  	   "join team away_team on g.away_team_id = away_team.id " + 
														  	   "left outer join team winning_team on g.winning_team_id = winning_team.id " + 
														  	   "left outer join team pick_team on p.team_id = pick_team.id ";
	
	/**
	 * 
	 * The "base" select (without the "from") for getting "compact" picks out.
	 * 
	 */
	protected static final String SELECT_COMPACT_PICK_BASE = "select s.year as year, " + 
																	"w.sequence_number as sequence_number, " +
																	"w.type as week_type, " +
																	"w.key as week_key, " +
																	"w.label as week_label, " +
																	"home_team.abbreviation as home_team_abbreviation, " + 
																	"away_team.abbreviation as away_team_abbreviation, " +
																	"(case when g.winning_team_id = -1 then 'TIE' " +
																		  "else winning_team.abbreviation " +
																    "end) as winning_team_abbreviation ";
	
	/**
	 * 
	 * Gets the record for the players by division.
	 * 
	 */
	protected static final String SELECT_DIVISION_RECORD = "select pick_totals.division_id, " + 
																  "pick_totals.division, " + 
																  "pick_totals.player_id, " + 
																  "pick_totals.player_name, " + 
																  "sum(pick_totals.wins) as wins, " + 
																  "sum(pick_totals.losses) as losses, " + 
																  "sum(pick_totals.ties) as ties " + 
														   "from (select d.id as division_id, " + 
																	    "d.abbreviation as division, " + 
																	    "pl.id as player_id, " + 
																	    "pl.name as player_name, " + 
																	    "(case when p.team_id = g.winning_team_id " + 
																	          "then 1  " + 
																	          "else 0 " + 
																	    "end) as wins, " + 
																	    "(case when g.winning_team_id != -1 and (p.team_id is not null and p.team_id != g.winning_team_id) " + 
																	          "then 1 " + 
																	          "else 0 " + 
																	    "end) as losses, " + 
																	    "(case when g.winning_team_id = -1 " + 
																	          "then 1 " + 
																	          "else 0 " + 
																	    "end) as ties " + 
																"from pick p join game g on p.game_id = g.id " + 
																     "join player pl on p.player_id = pl.id " + 
																     "join team home_team on g.home_team_id = home_team.id  " + 
																     "join team away_team on g.away_team_id = away_team.id " + 
																     "join week w on g.week_id = w.id " + 
																     "join season s on w.season_id = s.id " + 
																     "join player_division pd on pl.id = pd.player_id and pd.season_id = s.id " + 
																     "join division d on pd.division_id = d.id " +
															    " %s " +
															    ") pick_totals " + 
													       "group by pick_totals.player_id, pick_totals.player_name, pick_totals.division_id, pick_totals.division ";
	
	/**
	 * 
	 * Usually pays off to have an empty constructor.  If you use this one, you should
	 * set the data source before doing anything.
	 * 
	 */
	public OldNFLPicksDataService(){
	}
	
	/**
	 * 
	 * Makes a data service that'll use the given data source to pull
	 * the data.
	 * 
	 * @param dataSource
	 */
	public OldNFLPicksDataService(DataSource dataSource){
		setDataSource(dataSource);
	}
	
	/**
	 * 
	 * This function gets full versions of all the season objects.  Not much to it.
	 * 
	 * @return
	 */
	public List<Season> getSeasons(){
		
		//Steps to do:
		//	1. Run the query and map all the season results.
		
		List<Season> seasons = getSeasons(false);
		
		return seasons;
	}
	
	/**
	 * 
	 * This function will get all the seasons for all the years.
	 * If shallow is true, it won't fill them out with the weeks and games.
	 * If it's false, it'll fill out all the objects so you'll get all the
	 * games and weeks.
	 * 
	 * @param shallow
	 * @return
	 */
	public List<Season> getSeasons(boolean shallow){
		
		//Steps to do:
		//	1. Run the query and map all the season results.
		
		List<Season> seasons = new ArrayList<Season>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(SELECT_SEASON);
			results = statement.executeQuery();
			
			while (results.next()){
				Season season = mapSeason(results, shallow);
				seasons.add(season);
			}
		}
		catch (Exception e){
			log.error("Error getting seasons!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return seasons;
	}
	
	/**
	 * 
	 * Gets the season with the given id.  It will do a "full" get (not a shallow one), so
	 * it'll come back with all the weeks and games.
	 * 
	 * @param id
	 * @return
	 */
	public Season getSeason(int id){
		
		Season season = getSeason(id, false);
		
		return season;
	}
	
	/**
	 * 
	 * Gets the season with the given id.  If "shallow" is true, it just gets that season.
	 * If it's false, it will get all the weeks and games in the season too.
	 * 
	 * @param id
	 * @param shallow
	 * @return
	 */
	public Season getSeason(int id, boolean shallow){
		
		//Steps to do:
		//	1. Run the query and map the results.
		//	2. That's it.  The map function takes care of the deep retrieve
		//	   if it needs to do that.
		
		Season season = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			String query = SELECT_SEASON + 
						   "where id = ? ";
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			results = statement.executeQuery();
			
			if (results.next()){
				season = mapSeason(results, shallow);
			}
		}
		catch (Exception e){
			log.error("Error getting season! id = " + id + ", shallow = " + shallow, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return season;
	}
	
	/**
	 * 
	 * Gets the full conference object for the conference with the given name.  If you want
	 * just the conference, call the other function and set shallow to true.
	 * 
	 * @param name
	 * @return
	 */
	public TeamConference getTeamConference(String name){
		
		TeamConference conference = getTeamConference(name, false);
		
		return conference;
	}
	
	/**
	 * 
	 * Gets the conference with the give name from the database.  If shallow is false, it'll
	 * get the divisions and teams in the conference too.  If it's true, it won't.
	 * 
	 * @param name
	 * @param shallow
	 * @return
	 */
	public TeamConference getTeamConference(String name, boolean shallow){
		
		TeamConference conference = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement(SELECT_TEAM_CONFERENCE);
			results = statement.executeQuery();
			
			if (results.next()){
				conference = mapTeamConferenceResult(results, shallow);
			}
		}
		catch (Exception e){
			log.error("Error getting conferences!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return conference;
	}
	
	/**
	 * 
	 * This function will save the given conference to the database.  If it has an id,
	 * it'll do an update.  If it doesn't, it'll do an insert.
	 * 
	 * The conference's name is expected to be unique, so it'll use that to get the conference
	 * out after it's saved and return the conference it finds.
	 * 
	 * It will return a "shallow" version of the conference.
	 * 
	 * @param conference
	 * @return
	 */
	public TeamConference saveTeamConference(TeamConference conference){
		
		//Steps to do:
		//	1. Pull out the id.
		//	2. If it's not a real id, to an insert.
		//	3. Otherwise, do an update.
		//	4. After it's saved, get the conference out by its name.
		
		int id = conference.getId();
		
		int numberOfAffectedRows = 0;
		
		if (id <= 0){
			numberOfAffectedRows = insertTeamConference(conference);
		}
		else {
			numberOfAffectedRows = updateTeamConference(conference);
		}
		
		TeamConference savedConference = null;
		
		if (numberOfAffectedRows == 1){
			savedConference = getTeamConference(conference.getName(), true);
		}
		
		return savedConference;
	}
	
	/**
	 * 
	 * Does a "shallow" insert of the given conference.  Not much to it.
	 * Doesn't insert the divisions or teams in the conference.
	 * 
	 * @param conference
	 * @return
	 */
	protected int insertTeamConference(TeamConference conference){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();

			statement = connection.prepareStatement(INSERT_TEAM_CONFERENCE);
			statement.setString(1, conference.getName());
			statement.setString(2, conference.getStartYear());
			statement.setString(3, conference.getEndYear());
			statement.setString(4, conference.getCurrentName());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error inserting conference!  conference = " + conference, e);
			rollback(connection);
		}
		finally {
			close(statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * Updates the conference record for the given conference using its id.
	 * Not much to it.
	 * 
	 * @param conference
	 * @return
	 */
	protected int updateTeamConference(TeamConference conference){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(UPDATE_TEAM_CONFERENCE);
			statement.setString(1, conference.getName());
			statement.setString(2, conference.getStartYear());
			statement.setString(3, conference.getEndYear());
			statement.setString(4, conference.getCurrentName());
			statement.setInt(5, conference.getId());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error updating conference!  conference = " + conference, e);
			rollback(connection);
		}
		finally {
			close(statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * This function will get all the conferences with them all
	 * filled in with the divisions and teams.
	 * 
	 * @return
	 */
	public List<TeamConference> getTeamConferences(){
		
		List<TeamConference> conferences = getTeamConferences(false);
		
		return conferences;
	}
	
	/**
	 * 
	 * This function will get the conferences.  If shallow is false, it'll
	 * get all the divisions and teams in each division too.  If it's true, it'll
	 * just get the conferences themselves.
	 * 
	 * @param shallow
	 * @return
	 */
	public List<TeamConference> getTeamConferences(boolean shallow){
		
		//Steps to do:
		//	1. Run the query.
		//	2. Map the results.
		
		List<TeamConference> conferences = new ArrayList<TeamConference>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement(SELECT_TEAM_CONFERENCE);
			results = statement.executeQuery();
			
			while (results.next()){
				TeamConference conference = mapTeamConferenceResult(results, shallow);
				conferences.add(conference);
			}
		}
		catch (Exception e){
			log.error("Error getting conferences!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return conferences;
	}
	
	/**
	 * 
	 * This function will turn the result of a query to the conference table (for all the columns) into
	 * an object.  If "shallow" is false, it'll go through and get all the divisions in the conference
	 * too.
	 * 
	 * @param results
	 * @param shallow
	 * @return
	 * @throws SQLException
	 */
	protected TeamConference mapTeamConferenceResult(ResultSet results, boolean shallow) throws SQLException {
		
		//Steps to do:
		//	1. Make the conference object.
		//	2. Add in the divisions if it's not supposed to be shallow.
		
		TeamConference conference = new TeamConference();
		conference.setId(results.getInt("id"));
		conference.setName(results.getString("name"));
		conference.setCurrentName(results.getString("current_name"));
		conference.setStartYear(results.getString("start_year"));
		conference.setEndYear(results.getString("end_year"));
		
		if (!shallow){
			int conferenceId = results.getInt("id");
			List<TeamDivision> divisions = getTeamDivisionsInTeamConference(conferenceId, shallow);
			conference.setDivisions(divisions);
		}
		
		return conference;
	}
	
	/**
	 * 
	 * Gets the division in the given conference with the given name.  Gets the full
	 * version (sets shallow to false).
	 * 
	 * @param conferenceId
	 * @param name
	 * @return
	 */
	public TeamDivision getTeamDivision(int conferenceId, String name){
		
		TeamDivision division = getTeamDivision(conferenceId, name, false);
		
		return division;
	}
	
	/**
	 * 
	 * Gets the division with the given name in the given conference.  If shallow is false, it'll
	 * get all the teams in the division too.  If it's true, it'll just get the division by itself.
	 * 
	 * @param conferenceId
	 * @param name
	 * @param shallow
	 * @return
	 */
	public TeamDivision getTeamDivision(int conferenceId, String name, boolean shallow){
		
		TeamDivision division = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		String query = SELECT_TEAM_DIVISION + 
					   "where name = ? " + 
					         "and team_conference_id = ? ";
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, name);
			statement.setInt(2, conferenceId);
			
			results = statement.executeQuery();
			
			if (results.next()){
				division = mapTeamDivisionResult(results, shallow);
			}
		}
		catch (Exception e){
			log.error("Error getting division!  conferenceId = " + conferenceId + ", name = " + name + ", shallow = " + shallow, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return division;
	}
	
	/**
	 * 
	 * Gets the full division object in the conference with the given name.  If you want
	 * just the division, without the teams, call the other function and set shallow to true.
	 * 
	 * @param conferenceName
	 * @param name
	 * @return
	 */
	public TeamDivision getTeamDivision(String conferenceName, String name){
		
		TeamDivision division = getTeamDivision(conferenceName, name, false);
		
		return division;
	}
	
	/**
	 * 
	 * Gets the division with the give name in the given conference from the database.  If shallow is false, it'll
	 * get the  teams in the division too.  If it's true, it won't.
	 * 
	 * @param conferenceName
	 * @param name
	 * @param shallow
	 * @return
	 */
	public TeamDivision getTeamDivision(String conferenceName, String name, boolean shallow){
		
		TeamDivision division = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		String query = SELECT_TEAM_DIVISION + 
					   "where name = ? " + 
					         "and team_conference_id in (select id " + 
					         					   "from team_conference " + 
					         					   "where name = ? ) ";
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, name);
			statement.setString(2, conferenceName);
			
			results = statement.executeQuery();
			
			if (results.next()){
				division = mapTeamDivisionResult(results, shallow);
			}
		}
		catch (Exception e){
			log.error("Error getting division!  conferenceName = " + conferenceName + ", name = " + name + ", shallow = " + shallow, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return division;
	}
	
	/**
	 * 
	 * Saves the given division to the database.  If it has an id, it'll do an update.
	 * Otherwise, it'll do an insert.
	 * 
	 * It'll return the saved division with the id filled in.  It gets that saved division
	 * by using the fact that a division name is unique within a conference.
	 * 
	 * @param division
	 * @return
	 */
	public TeamDivision saveTeamDivision(TeamDivision division){
		
		int id = division.getId();
		
		int numberOfAffectedRows = 0;
		
		if (id <= 0){
			numberOfAffectedRows = insertTeamDivision(division);
		}
		else {
			numberOfAffectedRows = updateTeamDivision(division);
		}
		
		TeamDivision savedDivision = null;

		//If everything was ok, we can get the saved division by the conference and name since
		//the name is unique within the conference.
		if (numberOfAffectedRows == 1){
			savedDivision = getTeamDivision(division.getConferenceId(), division.getName(), true);
		}
		
		return savedDivision;
	}
	
	/**
	 * 
	 * Inserts a record for the given division in the division table.  Not much to it.
	 * 
	 * @param division
	 * @return
	 */
	protected int insertTeamDivision(TeamDivision division){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();

			statement = connection.prepareStatement(INSERT_TEAM_DIVISION);
			statement.setInt(1, division.getConferenceId());
			statement.setString(2, division.getName());
			statement.setString(3, division.getStartYear());
			statement.setString(4, division.getEndYear());
			statement.setString(5, division.getCurrentName());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error inserting division!  division= " + division, e);
			rollback(connection);
		}
		finally {
			close(statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * Updates the record in the division table for the division with the given division's
	 * id. ... division.
	 * 
	 * @param division
	 * @return
	 */
	protected int updateTeamDivision(TeamDivision division){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(UPDATE_TEAM_DIVISION);
			statement.setInt(1, division.getConferenceId());
			statement.setString(2, division.getName());
			statement.setString(3, division.getStartYear());
			statement.setString(4, division.getEndYear());
			statement.setString(5, division.getCurrentName());
			statement.setInt(6, division.getId());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error updating division!  division = " + division, e);
			rollback(connection);
		}
		finally {
			close(statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * This function will get all the divisions.  It'll do a "deep" retrieve
	 * and get all the teams too.
	 * 
	 * @return
	 */
	public List<TeamDivision> getTeamDivisions(){
		
		List<TeamDivision> divisions = getTeamDivisions(false);
		
		return divisions;
	}
	
	/**
	 * 
	 * This function will get all the divisions.  If shallow is false, it'll get all
	 * the teams too.  If it's true, it won't.
	 * 
	 * @param shallow
	 * @return
	 */
	public List<TeamDivision> getTeamDivisions(boolean shallow){
		
		//Steps to do:
		//	1. Run the query for the division and then map it.
		//	2. That's it.
		
		List<TeamDivision> divisions = new ArrayList<TeamDivision>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(SELECT_TEAM_DIVISION);
			results = statement.executeQuery();
			
			while (results.next()){
				TeamDivision division = mapTeamDivisionResult(results, shallow);
				divisions.add(division);
			}
		}
		catch (Exception e){
			log.error("Error getting divisions!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return divisions;
	}
	
	/**
	 * 
	 * This function will get the divisions in the given conference.  If shallow is false,
	 * it'll get the teams too.  If it's true, it won't.
	 * 
	 * @param conferenceId
	 * @param shallow
	 * @return
	 */
	public List<TeamDivision> getTeamDivisionsInTeamConference(int conferenceId, boolean shallow){
		
		//Steps to do:
		//	1. Run the query and map the results.
		//	2. That's it.
		
		List<TeamDivision> divisions = new ArrayList<TeamDivision>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			String query = SELECT_TEAM_DIVISION + " where team_conference_id = ? ";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, conferenceId);
			results = statement.executeQuery();
			
			while (results.next()){
				TeamDivision division = mapTeamDivisionResult(results, shallow);
				divisions.add(division);
			}
		}
		catch (Exception e){
			log.error("Error getting divisions!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return divisions;
		
	}
	
	/**
	 * 
	 * This function will take the result of a query to the division table for all the columns into
	 * a Division object.  If shallow is false, it'll get all the teams too.  If it's true, it won't.
	 * 
	 * @param results
	 * @param shallow
	 * @return
	 * @throws SQLException
	 */
	protected TeamDivision mapTeamDivisionResult(ResultSet results, boolean shallow) throws SQLException {
		
		TeamDivision division = new TeamDivision();
		division.setId(results.getInt("id"));
		division.setConferenceId(results.getInt("team_conference_id"));
		division.setName(results.getString("name"));
		division.setCurrentName(results.getString("current_name"));
		division.setStartYear(results.getString("start_year"));
		division.setEndYear(results.getString("end_year"));
		division.setCurrentName(results.getString("current_name"));
		
		if (!shallow){
			List<Team> teams = getTeamsInDivision(division.getId());
			division.setTeams(teams);
		}
		
		return division;
	}
	
	/**
	 * 
	 * This function will get all the teams.  That's it.
	 * 
	 * @return
	 */
	public List<Team> getTeams(){
		
		//Steps to do:
		//	1. Run the query and map the results.
		//	2. That's it.
		
		List<Team> teams = new ArrayList<Team>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(SELECT_TEAM);
			results = statement.executeQuery();
			
			while (results.next()){
				Team teamInfo = mapTeamsResult(results);
				teams.add(teamInfo);
			}
		}
		catch (Exception e){
			log.error("Error getting teams!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return teams;
	}
	
	/**
	 * 
	 * This function will get all the teams in the given division.
	 * 
	 * @param divisionId
	 * @param shallow
	 * @return
	 */
	public List<Team> getTeamsInDivision(int divisionId){
		
		List<Team> teams = new ArrayList<Team>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			String query = SELECT_TEAM + 
						   " where team_division_id = ? ";
		
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, divisionId);
			
			results = statement.executeQuery();
			
			while (results.next()){
				Team teamInfo = mapTeamsResult(results);
				teams.add(teamInfo);
			}
		}
		catch (Exception e){
			log.error("Error getting teams!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return teams;
		
	}
	
	/**
	 * 
	 * This function will get the team with the given abbreviation.  It's here because
	 * that's usually how we get a team from the outside.
	 * 
	 * @param abbreviation
	 * @return
	 */
	public Team getTeamByAbbreviation(String abbreviation){
		
		//Steps to do:
		//	1. Run the query and send back the result.
		
		Team team = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		//this should just get it by abbreviation ... there should be another one that gets
		//all teams as a group ... like oak and lv
		try {
			String query = SELECT_TEAM + 
						   "where abbreviation = ? ";
			
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, abbreviation);
			
			results = statement.executeQuery();
			
			if (results.next()){
				team = mapTeamsResult(results);
			}
		}
		catch (Exception e){
			log.error("Error getting team! abbreviation = " + abbreviation, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return team;
	}
	
	/**
	 * 
	 * This function will get the team with the given id.  Not much to it!
	 * 
	 * @param id
	 * @return
	 */
	public Team getTeam(int id){
		
		Team team = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_TEAM + 
						   "where id = ? ";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			results = statement.executeQuery();
			
			if (results.next()){
				team = mapTeamsResult(results);
			}
		}
		catch (Exception e){
			log.error("Error getting team! id = " + id, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return team;
	}
	
	/**
	 * 
	 * Saves the given team to the database.  If it has an id, it'll do an update.
	 * Otherwise, it'll do an insert.
	 * 
	 * It'll return the saved team with the id filled in.  It gets that saved team by
	 * using its abbreviation (which should be unique among teams).
	 * 
	 * @param division
	 * @return
	 */
	public Team saveTeam(Team team){
		
		int id = team.getId();
		
		int numberOfAffectedRows = 0;
		
		if (id <= 0){
			numberOfAffectedRows = insertTeam(team);
		}
		else {
			numberOfAffectedRows = updateTeam(team);
		}
		
		Team savedTeam = null;

		//If everything was ok, we can get the saved team by its abbreviation.
		if (numberOfAffectedRows == 1){
			savedTeam = getTeamByAbbreviation(team.getAbbreviation());
		}
		
		return savedTeam;
	}
	
	/**
	 * 
	 * Inserts a record for the given team in the team table.  Not much to it.
	 * 
	 * @param team
	 * @return
	 */
	protected int insertTeam(Team team){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();

			statement = connection.prepareStatement(INSERT_TEAM);
			statement.setInt(1, team.getDivisionId());
			statement.setString(2, team.getCity());
			statement.setString(3, team.getNickname());
			statement.setString(4, team.getAbbreviation());
			statement.setString(5, team.getStartYear());
			statement.setString(6, team.getEndYear());
			statement.setString(7, team.getCurrentAbbreviation());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error inserting team!  team = " + team, e);
			rollback(connection);
		}
		finally {
			close(statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * Updates the given team in the team table using its id.  Not much to it.
	 * 
	 * @param team
	 * @return
	 */
	protected int updateTeam(Team team){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(UPDATE_TEAM);
			statement.setInt(1, team.getDivisionId());
			statement.setString(2, team.getCity());
			statement.setString(3, team.getNickname());
			statement.setString(4, team.getAbbreviation());
			statement.setString(5, team.getStartYear());
			statement.setString(6, team.getEndYear());
			statement.setString(7, team.getCurrentAbbreviation());
			statement.setInt(8, team.getId());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error updating team!  team = " + team, e);
			rollback(connection);
		}
		finally {
			close(statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * This function will map the given result to a team object.  It expects the result to have 
	 * all of the team table's columns in it.
	 * 
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	protected Team mapTeamsResult(ResultSet result) throws SQLException {
		
		Team team = new Team();
		team.setId(result.getInt("id"));
		team.setDivisionId(result.getInt("team_division_id"));
		team.setCity(result.getString("city"));
		team.setNickname(result.getString("nickname"));
		team.setAbbreviation(result.getString("abbreviation"));
		team.setCurrentAbbreviation(result.getString("current_abbreviation"));
		team.setStartYear(result.getString("start_year"));
		team.setEndYear(result.getString("end_year"));
		
		return team;
	}
	
	/**
	 * 
	 * This function gets all the seasons for the given years.  It does a "full"
	 * get on those seasons (fills out the Season objects with the weeks and games).
	 * 
	 * @param years
	 * @return
	 */
	public List<Season> getSeasons(List<String> years){
		
		List<Season> seasons = getSeasons(years, false);
		
		return seasons;
	}
	
	/**
	 * 
	 * This function will get all the seasons for the given years.  If shallow is true,
	 * it'll return the Season objects without the weeks and games in them.  If it's false,
	 * it'll go through and fill out all of the stuff in each season object.
	 * 
	 * @param years
	 * @param shallow
	 * @return
	 */
	public List<Season> getSeasons(List<String> years, boolean shallow){
		
		//Steps to do:
		//	1. Make the query, add the years, and run it.
		//	2. Tell the mapper whether it's shallow or not so it
		//	   knows whether to fill out the objects or not.
		
		List<Season> seasons = new ArrayList<Season>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			//We'll have to have a ? in the string for each year in the query.
			String yearInClause = DatabaseUtil.createInClauseParameterString(years.size());
			
			String query = SELECT_SEASON + " where year in " + yearInClause;
			connection = getConnection();
			statement = connection.prepareStatement(SELECT_SEASON);
			
			int parameterIndex = 0;
			
			for (int index = 0; index < years.size(); index++){
				String year = years.get(index);
				parameterIndex = index + 1;
				statement.setString(parameterIndex, year);
			}
			
			results = statement.executeQuery(query);
			
			while (results.next()){
				//Send whether it's shallow to the mapper so it knows whether to 
				//add in the other stuff.
				Season season = mapSeason(results, shallow);
				
				seasons.add(season);
			}
		}
		catch (Exception e){
			log.error("Error getting seasons!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return seasons;
	}
	
	/**
	 * 
	 * This function will get the season for the given year.
	 * 
	 * @param year
	 * @return
	 */
	public Season getSeasonByYear(String year, boolean shallow){
		
		//Steps to do:
		//	1. Run the query and map the results.
		
		Season season = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			
			String query = SELECT_SEASON + 
						   "where year = ? ";
			
			statement = connection.prepareStatement(query);
			statement.setString(1, year);
			results = statement.executeQuery();
			
			if (results.next()){
				season = mapSeason(results, shallow);
			}
		}
		catch (Exception e){
			log.error("Error getting season by year! year = " + year, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return season;
	}
	
	/**
	 * 
	 * This function will map the given season result to an object.  The result should have all
	 * the columns from the season table.  If shallow is false, it'll map all the weeks too.
	 * 
	 * @param result
	 * @param shallow
	 * @return
	 * @throws SQLException
	 */
	protected Season mapSeason(ResultSet result, boolean shallow) throws SQLException {
		
		Season season = new Season();
		int id = result.getInt("id");
		season.setId(id);
		season.setYear(result.getString("year"));

		if (!shallow){
			List<Week> weeks = getWeeks(id);
			season.setWeeks(weeks);
		}
		
		return season;
	}
	
	/**
	 * 
	 * Gets the years in the order they should be for the criteria (descending).
	 * 
	 * @return
	 */
	public List<String> getYearsForCriteria(){
		
		//Steps to do:
		//	1. Just run the query and pull out the years.

		List<String> years = new ArrayList<String>();

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			connection = getConnection();
			statement = connection.prepareStatement("select year from season order by year desc");
			results = statement.executeQuery();

			while (results.next()){
				String year = results.getString(1);
				years.add(year);
			}
		}
		catch (Exception e){
			log.error("Error getting years!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}

		return years;
	}
	
	/**
	 * 
	 * This function gets all the years that we have for all the seasons.  Here so 
	 * the caller doesn't have to deal with Season objects if all they want are
	 * all the years.
	 * 
	 * @return
	 */
	public List<String> getYears(){
		
		//Steps to do:
		//	1. Just run the query and pull out the years.
		
		List<String> years = new ArrayList<String>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement("select year from season");
			results = statement.executeQuery();
			
			while (results.next()){
				String year = results.getString(1);
				years.add(year);
			}
		}
		catch (Exception e){
			log.error("Error getting years!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return years;
	}
	
	/**
	 * 
	 * This function will get the "current" year.  It says which year is current by just ordering the
	 * seasons by their years and picking the first one.
	 * 
	 * @return
	 */
	public String getCurrentYear(){
		
		//Steps to do:
		//	1. Order the seasons by their year and then the current one
		//	   will be on top.
		
		String currentYear = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement("select year from season order by year desc");
			results = statement.executeQuery();
			
			if (results.next()){
				currentYear = results.getString(1);
			}
		}
		catch (Exception e){
			log.error("Error getting current year!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return currentYear;
	}
	
	/**
	 * 
	 * This function will get all the weeks for all the seasons and all the
	 * games in those weeks.
	 * 
	 * @return
	 */
	public List<Week> getWeeks(){
		
		//Steps to do:
		//	1. Call the function that does the work with the arguments that will
		//	   make it get everything.
		
		List<Week> weeks = getWeeks(null, null, false);
		
		return weeks;
	}
	
	/**
	 * 
	 * This function gets all the weeks for the given year.  Not much to it.
	 * It does a full retrieval of those weeks and gets the games too.
	 * 
	 * @param year
	 * @return
	 */
	public List<Week> getWeeks(String year){
		
		List<String> years = new ArrayList<String>();
		years.add(year);
		
		List<Week> weeks = getWeeks(years, null, false);
		
		return weeks;
	}
	
	/**
	 * 
	 * This function will get the weeks in the given years with the "keys".  It does
	 * a "full" retrieval of the weeks it finds, getting their games too.
	 * 
	 * @param years
	 * @param weekKeys
	 * @return
	 */
	public List<Week> getWeeks(List<String> years, List<String> weekKeys){
	
		List<Week> weeks = getWeeks(years, weekKeys, false);
		
		return weeks;
	}
	
	/**
	 * 
	 * This function will get all the weeks for the given years and weeks.  If shallow is true, it'll
	 * just get the actual week records.  If it's false, it'll get the games in the weeks too.
	 * 
	 * If either years or weeks is null, it'll just ignore that and get them all.  So, like if years are
	 * given, but weeks is null, it'll get all of the weeks for the given years.  If weeks are given but
	 * years are null, it'll get those specific weeks for all years.
	 * 
	 * @param years
	 * @param weekKeys
	 * @param shallow
	 * @return
	 */
	public List<Week> getWeeks(List<String> years, List<String> weekKeys, boolean shallow){
		
		//Steps to do:
		//	1. Just take the query and add in the years and weeks stuff if we were given
		//	   years and weeks.
		
		List<Week> weeks = new ArrayList<Week>();
		
//		List<Integer> weekSequenceNumberIntegers = Util.toIntegers(weekKeys);
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			StringBuilder stringBuilder = new StringBuilder(SELECT_WEEK);
			
			boolean addedWhere = false;
			
			//Add in years if we have them.
			if (years != null && years.size() > 0){
				if (!addedWhere){
					 stringBuilder.append(" where ");
					 addedWhere = true;
				}
				else {
					stringBuilder.append(" and ");
				}
				
				String inParameterString = DatabaseUtil.createInClauseParameterString(years.size());
				
				stringBuilder.append("season_id in (select id " + 
									 "from season " + 
									 "where year in ")
							 .append(inParameterString)
							 .append(")");
			}
			
			//And add in weeks too.
			if (weekKeys != null && weekKeys.size() > 0){
				if (!addedWhere){
					 stringBuilder.append(" where ");
					 addedWhere = true;
				}
				else {
					stringBuilder.append(" and ");
				}
				
				String inParameterString = DatabaseUtil.createInClauseParameterString(weekKeys.size());
			
				stringBuilder.append("key in ")
							 .append(inParameterString)
							 .append(")");
			}
			
			String query = stringBuilder.toString();
			
			connection = getConnection();
			statement = connection.prepareStatement(query);
			
			int parameterIndex = 1;
			
			if (years != null && years.size() > 0){
				for (int index = 0; index < years.size(); index++){
					String year = years.get(index);
					statement.setString(parameterIndex, year);
					parameterIndex++;
				}
			}
			
			if (weekKeys != null && weekKeys.size() > 0){
				for (int index = 0; index < weeks.size(); index++){
					String weekKey = weekKeys.get(index);
					statement.setString(parameterIndex, weekKey);
					parameterIndex++;
				}
			}
			
			results = statement.executeQuery();
			
			while (results.next()){
				Week week = mapWeek(results, shallow);
				weeks.add(week);
			}
		}
		catch (Exception e){
			log.error("Error getting weeks!  years = " + years + ", weekKeys = " + weekKeys + ", shallow = " + shallow, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return weeks;
	}
	
	/**
	 * 
	 * This function will get the weeks in the season with the given id.  It will
	 * do a "full" retrieve of those weeks and include the games too.
	 * 
	 * @param seasonId
	 * @return
	 */
	public List<Week> getWeeks(int seasonId){
		
		//Steps to do:
		//	1. Run the query.
		//	2. Send back the results.
		
		List<Week> weeks = new ArrayList<Week>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			String query = SELECT_WEEK + 
						   "where season_id = ? ";
			statement = connection.prepareStatement(query);
			statement.setInt(1, seasonId);
			results = statement.executeQuery();
			
			while (results.next()){
				Week week = mapWeek(results);
				weeks.add(week);
			}
		}
		catch (Exception e){
			log.error("Error getting weeks! seasonId = " + seasonId, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return weeks;
	}
	
	/**
	 * 
	 * This function will get the week with the given id.  It will do a full retrieval
	 * of the week and include the games in it too.
	 * 
	 * @param id
	 * @return
	 */
	public Week getWeek(int id){
		
		//Steps to do:
		//	1. Run the query.
		//	2. Return the results.
		
		Week week = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			String query = SELECT_WEEK + 
						   "where id = ? ";
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			results = statement.executeQuery();
			
			if (results.next()){
				week = mapWeek(results);
			}
		}
		catch (Exception e){
			log.error("Error getting week! id = " + id, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return week;
	}
	
	/**
	 * 
	 * This function will save the given season to the database.  It will do a "shallow"
	 * save and just save the season record.  It won't save the weeks in the season.
	 * 
	 * It expects the year for a season to be unique so, after it saves the season, it will
	 * get out what was saved using the season's year and return that result.
	 * 
	 * If the season has an id, it'll do an update.  If it doesn't, it'll do an insert.
	 * 
	 * @param season
	 * @return
	 */
	public Season saveSeason(Season season){
		
		//Steps to do:
		//	1. If the season doesn't have a real id, do an insert.
		//	2. Otherwise, do an update.
		//	3. Pull out the season that was saved by its year.
		
		
		int id = season.getId();
		
		int numberOfAffectedRows = 0;
		
		if (id <= 0){
			numberOfAffectedRows = insertSeason(season);
		}
		else {
			numberOfAffectedRows = updateSeason(season);
		}
		
		Season savedSeason = null;
		
		if (numberOfAffectedRows == 1){
			savedSeason = getSeasonByYear(season.getYear(), true);
		}
		
		return savedSeason;
	}
	
	/**
	 * 
	 * This function will insert the season into the season table.  Not much to it.
	 * 
	 * @param season
	 * @return
	 */
	protected int insertSeason(Season season){
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(INSERT_SEASON);
			statement.setString(1, season.getYear());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error inserting season! season = " + season, e);
			rollback(connection);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * This function will update the given season in the season table.  .ti ot hcum toN
	 * 
	 * @param season
	 * @return
	 */
	protected int updateSeason(Season season){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(UPDATE_SEASON);
			statement.setString(1, season.getYear());
			statement.setInt(2, season.getId());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error updating season! season = " + season, e);
			rollback(connection);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * This function will save the given player in the database.  If they have an id,
	 * it'll update them.  If they don't, it'll do an insert.
	 * 
	 * It expects the player's name to be unique, so it will get them out by their name
	 * after it's saved them.
	 * 
	 * @param player
	 * @return
	 */
	public Player savePlayer(Player player){
		
		//Steps to do:
		//	1. Pull out their id.
		//	2. If they don't have one, insert them.
		//	3. Otherwise, update them.
		//	4. Pull out what was saved using their name.
		
		int id = player.getId();
		
		int numberOfRowsAffected = 0;
		
		if (id <= 0){
			numberOfRowsAffected = insertPlayer(player);
		}
		else {
			numberOfRowsAffected = updatePlayer(player);
		}
		
		Player savedPlayer = null;
		
		if (numberOfRowsAffected == 1){
			savedPlayer = getPlayer(player.getName());
		}
		
		return savedPlayer;
	}
	
	/**
	 * 
	 * This function will insert the given player in the database.
	 * Not much to it.
	 * 
	 * @param player
	 * @return
	 */
	protected int insertPlayer(Player player){
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(INSERT_PLAYER);
			statement.setString(1, player.getName());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error inserting player! player = " + player, e);
			rollback(connection);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * This function will update the given player in the database.  Not much to it.
	 * 
	 * @param player
	 * @return
	 */
	protected int updatePlayer(Player player){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(UPDATE_PLAYER);
			statement.setString(1, player.getName());
			statement.setInt(2, player.getId());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error updating player! player = " + player, e);
			rollback(connection);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * This function will save the given week in the database.  If the week
	 * doesn't have an id, it'll do an insert.  If it does, it'll do an update.
	 * 
	 * It'll pull what it saved out of the database using the given week's season id
	 * and week number (that combination should be unique).
	 * 
	 * @param week
	 * @return
	 */
	public Week saveWeek(Week week){
		
		//Steps to do:
		//	1. If it doesn't have an id, do an insert.
		//	2. If it does, do an update.
		
		int id = week.getId();
		
		int numberOfRowsAffected = 0;
		
		if (id <= 0){
			numberOfRowsAffected = insertWeek(week);
		}
		else {
			numberOfRowsAffected = updateWeek(week);
		}
		
		Week savedWeek = null;
		
		if (numberOfRowsAffected == 1){
			savedWeek = getWeek(week.getSeasonId(), week.getKey());
		}
		
		return savedWeek;
	}
	
	/**
	 * 
	 * This function will insert the given week into the database.  Not much to it.
	 * 
	 * @param week
	 * @return
	 */
	protected int insertWeek(Week week){
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();

			statement = connection.prepareStatement(INSERT_WEEK);
			statement.setInt(1, week.getSeasonId());
			statement.setInt(2, week.getSequenceNumber());
			statement.setString(3, week.getType());
			statement.setString(4, week.getKey());
			statement.setString(5, week.getLabel());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error inserting week! week = " + week, e);
			rollback(connection);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * This function will update the given week in the database.
	 * 
	 * @param week
	 * @return
	 */
	protected int updateWeek(Week week){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();

			statement = connection.prepareStatement(UPDATE_WEEK);
			statement.setInt(1, week.getSeasonId());
			statement.setInt(2, week.getSequenceNumber());
			statement.setString(3, week.getType());
			statement.setString(4, week.getKey());
			statement.setString(5, week.getLabel());
			statement.setInt(6, week.getId());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error inserting week! week = " + week, e);
			rollback(connection);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
		
	}
	
	/**
	 * 
	 * This function will get the week in the given season with the given key.
	 * That combination should be unique.
	 * 
	 * It'll do a full retrieval of the week, so it'll include the games too.
	 * 
	 * @param seasonId
	 * @param weekKey
	 * @return
	 */
	public Week getWeek(int seasonId, String weekKey){
		
		Week retrievedWeek = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			String query = SELECT_WEEK + 
						   "where season_id = ?" +
						   		 "and key = ? ";
			statement = connection.prepareStatement(query);
			statement.setInt(1, seasonId);
			statement.setString(2, weekKey);
			results = statement.executeQuery();
			
			if (results.next()){
				retrievedWeek = mapWeek(results);
			}
		}
		catch (Exception e){
			log.error("Error getting week! seasonId = " + seasonId + ", weekSequenceNumber = " + weekKey, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return retrievedWeek;
	}
	
	/**
	 * 
	 * This function will get the week in the given season with the given sequence number.
	 * That combination should be unique.
	 * 
	 * It'll do a full retrieval of the week, so it'll include the games too.
	 * 
	 * @param seasonId
	 * @param weekSequenceNumber
	 * @return
	 */
	public Week getWeek(int seasonId, int weekSequenceNumber){
		
		Week retrievedWeek = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			String query = SELECT_WEEK + 
						   "where season_id = ?" +
						   		 "and sequence_number = ? ";
			statement = connection.prepareStatement(query);
			statement.setInt(1, seasonId);
			statement.setInt(2, weekSequenceNumber);
			results = statement.executeQuery();
			
			if (results.next()){
				retrievedWeek = mapWeek(results);
			}
		}
		catch (Exception e){
			log.error("Error getting week! seasonId = " + seasonId + ", weekSequenceNumber = " + weekSequenceNumber, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return retrievedWeek;
	}
	
	/**
	 * 
	 * This function will get the week for the given year and sequence number.
	 * 
	 * @param year
	 * @param weekSequenceNumber
	 * @return
	 */
	public Week getWeekBySequenceNumber(String year, int weekSequenceNumber){
		
		//Steps to do:
		//	1. Make the query.
		//	2. Return the results.

		Week retrievedWeek = null;

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			connection = getConnection();
			
			String query = SELECT_WEEK + 
					"where season_id in (select id " +
					"from season " +
					"where year = ? )" +
					"and sequence_number = ? ";
			
			statement = connection.prepareStatement(query);
			statement.setString(1, year);
			statement.setInt(2, weekSequenceNumber);
			results = statement.executeQuery();

			if (results.next()){
				retrievedWeek = mapWeek(results);
			}
		}
		catch (Exception e){
			log.error("Error getting week! year = " + year + ", weekSequenceNumber = " + weekSequenceNumber, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}

		return retrievedWeek;
	}
	
	/**
	 * 
	 * This function will get the week in the given year with the given key.
	 * It'll do a full retrieval of the week and include the games in it.
	 * 
	 * @param year
	 * @param weekKey
	 * @return
	 */
	public Week getWeek(String year, String weekKey){
		
		//Steps to do:
		//	1. Make the query.
		//	2. Return the results.
		
		Week retrievedWeek = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			String query = SELECT_WEEK + 
						   "where season_id in (select id " +
						   					   "from season " +
						   					   "where year = ? )" +
						   		 "and key = ? ";
			statement = connection.prepareStatement(query);
			statement.setString(1, year);
			statement.setString(2, weekKey);
			results = statement.executeQuery();
			
			if (results.next()){
				retrievedWeek = mapWeek(results);
			}
		}
		catch (Exception e){
			log.error("Error getting week! year = " + year + ", weekKey = " + weekKey, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return retrievedWeek;
	}
	
	/**
	 * 
	 * This function will get the current week "key" that's based on the picks
	 * that have been filled in.  The current week is defined as the biggest
	 * week number with results for the current season.
	 * 
	 * @return
	 */
	public String getCurrentWeekKey(){
		
		Week currentWeek = getCurrentWeek();
		
		String weekKey = currentWeek.getKey();
		
		return weekKey;
	}
	
	/**
	 * 
	 * This function will get the current week number that we're on based on the picks
	 * that have been filled in.  The current week number is basically defined as
	 * the biggest week number with results for the current season.
	 * 
	 * @return
	 */
	public int getCurrentWeekSequenceNumber(){
		
		Week currentWeek = getCurrentWeek();
		
		int currentWeekSequenceNumber = currentWeek.getSequenceNumber();
		
		return currentWeekSequenceNumber;
	}
	
	/**
	 * 
	 * This function will get the "next" week number.  This is basically defined as:
	 * the current week number plus 1.  It'll make sure it doesn't go beyond the number
	 * of weeks in the current year.  It'll get the current year and, if it's before 2021,
	 * make sure it doesn't go beyond 21 (the superbowl).  If it's 2021 or after, it'll make
	 * sure it doesn't go after 22.
	 * 
	 * @return
	 */
	public int getNextWeekSequenceNumber(){
		
		int currentWeekSequenceNumber = getCurrentWeekSequenceNumber();
		String year = getCurrentYear();
		
		int yearInt = Util.toInteger(year);
		
		int nextWeekSequenceNumber = currentWeekSequenceNumber + 1;
		
		if (yearInt < 2021){
			if (nextWeekSequenceNumber > 21){
				nextWeekSequenceNumber = 21;
			}
		}
		else {
			if (nextWeekSequenceNumber > 22){
				nextWeekSequenceNumber = 22;
			}
		}
		
		return nextWeekSequenceNumber;
	}
	
	/**
	 * 
	 * This function will get the current week.  The current week is defined as:
	 * 
	 * 		The smallest week in the current season that has a game without a winner.
	 * 
	 * This makes it so a week is "in progress" until all of its games have winners.
	 * Once they do, the next week becomes the current week because it's the smallest
	 * week that has a game without a winner.
	 * 
	 * It will do a full retrieval for the week and include all the games in it.
	 * 
	 * @return
	 */
	public Week getCurrentWeek() {
		
		//Steps to do:
		//	1. Get the current year.
		//	2. Run the query to get the smallest week without results in 
		//	   the current year.
		//	3. If we find something, that's the current week.
		//	4. If we don't, that means it should be week 1 for the current year.
		
		String currentYear = getCurrentYear();
		
		Week currentWeek = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			
			String query = SELECT_WEEK + 
						   " where season_id in (select id " +
							    				"from season where year = ? ) " + 
							    				"and id in (select week_id " +
							    						   "from game g " + 
							    						   "where g.winning_team_id is null) " + 
					       " order by sequence_number asc offset 0 limit 1 ";
			
			statement = connection.prepareStatement(query);
			statement.setString(1, currentYear);
			results = statement.executeQuery();
			
			if (results.next()){
				currentWeek = mapWeek(results);
			}
			//If we don't find a week that has results, that means we're on week number 1.
			else {
				currentWeek = getWeekBySequenceNumber(currentYear, 1);
			}
		}
		catch (Exception e){
			log.error("Error getting week! currentYear = " + currentYear, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return currentWeek;
	}
	
	/**
	 * 
	 * This function will map the result that should be from the "week" table into an
	 * object.  It'll do a "full" mapping, which means it will get the games in the week
	 * too.
	 * 
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	protected Week mapWeek(ResultSet result) throws SQLException {
		
		Week week = mapWeek(result, false);
		
		return week;
	}
	
	/**
	 * 
	 * This function will map the week for the given result (it should match up with the columns
	 * from the week table).  If shallow is true, that's all it'll do.  If it's false, it'll get and map
	 * the games in the week too.
	 * 
	 * @param result
	 * @param shallow
	 * @return
	 * @throws SQLException
	 */
	protected Week mapWeek(ResultSet result, boolean shallow) throws SQLException {
		
		Week week = new Week();
		int weekId = result.getInt("id");
		week.setId(weekId);
		int seasonId = result.getInt("season_id");
		week.setSeasonId(seasonId);
		week.setSequenceNumber(result.getInt("sequence_number"));
		week.setType(result.getString("type"));
		week.setKey(result.getString("key"));
		week.setLabel(result.getString("label"));
		
		if (!shallow){
			List<Game> games = getGames(weekId);
			week.setGames(games);
		}
		
		return week;
	}
	
	/**
	 * 
	 * This function will get the games for the week with the given id.  It will do a "full" retrieval
	 * of the games, so it'll include the teams too.  It will order the games by id in ascending order
	 * so that we can predict the order.
	 * 
	 * @param weekId
	 * @return
	 */
	public List<Game> getGames(int weekId){
		
		List<Game> games = new ArrayList<Game>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			String query = SELECT_GAME + 
						   " where week_id = ? " +
						   " order by id asc "; 
			statement = connection.prepareStatement(query);
			statement.setInt(1, weekId);
			results = statement.executeQuery();
			
			while (results.next()){
				Game game = mapGame(results);
				games.add(game);
			}
		}
		catch (Exception e){
			log.error("Error getting games! weekId = " + weekId, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return games;
	}
	
	/**
	 * 
	 * This function will get all the games that we have for all seasons and weeks.
	 * It will do a "full" retrieval on the games.
	 * 
	 * @return
	 */
	public List<Game> getGames(){
		
		List<Game> games = new ArrayList<Game>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(SELECT_GAME);
			results = statement.executeQuery();
			
			while (results.next()){
				Game game = mapGame(results);
				games.add(game);
			}
		}
		catch (Exception e){
			log.error("Error getting games!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return games;
	}
	
	/**
	 * 
	 * This function will get all the games in the given year and week.  It'll
	 * do a full retrieval on the games and order them by their id (so that the earliest
	 * ones should come first).
	 * 
	 * @param year
	 * @param weekKey
	 * @return
	 */
	public List<Game> getGames(String year, String weekKey){
		
		List<Game> games = new ArrayList<Game>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			String query = SELECT_GAME + 
						   " where week_id in (select w.id " +
						   					  "from week w " + 
						   					  "where key = ? " + 
						   					  	    "and season_id in (select id " +
						   					  					      "from season " + 
						   					  					      "where year = ?)) " +
						   "order by id asc ";
			statement = connection.prepareStatement(query);
			statement.setString(1, weekKey);
			statement.setString(2, year);
			results = statement.executeQuery();
			
			while (results.next()){
				Game game = mapGame(results);
				games.add(game);
			}
		}
		catch (Exception e){
			log.error("Error getting games! weekKey = " + weekKey + ", year = " + year, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return games;
	}
	
	/**
	 * 
	 * This function will get the games for the current week.  Not much to it.  It just
	 * gets the current week and then uses that to get the games in it.
	 * 
	 * @return
	 */
	public List<Game> getGamesForCurrentWeek(){

		//Steps to do:
		//	1. Get the current week.
		//	2. The games will be inside.
		
		Week currentWeek = getCurrentWeek();
		List<Game> games = currentWeek.getGames();
		
		return games;
		
	}
	
	/**
	 * 
	 * This function will get the games for the given years, weeks, and teams.  If either of those are null,
	 * it'll just not include them in the query.
	 * 
	 * It'll do a full retrieval of the games and so it'll include the teams too.
	 * 
	 * @param years
	 * @param weekKeys
	 * @param teams
	 * @return
	 */
	public List<Game> getGames(List<String> years, List<String> weekKeys, List<String> teams){
		
		//Steps to do:
		//	1. Add in each of the clauses for the years, weeks, and teams if we have them.
		//	2. Make the query.
		//	3. Return the results.
		
		List<Game> games = new ArrayList<Game>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_GAME;
			
			boolean addedWhere = true;
			
			if (years != null && years.size() > 0){
				String yearsParameterString = DatabaseUtil.createInClauseParameterString(years.size());
				query = query + " where week_id in (select id " + 
												   "from week " + 
												   "where season_id in (select id " + 
												   					   "from season " + 
												   					   "where year in (" + yearsParameterString + "))) ";
				
				addedWhere = true;
			}
			
			if (weekKeys != null && weekKeys.size() > 0){
				String weekKeysParameterString = DatabaseUtil.createInClauseParameterString(weekKeys.size());
				
				if (addedWhere){
					query = query + " and ";
				}
				else {
					query = query + " where ";
					addedWhere = true;
				}
				
				query = query + " week_id in (select id " +
											 "from week " + 
											 "where key in (" + weekKeysParameterString + ")) ";
			}
			
			if (teams != null && teams.size() > 0){
				String teamsParameterString = DatabaseUtil.createInClauseParameterString(weekKeys.size());
				
				if (addedWhere){
					query = query + " and ";
				}
				else {
					query = query + " where ";
					addedWhere = true;
				}
		
				//The team could be the home or away team for the game.
				query = query + " (home_team_id in (select id " + 
												   "from team " + 
												   "where abbreviation in (" + teamsParameterString + ")) " +
								  "or away_team_id in (select id " + 
												   	  "from team " + 
												   	  "where abbreviation in (" + teamsParameterString + "))) ";
			}
			
			connection = getConnection();
			statement = connection.prepareStatement(query);
			
			int argumentNumber = 1;
			if (years != null && years.size() > 0){
				for (int index = 0; index < years.size(); index++){
					String year = years.get(index);
					statement.setString(argumentNumber, year);
					argumentNumber++;
				}
			}
			
			if (weekKeys != null && weekKeys.size() > 0){
				for (int index = 0; index < weekKeys.size(); index++){
					String weekKey = weekKeys.get(index);
					statement.setString(argumentNumber, weekKey);
					argumentNumber++;
				}
			}
			
			if (teams != null && teams.size() > 0){
				for (int index = 0; index < teams.size(); index++){
					String team = teams.get(index);
					statement.setString(argumentNumber, team);
					argumentNumber++;
				}
			}
			
			results = statement.executeQuery();
			
			while (results.next()){
				Game game = mapGame(results);
				games.add(game);
			}
		}
		catch (Exception e){
			log.error("Error getting games! years = " + years + ", weekKeys = " + weekKeys + ", teams = " + teams, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return games;
	}
	
	/**
	 * 
	 * This function will get the game for the given year, in the given week, with the given away and home teams.
	 * 
	 * It expects all the arguments to be given.
	 * 
	 * @param year
	 * @param weekSequenceNumber
	 * @param awayTeamAbbreviation
	 * @param homeTeamAbbreviation
	 * @return
	 */
	public Game getGame(String year, String weekSequenceNumber, String awayTeamAbbreviation, String homeTeamAbbreviation){
		
		//Steps to do:
		//	1. Build the query.
		//	2. Run it.
		//	3. Return the results.
		
		Game game = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_GAME + 
						   "where week_id in (select w.id " +
						   					 "from week w " + 
						   					 "where w.sequence_number = ? " + 
						   					 	   "and w.season_id in (select s.id " + 
						   					 					 	   "from season s " + 
						   					 					 	   "where s.year = ?)) " +
						   					 	   "and (home_team_id in (select t.id " +
						   					 		    				 "from team t " + 
						   					 		    				 "where t.abbreviation = ?) " +
						   					 		    "and away_team_id in (select t.id " + 
						   					 		       					 "from team t " + 
						   					 		       					 "where t.abbreviation = ?)) ";
			
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, Integer.parseInt(weekSequenceNumber));
			statement.setString(2, year);
			statement.setString(3, homeTeamAbbreviation);
			statement.setString(4, awayTeamAbbreviation);
			
			results = statement.executeQuery();
			
			if (results.next()){
				game = mapGame(results);
			}
		}
		catch (Exception e){
			log.error("Error getting game! year = " + year + ", weekSequenceNumber = " + weekSequenceNumber + ", awayTeamAbbreviation = " + awayTeamAbbreviation + 
					  ", homeTeamAbbreviation = " + homeTeamAbbreviation, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return game;
	}
	
	/**
	 * 
	 * This function will get the game in the given year and week with the given team.
	 * The team could be the home or away team for the game.  
	 * 
	 * It expects all the arguments to be given.
	 * 
	 * It'll do a full retrieval on the game it finds.
	 * 
	 * @param year
	 * @param weekSequenceNumber
	 * @param teamAbbreviation
	 * @return
	 */
	public Game getGame(String year, int weekSequenceNumber, String teamAbbreviation){
		
		//Steps to do:
		//	1. Build the query.
		//	2. Run it.
		//	3. Send back the result.
		
		Game game = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_GAME + 
						   "where week_id in (select w.id " +
						   					 "from week w " + 
						   					 "where w.sequence_number = ? " + 
						   					 "and w.season_id in (select s.id " + 
						   					 					 "from season s " + 
						   					 					 "where s.year = ?)) " +
						   		  "and (home_team_id in (select t.id " +
						   					 		    "from team t " + 
						   					 		    "where t.abbreviation = ?) " +
						   			   "or away_team_id in (select t.id " + 
						   					 		       "from team t " + 
						   					 		       "where t.abbreviation = ?)) ";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, weekSequenceNumber);
			statement.setString(2, year);
			statement.setString(3, teamAbbreviation);
			statement.setString(4, teamAbbreviation);
			
			results = statement.executeQuery();
			
			if (results.next()){
				game = mapGame(results);
			}
		}
		catch (Exception e){
			log.error("Error getting game! year = " + year + ", weekSequenceNumber = " + weekSequenceNumber + ", teamAbbreviation = " + teamAbbreviation, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return game;
	}
	
	/**
	 * 
	 * This function will get the game in the given week with the given home
	 * and away teams.  It expects all arguments to be given and it'll do a full
	 * retrieval on the game too.
	 * 
	 * @param weekId
	 * @param homeTeamId
	 * @param awayTeamId
	 * @return
	 */
	public Game getGame(int weekId, int homeTeamId, int awayTeamId){
		
		//Steps to do:
		//	1. Build the query.
		//	2. Run it.
		//	3. Send back the results.
		
		Game game = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_GAME + 
						   "where week_id = ? " +
						   	     "and home_team_id = ? " +
						   	     "and away_team_id = ? ";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, weekId);
			statement.setInt(2, homeTeamId);
			statement.setInt(3, awayTeamId);
			results = statement.executeQuery();
			
			if (results.next()){
				game = mapGame(results);
			}
		}
		catch (Exception e){
			log.error("Error getting game! weekId = " + weekId + ", homeTeamId = " + homeTeamId + ", awayTeamId = " + awayTeamId, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return game;
		
	}
	
	/**
	 * 
	 * This function will get the game with the given id.  Not much to it.
	 * 
	 * @param id
	 * @return
	 */
	public Game getGame(int id){
		
		Game game = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_GAME + 
						   "where id = ? ";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			results = statement.executeQuery();
			
			if (results.next()){
				game = mapGame(results);
			}
		}
		catch (Exception e){
			log.error("Error getting game! id = " + id, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return game;
	}
	
	/**
	 * 
	 * This function will save the given game to the database.  If it doesn't
	 * have an id, it'll do an insert.  Otherwise, it'll do an update.  It expects
	 * the combination of week id, home team, and away team to be unique, so it'll
	 * use those variables to get the game after it's done saving and return what it
	 * found.
	 * 
	 * @param game
	 * @return
	 */
	public Game saveGame(Game game){
		
		//Steps to do:
		//	1. If it doesn't have an id, insert it.
		//	2. Otherwise, if it does, update it.
		//	3. Pull out the game that was saved by its week and teams.
		
		int id = game.getId();
		
		int numberOfAffectedRows = 0;
		
		if (id <= 0){
			numberOfAffectedRows = insertGame(game);
		}
		else {
			numberOfAffectedRows = updateGame(game);
		}
		
		Game savedGame = null;
		
		if (numberOfAffectedRows == 1){
			int weekId = game.getWeekId();
			int homeTeamId = game.getHomeTeam().getId();
			int awayTeamId = game.getAwayTeam().getId();
			savedGame = getGame(weekId, homeTeamId, awayTeamId);
		}
		
		return savedGame;
	}
	
	/**
	 * 
	 * This function will insert the game into the game table.  Not much to it.
	 * 
	 * @param game
	 * @return
	 */
	protected int insertGame(Game game){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(INSERT_GAME);
			statement.setInt(1, game.getWeekId());
			statement.setInt(2, game.getHomeTeam().getId());
			statement.setInt(3, game.getAwayTeam().getId());
			
			Team winningTeam = game.getWinningTeam();
			if (winningTeam != null){
				statement.setInt(4, winningTeam.getId());
			}
			else {
				boolean tie = game.getTie();
				if (tie){
					statement.setInt(4, -1);
				}
				else {
					statement.setNull(4, Types.INTEGER);
				}
			}
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error inserting game! game = " + game, e);
			rollback(connection);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * This function will update the game.  Not much to it.
	 * 
	 * The only interesting thing is that, if there's a winning team, it'll
	 * set the winning team id to that team.  If there isn't and there's a tie,
	 * it'll set the winning team to -1.  If there's no winning team and there
	 * isn't a tie, it'll null out the winning team id.
	 * 
	 * In this way, it can be used to "reset" a game so that there's no winner.
	 * 
	 * @param game
	 * @return
	 */
	protected int updateGame(Game game){
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(UPDATE_GAME);
			statement.setInt(1, game.getWeekId());
			statement.setInt(2, game.getHomeTeam().getId());
			statement.setInt(3, game.getAwayTeam().getId());
			
			//If there's a winning team, set that team.
			Team winningTeam = game.getWinningTeam();
			if (winningTeam != null){
				statement.setInt(4, winningTeam.getId());
			}
			//Otherwise, check whether there's a tie.  If there is, the winning
			//team is -1.  If there isn't a tie, that means there is no winner,
			//so the winning team should be null.
			else {
				boolean tie = game.getTie();
				if (tie){
					statement.setInt(4, NFLPicksConstants.TIE_WINNING_TEAM_ID);
				}
				else {
					statement.setNull(4, Types.INTEGER);
				}
			}
			
			statement.setInt(5, game.getId());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error updating game! game = " + game, e);
			rollback(connection);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * This function will map the given results into a game object.  It'll include
	 * the teams too.
	 * 
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	protected Game mapGame(ResultSet results) throws SQLException {
		Game game = new Game();
		
		game.setId(results.getInt("id"));
		game.setWeekId(results.getInt("week_id"));
		
		int homeTeamId = results.getInt("home_team_id");
		Team homeTeam = getTeam(homeTeamId);
		game.setHomeTeam(homeTeam);
		
		int awayTeamId = results.getInt("away_team_id");
		Team awayTeam = getTeam(awayTeamId);
		game.setAwayTeam(awayTeam);
		
		int winningTeamId = results.getInt("winning_team_id");
		if (winningTeamId == NFLPicksConstants.TIE_WINNING_TEAM_ID){
			game.setTie(true);
		}
		else if (winningTeamId > 0) {
			Team winningTeam = getTeam(winningTeamId);
			game.setWinningTeam(winningTeam);
		}

		return game;
	}
	
	/**
	 * 
	 * This function will get the pick that the given player made for the given year, in the given week, in the game with
	 * the given teams.  It expects all arguments to be given.
	 * 
	 * @param playerName
	 * @param year
	 * @param weekSequenceNumber
	 * @param homeTeamAbbreviation
	 * @param awayTeamAbbreviation
	 * @return
	 */
	public Pick getPick(String playerName, String year, int weekSequenceNumber, String homeTeamAbbreviation, String awayTeamAbbreviation){
		
		//Steps to do:
		//	1. Make the query.
		//	2. Run it.
		//	3. Send back what was found.
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		Pick pick = null;
		
		try {
			connection = getConnection();
			
			String query = SELECT_PICK +
						   "where player_id in (select id " +
						   					   "from player " +
						   					   "where name = ? )" +
						   	      "and game_id in (select id " +
							   					  "from game " + 
							   					  "where week_id in (select id " + 
							   					 				    "from week " + 
							   					 				    "where season_id in (select id " +
							   					 				   					    "from season " +
							   					 				   					    "where year = ?) " +
							   					 				   		  "and sequence_number = ? ) " +
							   					 	    "and home_team_id in (select id " +
							   					 				   		  	 "from team " +
							   					 				   		  	 "where abbreviation = ?) " +
							   					 		"and away_team_id in (select id " + 
							   					 				   		  	 "from team " + 
							   					 				   		  	 "where abbreviation = ?) " +
							   					  ")";
			
			statement = connection.prepareStatement(query);
			statement.setString(1, playerName);
			statement.setString(2, year);
			statement.setInt(3, weekSequenceNumber);
			statement.setString(4, homeTeamAbbreviation);
			statement.setString(5, awayTeamAbbreviation);
			results = statement.executeQuery();
			
			if (results.next()){
				pick = mapPick(results);
			}
		}
		catch (Exception e){
			log.error("Error getting picks! playerName = " + playerName + ", year = " + year + ", weekSequenceNumber = " + weekSequenceNumber + 
					  ", homeTeamAbbreviation = " + homeTeamAbbreviation + ", awayTeamAbbreviation = " + awayTeamAbbreviation, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return pick;
	}
	
	/**
	 * 
	 * This function will get the picks made by the given player, in the given year, and in
	 * the given week.  It expects all the arguments to be given.
	 * 
	 * @param playerId
	 * @param year
	 * @param weekSequenceNumber
	 * @return
	 */
	public List<Pick> getPicks(int playerId, String year, int weekSequenceNumber){
		
		//Steps to do:
		//	1. Build the query.
		//	2. Run it.
		//	3. Send back the results.
		
		List<Pick> picks = new ArrayList<Pick>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			String query = SELECT_PICK +
						   "where playerId = ? " + 
						   	      "and game_id in (select id " +
							   					  "from game " + 
							   					  "where week_id in (select id " + 
							   					 				    "from week " + 
							   					 				    "where season_id in (select id " +
							   					 				   					    "from season " +
							   					 				   					    "where year = ?) " +
							   					 				   		  "and sequence_number = ? ";
			statement = connection.prepareStatement(query);
			statement.setInt(1, playerId);
			statement.setString(2, year);
			statement.setInt(3, weekSequenceNumber);
			results = statement.executeQuery();
			
			while (results.next()){
				Pick pick = mapPick(results);
				picks.add(pick);
			}
		}
		catch (Exception e){
			log.error("Error getting picks! playerId = " + playerId + ", year = " + year + ", weekSequenceNumber = " + weekSequenceNumber, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return picks;
	}
	
	/**
	 * 
	 * This function will get the picks in the given year for the given week.  It expects
	 * both arguments to be given.
	 * 
	 * @param year
	 * @param weekKey
	 * @return
	 */
	public List<Pick> getPicks(String year, String weekKey){
		
		//Steps to do:
		//	1. Convert the arguments to what the real function expects (lists).
		//	2. Call it to do the work.
		
		List<String> years = Arrays.asList(year);
		List<String> weekSequenceNumbers = Arrays.asList(weekKey);
		
		List<Pick> picks = getPicks(years, weekSequenceNumbers, null, null);
		
		return picks;
	}

	/**
	 * 
	 * This function will get the picks that the given player made in the given year and week.
	 * It expects all the arguments to be given.
	 * 
	 * @param player
	 * @param year
	 * @param weekKey
	 * @return
	 */
	public List<Pick> getPicks(String player, String year, String weekKey){
		
		//Steps to do:
		//	1. Convert the arguments to what the real function expects (lists).
		//	2. Call it to do the work.
		
		List<String> players = Arrays.asList(player);
		List<String> years = Arrays.asList(year);
		List<String> weekSequenceNumbers = Arrays.asList(weekKey);
		
		List<Pick> picks = getPicks(years, weekSequenceNumbers, players, null);
		
		return picks;
	}
	
	/**
	 * 
	 * A convenience function to get picks for multiple players for the same year and week.
	 * 
	 * @param players
	 * @param year
	 * @param weekKey
	 * @return
	 */
	public List<Pick> getPicks(List<String> players, String year, String weekKey){
		
		List<String> years = Arrays.asList(year);
		List<String> weekSequenceNumbers = Arrays.asList(weekKey);
		
		List<Pick> picks = getPicks(years, weekSequenceNumbers, players, null);
		
		return picks;
	}

	/**
	 * 
	 * This function will get the picks for the given arguments.  They're all optional, so it will only include them
	 * if they're given.  The given teams could be the home or away teams for the games that the picks are for.
	 * 
	 * @param years
	 * @param weekKeys
	 * @param playerNames
	 * @param teamNames
	 * @return
	 */
	public List<Pick> getPicks(List<String> years, List<String> weekKeys, List<String> playerNames, List<String> teamNames){
		
		//Steps to do:
		//	1. Add the arguments that we have to build the query.
		//	2. Run it.
		//	3. Return what it found.
		
		List<Pick> picks = new ArrayList<Pick>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			
			//another programming language idea ... where if you "add" something, it just adds to the data structure.
			
			boolean hasWeeks = false;
			if (weekKeys != null && weekKeys.size() > 0){
				hasWeeks = true;
			}
			
			boolean hasYears = false;
			if (years != null && years.size() > 0){
				hasYears = true;
			}
			
			boolean hasPlayers = false;
			if (playerNames != null && playerNames.size() > 0){
				hasPlayers = true;
			}
			
			boolean hasTeams = false;
			if (teamNames != null && teamNames.size() > 0){
				hasTeams = true;
			}
			
			String query = SELECT_PICK;
			
			boolean addedWhere = false;
			
			if (hasWeeks){
				query = query + " where game_id in (select id " + 
												   "from game " + 
												   "where week_id in (select id " + 
												   					 "from week " +
												   					 "where key in (" + DatabaseUtil.createInClauseParameterString(weekKeys.size()) + "))) ";
				
				addedWhere = true;
			}
			
			if (hasYears){
				
				if (addedWhere){
					query = query + " and ";
				}
				else {
					query = query + " where ";
					addedWhere = true;
				}
				
				query = query +  " game_id in (select id " + 
						   					  "from game " + 
						   					  "where week_id in (select id " + 
						   									    "from week " +
						   										"where season_id in (select id " +
						   														    "from season " + 
						   											  				"where year in (" + DatabaseUtil.createInClauseParameterString(years.size()) + ")))) ";
			}
			
			
			if (hasPlayers){
				
				if (addedWhere){
					query = query + " and ";
				}
				else {
					query = query + " where ";
					addedWhere = true;
				}
				
				query = query + " player_id in (select id from player where name in " + DatabaseUtil.createInClauseParameterString(playerNames.size()) + " ) ";
			}
			
			if (hasTeams){
				
				if (addedWhere){
					query = query + " and ";
				}
				else {
					query = query + " where ";
					addedWhere = true;
				}
				
				query = query + " team_id in (select id from team where abbreviation in " + DatabaseUtil.createInClauseParameterString(teamNames.size()) + " ) ";
			}
			
			connection = getConnection();
			statement = connection.prepareStatement(query);
			
			int parameterIndex = 1;
			if (hasWeeks){
				
				for (int index = 0; index < weekKeys.size(); index++){
					String weekKey = weekKeys.get(index);
					statement.setString(parameterIndex, weekKey);
					parameterIndex++;
				}
			}
			
			if (hasYears){
				for (int index = 0; index < years.size(); index++){
					String year = years.get(index);
					statement.setString(parameterIndex, year);
					parameterIndex++;
				}
			}
			
			if (hasPlayers){
				for (int index = 0; index < playerNames.size(); index++){
					String playerName = playerNames.get(index);
					statement.setString(parameterIndex, playerName);
					parameterIndex++;
				}
			}
			
			if (hasTeams){
				for (int index = 0; index < teamNames.size(); index++){
					String teamName = teamNames.get(index);
					statement.setString(parameterIndex, teamName);
					parameterIndex++;
				}
			}
			
			results = statement.executeQuery();
			
			while (results.next()){
				Pick pick = mapPick(results);
				picks.add(pick);
			}
		}
		catch (Exception e){
			log.error("Error getting picks! years = " + years + ", weekSequenceNumbers = " + weekKeys + ", playerNames = " + playerNames + ", teamNames = " + teamNames, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return picks;
	}
	
	/**
	 * 
	 * This function will get all the picks for all the players, seasons, and weeks.  Not much to it.
	 * 
	 * @return
	 */
	public List<Pick> getPicks(){
		
		List<Pick> picks = new ArrayList<Pick>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(SELECT_PICK);
			results = statement.executeQuery();
			
			while (results.next()){
				Pick pick = mapPick(results);
				picks.add(pick);
			}
		}
		catch (Exception e){
			log.error("Error getting picks!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return picks;
	}
	
	/**
	 * 
	 * This function will save the given pick to the database.  If it has an id, it'll
	 * do an update.  If it doesn't, it'll do an insert.  It expects the pick to
	 * have both the game and player objects in it and it will use the game and player id
	 * to get the saved pick (since that combination should be unique).
	 * 
	 * @param pick
	 * @return
	 */
	public Pick savePick(Pick pick){
		
		//Steps to do:
		//	1. If the pick doesn't have an id, insert it.
		//	2. If it does, update it.
		//	3. Pull out what was saved by using the game and the player.
		
		int id = pick.getId();
		
		int numberOfAffectedRows = 0;
		
		if (id <= 0){
			numberOfAffectedRows = insertPick(pick);
		}
		else {
			numberOfAffectedRows = updatePick(pick);
		}
		
		Pick savedPick = null;
		
		if (numberOfAffectedRows == 1){
			Game game = pick.getGame();
			Player player = pick.getPlayer();
			savedPick = getPick(game.getId(), player.getId());
		}
		
		return savedPick;
	}
	
	/**
	 * 
	 * This function will insert the given pick into the database.  Not much to it.
	 * 
	 * @param pick
	 * @return
	 */
	protected int insertPick(Pick pick){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = getConnection();
			statement = connection.prepareStatement(INSERT_PICK);
			statement.setInt(1, pick.getGame().getId());
			statement.setInt(2, pick.getPlayer().getId());
			
			Team pickedTeam = pick.getTeam();
			if (pickedTeam != null){
				statement.setInt(3, pickedTeam.getId());
			}
			else {
				statement.setNull(3, Types.INTEGER);
			}
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error inserting pick! pick = " + pick, e);
			rollback(connection);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}

	/**
	 * 
	 * This function will update the given pick in the database.  Not much to it.
	 * 
	 * @param pick
	 * @return
	 */
	protected int updatePick(Pick pick){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
	
		try {
			connection = getConnection();
			statement = connection.prepareStatement(UPDATE_PICK);
			statement.setInt(1, pick.getGame().getId());
			statement.setInt(2, pick.getPlayer().getId());
			
			Team pickedTeam = pick.getTeam();
			if (pickedTeam != null){
				statement.setInt(3, pickedTeam.getId());
			}
			else {
				statement.setNull(3, Types.INTEGER);
			}
			
			statement.setInt(4, pick.getId());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error updating pick! pick = " + pick, e);
			rollback(connection);
		}
		finally {
			close(statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 *
	 * This function will delete the given pick.  It pulls out the pick id, game id, and player id
	 * and will quit if any of them aren't there because I figured it might be a good idea to be
	 * extra sure before doing a delete on anything.
	 * 
	 * @param pick
	 * @return
	 */
	public int deletePick(Pick pick) {
		
		//Steps to do:
		//	1. Pull out the ids and quit if any aren't there.
		//	2. Call the function that does the work.
		
		int id = pick.getId();

		Game game = pick.getGame();
		if (game == null){
			return 0;
		}
		int gameId = game.getId();
		
		Player player = pick.getPlayer();
		if (player == null){
			return 0;
		}
		int playerId = player.getId();
		
		int numberOfRowsAffected = deletePick(id, gameId, playerId);
		
		return numberOfRowsAffected;
	}
	
	/**
	 * 
	 * This function will delete the pick with the given id for the given game and player.
	 * I originally wasn't going to have a delete function, but I think it kind of makes sense
	 * in this case.
	 * 
	 * @param id
	 * @param gameId
	 * @param playerId
	 * @return
	 */
	public int deletePick(int id, int gameId, int playerId) {
	
		Connection connection = null;
		PreparedStatement statement = null;
		
		int numberOfAffectedRows = 0;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(DELETE_PICK);
			statement.setInt(1, gameId);
			statement.setInt(2, playerId);
			statement.setInt(3, id);
			
			numberOfAffectedRows = statement.executeUpdate();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error deleting pick!  id = " + id + ", gameId = " + gameId, e);
			rollback(connection);
		}
		finally {
			close(statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * This function will get the pick for the given game and player.  That combination
	 * should be unique because a player should only have one pick per game.  Not much to it.
	 * 
	 * @param gameId
	 * @param playerId
	 * @return
	 */
	public Pick getPick(int gameId, int playerId){
		
		Pick pick = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_PICK + 
						   "where game_id = ? " +
						   	     "and player_id = ? ";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, gameId);
			statement.setInt(2, playerId);
			results = statement.executeQuery();
			
			if (results.next()){
				pick = mapPick(results);
			}
		}
		catch (Exception e){
			log.error("Error getting pick! gameId = " + gameId + ", playerId = " + playerId, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return pick;
	}
	
	/**
	 * 
	 * This function will get the pick with the given id.  Not much to it.
	 * 
	 * @param id
	 * @return
	 */
	public Pick getPick(int id){
		
		Pick pick = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_PICK + 
						   "where id = ? ";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			results = statement.executeQuery();
			
			if (results.next()){
				pick = mapPick(results);
			}
		}
		catch (Exception e){
			log.error("Error getting pick! id = " + id, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return pick;
	}
	
	/**
	 * 
	 * This function will map the results from the pick table into a Pick object.
	 * It'll map the teams and the game for the pick too.
	 * 
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	protected Pick mapPick(ResultSet results) throws SQLException {
		Pick pick = new Pick();
		
		pick.setId(results.getInt("id"));
		int gameId = results.getInt("game_id");
		Game game = getGame(gameId);
		pick.setGame(game);
		
		int playerId = results.getInt("player_id");
		Player player = getPlayer(playerId);
		pick.setPlayer(player);
		
		int pickedTeamId = results.getInt("team_id");
		Team team = getTeam(pickedTeamId);
		pick.setTeam(team);
		
		if (game != null && team != null){
			Team winningTeam = game.getWinningTeam();
			boolean tie = game.getTie();
			
			if (tie){
				pick.setResult(NFLPicksConstants.RESULT_TIE);
			}
			else if (winningTeam != null){
				int winningTeamId = winningTeam.getId();
				
				if (winningTeamId == pickedTeamId){
					pick.setResult(NFLPicksConstants.RESULT_WIN);
				}
				else {
					pick.setResult(NFLPicksConstants.RESULT_LOSS);
				}
			}
		}
		
		return pick;
	}
	
	//somebody should make a programming language where you can't change the
	//variable names.......
	//call it "global"
	
	/**
	 * 
	 * This function will get the players with the given names.  Not much to it.
	 * If there are no given players, it'll get them all.
	 * 
	 * The player objects will be returned in the same order that the given names are in.
	 * 
	 * @param playerNames
	 * @return
	 */
	public List<Player> getPlayers(List<String> playerNames){
		
		//Steps to do:
		//	1. Build the query.
		//	2. Run it.
		//	3. Send back the results.
		
		List<Player> players = new ArrayList<Player>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			StringBuilder stringBuilder = new StringBuilder(SELECT_PLAYER);
			
			if (playerNames != null && playerNames.size() > 0){
				String inParameterString = DatabaseUtil.createInClauseParameterString(playerNames.size());
				stringBuilder.append(" where name in ").append(inParameterString);
			}
			
			String query = stringBuilder.toString();
			
			connection = getConnection();
			statement = connection.prepareStatement(query);
			
			if (playerNames != null && playerNames.size() > 0){
				int parameterIndex = 1;
				for (int index = 0; index < playerNames.size(); index++){
					String playerName = playerNames.get(index);
					statement.setString(parameterIndex, playerName);
					parameterIndex++;
				}
			}
			
			results = statement.executeQuery();
			
			Player[] playersArray = new Player[playerNames.size()];
			
			while (results.next()){
				Player playerInfo = mapPlayer(results);
				//Make sure to put the player in the list at the same position its name was.
				int indexOfPlayerName = playerNames.indexOf(playerInfo.getName());
				playersArray[indexOfPlayerName] = playerInfo;
			}
			
			players = Arrays.asList(playersArray);
		}
		catch (Exception e){
			log.error("Error getting players!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return players;
	}
	
	/**
	 * 
	 * This function will get the players who made at least one pick in any of the given years.  They don't
	 * have to have made a pick in every year, just one is good enough.
	 * 
	 * If the years aren't given, it'll get all the players.
	 * 
	 * @param years
	 * @return
	 */
	public List<Player> getPlayersForYears(List<String> years){
		
		//Steps to do:
		//	1. If there aren't any years, get all the players.
		//	2. Otherwise, get the players who have at least one pick
		//	   in any of the given yers.
		//	3. That's it.
		
		List<Player> players = new ArrayList<Player>();
		
		if (years == null){
			players = getPlayers();
			return players;
		}
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			String query = SELECT_PLAYER + 
						   "where id in (select player_id " + 
						   				"from pick " + 
						   				"where game_id in (select id " + 
						   								  "from game " + 
						   								  "where week_id in (select id " + 
						   								  					"from week " + 
						   								  					"where season_id in (select id " + 
						   								  										"from season " + 
						   								  										"where year in " + DatabaseUtil.createInClauseParameterString(years.size()) + "))))";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			
			for (int index = 0; index < years.size(); index++){
				String year = years.get(index);
				int parameterIndex = index + 1;
				statement.setString(parameterIndex, year);
			}
			
			results = statement.executeQuery();
			
			while (results.next()){
				Player playerInfo = mapPlayer(results);
				players.add(playerInfo);
			}
		}
		catch (Exception e){
			log.error("Error getting players!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return players;
	}
	
	/**
	 * 
	 * This function will get the players who made picks in the given year.
	 * If the year is null, it'll get all the players.
	 * 
	 * @param year
	 * @return
	 */
	public List<Player> getPlayersForYear(String year){
		
		//Steps to do:
		//	1. If no year was given, then get all the players.
		//	2. Otherwise, call the function that does the work.
		
		List<Player> playersForYear = null;
		
		if (year == null){
			playersForYear = getPlayers();
			return playersForYear;
		}
		
		List<String> years = Arrays.asList(year);
		
		playersForYear = getPlayersForYears(years);
		
		return playersForYear;
	}

	/**
	 * 
	 * This will get the players for the given division id.  Not much to it.
	 * 
	 * @param divisionId
	 * @return
	 */
	public List<Player> getPlayersForDivision(int divisionId){
		
		//need a way to say like "current" or "all"
		
		List<Player> players = new ArrayList<Player>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			String query = SELECT_PLAYER + 
						   "where id in (select player_id " + 
						   				"from player_division " +
						   				"where division_id = ?)";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, divisionId);
			
			results = statement.executeQuery();
			
			while (results.next()){
				Player player = mapPlayer(results);
				players.add(player);
			}
		}
		catch (Exception e){
			log.error("Error getting players!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return players;
	}
	
	/**
	 * 
	 * This function will get all the players.
	 * Not much to it.
	 * 
	 * @return
	 */
	public List<Player> getPlayers(){
		
		List<Player> players = new ArrayList<Player>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(SELECT_PLAYER);
			results = statement.executeQuery();
			
			while (results.next()){
				Player playerInfo = mapPlayer(results);
				players.add(playerInfo);
			}
		}
		catch (Exception e){
			log.error("Error getting players!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return players;
	}
	
	/**
	 * 
	 * This function will get the player with the given id.  Not much to it.
	 * 
	 * @param id
	 * @return
	 */
	public Player getPlayer(int id){
		
		Player player = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_PLAYER + 
						   "where id = ? ";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			results = statement.executeQuery();
			
			if (results.next()){
				player = mapPlayer(results);
			}
		}
		catch (Exception e){
			log.error("Error getting player! id = " + id, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return player;
	}
	
	/**
	 * 
	 * This function will get the players who made picks in any of the given years.  It expects
	 * the years argument to be given.  Each player doesn't have to have made a pick in each year, just
	 * having made a pick in a single year is good enough.
	 * 
	 * @param years
	 * @return
	 */
	public List<Player> getActivePlayers(List<String> years){
		
		List<Player> players = new ArrayList<Player>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			String yearInParameterString = DatabaseUtil.createInClauseParameterString(years.size());
			
			String query = SELECT_PLAYER + 
						   "where id in (select player_id " + 
						   				"from pick " + 
						   				"where game_id in (select id " + 
						   								  "from game " + 
						   								  "where week_id in (select id " + 
						   								  					"from week " + 
						   								  					"where season_id in (select id " + 
						   								  										"from season " + 
						   								  										"where year in " + yearInParameterString + " ))))";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			for (int index = 0; index < years.size(); index++){
				String year = years.get(index);
				int parameterIndex = index + 1;
				statement.setString(parameterIndex, year);
			}
			
			results = statement.executeQuery();
			
			while (results.next()){
				Player playerInfo = mapPlayer(results);
				players.add(playerInfo);
			}
		}
		catch (Exception e){
			log.error("Error getting players!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return players;
	}
	
	/**
	 * 
	 * This function will check to make see if the given player made at least one
	 * pick in the given year.  If they did, it'll return true.  If they didn't, it'll
	 * return false.  It expects both arguments to be given.
	 * 
	 * @param player
	 * @param year
	 * @return
	 */
	public boolean wasPlayerActiveInYear(String player, String year){
		
		//Steps to do:
		//	1. Run the query that will check to see if they made a pick
		//	   in the given year.
		//	2. Return true if they did and false if they didn't.
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		int numberOfPicksInYear = 0;
		
		try {
			String query = "select count(*) " +
					 	   "from pick " +
					 	   "where player_id in (select id  " +
					 					 	   "from player " +
					 					 	   "where name = ?) " +
					 			  "and game_id in (select id  " +
										   		  "from game  " +
										   		  "where week_id in (select id " + 
										   					 	    "from week " +
										   					 	    "where season_id in (select id " +
										   										 	    "from season " + 
										   										 	    "where year = ?)))";
			
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, player);
			statement.setString(2, year);
			results = statement.executeQuery();
			
			if (results.next()){
				numberOfPicksInYear = results.getInt(1);
			}
			
		}
		catch (Exception e){
			log.error("Error checking whether player was active in year!  player = " + player + ", year = " + year, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		if (numberOfPicksInYear > 0){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * This function will get the player with the given name.  Not much to it.
	 * 
	 * @param name
	 * @return
	 */
	public Player getPlayer(String name){
		
		Player player = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_PLAYER + 
						   "where name = ? ";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, name);
			results = statement.executeQuery();
			
			if (results.next()){
				player = mapPlayer(results);
			}
		}
		catch (Exception e){
			log.error("Error getting player! name = " + name, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return player;
	}
	
	/**
	 * 
	 * This function will map the result of a query to the player table to an object.
	 * 
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	protected Player mapPlayer(ResultSet results) throws SQLException {
		Player player = new Player();
		player.setId(results.getInt("id"));
		player.setName(results.getString("name"));
		return player;
	}
	
	/**
	 * 
	 * This function will get the records for the given players in the given years and weeks.  All the arguments
	 * are optional and will only be included if they're given.
	 * 
	 * @param years
	 * @param weekKeys
	 * @param players
	 * @return
	 */
	public List<Record> getRecords(List<String> years, List<String> weekKeys, List<String> players, List<String> teams){
		
		//Steps to do:
		//	1. Add in the arguments we were given to the query if they're there.
		//	2. Run the query.
		//	3. Send back what it found.
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		List<Record> records = new ArrayList<Record>();
		
		try {
			connection = dataSource.getConnection();
			
			String recordsCriteria = createRecordsCriteria(years, weekKeys, players, teams);
			
			String query = String.format(SELECT_RECORD, recordsCriteria);
			
			statement = connection.prepareStatement(query);
			
			//Players go first...
			int parameterIndex = 1;
			if (players != null && players.size() > 0){
				for (int index = 0; index < players.size(); index++){
					String player = players.get(index);
					statement.setString(parameterIndex, player);
					parameterIndex++;
				}
			}
			
			//Then weeks
			if (weekKeys != null && weekKeys.size() > 0){
				for (int index = 0; index < weekKeys.size(); index++){
					String weekKey = weekKeys.get(index);
					statement.setString(parameterIndex, weekKey);
					parameterIndex++;
				}
			}
			
			//Then years
			if (years != null && years.size() > 0){
				for (int index = 0; index < years.size(); index++){
					String year = years.get(index);
					statement.setString(parameterIndex, year);
					parameterIndex++;
				}
			}
			
			//Then teams ... twice
			if (teams != null && teams.size() > 0){
				for (int index = 0; index < teams.size(); index++){
					String team = teams.get(index);
					statement.setString(parameterIndex, team);
					parameterIndex++;
				}
				
				for (int index = 0; index < teams.size(); index++){
					String team = teams.get(index);
					statement.setString(parameterIndex, team);
					parameterIndex++;
				}
			}
			
			results = statement.executeQuery();
			
			while (results.next()){
				Record record = mapRecord(results);
				records.add(record);
			}
		}
		catch (Exception e){
			log.error("Error getting records! years = " + years + ", weekSequenceNumbers = " + weekKeys + ", players = " + players, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return records;
	}
	
	/**
	 * 
	 * This function will map the given result to a record object.  It expects
	 * the result to have the player, wins, losses, and ties in it.
	 * 
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	protected Record mapRecord(ResultSet results) throws SQLException {
		
		Record record = new Record();
		
		int playerId = results.getInt("player_id");
		Player player = getPlayer(playerId);
		record.setPlayer(player);
		
		record.setWins(results.getInt("wins"));
		record.setLosses(results.getInt("losses"));
		record.setTies(results.getInt("ties"));
		
		return record;
	}
	
	/**
	 * 
	 * This function will create the "criteria" part for the records query (basically, the "where" part).
	 * It's here because we need to do this in a few places and I figured it would be a good idea to just
	 * do it once and reuse it.  It will add in each argument that it's given and will skip
	 * the arguments that aren't given.
	 * 
	 * @param years
	 * @param weekKeys
	 * @param players
	 * @return
	 */
	protected String createRecordsCriteria(List<String> years, List<String> weekKeys, List<String> players, List<String> teams){
		
		//Steps to do:
		//	1. Add in the where clauses for the arguments that were given.
		//	2. That's it.
		
		StringBuilder whereClause = new StringBuilder();

		boolean addedWhere = false;

		//First goes player name
		if (players != null && players.size() > 0){
			addedWhere = true;
			whereClause.append("where pl.name in (");
			for (int index = 0; index < players.size(); index++){
				if (index > 0){
					whereClause.append(", ");
				}
				whereClause.append("?");
			}
			whereClause.append(") ");
		}
		
		boolean hasYears = years != null && years.size() > 0;
		boolean hasWeeks = weekKeys != null && weekKeys.size() > 0;
		
		if (hasYears || hasWeeks){
			
			if (!addedWhere){
				whereClause.append("where ");
			}
			else {
				whereClause.append(" and ");
			}
			
			whereClause.append("g.week_id in (select w.id from week w where ");
		
			if (hasWeeks){
				whereClause.append("w.key in (");
				for (int index = 0; index < weekKeys.size(); index++){
					if (index > 0){
						whereClause.append(", ");
					}
					whereClause.append("?");
				}
				whereClause.append(") ");
			}
			
			if (hasYears){
				
				if (hasWeeks){
					whereClause.append(" and ");
				}
				
				whereClause.append("w.season_id in (select s.id from season s where s.year in (");
				
				for (int index = 0; index < years.size(); index++){
					if (index > 0){
						whereClause.append(", ");
					}
					
					whereClause.append("?");
				}
				
				whereClause.append("))");
			}
			
			whereClause.append(")");
		}
		
		if (teams != null && teams.size() > 0){
			
			if (!addedWhere){
				whereClause.append("where ");
			}
			else {
				whereClause.append(" and ");
			}
			
			whereClause.append("(").append("home_team.abbreviation in (");
			
			for (int index = 0; index < teams.size(); index++){
				if (index > 0){
					whereClause.append(", ");
				}
				whereClause.append("?");
			}
			
			whereClause.append(") or away_team.abbreviation in (");
			
			for (int index = 0; index < teams.size(); index++){
				if (index > 0){
					whereClause.append(", ");
				}
				whereClause.append("?");
			}
			
			whereClause.append(")").append(")");
		}
		
		return whereClause.toString();
	}
	
	/**
	 * 
	 * This function will get all the "compact" picks.  These are the picks that basically
	 * have only the bare minimum of info we need.
	 * 
	 * @return
	 */
	public List<CompactPick> getCompactPicks(){
		
		List<CompactPick> compactPicks = getCompactPicks(null, null, null, null);
		
		return compactPicks;
		
	}
	
	/**
	 * 
	 * This function will get the "compact" picks for the given years, weeks, players, and teams.  It will only include each argument
	 * if it's given and will skip the ones that aren't.
	 * 
	 * The "compact" picks are objects that have the bare minimum of info that we need to show a pick (the player, game, and team picked).
	 * 
	 * @param years
	 * @param weekKeys
	 * @param playerNames
	 * @param teams
	 * @return
	 */
	public List<CompactPick> getCompactPicks(List<String> years, List<String> weekKeys, List<String> playerNames, List<String> teams) {
		
		//Steps to do:
		//	1. Make the query.
		//	2. Handle adding the players names as columns for their picks.
		//	3. Add in each argument that we're given.
		//	4. Run it.
		//	5. Return what was found.
		
		List<CompactPick> compactPicks = new ArrayList<CompactPick>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = dataSource.getConnection();

			//This gives us the first part of the bare minimum of info that we need for the compact picks query.
			String selectBase = SELECT_COMPACT_PICK_BASE; 
			
			List<Player> players = null;

			//If we weren't given specific players, we'll want them al.
			if (playerNames == null){
				players = getPlayers();
				playerNames = ModelUtil.getPlayerNames(players);
				//Yeah, I could do it like this...
				//playerNames = players.stream().map(p -> p.getName()).collect(Collectors.toList());
			}
			//Otherwise, only get the ones we were given and make sure we get them in the order that they
			//were given.
			else {
				players = getPlayers(playerNames);
			}
			
			//If we're given player names, we have to convert them into names that will be ok to select
			//as columns, so we need to get rid of the spaces.
			//Later, when we're pulling out the result for each player, we'll use the converted player names
			//to get them.
			List<String> playerNamesToUse = new ArrayList<String>();
			List<Integer> playerIdsToUse = new ArrayList<Integer>();
			
			for (int index = 0; index < players.size(); index++){
				Player player = players.get(index);
				String playerName = player.getName();
				Integer playerId = player.getId();
				
				//For a player name to be a column in the select statement, it has to be lowercase and with no spaces.
				String playerNameToUse = playerName.toLowerCase().replaceAll("\\s+", "_") + "_pick";
				playerNamesToUse.add(playerNameToUse);
				playerIdsToUse.add(playerId);
				
				String playerPickSelect = "(select picked_team.abbreviation " + 
										  "from pick p join team picked_team on p.team_id = picked_team.id " + 
										  "where p.game_id = g.id " + 
										  "and p.player_id = ?) as " + playerNameToUse;
				
				selectBase = selectBase + ", " + playerPickSelect;
			}
			
			//Now that we have the players names as columns, we can add in the "from" that says
			//where we'll get the data.
			String fromBase = "from season s join week w on s.id = w.season_id " + 
							  "join game g on w.id = g.week_id " + 
							  "join team home_team on g.home_team_id = home_team.id " + 
							  "join team away_team on g.away_team_id = away_team.id " + 
							  "left outer join team winning_team on g.winning_team_id = winning_team.id ";
			
			//Now, we just have to do the usual thing of adding in the arguments if we have them.
			String whereBase = "";
			
			boolean addedWhere = false;
			boolean hasYears = Util.hasSomething(years);
			boolean hasWeeks = Util.hasSomething(weekKeys);
			boolean hasTeams = Util.hasSomething(teams);
			
			if (hasYears){
				addedWhere = true;
				String inParameterString = DatabaseUtil.createInClauseParameterString(years.size());
				whereBase = "where s.year in " + inParameterString;
			}
			
			if (hasWeeks){
				
				if (addedWhere){
					whereBase = whereBase + " and ";
				}
				else {
					whereBase = "where ";
					addedWhere = true;
				}
				
				String inParameterString = DatabaseUtil.createInClauseParameterString(weekKeys.size());
				whereBase = whereBase + " w.key in " + inParameterString;
			}
			
			if (hasTeams){
				
				if (addedWhere){
					whereBase = whereBase + " and ";
				}
				else {
					whereBase = "where ";
					addedWhere = true;
				}
				
				String inParameterString = DatabaseUtil.createInClauseParameterString(teams.size());
				whereBase = whereBase + " (home_team.abbreviation in " + inParameterString + 
									 	  "or away_team.abbreviation in " + inParameterString + ") ";
			}
			
			String orderBy = "order by s.year asc, w.sequence_number asc, g.id asc ";
			
			String query = selectBase + " " + fromBase + " " + whereBase + " " + orderBy;
			
			statement = connection.prepareStatement(query);
			
			int parameterIndex = 1;
			
			for (int index = 0; index < playerIdsToUse.size(); index++){
				Integer playerId = playerIdsToUse.get(index);
				statement.setInt(parameterIndex, playerId);
				parameterIndex++;
			}
			
			if (hasYears){
				for (int index = 0; index < years.size(); index++){
					String year = years.get(index);
					statement.setString(parameterIndex, year);
					parameterIndex++;
				}
			}
			
			if (hasWeeks){
				for (int index = 0; index < weekKeys.size(); index++){
					String weekKey = weekKeys.get(index);
					statement.setString(parameterIndex, weekKey);
					parameterIndex++;
				}
			}
			
			if (hasTeams){
				for (int index = 0; index < teams.size(); index++){
					String team = teams.get(index);
					statement.setString(parameterIndex, team);
					parameterIndex++;
				}
				
				for (int index = 0; index < teams.size(); index++){
					String team = teams.get(index);
					statement.setString(parameterIndex, team);
					parameterIndex++;
				}
			}
			
			results = statement.executeQuery();
			
			while (results.next()){
				CompactPick compactPick = mapCompactPick(results, playerNamesToUse, playerNames);
				compactPicks.add(compactPick);
			}
		}
		catch (Exception e){
			log.error("Error getting compact picks!", e);
			rollback(connection);
		}
		finally {
			DatabaseUtil.close(results, statement, connection);
		}
		
		return compactPicks;
	}

	/**
	 * 
	 * This function will map the "compact" pick from the given result.  It expects every "player name to use" to be a column in the
	 * given result that has the team that the player picked.  It also expects the playerNamesToUse to be in the same "order" as
	 * the given "playerNames" so that it can use the player's actual name in the pick object will using the "name to use" to get
	 * their pick out.
	 * 
	 * The "player names to use" are there because some players have spaces in their names and we can't have spaces in column
	 * names that we select (well, we probably can, but it's a little awkward).
	 * 
	 * @param results
	 * @param playerNamesToUse
	 * @param playerNames
	 * @return
	 * @throws SQLException
	 */
	protected CompactPick mapCompactPick(ResultSet results, List<String> playerNamesToUse, List<String> playerNames) throws SQLException {
		
		//Steps to do:
		//	1. Pull out the normal columns.
		//	2. Go through each player name that should be a column and pull out
		//	   its result.
		//	3. That's it.  Should have the week's info and the picks for all the players and that's
		//	   all we need.
		
		CompactPick compactPick = new CompactPick();
		
		String year = results.getString("year");
		int weekSequenceNumber = results.getInt("sequence_number");
		String weekType = results.getString("week_type");
		String weekKey = results.getString("week_key");
		String weekLabel = results.getString("week_label");
		String homeTeamAbbreviation = results.getString("home_team_abbreviation");
		String awayTeamAbbreviation = results.getString("away_team_abbreviation");
		String winningTeamAbbreviation = results.getString("winning_team_abbreviation");
		
		List<CompactPlayerPick> playerPicks = new ArrayList<CompactPlayerPick>();
		for (int index = 0; index < playerNamesToUse.size(); index++){
			String playerNameToUse = playerNamesToUse.get(index);
			String playerName = playerNames.get(index);
			
			String pick = results.getString(playerNameToUse);
			CompactPlayerPick playerPick = new CompactPlayerPick(playerName, pick);
			
			playerPicks.add(playerPick);
		}
		
		compactPick.setYear(year);
		compactPick.setWeekSequenceNumber(weekSequenceNumber);
		compactPick.setWeekType(weekType);
		compactPick.setWeekKey(weekKey);
		compactPick.setWeekLabel(weekLabel);
		compactPick.setHomeTeamAbbreviation(homeTeamAbbreviation);
		compactPick.setAwayTeamAbbreviation(awayTeamAbbreviation);
		compactPick.setWinningTeamAbbreviation(winningTeamAbbreviation);
		compactPick.setPlayerPicks(playerPicks);
		
		return compactPick;
	}
	
	/**
	 * 
	 * This class can be used to sort "week records" for players so that they're in ascending
	 * order by season and week and then in descending order by the record (number of wins, then
	 * ascending number of losses, and then ascending by player name if the number of wins and
	 * losses are the same).
	 * 
	 * @author albundy
	 *
	 */
	protected class WeekRecordComparator implements Comparator<WeekRecordForPlayer> {

		public int compare(WeekRecordForPlayer weekRecord1, WeekRecordForPlayer weekRecord2) {
			
			//Steps to do:
			//	1. They should be in ascending order by season.
			//	2. They should be in ascending order by week.
			//	3. They should be in descending order by number of wins.
			//	4. They should be in ascending order by number of losses.
			//	5. And, finally, ascending order by player name if they have the same season,
			//	   week, wins, and losses.
			
			//If they're not in the same season, that should control the order.
			Season season1 = weekRecord1.getSeason();
			String year1 = season1.getYear();
			Season season2 = weekRecord2.getSeason();
			String year2 = season2.getYear();
			
			int seasonResult = year1.compareTo(year2);
			
			if (seasonResult != 0){
				return seasonResult;
			}
			
			//If they're in the same season, then try the week.
			Week week1 = weekRecord1.getWeek();
			Week week2 = weekRecord2.getWeek();
			
			int weekSequenceNumber1 = week1.getSequenceNumber();
			int weekSequenceNumber2 = week2.getSequenceNumber();
			
			//We want it ascending by week.
			if (weekSequenceNumber1 < weekSequenceNumber2){
				return -1;
			}
			else if (weekSequenceNumber1 > weekSequenceNumber2){
				return 1;
			}
			
			//If they're in the same season and week, we want the one with the most wins first.
			Record record1 = weekRecord1.getRecord();
			Record record2 = weekRecord2.getRecord();
			int wins1 = record1.getWins();
			int wins2 = record2.getWins();
			
			if (wins1 > wins2){
				return -1;
			}
			else if (wins1 < wins2){
				return 1;
			}
			
			//If they have the same season, week, and wins, we want the one with the fewest losses
			//first.
			int losses1 = record1.getLosses();
			int losses2 = record2.getLosses();
			
			if (losses1 < losses2){
				return -1;
			}
			else if (losses1 > losses2){
				return 1;
			}
			
			//If we get here, they have the same everything, so just sort on their name.
			String playerName1 = weekRecord1.getPlayer().getName();
			String playerName2 = weekRecord2.getPlayer().getName();
			
			int result = playerName1.compareTo(playerName2);
			
			return result;
		}
	}
	
	/**
	 * 
	 * This function will get the "weeks won by week".  This is basically a list of weeks, the players who won them, and
	 * their records in the weeks that they won.
	 * 
	 * It will get all the records for the given years, weeks, and players, and then filter them out so that it only keeps
	 * the best records in each week.  Since there can be ties, each week can have multiple winners.
	 * 
	 * Each "WeekRecordForPlayers" will contain the year and week that the record was for, the record itself, and a list of players
	 * who had that record.  
	 * 
	 * This is different from the "getWeeksWon" function because this gets them more by week ("who won weeks 1, 2, and 3 in 2017?") and that
	 * function gets them more by player ("how many weeks did Doodle win in 2017?").  They're <i>pretty</i> similar, but different enough
	 * that I decided to put them in different functions.
	 * 
	 * @param years
	 * @param weekKeys
	 * @param players
	 * @param onlyFinishedGames
	 * @return
	 */
	public List<WeekRecordForPlayers> getWeeksWonByWeek(List<String> years, List<String> weekKeys, List<String> players, boolean onlyFinishedGames){
		
		//Steps to do:
		//	1. Get the weekly records for the years and weeks we were given.
		//	2. Go through and get out the "winner" for each week and their record.
		//	3. Make sure we only keep records for players that were given (see below for a longer
		//	   explanation).
		
		//This orders the records by year, week, and record, so that the winner for each week should
		//be the first record within its "block".
		String query = SELECT_WEEK_RECORDS_ORDER_BY_WEEK_AND_RECORD;
		
		//Get the player records for the years and weeks and include all players.
		//We need to include all players because we want the winning records for each week and we need to have all the records
		//for a week to figure out who won it.  We can't do that if we only get records for certain players because it's possible
		//that those players <i>weren't</i> the winners for a particular week.  In order to know who won a particular week, we
		//need all the records for that week.
		//Then, later, we'll go through all the winning records and make sure we only include ones for players that we were given.
		//That way we will wind up with the weeks won for the given players.
		List<WeekRecordForPlayer> playerWeekRecords = getPlayerWeekRecords(query, years, weekKeys, null, onlyFinishedGames);

		List<WeekRecordForPlayers> weeksWonByWeek = new ArrayList<WeekRecordForPlayers>();
		
		WeekRecordForPlayers currentWeekRecord = null;
		
		String currentYear = null;
		int currentWeekSequenceNumber = -1;
		
		//Now go through each weekly record and keep only the winning ones for each week.
		
		for (int index = 0; index < playerWeekRecords.size(); index++){
			WeekRecordForPlayer playerWeekRecord = playerWeekRecords.get(index);

			String recordYear = playerWeekRecord.getSeason().getYear();
			int recordWeekSequenceNumber = playerWeekRecord.getWeek().getSequenceNumber();
			
			//If we're at the start or we switched to a new year or week, make a new object and keep
			//the first record as the winner.  The first record for a year or a week will be the
			//one with the most wins because that's how we sorted the records in the query.  So, we know
			//we'll want to keep it.
			if (currentYear == null || 
					(!recordYear.equals(currentYear)) || recordWeekSequenceNumber != currentWeekSequenceNumber){
				
				currentWeekRecord = new WeekRecordForPlayers();
				currentWeekRecord.setRecord(playerWeekRecord.getRecord());
				currentWeekRecord.setSeason(playerWeekRecord.getSeason());
				currentWeekRecord.setWeek(playerWeekRecord.getWeek());
				List<Player> playersForRecord = new ArrayList<Player>();
				playersForRecord.add(playerWeekRecord.getPlayer());
				currentWeekRecord.setPlayers(playersForRecord);
				
				weeksWonByWeek.add(currentWeekRecord);
				
				currentYear = recordYear;
				currentWeekSequenceNumber = recordWeekSequenceNumber;
			}
			//Otherwise, get the wins for the record.  If it's the same as the number of wins for the
			//current "winning record", then we want to keep it.
			else {
				boolean addPlayer = false;
				int currentWins = currentWeekRecord.getRecord().getWins();
				int recordWins = playerWeekRecord.getRecord().getWins();
				
				if (currentWins == recordWins){
					addPlayer = true;
				}
				
				if (addPlayer){
					currentWeekRecord.getPlayers().add(playerWeekRecord.getPlayer());
				}
			}
		}
		
		//Now that we have all the winning records for every week, it's time to go through and keep only the records for the players
		//that this function was given.  That way, we end up with only the winning records for certain players.  And, if a week
		//had a winner that wasn't in the given list of players, we just lose that week.
		if (players != null && players.size() > 0){
			List<WeekRecordForPlayers> filteredWeeksWonByWeek = new ArrayList<WeekRecordForPlayers>();
			
			//Go through the record for each week.
			for (int index = 0; index < weeksWonByWeek.size(); index++){
				WeekRecordForPlayers weekRecord = weeksWonByWeek.get(index);
				
				//Pull out the players with that record.
				List<Player> winningPlayers = weekRecord.getPlayers();
				
				boolean keepRecord = false;
				
				//If the record included a player that this function was given, then we'll want to keep it.
				//If it didn't, then we won't want to keep it because it was won by a player who wasn't in 
				//the list we were given.
				for (int playerIndex = 0; playerIndex < winningPlayers.size(); playerIndex++){
					Player player = winningPlayers.get(playerIndex);
					if (players.contains(player.getName())){
						keepRecord = true;
						break;
					}
				}
				
				if (keepRecord){
					filteredWeeksWonByWeek.add(weekRecord);
				}
			}
			
			//And now we have our finished result:  The weekly records of winning weeks for the players,
			//years, and weeks we were given.
			weeksWonByWeek = filteredWeeksWonByWeek;
		}
		
		return weeksWonByWeek;
	}
	
	//this should only return the week records for each player for the weeks that they won.
	/**
	 * 
	 * This function will get the weeks won for the given players in the given years and weeks.  It's a little different
	 * from the "getWeeksWonByWeek" function.  That one is more "week" focused ("get me the winners for week 9, 2018") and this
	 * one is more "player" focused ("get me the weeks won by these players").
	 * 
	 * In other words, that one will get a map where it's like "week" -> players.  This one will get a map where it's like
	 * "player" -> weeks they won.
	 * 
	 * @param years
	 * @param weekKeys
	 * @param players
	 * @param onlyFinishedGames
	 * @return
	 */
	public List<WeekRecordsForPlayer> getWeeksWon(List<String> years, List<String> weekKeys, List<String> players, boolean onlyFinishedGames){

		//Steps to do:
		//	1. Get the records for the years and weeks we're interested in.
		//	2. Go through and get the best weeks for those years.
		//	3. Group them by player and filter out players we weren't given.
		//	4. We'll wind up with the weeks won by the given players, grouped by those players.
		
		//We need all the players who played in the years we're interested in.
		//That's because we need to compare the players we want with all the players to see
		//how many weeks the players we care about actually won.  
		//If we just used the players we were given, we'd only get their records and the "wins"
		//would be "amongst" only those players ....
		List<Player> playersForYears = getPlayersForYears(years);
		
		List<String> playerNamesForYears = new ArrayList<String>();
		for (int index = 0; index < playersForYears.size(); index++){
			Player player = playersForYears.get(index);
			playerNamesForYears.add(player.getName());
		}
		
		List<WeekRecordForPlayer> playerWeekRecords = getPlayerWeekRecords(years, weekKeys, playerNamesForYears, onlyFinishedGames);
		
		//This map will hold the "year and week" to best record for that year and week.  Basically, it'll group
		//the records by the best ones for each year and week, with the key being the year and week combination and
		//the value being the best records for that combination.
		Map<String, List<WeekRecordForPlayer>> bestRecordsMap = new HashMap<String, List<WeekRecordForPlayer>>();

		//Go through all the records and group them by year and week, keeping only the best ones from each week.
		for (int index = 0; index < playerWeekRecords.size(); index++){
			WeekRecordForPlayer playerWeekRecord = playerWeekRecords.get(index);
			Record record = playerWeekRecord.getRecord();

			//The query we use can bring back records with 0 wins, so we need to filter those out.
			//I don't want to change the query because it's used by other functions.
			if (record.getWins() == 0){
				continue;
			}
			
			//Get the year and week for the record.
			Season season = playerWeekRecord.getSeason();
			String recordYear = season.getYear();
			
			Week week = playerWeekRecord.getWeek();
			int weekSequenceNumber = week.getSequenceNumber();
			
			String key = recordYear + "-" + weekSequenceNumber;
			
			//Get the current records we have for the year and week.
			List<WeekRecordForPlayer> currentBestRecords = bestRecordsMap.get(key);
			
			boolean addRecord = false;
			
			//If we don't have any, then we can assume this is the best one.
			if (currentBestRecords == null){
				currentBestRecords = new ArrayList<WeekRecordForPlayer>();
				addRecord = true;
			}
			//If we do have some, then we just have to compare the record to the current
			//best one we have and keep it if it's better.
			else {
				WeekRecordForPlayer currentBestWeekRecord = currentBestRecords.get(0);
				Record currentBestRecord = currentBestWeekRecord.getRecord();
				int currentBestWins = currentBestRecord.getWins();
				int currentBestLosses = currentBestRecord.getLosses();
				
				//Keep the record if it's better than the current one.
				int wins = record.getWins();
				int losses = record.getLosses();
				
				if (wins > currentBestWins){
					addRecord = true;
					//If it's better than the current one, empty out the current ones.
					currentBestRecords = new ArrayList<WeekRecordForPlayer>();
				}
				else if (wins == currentBestWins){
					//Or, if it has fewer losses, it's better.
					if (losses < currentBestLosses){
						addRecord = true;
						currentBestRecords = new ArrayList<WeekRecordForPlayer>();
					}
					//If it has the same number of wins and losses, it's the same, so keep what's
					//already there and add it in.
					else  if (losses == currentBestLosses){
						addRecord = true;
					}
				}
			}
			
			if (addRecord){
				currentBestRecords.add(playerWeekRecord);
				bestRecordsMap.put(key, currentBestRecords);
			}
		}
		
		//Now we have the best records grouped by year and week.  We just have to go through and group
		//them by player now.
		//This map will hold playerId -> weeks they won, which will group everything by player.
		Map<Integer, WeekRecordsForPlayer> playerWeeksWonMap = new HashMap<Integer, WeekRecordsForPlayer>();
		
		List<String> bestRecordKeys = new ArrayList<String>();
		bestRecordKeys.addAll(bestRecordsMap.keySet());

		//Go through each year and week combination.
		for (int index = 0; index < bestRecordKeys.size(); index++){
			String bestRecordKey = bestRecordKeys.get(index);
			
			//Get the records it has.
			List<WeekRecordForPlayer> records = bestRecordsMap.get(bestRecordKey);
			
			for (int recordIndex = 0; recordIndex < records.size(); recordIndex++){
				WeekRecordForPlayer record = records.get(recordIndex);
				
				Player player = record.getPlayer();

				//If the record is for a player we don't care about, then we don't care about it.
				if (players != null && !players.contains(player.getName())){
					continue;
				}
				
				//Otherwise, we do, so get the weeks that we have for the player.
				int playerId = player.getId();
				WeekRecordsForPlayer weeksWonForPlayer = playerWeeksWonMap.get(Integer.valueOf(playerId));
				
				//If we don't have any weeks for them, make some new ones.
				if (weeksWonForPlayer == null){
					weeksWonForPlayer = new WeekRecordsForPlayer(player, new ArrayList<WeekRecord>());
				}
				
				//Add in the record we're on.
				WeekRecord weekRecord = new WeekRecord(record.getSeason(), record.getWeek(), record.getRecord());
				weeksWonForPlayer.getWeekRecords().add(weekRecord);
				
				//Make sure it's grouped by id.
				playerWeeksWonMap.put(Integer.valueOf(playerId), weeksWonForPlayer);
			}
		}

		//Now that everything's grouped by player, we just have to add the groups to what we're returning.
		//The ui will take care of organizing it.
		List<WeekRecordsForPlayer> playerWeeksWonList = new ArrayList<WeekRecordsForPlayer>();
		playerWeeksWonList.addAll(playerWeeksWonMap.values());
		
		return playerWeeksWonList;
	}
	
	/**
	 * 
	 * This function will get the weekly records for the given years, weeks, and players.  There's not much to it.
	 * 
	 * @param years
	 * @param weeks
	 * @param players
	 * @param onlyFinishedGames
	 * @return
	 */
	public List<WeekRecordForPlayer> getPlayerWeekRecords(List<String> years, List<String> weeks, List<String> players, boolean onlyFinishedGames){
		
		String query = SELECT_WEEK_RECORDS;
		
		List<WeekRecordForPlayer> playerWeekRecords = getPlayerWeekRecords(query, years, weeks, players, onlyFinishedGames);
		
		return playerWeekRecords;
	}
	
	/**
	 * 
	 * This function will get the weekly records for the given years, weeks, and players.  It's here so that the caller can
	 * kind of "customize" what it brings back.  It will do the work of adding the "where" clause for the years, weeks, and players
	 * while the caller can say what it wants back from the tables.  It assumes the pick, game, week, season, and player tables
	 * are part of the query with their usual "aliases".
	 * 
	 * It's mainly here because I was doing this in two separate places and I figured it would be better to just do it in one instead.
	 * 
	 * The onlyFinishedGames switch is there to say if we only want records for games that have results.  I kind of forget why I added it
	 * though.
	 * 
	 * @param query
	 * @param years
	 * @param weekKeys
	 * @param players
	 * @param onlyFinishedGames
	 * @return
	 */
	protected List<WeekRecordForPlayer> getPlayerWeekRecords(String query, List<String> years, List<String> weekKeys, List<String> players, boolean onlyFinishedGames){
		
		//Steps to do:
		//	1. Get the "criteria" (the where clause) we should use for the given years, weeks, and players.
		//	2. Add that to the query we were given.
		//	3. Add the arguments for what we were given.
		//	4. Run the query.
		//	5. Map the results.
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		List<WeekRecordForPlayer> playerWeekRecords = new ArrayList<WeekRecordForPlayer>();
		
		try {
			connection = dataSource.getConnection();
			
			String weekRecordsCriteria = createWeekRecordsCriteria(years, weekKeys, players, onlyFinishedGames);
			
			String fullQuery = String.format(query, weekRecordsCriteria);
			
			statement = connection.prepareStatement(fullQuery);
			
			//Players go first...
			int parameterIndex = 1;
			if (players != null && players.size() > 0){
				for (int index = 0; index < players.size(); index++){
					String playerName = players.get(index);
					statement.setString(parameterIndex, playerName);
					parameterIndex++;
				}
			}
			
			//Then weeks
			if (weekKeys != null && weekKeys.size() > 0){
				for (int index = 0; index < weekKeys.size(); index++){
					String weekKey = weekKeys.get(index);
					statement.setString(parameterIndex, weekKey);
					parameterIndex++;
				}
			}
			
			//Then years
			if (years != null && years.size() > 0){
				for (int index = 0; index < years.size(); index++){
					String year = years.get(index);
					statement.setString(parameterIndex, year);
					parameterIndex++;
				}
			}
			
			results = statement.executeQuery();
			
			while (results.next()){
				WeekRecordForPlayer playerWeekRecord = mapPlayerWeekRecord(results);
				playerWeekRecords.add(playerWeekRecord);
			}
		}
		catch (Exception e){
			log.error("Error getting records! years = " + years + ", weekSequenceNumbers = " + weekKeys + ", players = " + players, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return playerWeekRecords;
	}
	
	/**
	 * 
	 * This function will make the "criteria" for getting the weekly records.  It's here because I was doing this in multiple
	 * places and I figured it was best to just do it once.  It will assume that the table aliases used for each table are:
	 * 
	 * 		years - table = season, alias = s
	 * 		weekSequenceNumbers - table = week, alias = w
	 * 		players - table = player, alias = pl
	 * 
	 * If "onlyFinishedGames" is true, it'll add a "criteria" that says the game's winning team id can't be null (using the alias "g"
	 * for the game table).
	 * 
	 * Each criteria is optional and will only be added if it's given.
	 * 
	 * @param years
	 * @param weekKeys
	 * @param players
	 * @param onlyFinishedGames
	 * @return
	 */
	protected String createWeekRecordsCriteria(List<String> years, List<String> weekKeys, List<String> players, boolean onlyFinishedGames){
		
		//Steps to do:
		//	1. Go through each thing we were given and add it if it's got something in it.
		//	2. That's it.
		
		StringBuilder whereClause = new StringBuilder();

		boolean addedWhere = false;

		//First goes player name
		if (players != null && players.size() > 0){
			addedWhere = true;
			whereClause.append("where pl.name in (");
			for (int index = 0; index < players.size(); index++){
				if (index > 0){
					whereClause.append(", ");
				}
				whereClause.append("?");
			}
			whereClause.append(") ");
		}
		
		boolean hasYears = years != null && years.size() > 0;
		boolean hasWeeks = weekKeys != null && weekKeys.size() > 0;
		
		if (hasYears || hasWeeks){
			
			if (!addedWhere){
				whereClause.append("where ");
				addedWhere = true;
			}
			else {
				whereClause.append(" and ");
			}
			
			if (hasWeeks){
				whereClause.append("w.key in (");
				for (int index = 0; index < weekKeys.size(); index++){
					if (index > 0){
						whereClause.append(", ");
					}
					whereClause.append("?");
				}
				whereClause.append(") ");
			}
			
			if (hasYears){
				
				if (hasWeeks){
					whereClause.append(" and ");
				}
				
				whereClause.append("s.year in (");
				
				for (int index = 0; index < years.size(); index++){
					if (index > 0){
						whereClause.append(", ");
					}
					
					whereClause.append("?");
				}
				
				whereClause.append(")");
			}
		}
		
		if (onlyFinishedGames){
			
			if (!addedWhere){
				whereClause.append(" where ");
			}
			else {
				whereClause.append(" and ");
			}
			
			whereClause.append(" g.winning_team_id is not null ");
		}
		
		return whereClause.toString();
	}
	
	/**
	 * 
	 * This function will map a result from a "player week record" query.  It expects the result
	 * to have these columns: season_id, year, week_id, sequence_number, week_label, player_id,
	 * player_name, wins, losses, ties
	 * 
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	protected WeekRecordForPlayer mapPlayerWeekRecord(ResultSet results) throws SQLException {

		//Steps to do:
		//	1. Pull out the variables from the result.
		//	2. Add them into objects and put the objects in the "week record" container.
		
		int seasonId = results.getInt("season_id");
		String year = results.getString("year");
		Season season = new Season(seasonId, year);

		int weekId = results.getInt("week_id");
		int weekSequenceNumber = results.getInt("sequence_number");
		String weekType = results.getString("week_type");
		String weekKey = results.getString("week_key");
		String weekLabel = results.getString("week_label");
		Week week = new Week(weekId, seasonId, weekSequenceNumber, weekType, weekKey, weekLabel);
		
		int playerId = results.getInt("player_id");
		String playerName = results.getString("player_name");
		Player player = new Player(playerId, playerName);
		
		int wins = results.getInt("wins");
		int losses = results.getInt("losses");
		int ties = results.getInt("ties");
		Record record = new Record(player, wins, losses, ties);
		
		WeekRecordForPlayer weekRecord = new WeekRecordForPlayer(player, season, week, record);
		
		return weekRecord;
	}
	
	/**
	 * 
	 * This function will get the "week standings" for the given years, weeks, and players.  It basically just
	 * gets the weekly records for them and makes sure they're sorted so that the best weeks (in terms of number
	 * of wins) come first.
	 * 
	 * @param years
	 * @param weekKeys
	 * @param players
	 * @return
	 */
	public List<WeekRecordForPlayer> getWeekStandings(List<String> years, List<String> weekKeys, List<String> players){
		
		//Steps to do:
		//	1. Make the "where" clause out of what we were given.
		//	2. Add that to the query for the "best weeks".
		//	3. Run it and map the results.
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		List<WeekRecordForPlayer> playerWeekRecords = new ArrayList<WeekRecordForPlayer>();
		
		try {
			connection = dataSource.getConnection();
			
			String recordsCriteria = createRecordsCriteria(years, weekKeys, players, null);
			
			String query = String.format(SELECT_BEST_WEEKS, recordsCriteria);
			
			statement = connection.prepareStatement(query);
			
			//Players go first...
			int parameterIndex = 1;
			if (players != null && players.size() > 0){
				for (int index = 0; index < players.size(); index++){
					String player = players.get(index);
					statement.setString(parameterIndex, player);
					parameterIndex++;
				}
			}
			
			//Then weeks
			if (weekKeys != null && weekKeys.size() > 0){
				for (int index = 0; index < weekKeys.size(); index++){
					String weekKey = weekKeys.get(index);
					statement.setString(parameterIndex, weekKey);
					parameterIndex++;
				}
			}
			
			//Then years
			if (years != null && years.size() > 0){
				for (int index = 0; index < years.size(); index++){
					String year = years.get(index);
					statement.setString(parameterIndex, year);
					parameterIndex++;
				}
			}
			
			results = statement.executeQuery();
			
			while (results.next()){
				WeekRecordForPlayer playerWeekRecord = mapPlayerWeekRecord(results);
				playerWeekRecords.add(playerWeekRecord);
			}
		}
		catch (Exception e){
			log.error("Error getting records! years = " + years + ", weekSequenceNumbers = " + weekKeys + ", players = " + players, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return playerWeekRecords;
	}
	
	/**
	 * 
	 * This function will get all the years that have been completed.  It's here so we can know
	 * what years there are championships for (so we don't include the current year in that).
	 * 
	 * @return
	 */
	public List<String> getAllCompletedYears(){
		
		List<String> completedYears = getCompletedYears(null);
		
		return completedYears;
	}
	
	/**
	 * 
	 * This function will go through the given years and check whether they're completed.
	 * If they are, it'll return them in the list it returns.  It will basically filter out
	 * non-completed years from the given list.
	 * 
	 * It says a "completed year" is one where the superbowl has a winner or when there's a season
	 * with a higher id.  So, it's a little bootleg, but there are contradictions in the data.
	 * 
	 * I tried to make it say "a completed season is one where there are no weeks with games without
	 * winners".  That seemed right, but would have ran into the problem that a completed regular season
	 * would qualify.  So, for the current year, when I filled in the last game for the regular season,
	 * it would have declared the person with the best record to be the champion, when there were still
	 * games left to be played.
	 * 
	 * I couldn't have it rely just on the fact that a superbowl exists because some years we didn't pick
	 * the playoffs.  
	 * 
	 * So, the only way I could figure to do it and make it so:
	 * 
	 * 		1. It showed the champions for years without superbowls.
	 * 		2. It didn't show the champions at the end of the regular season.
	 * 
	 * Was to have it look for either a superbowl winner or for a season after the current one.  This
	 * assumes two things:
	 * 
	 * 		1. From here on, we'll always have a winner for the superbowl and won't stop at the end
	 * 		   of the regular season.
	 * 		2. Each season will have an id that's greater than the last.
	 * 
	 * If either of those change, this code will have to change.  I thought about making it look at the year
	 * and that's probably the way to go.
	 * 
	 * The only other way I could think of would be to find a way to make it easy to add a game from the UI.
	 * Then, before the regular season ended, I could add a playoff game and, before each playoff game ended,
	 * I could add at least one next round game.  If I could do that, then this would work:
	 * 
	 * 		A completed season is one where there are no weeks that have a game without a winner.
	 * 
	 * But, until then, this will have to work.
	 * 
	 * @param years
	 * @return
	 */
	public List<String> getCompletedYears(List<String> years){
		
		//Steps to do:
		//	1. Run the query.
		//	2. Go through the results and add them all to the list
		//	   we're sending back.
		
		List<String> completedYears = new ArrayList<String>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = dataSource.getConnection();
			
			String query = "select s.year " +
						   "from season s " + 
						   //Either a superbowl exists
						   "where (s.id in (select w.season_id " + 
						   				   "from week w " + 
						   				   "where w.key = 'superbowl' " + 
						   				  	     "and w.id in (select g.week_id " + 
						   				  	    			  "from game g " + 
						   				  	    			  "where g.winning_team_id is not null)) " +
						   	     //Or there's another season after the current.
						   		 " or s.id < (select max(id) " + 
						   				  	 "from season) " +
						   		 " ) ";
			
			boolean addedYears = false;
			
			if (years != null && years.size() > 0){
				String yearInParameterString = DatabaseUtil.createInClauseParameterString(years.size());
				query = query + " and s.year in " + yearInParameterString;
				addedYears = true;
			}
			
			statement = connection.prepareStatement(query);
			
			if (addedYears){
				for (int index = 0; index < years.size(); index++){
					String year = years.get(index);
					int parameterIndex = index + 1;
					statement.setString(parameterIndex, year);
				}
			}
			
			results = statement.executeQuery();
			
			while (results.next()){
				String year = results.getString("year");
				completedYears.add(year);
			}
			
			Collections.sort(completedYears);
		}
		catch (Exception e){
			log.error("Error getting completed years!", e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return completedYears;
	}
	
	/**
	 * 
	 * This function will get all the championships for all years and players.  Not much to it.
	 * 
	 * @return
	 */
	public List<Championship> getAllChampionships(){
		
		List<Championship> allChampionships = getChampionships(null, null);
		
		return allChampionships;
	}
	
	/**
	 * 
	 * This function will get the championships for the given years and players.  In order for there
	 * to be a championship, the year has to have been completed (there has to be a result in the superbowl).
	 * 
	 * @param years
	 * @param players
	 * @return
	 */
	public List<Championship> getChampionships(List<String> years, List<String> players){

		//Steps to do:
		//	1. Get all the completed years.
		//	2. Use those years to filter out years that we were given that haven't been
		//	   completed.
		//	3. Run the query that will get the ordered records for the season.
		//	4. Go through those results and keep the best records from each year.  Those
		//	   are the records of the champions.
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		List<Championship> championships = new ArrayList<Championship>();
		
		try {
			connection = dataSource.getConnection();
			
			String recordsForYearCriteria = "";
			
			List<String> allCompletedYears = getAllCompletedYears();
			
			List<String> yearsToUse = new ArrayList<String>();
			
			if (years != null && years.size() > 0){
				for (int index = 0; index < years.size(); index++){
					String year = years.get(index);
					if (allCompletedYears.contains(year)){
						yearsToUse.add(year);
					}
				}
			}
			else {
				yearsToUse = allCompletedYears;
			}
			
			if (yearsToUse.size() == 0){
				return championships;
			}
			
			if (yearsToUse != null && yearsToUse.size() > 0){
				recordsForYearCriteria = " where s.year in " + DatabaseUtil.createInClauseParameterString(yearsToUse.size());
			}
			
			String query = String.format(SELECT_ORDERED_BEST_RECORDS, recordsForYearCriteria);
			
			statement = connection.prepareStatement(query);
			
			int parameterIndex = 1;
			
			if (yearsToUse != null && yearsToUse.size() > 0){
				for (int index = 0; index < yearsToUse.size(); index++){
					String year = yearsToUse.get(index);
					statement.setString(parameterIndex, year);
					parameterIndex++;
				}
			}
						
			results = statement.executeQuery();

			//Now that we have the records for each season and they're ordered by wins, we just have
			//to go through and pick out the best ones for each season.
			String currentYear = null;
			int currentChampionWins = -1;
			int currentChampionLosses = -1;
			
			boolean skipToNextYear = false;
			
			Championship championship = null;
			
			while (results.next()){
				String year = results.getString("year");
				
				//If we haven't picked anything out yet, this is the best record for the most current year, so 
				//it's the record of a championship.
				if (currentYear == null){
					championship = mapChampionship(results);
					championships.add(championship);
					currentYear = year;
					currentChampionWins = championship.getRecord().getWins();
					currentChampionLosses = championship.getRecord().getLosses();
					
					continue;
				}
				
				//Otherwise, if it's the same year that we're on, the only way it could be a championship
				//is if there's the exact same number of wins and losses.
				if (currentYear.equals(year)){

					//If we've already got the best record for the year, this switch will be flipped and that tells
					//us to just keep going to the next year.
					if (skipToNextYear){
						continue;
					}
				
					//Otherwise, get the wins and losses of the record we're on.
					int currentWins = results.getInt("wins");
					int currentLosses = results.getInt("losses");
					
					//If it's the same as the current wins and losses of the current championship, there was a tie, so they're
					//both champions.
					if (currentWins == currentChampionWins && currentLosses == currentChampionLosses){
						championship = mapChampionship(results);
						championships.add(championship);
					}
					//Otherwise, it wasn't a championship year for that guy, so just flip the switch that will make it so we just
					//skip to the record where the year changes.
					else {
						skipToNextYear = true;
					}
				}
				//If we're on a new year, then the best record should be at the top, so it should be a championship.
				else {
					championship = mapChampionship(results);
					championships.add(championship);
					currentYear = year;
					currentChampionWins = championship.getRecord().getWins();
					currentChampionLosses = championship.getRecord().getLosses();
				}
			}
			
			//Now that we have all the championships, we want to go through and filter them so that we only
			//include championships that were won by a player that was given in the function call.
			if (players != null && players.size() > 0){
				List<Championship> filteredChampionships = new ArrayList<Championship>();
				
				for (int index = 0; index < championships.size(); index++){
					Championship currentChampionship = championships.get(index);
					
					for (int playerIndex = 0; playerIndex < players.size(); playerIndex++){
						String player = players.get(playerIndex);
						
						if (player.equals(currentChampionship.getPlayer().getName())){
							filteredChampionships.add(currentChampionship);
						}
					}
				}
				
				championships = filteredChampionships;
			}
		}
		catch (Exception e){
			log.error("Error getting championships! years = " + years + ", players = " + players, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return championships;
	}
	
	/**
	 * 
	 * This function will map a championship from the given result.  It expects the result
	 * to have these columns: season_id, year, player_id, player_name, wins, losses, ties.
	 * It just plops those in a record object and plops that in a championship object.
	 * 
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	protected Championship mapChampionship(ResultSet results) throws SQLException {
		
		int seasonId = results.getInt("season_id");
		String year = results.getString("year");
		Season season = new Season(seasonId, year);

		int playerId = results.getInt("player_id");
		String playerName = results.getString("player_name");
		Player player = new Player(playerId, playerName);
		
		int wins = results.getInt("wins");
		int losses = results.getInt("losses");
 		int ties = results.getInt("ties");
		Record record = new Record(player, wins, losses, ties);
		
		Championship championship = new Championship(player, season, record);
		
		return championship;
	}
	
	/**
	 * 
	 * This function will get the championships that the given players have won in the given years.
	 * It will group them by player and have each player associated with the championships they've
	 * won in the returned list.  If a player didn't win a championship in any of the given years, they
	 * won't be returned in the given list.
	 * 
	 * It will sort them so that the player who has the most championships comes first.
	 * 
	 * @param years
	 * @param players
	 * @return
	 */
	public List<ChampionshipsForPlayer> getPlayerChampionships(List<String> years, List<String> players){
		
		//Steps to do:
		//	1. Get the championships for the given years and players.
		//	2. Group them by player.
		//	3. Sort them so that the player with the most championships comes first.
		
		List<Championship> championships = getChampionships(years, players);
		
		Map<Integer, ChampionshipsForPlayer> playerToChampionshipsMap = new HashMap<Integer, ChampionshipsForPlayer>();
		
		for (int index = 0; index < championships.size(); index++){
			Championship championship = championships.get(index);
			
			Player player = championship.getPlayer();
			
			Integer playerId = Integer.valueOf(player.getId());
			
			ChampionshipsForPlayer playerChampionships = playerToChampionshipsMap.get(playerId);
			
			if (playerChampionships == null){
				playerChampionships = new ChampionshipsForPlayer(player, new ArrayList<Championship>());
			}
			
			List<Championship> championshipsForPlayer = playerChampionships.getChampionships();
			championshipsForPlayer.add(championship);
			playerChampionships.setChampionships(championshipsForPlayer);
			
			playerToChampionshipsMap.put(playerId, playerChampionships);
		}
		
		List<ChampionshipsForPlayer> playerChampionshipsList = new ArrayList<ChampionshipsForPlayer>(playerToChampionshipsMap.values());
		
		Collections.sort(playerChampionshipsList, new PlayerChampionshipsComparator());
		
		return playerChampionshipsList;
	}
	
	/**
	 * 
	 * A dumb class that will sort a list of "championships for players" so that the one with the most
	 * championships come first.
	 * 
	 * @author albundy
	 *
	 */
	protected class PlayerChampionshipsComparator implements Comparator<ChampionshipsForPlayer> {

		public int compare(ChampionshipsForPlayer playerChampionships1, ChampionshipsForPlayer playerChampionships2) {
			
			List<Championship> championships1 = playerChampionships1.getChampionships();
			List<Championship> championships2 = playerChampionships2.getChampionships();
			
			int numberOfChampionships1 = championships1.size();
			int numberOfChampionships2 = championships2.size();
			
			if (numberOfChampionships1 > numberOfChampionships2){
				return -1;
			}
			else if (numberOfChampionships1 < numberOfChampionships2){
				return 1;
			}
			
			return 0;
		}
	}

	/**
	 * 
	 * This function will get the division titles for the given years and players.  In order for there
	 * to be a division titles, the year has to have been completed (there has to be a result in the superbowl).
	 * 
	 * @param years
	 * @param players
	 * @return
	 */
	public List<DivisionTitle> getDivisionTitles(List<String> years, List<String> players){

		//Steps to do:
		//	1. Get all the completed years.
		//	2. Use those years to filter out years that we were given that haven't been
		//	   completed.
		//	3. Run the query that will get the ordered records for the season.
		//	4. Go through those results and keep the best records from each year for each division.  Those
		//	   are the records of the division titles.
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		List<DivisionTitle> divisionTitles = new ArrayList<DivisionTitle>();
		
		try {
			connection = dataSource.getConnection();
			
			String recordsForYearCriteria = "";
			
			List<String> allCompletedYears = getAllCompletedYears();
			
			List<String> yearsToUse = new ArrayList<String>();
			
			if (years != null && years.size() > 0){
				for (int index = 0; index < years.size(); index++){
					String year = years.get(index);
					if (allCompletedYears.contains(year)){
						yearsToUse.add(year);
					}
				}
			}
			else {
				yearsToUse = allCompletedYears;
			}
			
			if (yearsToUse.size() == 0){
				return divisionTitles;
			}
			
			if (yearsToUse != null && yearsToUse.size() > 0){
				recordsForYearCriteria = " where s.year in " + DatabaseUtil.createInClauseParameterString(yearsToUse.size());
			}
			
			String query = String.format(SELECT_ORDERED_BEST_DIVISION_RECORDS, recordsForYearCriteria);
			
			statement = connection.prepareStatement(query);
			
			int parameterIndex = 1;
			
			if (yearsToUse != null && yearsToUse.size() > 0){
				for (int index = 0; index < yearsToUse.size(); index++){
					String year = yearsToUse.get(index);
					statement.setString(parameterIndex, year);
					parameterIndex++;
				}
			}
						
			results = statement.executeQuery();

			//Now that we have the records for each season and they're ordered by wins, we just have
			//to go through and pick out the best ones for each season and division.
			String currentYear = null;
			String currentDivisionName = null;
			int currentDivisionTitleWins = -1;
			int currentDivisionTitleLosses = -1;
			
			boolean skipToNextYear = false;
			
			DivisionTitle divisionTitle = null;
			
			while (results.next()){
				String year = results.getString("year");
				String divisionName = results.getString("division_name");
				
				//If we haven't picked anything out yet, this is the best record for the most current year, so 
				//it's the record of a division title.
				if (currentYear == null){
					divisionTitle = mapDivisionTitle(results);
					divisionTitles.add(divisionTitle);
					currentYear = year;
					currentDivisionName = divisionName;
					currentDivisionTitleWins = divisionTitle.getRecord().getWins();
					currentDivisionTitleLosses = divisionTitle.getRecord().getLosses();
					
					continue;
				}
				
				//Otherwise, if it's the same year that we're on, the only way it could be a division title
				//is if there's the exact same number of wins and losses in the same division.
				if (currentYear.equals(year) && currentDivisionName.equals(divisionName)){

					//If we've already got the best record for the year and division, this switch will be flipped and that tells
					//us to just keep going to the next year.
					if (skipToNextYear){
						continue;
					}
				
					//Otherwise, get the wins and losses of the record we're on.
					int currentWins = results.getInt("wins");
					int currentLosses = results.getInt("losses");
					
					//If it's the same as the current wins and losses of the current division title, there was a tie, so they're
					//both division title holders.
					if (currentWins == currentDivisionTitleWins && currentLosses == currentDivisionTitleLosses){
						divisionTitle = mapDivisionTitle(results);
						divisionTitles.add(divisionTitle);
					}
					//Otherwise, it wasn't a division title year for that guy, so just flip the switch that will make it so we just
					//skip to the record where the year or division changes.
					else {
						skipToNextYear = true;
					}
				}
				//If we're on a new year or new division, then the best record should be at the top, so it should be a division title.
				else {
					divisionTitle = mapDivisionTitle(results);
					divisionTitles.add(divisionTitle);
					currentYear = year;
					currentDivisionName = divisionName;
					currentDivisionTitleWins = divisionTitle.getRecord().getWins();
					currentDivisionTitleLosses = divisionTitle.getRecord().getLosses();
				}
			}
			
			//Now that we have all the division titles, we want to go through and filter them so that we only
			//include division titles that were won by a player that was given in the function call.
			if (players != null && players.size() > 0){
				List<DivisionTitle> filteredDivisionTitles = new ArrayList<DivisionTitle>();
				
				for (int index = 0; index < divisionTitles.size(); index++){
					DivisionTitle currentDivisionTitle = divisionTitles.get(index);
					
					for (int playerIndex = 0; playerIndex < players.size(); playerIndex++){
						String player = players.get(playerIndex);
						
						if (player.equals(currentDivisionTitle.getPlayer().getName())){
							filteredDivisionTitles.add(currentDivisionTitle);
						}
					}
				}
				
				divisionTitles = filteredDivisionTitles;
			}
		}
		catch (Exception e){
			log.error("Error getting division titles! years = " + years + ", players = " + players, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return divisionTitles;
	}
	
	/**
	 * 
	 * This function will map a division title from the given result.  It expects the result
	 * to have these columns: division_id, division_name, division_abbreviation, season_id, year, player_id, player_name, wins, losses, ties.
	 * It just plops those in a record object and plops that in a division title object.
	 * 
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	protected DivisionTitle mapDivisionTitle(ResultSet results) throws SQLException {
		
		int divisionId = results.getInt("division_id");
		String divisionName = results.getString("division_name");
		String divisionAbbreviation = results.getString("division_abbreviation");
		Division division = new Division(divisionId, divisionName, divisionAbbreviation);
		
		int seasonId = results.getInt("season_id");
		String year = results.getString("year");
		Season season = new Season(seasonId, year);

		int playerId = results.getInt("player_id");
		String playerName = results.getString("player_name");
		Player player = new Player(playerId, playerName);
		
		int wins = results.getInt("wins");
		int losses = results.getInt("losses");
 		int ties = results.getInt("ties");
		Record record = new Record(player, wins, losses, ties);
		
		DivisionTitle divisionTitle = new DivisionTitle(division, player, season, record);
		
		return divisionTitle;
	}
	
	/**
	 * 
	 * This function will get the division titles that the given players have won in the given years.
	 * It will group them by player and have each player associated with the division titles they've
	 * won in the returned list.  If a player didn't win a division title in any of the given years, they
	 * won't be returned in the given list.
	 * 
	 * It will sort them so that the player who has the most division titles comes first.
	 * 
	 * @param years
	 * @param players
	 * @return
	 */
	public List<DivisionTitlesForPlayer> getPlayerDivisionTitles(List<String> years, List<String> players){
		
		//Steps to do:
		//	1. Get the division titles for the given years and players.
		//	2. Group them by player.
		//	3. Sort them so that the player with the most division titles comes first.
		
		List<DivisionTitle> divisionTitles = getDivisionTitles(years, players);
		
		Map<Integer, DivisionTitlesForPlayer> playerToDivisionTitleMap = new HashMap<Integer, DivisionTitlesForPlayer>();
		
		for (int index = 0; index < divisionTitles.size(); index++){
			DivisionTitle divisionTitle = divisionTitles.get(index);
			
			Player player = divisionTitle.getPlayer();
			
			Integer playerId = Integer.valueOf(player.getId());
			
			DivisionTitlesForPlayer playerDivisionTitles = playerToDivisionTitleMap.get(playerId);
			
			if (playerDivisionTitles == null){
				playerDivisionTitles = new DivisionTitlesForPlayer(player, new ArrayList<DivisionTitle>());
			}
			
			List<DivisionTitle> divisionTitlesForPlayer = playerDivisionTitles.getDivisionTitles();
			divisionTitlesForPlayer.add(divisionTitle);
			playerDivisionTitles.setDivisionTitles(divisionTitlesForPlayer);
			
			playerToDivisionTitleMap.put(playerId, playerDivisionTitles);
		}
		
		List<DivisionTitlesForPlayer> playerDivisionTitlesList = new ArrayList<DivisionTitlesForPlayer>(playerToDivisionTitleMap.values());
		
		Collections.sort(playerDivisionTitlesList, new PlayerDivisionTitlesComparator());
		
		return playerDivisionTitlesList;
	}
	
	/**
	 * 
	 * A dumb class that will sort a list of "division titles for players" so that the one with the most
	 * division titles comes first.
	 * 
	 * @author albundy
	 *
	 */
	protected class PlayerDivisionTitlesComparator implements Comparator<DivisionTitlesForPlayer> {

		public int compare(DivisionTitlesForPlayer playerChampionships1, DivisionTitlesForPlayer playerChampionships2) {
			
			List<DivisionTitle> divisionTitles1 = playerChampionships1.getDivisionTitles();
			List<DivisionTitle> divisionTitles2 = playerChampionships2.getDivisionTitles();
			
			int numberOfDivisionTitles1 = divisionTitles1.size();
			int numberOfDivisionTitles2 = divisionTitles2.size();
			
			if (numberOfDivisionTitles1 > numberOfDivisionTitles2){
				return -1;
			}
			else if (numberOfDivisionTitles1 < numberOfDivisionTitles2){
				return 1;
			}
			
			return 0;
		}
	}

	/**
	 * 
	 * This function will get the "pick accuracy" summaries for the given years, weeks, players, and teams.  All of the arguments are optional
	 * and will only be included if they're given.
	 * 
	 * This is kind of slow, but I figured it would be good to keep around.
	 * 
	 * @param years
	 * @param weekKeys
	 * @param players
	 * @param teamAbbreviations
	 * @return
	 */
	public List<PickAccuracySummary> getPickAccuracySummariesOldAndSlow(List<String> years, List<String> weekKeys, List<String> players, List<String> teamAbbreviations){

		//Steps to do:
		//	1. Add in each "criteria" that we have to the query.
		//	2. Run the query.
		//	3. Map the results.
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		List<PickAccuracySummary> pickAccuracySummaries = new ArrayList<PickAccuracySummary>();
		
		try {
			
			//Make sure we only get players who have a pick in a year.
			//If they don't have a pick, we don't care about their accuracy.
			if (years != null && years.size() > 0) {
				List<Player> playersForYears = getPlayersForYears(years);
				
				//We can't just use all the players for the years because we might have been given
				//a subset of that.  So, instead, we just have to go through each player that made picks
				//and check if they're in the list.  If they are, then we should keep them.  If they aren't,
				//we don't care about them.
				//At the end of this filtering, we'll have the players who were given in the original list who
				//also made at least one pick in one of the years we were given.
				List<String> playersToUse = new ArrayList<String>();
				for (int index = 0; index < playersForYears.size(); index++) {
					Player player = playersForYears.get(index);
					String name = player.getName();
					if (players.contains(name)) {
						playersToUse.add(name);
					}
				}
				
				players = playersToUse;
			}
			
			StringBuilder whereClauseStringBuilder = new StringBuilder();
			boolean addedWhereClause = true;
			int numberOfPlayers = 0;
			if (players != null){
				numberOfPlayers = players.size();
			}

			int numberOfYears = 0;
			if (years != null){
				numberOfYears = years.size();
			}
			
			int numberOfWeeks = 0;
			if (weekKeys != null){
				numberOfWeeks = weekKeys.size();
			}

			int numberOfTeams = 0;
			if (teamAbbreviations != null){
				numberOfTeams = teamAbbreviations.size();
			}

			if (numberOfPlayers > 0){
				String playerInClauseString = DatabaseUtil.createInClauseParameterString(numberOfPlayers);
				whereClauseStringBuilder.append(" and pl.name in ").append(playerInClauseString);
				addedWhereClause = true;
			}

			if (numberOfYears > 0){
				String yearInClauseString = DatabaseUtil.createInClauseParameterString(numberOfYears);

				if (addedWhereClause){
					whereClauseStringBuilder.append(" and ");
				}
				else {
					whereClauseStringBuilder.append(" where ");
					addedWhereClause = true;
				}

				whereClauseStringBuilder.append(" s.year in ").append(yearInClauseString);
			}
			
			if (numberOfTeams > 0){
				String teamInClauseString = DatabaseUtil.createInClauseParameterString(numberOfTeams);

				if (addedWhereClause){
					whereClauseStringBuilder.append(" and ");
				}
				else {
					whereClauseStringBuilder.append(" where ");
					addedWhereClause = true;
				}

				whereClauseStringBuilder.append(" t.abbreviation in ").append(teamInClauseString);
			}

			String whereClause = whereClauseStringBuilder.toString();
			
			String weeksWhereClause = "";
			if (numberOfWeeks > 0){
				String weekInClauseString = DatabaseUtil.createInClauseParameterString(numberOfWeeks);

				weeksWhereClause = " and w.key in " + weekInClauseString;
				
				whereClauseStringBuilder.append(" wkey in ").append(weekInClauseString);
			}

			String pickAccuracyQuery = String.format(OLD_AND_SLOW_SELECT_PICK_ACCURACY_SUMMARY, 
													 weeksWhereClause, weeksWhereClause, weeksWhereClause, weeksWhereClause, weeksWhereClause,
													 weeksWhereClause, weeksWhereClause, weeksWhereClause, weeksWhereClause, weeksWhereClause,
													 weeksWhereClause,
												     whereClause);
			
			connection = dataSource.getConnection();
			
			statement = connection.prepareStatement(pickAccuracyQuery);
			
			int parameterIndex = 1;
			
			if (numberOfWeeks > 0){
				for (int i = 0; i < 11; i++){
					for (int index = 0; index < numberOfWeeks; index++){
						String weekKey = weekKeys.get(index);
						statement.setString(parameterIndex, weekKey);
						parameterIndex++;
					}
				}
			}
			
			for (int index = 0; index < numberOfPlayers; index++){
				String player = players.get(index);
				statement.setString(parameterIndex, player);
				parameterIndex++;
			}
			
			for (int index = 0; index < numberOfYears; index++){
				String year = years.get(index);
				statement.setString(parameterIndex, year);
				parameterIndex++;
			}
			
			for (int index = 0; index < numberOfTeams; index++){
				String teamAbbreviation = teamAbbreviations.get(index);
				statement.setString(parameterIndex, teamAbbreviation);
				parameterIndex++;
			}

			long queryStart = System.currentTimeMillis();
			
			results = statement.executeQuery();

			long queryElapsed = System.currentTimeMillis() - queryStart;
			
			log.info("Getting pick summaries took " + queryElapsed + " ms");
			
			while (results.next()){
				PickAccuracySummary pickAccuracySummary = mapPickAccuracySummary(results);
				pickAccuracySummaries.add(pickAccuracySummary);
			}
		}
		catch (Exception e){
			log.error("Error getting pick accuracy summary!  players = " + players + ", years = " + years + ", teamAbbreviations = " + teamAbbreviations, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return pickAccuracySummaries;
	}
	
	
	/**
	 * 
	 * This is the "new and improved" pick accuracy function.  It uses the new and improved query and just stuffs
	 * the given arguments in it.
	 * 
	 * @param years
	 * @param weekKeys
	 * @param players
	 * @param teamAbbreviations
	 * @return
	 */
	public List<PickAccuracySummary> getPickAccuracySummariesB2(List<String> years, List<String> weekKeys, List<String> players, List<String> teamAbbreviations){

		//Steps to do:
		//	1. Add in each "criteria" that we have to the query.
		//	2. Run the query.
		//	3. Map the results.
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		List<PickAccuracySummary> pickAccuracySummaries = new ArrayList<PickAccuracySummary>();
		
		try {
			
			//Make sure we only get players who have a pick in a year.
			//If they don't have a pick, we don't care about their accuracy.
			if (years != null && years.size() > 0) {
				List<Player> playersForYears = getPlayersForYears(years);
				
				//We can't just use all the players for the years because we might have been given
				//a subset of that.  So, instead, we just have to go through each player that made picks
				//and check if they're in the list.  If they are, then we should keep them.  If they aren't,
				//we don't care about them.
				//At the end of this filtering, we'll have the players who were given in the original list who
				//also made at least one pick in one of the years we were given.
				List<String> playersToUse = new ArrayList<String>();
				for (int index = 0; index < playersForYears.size(); index++) {
					Player player = playersForYears.get(index);
					String name = player.getName();
					if (players.contains(name)) {
						playersToUse.add(name);
					}
				}
				
				//If we have players who have made a pick in one of the years, use them.
				//If we don't (the size is 0), just roll with the players we were given.
				//In that case, we were given players who didn't make any picks in any of
				//the years we have.  That means we'll bring back no results and that's what we want.
				//Otherwise, we'll bring back results without using a player in the where clause and
				//we don't want that.
				if (playersToUse.size() > 0){
					players = playersToUse;
				}
			}
			
			//Get the sizes once so we can reuse them over and over.
			int numberOfPlayers = 0;
			if (players != null){
				numberOfPlayers = players.size();
			}

			int numberOfYears = 0;
			if (years != null){
				numberOfYears = years.size();
			}
			
			int numberOfWeeks = 0;
			if (weekKeys != null){
				numberOfWeeks = weekKeys.size();
			}

			int numberOfTeams = 0;
			if (teamAbbreviations != null){
				numberOfTeams = teamAbbreviations.size();
			}

			//Now we have to put the where clauses in the query's placeholders.
			//First up is the "week and season" where clause.  It looks like this
			//in the query:
			//  and g.week_id in (select w.id from week w ${WEEK_AND_SEASON_WHERE_CLAUSE})
			//
			//And, when filled in, it'll look like this:
			//  and g.week_id in (select w.id from week w where
			//											  season:
			//											  w.season_id in (select id from season where year in (?, ?, ?))
			//											  week
			//											  w.week_key in (?, ?, ?)
			//
			//The year goes first and then the "week key" goes second for each clause.
			
			StringBuilder weekAndYearWhereClauseStringBuilder = new StringBuilder();

			boolean addedWeekAndYearWhereClause = false;

			//Add in the years if we have them.
			if (numberOfYears > 0){
				String yearInClauseString = DatabaseUtil.createInClauseParameterString(numberOfYears);

				//Dumb
				if (addedWeekAndYearWhereClause){
					weekAndYearWhereClauseStringBuilder.append(" and ");
				}
				else {
					weekAndYearWhereClauseStringBuilder.append(" where ");
					addedWeekAndYearWhereClause = true;
				}

				weekAndYearWhereClauseStringBuilder.append(" w.season_id in (select id from season where year in ").append(yearInClauseString).append(" ) ");
			}
			
			//Then add in the weeks.
			if (numberOfWeeks > 0){
				String weekInClauseString = DatabaseUtil.createInClauseParameterString(numberOfWeeks);
				
				if (addedWeekAndYearWhereClause){
					weekAndYearWhereClauseStringBuilder.append(" and ");
				}
				else {
					weekAndYearWhereClauseStringBuilder.append(" where ");
					addedWeekAndYearWhereClause = true;
				}

				weekAndYearWhereClauseStringBuilder.append(" w.key in ").append(weekInClauseString);
			}
			
			//And we'll have a where clause we can put into every sub query that needs it.
			String weekAndYearWhereClause = weekAndYearWhereClauseStringBuilder.toString();

			//Same deal for the team and player.
			//
			//It's in the query like this:
			//	 ${PLAYER_AND_TEAM_WHERE_CLAUSE} 
			//
			//And it'll be like this when "expanded":
			//	and atapv.abbrevaion in (?, ?, ?)
			//  and atap.player_name in (?, ?, ?)
			
			StringBuilder playerAndTeamWhereClauseStringBuilder = new StringBuilder();
			boolean addedPlayerAndTeamWhereClause = false;
			
			//Add in the teams first.
			if (numberOfTeams > 0){
				String teamInClauseString = DatabaseUtil.createInClauseParameterString(numberOfTeams);

				if (addedPlayerAndTeamWhereClause){
					playerAndTeamWhereClauseStringBuilder.append(" and ");
				}
				else {
					playerAndTeamWhereClauseStringBuilder.append(" where ");
					addedPlayerAndTeamWhereClause = true;
				}

				playerAndTeamWhereClauseStringBuilder.append(" atapv.abbreviation in ").append(teamInClauseString);
			}
			
			//Then add in the players.
			if (numberOfPlayers > 0){
				String playerInClauseString = DatabaseUtil.createInClauseParameterString(numberOfPlayers);
				
				if (addedPlayerAndTeamWhereClause){
					playerAndTeamWhereClauseStringBuilder.append(" and ");
				}
				else {
					playerAndTeamWhereClauseStringBuilder.append(" where ");
					addedPlayerAndTeamWhereClause = true;
				}

				playerAndTeamWhereClauseStringBuilder.append(" atapv.player_name in ").append(playerInClauseString);
			}
			
			String playerAndTeamWhereClause = playerAndTeamWhereClauseStringBuilder.toString();

			//Now we have the where clauses for the weeks and seasons and the teams and players.
			//So, we just have to plop them in the query and then add the parameters.
			String query = PICK_ACCURACY_SUMMARY_BETTER2;
			
			//Plop them in the query.  The week and season where clause is used by 11 sub queries.
			//The player and team where clause is only used in one place.
			query = query.replace("${WEEK_AND_SEASON_WHERE_CLAUSE}", weekAndYearWhereClause);
			query = query.replace("${PLAYER_AND_TEAM_WHERE_CLAUSE}", playerAndTeamWhereClause);
			
			String pickAccuracyQuery = query;
			
			connection = dataSource.getConnection();
			
			statement = connection.prepareStatement(pickAccuracyQuery);
			
			//Now that it's ready to go, we just have to add in the parameters...
			int parameterIndex = 1;

			//We have to put in the parameters for each sub query first.  There are 11 sub queries
			//in the big query, so this will loop through 11 times and set the parameters for
			//each one.
			//Each sub query is the same.  It queries the weeks table, but puts the year first and then the week,
			//so that's the order the parameter values need to go in.
			for (int i = 0; i < 11; i++){
				//Add in the years if we had some.
				if (numberOfYears > 0){
					for (int index = 0; index < years.size(); index++){
						String year = years.get(index);
						statement.setString(parameterIndex, year);
						parameterIndex++;
					}
				}
				
				//Then add in the weeks.
				if (numberOfWeeks > 0){
					for (int index = 0; index < weekKeys.size(); index++){
						String weekKey = weekKeys.get(index);
						statement.setString(parameterIndex, weekKey);
						parameterIndex++;
					}
				}
			}

			//The team and player where clause just appears once, so we only have to add each team
			//and player once (team goes first).
			for (int index = 0; index < numberOfTeams; index++){
				String teamAbbreviation = teamAbbreviations.get(index);
				statement.setString(parameterIndex, teamAbbreviation);
				parameterIndex++;
			}
			
			for (int index = 0; index < numberOfPlayers; index++){
				String player = players.get(index);
				statement.setString(parameterIndex, player);
				parameterIndex++;
			}

			//And now we're done, so it's time to kick off the query.
			long queryStart = System.currentTimeMillis();
			
			results = statement.executeQuery();

			long queryElapsed = System.currentTimeMillis() - queryStart;
			
			//So we can see how long it takes...
			log.info("Getting pick summaries took " + queryElapsed + " ms");
			
			//And we just have to map the results and send them back.
			while (results.next()){
				PickAccuracySummary pickAccuracySummary = mapPickAccuracySummary(results);
				pickAccuracySummaries.add(pickAccuracySummary);
			}
		}
		catch (Exception e){
			log.error("Error getting pick accuracy summary!  players = " + players + ", years = " + years + ", teamAbbreviations = " + teamAbbreviations, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return pickAccuracySummaries;
	}

	/**
	 * 
	 * This function will map the result of the pick accuracy summary query.  It goes hand in hand with the
	 * SELECT_PICK_ACCURACY_SUMMARY query so the columns it picks will match that query.
	 * 
	 * @param results
	 * @return
	 * @throws SQLException
	 */
	protected PickAccuracySummary mapPickAccuracySummary(ResultSet results) throws SQLException {
		
		int playerId = results.getInt("player_id");
		String playerName = results.getString("player_name");
		
		Player player = new Player(playerId, playerName);
		
		int teamId = results.getInt("team_id");
		int divisionId = results.getInt("team_division_id");
		String teamCity = results.getString("team_city");
		String teamNickname = results.getString("team_nickname");
		String teamAbbreviation = results.getString("team_abbreviation");

		Team team = new Team(teamId, divisionId, teamCity, teamNickname, teamAbbreviation, null, null, null);
		
		int actualWins = results.getInt("actual_wins");
		int actualLosses = results.getInt("actual_losses");
		int actualTies = results.getInt("actual_ties");
		int predictedWins = results.getInt("predicted_wins");
		int predictedLosses = results.getInt("predicted_losses");
		int timesRight = results.getInt("times_right");
		int timesWrong = results.getInt("times_wrong");
		int timesPickedToWinRight = results.getInt("times_picked_to_win_right");
		int timesPickedToWinWrong = results.getInt("times_picked_to_win_wrong");
		int timesPickedToLoseRight = results.getInt("times_picked_to_lose_right");
		int timesPickedToLoseWrong = results.getInt("times_picked_to_lose_wrong");
		
		PickAccuracySummary pickAccuracySummary = new PickAccuracySummary(player, team, actualWins, actualLosses, actualTies, predictedWins, predictedLosses, timesRight, timesWrong,
																		  timesPickedToWinRight, timesPickedToWinWrong, timesPickedToLoseRight, timesPickedToLoseWrong);
		
		return pickAccuracySummary;
	}
	
	/**
	 * 
	 * This function will get the "pick splits" for the given years, weeks, players, and teams.  This basically breaks down the picks
	 * into "home" and "away" picks and puts the players in the groups that they chose.
	 * 
	 * @param years
	 * @param weekKeys
	 * @param playerNames
	 * @param teams
	 * @return
	 */
	public List<PickSplit> getPickSplits(List<String> years, List<String> weekKeys, List<String> playerNames, List<String> teams){
		
		//Steps to do:
		//	1. Make the query and add in the where clause criteria.
		//	2. Run the query.
		//	3. Map the results.
		
		List<PickSplit> pickSplits = new ArrayList<PickSplit>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		StringBuilder stringBuilder = new StringBuilder(SELECT_PICK_SPLIT_BASE);
		
		try {
			connection = getConnection();
			
			String whereBase = "";
			
			boolean addedWhere = false;
			boolean hasPlayers = Util.hasSomething(playerNames);
			boolean hasYears = Util.hasSomething(years);
			boolean hasWeeks = Util.hasSomething(weekKeys);
			boolean hasTeams = Util.hasSomething(teams);
			
			if (hasPlayers){
				if (addedWhere){
					whereBase = whereBase + " and ";
				}
				else {
					whereBase = "where ";
					addedWhere = true;
				}
				
				String inParameterString = DatabaseUtil.createInClauseParameterString(playerNames.size());
				whereBase = whereBase + " pl.name in " + inParameterString;
			}
			
			if (hasYears){
				if (addedWhere){
					whereBase = whereBase + " and ";
				}
				else {
					whereBase = "where ";
					addedWhere = true;
				}
				
				String inParameterString = DatabaseUtil.createInClauseParameterString(years.size());
				whereBase = whereBase + " s.year in " + inParameterString;
			}
			
			if (hasWeeks){
				
				if (addedWhere){
					whereBase = whereBase + " and ";
				}
				else {
					whereBase = "where ";
					addedWhere = true;
				}
				
				String inParameterString = DatabaseUtil.createInClauseParameterString(weekKeys.size());
				whereBase = whereBase + " w.key in " + inParameterString;
			}
			
			if (hasTeams){
				
				if (addedWhere){
					whereBase = whereBase + " and ";
				}
				else {
					whereBase = "where ";
					addedWhere = true;
				}
				
				String inParameterString = DatabaseUtil.createInClauseParameterString(teams.size());
				whereBase = whereBase + " (home_team.abbreviation in " + inParameterString + 
									 	  "or away_team.abbreviation in " + inParameterString + ") ";
			}
			
			stringBuilder.append(whereBase);
			
			String orderBy = "order by year asc, sequence_number asc, game_id asc, player_id asc ";
			
			stringBuilder.append(orderBy);
			
			String query = stringBuilder.toString();
			
			statement = connection.prepareStatement(query);
			
			int parameterIndex = 1;
			
			if (hasPlayers){
				for (int index = 0; index < playerNames.size(); index++){
					String playerName = playerNames.get(index);
					statement.setString(parameterIndex, playerName);
					parameterIndex++;
				}
			}
			
			if (hasYears){
				for (int index = 0; index < years.size(); index++){
					String year = years.get(index);
					statement.setString(parameterIndex, year);
					parameterIndex++;
				}
			}
			
			if (hasWeeks){
				for (int index = 0; index < weekKeys.size(); index++){
					String weekKey = weekKeys.get(index);
					statement.setString(parameterIndex, weekKey);
					parameterIndex++;
				}
			}
			
			if (hasTeams){
				for (int index = 0; index < teams.size(); index++){
					String team = teams.get(index);
					statement.setString(parameterIndex, team);
					parameterIndex++;
				}
				
				for (int index = 0; index < teams.size(); index++){
					String team = teams.get(index);
					statement.setString(parameterIndex, team);
					parameterIndex++;
				}
			}
			
			results = statement.executeQuery();
			
			int currentGameId = -1;
			int winningTeamId = -1;
			String year = null;
			int weekSequenceNumber = -1;
			String weekType = null;
			String weekKey = null;
			String weekLabel = null;
			String homeTeam = null;
			String awayTeam = null;
			String winningTeam = null;
			String player = null;
			String pickTeam = null;
			
			List<String> homeTeamPlayers = null;
			List<String> awayTeamPlayers = null;
			
			PickSplit currentPickSplit = null;
			
			while (results.next()){
				
				int gameId = results.getInt("game_id");
				
				if (gameId != currentGameId){
					
					//Sort the players by name so that they show up in a consistent order.
					if (currentPickSplit != null){
						Collections.sort(currentPickSplit.getHomeTeamPlayers());
						Collections.sort(currentPickSplit.getAwayTeamPlayers());
						pickSplits.add(currentPickSplit);
					}
					
					currentGameId = gameId;
					
					winningTeamId = results.getInt("winning_team_id");
					year = results.getString("year");
					weekSequenceNumber = results.getInt("sequence_number");
					weekType = results.getString("week_type");
					weekKey = results.getString("week_key");
					weekLabel = results.getString("week_label");
					homeTeam = results.getString("home_team");
					awayTeam = results.getString("away_team");
					winningTeam = results.getString("winning_team");
					
					currentPickSplit = new PickSplit();
					currentPickSplit.setYear(year);
					currentPickSplit.setWeekSequenceNumber(weekSequenceNumber);
					currentPickSplit.setWeekType(weekType);
					currentPickSplit.setWeekKey(weekKey);
					currentPickSplit.setWeekLabel(weekLabel);
					currentPickSplit.setHomeTeamAbbreviation(homeTeam);
					currentPickSplit.setAwayTeamAbbreviation(awayTeam);
					if (winningTeamId == NFLPicksConstants.TIE_WINNING_TEAM_ID){
						currentPickSplit.setWinningTeamAbbreviation(NFLPicksConstants.TIE_TEAM_ABBREVIATION);
					}
					else {
						currentPickSplit.setWinningTeamAbbreviation(winningTeam);
					}
					
					homeTeamPlayers = new ArrayList<String>();
					awayTeamPlayers = new ArrayList<String>();
					currentPickSplit.setHomeTeamPlayers(homeTeamPlayers);
					currentPickSplit.setAwayTeamPlayers(awayTeamPlayers);
				}
				
				pickTeam = results.getString("pick_team");
				player = results.getString("player");
				
				
				if (homeTeam.equals(pickTeam)){
					homeTeamPlayers.add(player);
				}
				else if (awayTeam.equals(pickTeam)){
					awayTeamPlayers.add(player);
				}
			}
			
			if (currentPickSplit != null){
				//We want the player names sorted in a consistent order.
				Collections.sort(currentPickSplit.getHomeTeamPlayers());
				Collections.sort(currentPickSplit.getAwayTeamPlayers());
				pickSplits.add(currentPickSplit);
			}
		}
		catch (Exception e){
			log.error("Error getting pick splits!  years = " + years + ", weekSequenceNumbers = " + weekKeys + ", playerNames = " + playerNames + ", teams = " + teams, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return pickSplits;
	}
	
	/**
	 * 
	 * This function will get the season long records for the given years and players.  Not much to it.
	 * 
	 * @param years
	 * @param weekKeys
	 * @param players
	 * @return
	 */
	public List<SeasonRecordForPlayer> getSeasonRecords(List<String> years, List<String> weekKeys, List<String> players){
		
		//Steps to do:
		//	1. Get all the championships since we want to mark them if somebody won
		//	   one in a season.
		//	2. Go through and get the records for the years, weeks, and players we were given.
		//	3. Map each one and that's it.
		
		List<SeasonRecordForPlayer> seasonRecords = new ArrayList<SeasonRecordForPlayer>();
		
		List<Championship> championships = getAllChampionships();
		
		//Here so we can easily get a championship for a year and player without having to 
		//go through the list every single time.
		//It will be used during the mapping of the results.
		Set<String> championshipKeys = new HashSet<String>();
		
		for (int index = 0; index < championships.size(); index++){
			Championship championship = championships.get(index);
			String year = championship.getSeason().getYear();
			String player = championship.getPlayer().getName();
			String key = year + "-" + player;
			championshipKeys.add(key);
		}
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			connection = getConnection();
			
			boolean addedWhere = false;
			
			String whereClause = "";
			
			boolean hasYears = Util.hasSomething(years);
			if (hasYears){
				if (addedWhere){
					whereClause = whereClause + " and ";
				}
				else {
					whereClause = whereClause + " where ";
					addedWhere = true;
				}
				
				String inParameterString = DatabaseUtil.createInClauseParameterString(years.size());
				whereClause = whereClause + " s.year in " + inParameterString;
			}
			
			//will this have to be smarter?
			//no i don't think that's a good idea ... it'll just have to target something other than week number
			//it needs to be a "compact representation" of the week
			boolean hasWeeks = Util.hasSomething(weekKeys);
			if (hasWeeks){
				if (addedWhere){
					whereClause = whereClause + " and ";
				}
				else {
					whereClause = whereClause + " where ";
					addedWhere = true;
				}
				
				String inParameterString = DatabaseUtil.createInClauseParameterString(weekKeys.size());
				whereClause = whereClause + " w.key in " + inParameterString;
			}
			
			boolean hasPlayers = Util.hasSomething(players);
			if (hasPlayers){
				if (addedWhere){
					whereClause = whereClause + " and ";
				}
				else {
					whereClause = whereClause + " where ";
					addedWhere = true;
				}
				String inParameterString = DatabaseUtil.createInClauseParameterString(players.size());
				whereClause = whereClause + " pl.name in " + inParameterString;
			}
			
			String query = String.format(SELECT_SEASON_RECORDS, whereClause);
			
			statement = connection.prepareStatement(query);
			int parameterIndex = 1;
			
			if (hasYears){
				for (int index = 0; index < years.size(); index++){
					String year = years.get(index);
					statement.setString(parameterIndex, year);
					parameterIndex++;
				}
			}
			
			if (hasWeeks){
				for (int index = 0; index < weekKeys.size(); index++){
					String weekKey = weekKeys.get(index);
					statement.setString(parameterIndex, weekKey);
					parameterIndex++;
				}
			}
			
			if (hasPlayers){
				for (int index = 0; index < players.size(); index++){
					String player = players.get(index);
					statement.setString(parameterIndex, player);
					parameterIndex++;
				}
			}
			
			results = statement.executeQuery();
			
			while (results.next()){
				//Pass in the championship "keys" so we can tell whether this season was a championship one
				//for the player.
				SeasonRecordForPlayer seasonRecord = mapSeasonRecordForPlayer(results, championshipKeys);
				seasonRecords.add(seasonRecord);
			}
		}
		catch (Exception e){
			log.error("Error getting season records!  years = " + years + ", weeks = " + weekKeys + ", players = " + players, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return seasonRecords;
	}
	
	/**
	 * 
	 * This function will map the result of a season record query and include whether that season was a championship one
	 * for the player with the record.  Not much to it.
	 * 
	 * @param results
	 * @param championshipKeys
	 * @return
	 * @throws SQLException
	 */
	protected SeasonRecordForPlayer mapSeasonRecordForPlayer(ResultSet results, Set<String> championshipKeys) throws SQLException {
		
		int seasonId = results.getInt("season_id");
		String year = results.getString("year");
		Season season = new Season(seasonId, year);

		int playerId = results.getInt("player_id");
		String playerName = results.getString("player_name");
		Player player = new Player(playerId, playerName);
		
		int wins = results.getInt("wins");
		int losses = results.getInt("losses");
		int ties = results.getInt("ties");
		Record record = new Record(player, wins, losses, ties);
		
		String championshipKey = year + "-" + playerName;
		boolean championship = championshipKeys.contains(championshipKey);
		
		SeasonRecordForPlayer seasonRecordForPlayer = new SeasonRecordForPlayer(player, season, record, championship);
		
		return seasonRecordForPlayer;
	}

	/**
	 * 
	 * This gets all the divisions with all the players in them.
	 * 
	 * @return
	 */
	public List<Division> getDivisions(){
		
		List<Division> divisions = getDivisions(false);
		
		return divisions;
	}
	
	//this whole thing needs to be redone
	//for every object, there should be one "get" function and all the others should call that
	//then, there should be special functions for running the queries that get the stats
	//and stuff like that.
	
	/**
	 * 
	 * This function gets all the divisions.  If shallow is true, it'll just get them and not get
	 * the nested stuff.
	 * 
	 * @param shallow
	 * @return
	 */
	public List<Division> getDivisions(boolean shallow){
		
		List<Division> divisions = new ArrayList<Division>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_DIVISION;
			connection = getConnection();
			statement = connection.prepareStatement(query);
			results = statement.executeQuery();
			
			while (results.next()){
				Division division = mapDivision(results, shallow);
				divisions.add(division);
			}
		}
		catch (Exception e){
			log.error("Error getting divisions! shallow = " + shallow, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return divisions;
	}
	
	/**
	 * 
	 * Gets a division with the given id.  If shallow is true, it'll get the players in each
	 * division too.
	 * 
	 * @param id
	 * @param shallow
	 * @return
	 */
	public Division getDivision(int id, boolean shallow){
		
		Division division = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_DIVISION + 
						   "where id = ? ";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			results = statement.executeQuery();
			
			if (results.next()){
				division = mapDivision(results, shallow);
			}
		}
		catch (Exception e){
			log.error("Error getting division! id = " + id + ", shallow = " + shallow, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return division;
	}
	
	/**
	 * 
	 * Gets the division with the give name.  If shallow is false, it'll get the players
	 * in the division too.
	 * 
	 * @param name
	 * @param shallow
	 * @return
	 */
	public Division getDivisionByName(String name, boolean shallow){
		
		Division division = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_DIVISION + 
						   "where name = ? ";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, name);
			results = statement.executeQuery();
			
			if (results.next()){
				division = mapDivision(results, shallow);
			}
		}
		catch (Exception e){
			log.error("Error getting division by name! name = " + name + ", shallow = " + shallow, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return division;
	}
	
	/**
	 * 
	 * This function will get the divisions with the given abbreviations.  If shallow is false, it'll
	 * get the nested stuff too.
	 * 
	 * @param abbreviations
	 * @param shallow
	 * @return
	 */
	public List<Division> getDivisionsByAbbreviation(List<String> abbreviations, boolean shallow){
		
		List<Division> divisions = new ArrayList<Division>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String inClause = DatabaseUtil.createInClauseParameterString(abbreviations.size());
			
			String query = SELECT_DIVISION + 
					   "where abbreviation in " + inClause;
			
			connection = getConnection();
			statement = connection.prepareStatement(query);
			for (int index = 0; index < abbreviations.size(); index++){
				String abbreviation = abbreviations.get(index);
				int parameterNumber = index + 1;
				statement.setString(parameterNumber, abbreviation);
			}
			
			results = statement.executeQuery();
			
			if (results.next()){
				Division division = mapDivision(results, shallow);
				divisions.add(division);
			}
		}
		catch (Exception e){
			log.error("Error getting divisions by abbreviation! abbreviations = " + abbreviations + ", shallow = " + shallow, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return divisions;
	}
	
	/**
	 * 
	 * This function will get the division by the given abbreviation.  If shallow is false,
	 * it'll get all the nested stuff too.
	 * 
	 * @param abbreviation
	 * @param shallow
	 * @return
	 */
	public Division getDivisionByAbbreviation(String abbreviation, boolean shallow){
		
		Division division = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_DIVISION + 
						   "where abbreviation = ? ";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, abbreviation);
			results = statement.executeQuery();
			
			if (results.next()){
				division = mapDivision(results, shallow);
			}
		}
		catch (Exception e){
			log.error("Error getting division by abbreviation! abbreviation = " + abbreviation + ", shallow = " + shallow, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return division;
	}
	
	/**
	 * 
	 * This function will save the given division object.  If it has an id, it'll do an update.
	 * If it doesn't, it'll do an insert.
	 * 
	 * @param division
	 * @return
	 */
	public Division saveDivision(Division division){
		
		//Steps to do:
		//	1. Pull out their id.
		//	2. If they don't have one, insert them.
		//	3. Otherwise, update them.
		//	4. Pull out what was saved using their name.
		
		int id = division.getId();
		
		int numberOfRowsAffected = 0;
		
		if (id <= 0){
			numberOfRowsAffected = insertDivision(division);
		}
		else {
			numberOfRowsAffected = updateDivision(division);
		}
		
		Division savedDivision = null;
		
		if (numberOfRowsAffected == 1){
			savedDivision = getDivisionByName(division.getName(), true);
		}
		
		return savedDivision;
	}
	
	/**
	 * 
	 * This function will insert the given player in the database.
	 * Not much to it.
	 * 
	 * @param division
	 * @return
	 */
	protected int insertDivision(Division division){
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(INSERT_DIVISION);
			statement.setString(1, division.getName());
			statement.setString(2, division.getAbbreviation());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error inserting division! division = " + division, e);
			rollback(connection);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * This function will update the given player in the database.  Not much to it.
	 * 
	 * @param division
	 * @return
	 */
	protected int updateDivision(Division division){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(UPDATE_DIVISION);
			statement.setString(1, division.getName());
			statement.setString(2, division.getAbbreviation());
			statement.setInt(3, division.getId());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error updating division! division = " + division, e);
			rollback(connection);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * This function will map the results of the query to the division table to an object.
	 * If shallow is false, it'll get the nested stuff and include it too.
	 * 
	 * @param results
	 * @param shallow
	 * @return
	 * @throws SQLException
	 */
	protected Division mapDivision(ResultSet results, boolean shallow) throws SQLException {
		
		Division division = new Division();
		division.setId(results.getInt("id"));
		division.setName(results.getString("name"));
		division.setAbbreviation(results.getString("abbreviation"));
		
		if (!shallow){
			int divisionId = division.getId();
			List<Player> players = getPlayersForDivision(divisionId);
			division.setPlayers(players);
		}
		
		return division;
	}
	
	/**
	 * 
	 * This function will get the records for the given players in the given years and weeks.  All the arguments
	 * are optional and will only be included if they're given.
	 * 
	 * The abbreviations should be used for the divisions.
	 * 
	 * @param divisions
	 * @param years
	 * @param weekKeys
	 * @param players
	 * @return
	 */
	public List<DivisionRecord> getDivisionRecords(List<String> divisions, List<String> years, List<String> weekKeys, List<String> players, List<String> teams){
		
		//Steps to do:
		//	1. Add in the arguments we were given to the query if they're there.
		//	2. Run the query.
		//	3. Send back what it found.
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		List<DivisionRecord> divisionRecords = new ArrayList<DivisionRecord>();
		
		try {
			List<Division> divisionList = new ArrayList<Division>();
			if (divisions != null && divisions.size() > 0){
				divisionList = getDivisionsByAbbreviation(divisions, true);
			}
			else {
				divisionList = getDivisions(true);
			}
			
			Map<String, DivisionRecord> divisionMap = new HashMap<String, DivisionRecord>();
			
			for (int index = 0; index < divisionList.size(); index++){
				Division division = divisionList.get(index);
				DivisionRecord divisionRecord = new DivisionRecord();
				divisionRecord.setDivision(division);
				divisionMap.put(division.getAbbreviation(), divisionRecord);
				divisionRecords.add(divisionRecord);
			}
			
			connection = dataSource.getConnection();
			
			String recordsCriteria = createDivisionRecordsCriteria(divisions, years, weekKeys, players, teams);
			
			String query = String.format(SELECT_DIVISION_RECORD, recordsCriteria);
			
			statement = connection.prepareStatement(query);
			
			//Players go first...
			int parameterIndex = 1;
			if (players != null && players.size() > 0){
				for (int index = 0; index < players.size(); index++){
					String player = players.get(index);
					statement.setString(parameterIndex, player);
					parameterIndex++;
				}
			}
			
			//then divisions...
			if (divisions != null && divisions.size() > 0){
				for (int index = 0; index < divisions.size(); index++){
					String division = divisions.get(index);
					statement.setString(parameterIndex, division);
					parameterIndex++;
				}
			}
			
			//Then weeks
			if (weekKeys != null && weekKeys.size() > 0){
				for (int index = 0; index < weekKeys.size(); index++){
					String weekKey = weekKeys.get(index);
					statement.setString(parameterIndex, weekKey);
					parameterIndex++;
				}
			}
			
			//Then years
			if (years != null && years.size() > 0){
				for (int index = 0; index < years.size(); index++){
					String year = years.get(index);
					statement.setString(parameterIndex, year);
					parameterIndex++;
				}
			}
			
			//Then teams ... twice
			if (teams != null && teams.size() > 0){
				for (int index = 0; index < teams.size(); index++){
					String team = teams.get(index);
					statement.setString(parameterIndex, team);
					parameterIndex++;
				}
				
				for (int index = 0; index < teams.size(); index++){
					String team = teams.get(index);
					statement.setString(parameterIndex, team);
					parameterIndex++;
				}
			}
			
			results = statement.executeQuery();
			
			while (results.next()){
				String division = results.getString("division");
				DivisionRecord divisionRecord = divisionMap.get(division);
				
				Record record = mapRecord(results);
				List<Record> recordsInDivision = divisionRecord.getRecords();
				if (recordsInDivision == null){
					recordsInDivision = new ArrayList<Record>();
				}
				recordsInDivision.add(record);
				divisionRecord.setRecords(recordsInDivision);
			}
		}
		catch (Exception e){
			log.error("Error getting records! years = " + years + ", weekSequenceNumbers = " + weekKeys + ", players = " + players, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return divisionRecords;
	}
	
	/**
	 * 
	 * This function will create the "criteria" part for the records query (basically, the "where" part).
	 * It's here because we need to do this in a few places and I figured it would be a good idea to just
	 * do it once and reuse it.  It will add in each argument that it's given and will skip
	 * the arguments that aren't given.
	 * 
	 * @param years
	 * @param weekKeys
	 * @param players
	 * @return
	 */
	protected String createDivisionRecordsCriteria(List<String> divisions, List<String> years, List<String> weekKeys, List<String> players, List<String> teams){
		
		//Steps to do:
		//	1. Add in the where clauses for the arguments that were given.
		//	2. That's it.
		
		StringBuilder whereClause = new StringBuilder();

		boolean addedWhere = false;

		//First goes player name
		if (players != null && players.size() > 0){
			addedWhere = true;
			whereClause.append("where pl.name in (");
			for (int index = 0; index < players.size(); index++){
				if (index > 0){
					whereClause.append(", ");
				}
				whereClause.append("?");
			}
			whereClause.append(") ");
		}
		
		//Then division?
		if (divisions != null && divisions.size() > 0){
			if (!addedWhere){
				whereClause.append("where ");
				addedWhere = true;
			}
			else {
				whereClause.append(" and ");
			}
			
			String divisionsInParameterString = DatabaseUtil.createInClauseParameterString(divisions.size());
			whereClause.append("d.abbreviation in ").append(divisionsInParameterString);
		}
		
		boolean hasYears = years != null && years.size() > 0;
		boolean hasWeeks = weekKeys != null && weekKeys.size() > 0;
		
		if (hasYears || hasWeeks){
			
			if (!addedWhere){
				whereClause.append("where ");
				addedWhere = true;
			}
			else {
				whereClause.append(" and ");
			}
			
			whereClause.append("g.week_id in (select w.id from week w where ");
		
			if (hasWeeks){
				whereClause.append("w.key in (");
				for (int index = 0; index < weekKeys.size(); index++){
					if (index > 0){
						whereClause.append(", ");
					}
					whereClause.append("?");
				}
				whereClause.append(") ");
			}
			
			if (hasYears){
				
				if (hasWeeks){
					whereClause.append(" and ");
				}
				
				whereClause.append("w.season_id in (select s.id from season s where s.year in (");
				
				for (int index = 0; index < years.size(); index++){
					if (index > 0){
						whereClause.append(", ");
					}
					
					whereClause.append("?");
				}
				
				whereClause.append("))");
			}
			
			whereClause.append(")");
		}
		
		if (teams != null && teams.size() > 0){
			
			if (!addedWhere){
				whereClause.append("where ");
			}
			else {
				whereClause.append(" and ");
			}
			
			whereClause.append("(").append("home_team.abbreviation in (");
			
			for (int index = 0; index < teams.size(); index++){
				if (index > 0){
					whereClause.append(", ");
				}
				whereClause.append("?");
			}
			
			whereClause.append(") or away_team.abbreviation in (");
			
			for (int index = 0; index < teams.size(); index++){
				if (index > 0){
					whereClause.append(", ");
				}
				whereClause.append("?");
			}
			
			whereClause.append(")").append(")");
		}
		
		return whereClause.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * This gets all the player divisions with everything in them.
	 * 
	 * @return
	 */
	public List<PlayerDivision> getPlayerDivisions(){
		
		List<PlayerDivision> divisions = getPlayerDivisions(null, null, null);
		
		return divisions;
	}
	
	//this whole thing needs to be redone
	//for every object, there should be one "get" function and all the others should call that
	//then, there should be special functions for running the queries that get the stats
	//and stuff like that.
	
	/**
	 * 
	 * Gets a player division with the given id.  If shallow is true, it'll get all the nested
	 * stuff too (it should normally be true).
	 * 
	 * @param id
	 * @param shallow
	 * @return
	 */
	public PlayerDivision getPlayerDivision(int id, boolean shallow){
		
		PlayerDivision division = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_PLAYER_DIVISION + 
						   "where id = ? ";
			
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			results = statement.executeQuery();
			
			if (results.next()){
				division = mapPlayerDivision(results, shallow);
			}
		}
		catch (Exception e){
			log.error("Error getting playerDivision! id = " + id + ", shallow = " + shallow, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return division;
	}
	
	/**
	 * 
	 * This function will get the all the player divisions that match the given values.  They're all optional, so
	 * it'll get all the records if none are given.
	 * 
	 * @param abbreviations
	 * @param shallow
	 * @return
	 */
	public List<PlayerDivision> getPlayerDivisions(Integer divisionId, Integer playerId, Integer seasonId){
		
		List<PlayerDivision> playerDivisions = new ArrayList<PlayerDivision>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_PLAYER_DIVISION;
			
			boolean addedWhere = false;
			
			if (divisionId != null){
				if (!addedWhere){
					query = query + " where ";
					addedWhere = true;
				}
				else {
					query = query + " and ";
				}
				
				query = query + " division_id = ? ";
			}
			
			if (playerId != null){
				if (!addedWhere){
					query = query + " where ";
					addedWhere = true;
				}
				else {
					query = query + " and ";
				}
				
				query = query + " player_id = ? ";
			}
			
			if (seasonId != null){
				if (!addedWhere){
					query = query + " where ";
					addedWhere = true;
				}
				else {
					query = query + " and ";
				}
				
				query = query + " season_id = ? ";
			}
			
			connection = getConnection();
			statement = connection.prepareStatement(query);
			
			int parameterIndex = 1;
			
			if (divisionId != null){
				statement.setInt(parameterIndex, divisionId);
				parameterIndex++;
			}
			
			if (playerId != null){
				statement.setInt(parameterIndex, playerId);
				parameterIndex++;
			}
			
			if (seasonId != null){
				statement.setInt(parameterIndex, seasonId);
				parameterIndex++;
			}
			
			results = statement.executeQuery();
			
			while (results.next()){
				PlayerDivision playerDivision = mapPlayerDivision(results, false);
				playerDivisions.add(playerDivision);
			}
		}
		catch (Exception e){
			log.error("Error getting player divisions! divisionId = " + divisionId + ", playerId = " + playerId + 
					  ", seasonId = " + seasonId, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return playerDivisions;
	}
	
	/**
	 * 
	 * This will get the player division record for the given names and year.  Just here because sometimes
	 * we don't have the ids.
	 * 
	 * @param divisionName
	 * @param playerName
	 * @param year
	 * @return
	 */
	public PlayerDivision getPlayerDivision(String divisionName, String playerName, String year){
		
		PlayerDivision playerDivision = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_PLAYER_DIVISION + 
						   "where division_id in (select id from division where name = ?) " +
						   	     "and player_id in (select id from player where name = ?) " +
						   	     "and season_id in (select id from season where year = ?) ";
			
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, divisionName);
			statement.setString(2, playerName);
			statement.setString(3, year);
			results = statement.executeQuery();
			
			if (results.next()){
				playerDivision = mapPlayerDivision(results, false);
			}
		}
		catch (Exception e){
			log.error("Error getting playerDivision! divisionName = " + divisionName + ", playerName = " + playerName + ", year = " + year, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return playerDivision;
	}
	
	/**
	 * 
	 * This function will get the player division record for the given division, player, and season id.
	 * 
	 * @param divisionId
	 * @param playerId
	 * @param seasonId
	 * @param shallow
	 * @return
	 */
	public PlayerDivision getPlayerDivision(int divisionId, int playerId, int seasonId, boolean shallow){
		
		PlayerDivision playerDivision = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_PLAYER_DIVISION + 
						   "where division_id = ? " +
						   	     "and player_id = ? " +
						   	     "and season_id = ? ";
			
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, divisionId);
			statement.setInt(2, playerId);
			statement.setInt(3, seasonId);
			results = statement.executeQuery();
			
			if (results.next()){
				playerDivision = mapPlayerDivision(results, shallow);
			}
		}
		catch (Exception e){
			log.error("Error getting playerDivision! divisionId = " + divisionId + ", playerId = " + playerId + ", seasonId = " + seasonId, e);
			rollback(connection);
		}
		finally {
			close(results, statement, connection);
		}
		
		return playerDivision;
	}
	
	/**
	 * 
	 * This function will save the given division object.  If it has an id, it'll do an update.
	 * If it doesn't, it'll do an insert.
	 * 
	 * @param playerDivision
	 * @return
	 */
	public PlayerDivision savePlayerDivision(PlayerDivision playerDivision){
		
		//Steps to do:
		//	1. Pull out their id.
		//	2. If they don't have one, insert them.
		//	3. Otherwise, update them.
		//	4. Pull out what was saved using their name.
		
		int id = playerDivision.getId();
		
		int numberOfRowsAffected = 0;
		
		if (id <= 0){
			numberOfRowsAffected = insertPlayerDivision(playerDivision);
		}
		else {
			numberOfRowsAffected = updatePlayerDivision(playerDivision);
		}
		
		PlayerDivision savedPlayerDivision = null;
		
		if (numberOfRowsAffected == 1){
			savedPlayerDivision = getPlayerDivision(playerDivision.getDivision().getId(), playerDivision.getPlayer().getId(), playerDivision.getSeason().getId(), true);
		}
		
		return savedPlayerDivision;
	}
	
	/**
	 * 
	 * This function will insert the given player division in the database.
	 * Not much to it.
	 * 
	 * @param division
	 * @return
	 */
	protected int insertPlayerDivision(PlayerDivision playerDivision){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(INSERT_PLAYER_DIVISION);
			statement.setInt(1, playerDivision.getDivision().getId());
			statement.setInt(2, playerDivision.getPlayer().getId());
			statement.setInt(3, playerDivision.getSeason().getId());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error inserting playerDivision! playerDivision = " + playerDivision, e);
			rollback(connection);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * This function will update the given player division in the database.  Not much to it.
	 * 
	 * @param division
	 * @return
	 */
	protected int updatePlayerDivision(PlayerDivision playerDivision){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(UPDATE_PLAYER_DIVISION);
			statement.setInt(1, playerDivision.getDivision().getId());
			statement.setInt(2, playerDivision.getPlayer().getId());
			statement.setInt(3, playerDivision.getSeason().getId());
			statement.setInt(4, playerDivision.getId());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error updating player division! playerDivision = " + playerDivision, e);
			rollback(connection);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	/**
	 * 
	 * This function will map the results of the query to the player_division table to an object.
	 * It will get the division, player, and season objects no matter what "shallow" is.  Shallow will control
	 * whether it gets what's in those objects though (players in the division, games in the season, ...).
	 * 
	 * @param results
	 * @param shallow
	 * @return
	 * @throws SQLException
	 */
	protected PlayerDivision mapPlayerDivision(ResultSet results, boolean shallow) throws SQLException {
		
		PlayerDivision playerDivision = new PlayerDivision();
		playerDivision.setId(results.getInt("id"));
		
		int divisionId = results.getInt("division_id");
		Division division = getDivision(divisionId, shallow);

		int playerId = results.getInt("player_id");
		Player player = getPlayer(playerId);

		int seasonId = results.getInt("season_id");
		Season season = getSeason(seasonId, shallow);

		playerDivision.setDivision(division);
		playerDivision.setPlayer(player);
		playerDivision.setSeason(season);
		
		return playerDivision;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * A convenience function for closing a statement and connection.
	 * 
	 * @param statement
	 * @param connection
	 */
	protected void close(PreparedStatement statement, Connection connection){
		DatabaseUtil.close(null, statement, connection);
	}
	
	/**
	 * 
	 * A convenience function for closing all the stuff for a database query.
	 * 
	 * @param results
	 * @param statement
	 * @param connection
	 */
	protected void close(ResultSet results, PreparedStatement statement, Connection connection){
		DatabaseUtil.close(results, statement, connection);
	}
	
	/**
	 * 
	 * A convenience function for rolling back whatever we were doing with a connection when
	 * there was an error.
	 * 
	 * @param connection
	 */
	protected void rollback(Connection connection){
		DatabaseUtil.rollback(connection);
	}
	
	/**
	 * 
	 * Gets a connection from the data source.
	 * 
	 * @return
	 */
	protected Connection getConnection(){
		
		Connection connection = null;
		
		try {
			connection = dataSource.getConnection();
		}
		catch (Exception e){
			log.error("Error getting connection!", e);
		}
		
		return connection;
	}
		
	public DataSource getDataSource(){
		return dataSource;
	}
	
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}
	
	
	/*
	 
	 this is like the "collective accuracy" of our picks...
	 
	 select 
   
   pick_accuracy_summary.team_id as team_id, 
   pick_accuracy_summary.division_id as division_id, 
   pick_accuracy_summary.team_name as team_name, 
   pick_accuracy_summary.team_nickname as team_nickname, 
   pick_accuracy_summary.team_abbreviation as team_abbreviation, 
   sum(pick_accuracy_summary.actual_wins) as actual_wins,  
   sum(pick_accuracy_summary.actual_losses) as actual_losses, 
   sum(pick_accuracy_summary.actual_ties) as actual_ties,  
   sum(pick_accuracy_summary.predicted_wins) as predicted_wins,  
   sum(pick_accuracy_summary.predicted_losses) as predicted_losses,  
   sum(pick_accuracy_summary.times_right) as times_right,  
   sum(pick_accuracy_summary.times_wrong) as times_wrong,  
   sum(pick_accuracy_summary.times_picked_to_win_right) as times_picked_to_win_right,  
   sum(pick_accuracy_summary.times_picked_to_win_wrong) as times_picked_to_win_wrong,  
   sum(pick_accuracy_summary.times_picked_to_lose_right) as times_picked_to_lose_right,  
   sum(pick_accuracy_summary.times_picked_to_lose_wrong) as times_picked_to_lose_wrong  
from (select   
		  
		  t.id as team_id,  
		  t.division_id as division_id, 
		  t.name as team_name,  
		  t.nickname as team_nickname, 
		  t.abbreviation as team_abbreviation,  
		  (select count(*)  
		   from game g  
		   where (g.home_team_id = t.id or  
		    	  g.away_team_id = t.id)  
		    	  and g.winning_team_id = t.id  
		    	  and g.week_id in (select w.id  
		    	  		    from week w  
		    	  		    where w.season_id in (select s.id from season s where year = '2016') and w.week = 2)  
		  ) as actual_wins,  
		  (select count(*)  
		   from game g  
		   where (g.home_team_id = t.id or  
		   	 	  g.away_team_id = t.id)  
		   	 	  and (g.winning_team_id != t.id and g.winning_team_id != -1)  
		   	 	  and g.week_id in (select w.id  
		   	 	  					from week w  
		   	 	  					where w.season_id in (select s.id from season s where year = '2016') and w.week = 2
									      )  
		  ) as actual_losses,  
		  (select count(*)  
		   from game g  
		   where (g.home_team_id = t.id or  
		   	      g.away_team_id = t.id)  
		   	      and g.winning_team_id = -1  
		   	      and g.week_id in (select w.id  
		   	      				    from week w  
		   	      				    where w.season_id in (select s.id from season s where year = '2016') and w.week = 2)  
		  ) as actual_ties,  
		  (select count(*)   
		   from pick p join game g on p.game_id = g.id  
		   where p.player_id in (select id from player where name in ('Jonathan', 'Benny boy')) 
		   		 and p.team_id = t.id  
		   		 and g.week_id in (select w.id  
		   		 				   from week w  
		   		 				   where w.season_id in (select s.id from season s where year = '2016') and w.week = 2)  
		  ) as predicted_wins,  
		  (select count(*)   
		   from pick p join game g on p.game_id = g.id  
		   where p.player_id in (select id from player where name in ('Jonathan', 'Benny boy')) 
		   		 and p.team_id != t.id  
		   		 and (g.home_team_id = t.id or g.away_team_id = t.id)  
		   		 and g.week_id in (select w.id  
		   		 				   from week w  
		   		 				   where w.season_id in (select s.id from season s where year = '2016') and w.week = 2)  
		  ) as predicted_losses, 
		  (select count(*)   
		   from pick p join game g on p.game_id = g.id  
		   where p.player_id in (select id from player where name in ('Jonathan', 'Benny boy')) 
		   		 and (g.home_team_id = t.id or g.away_team_id = t.id)  
		   		 and g.winning_team_id = p.team_id  
		   		 and g.week_id in (select w.id  
		   		 				   from week w  
		   		 				   where w.season_id in (select s.id from season s where year = '2016') and w.week = 2)  
		  ) as times_right,  
		  (select count(*)   
		   from pick p join game g on p.game_id = g.id  
		   where p.player_id in (select id from player where name in ('Jonathan', 'Benny boy')) 
		   		 and (g.home_team_id = t.id or g.away_team_id = t.id)  
		   		 and g.winning_team_id != p.team_id 
		   		 and g.winning_team_id != -1  
		   		 and g.week_id in (select w.id  
		   		 				   from week w  
		   		 				   where w.season_id in (select s.id from season s where year = '2016') and w.week = 2)  
		  ) as times_wrong,  
		  (select count(*)   
		   from pick p join game g on p.game_id = g.id  
		   where p.player_id in (select id from player where name in ('Jonathan', 'Benny boy')) 
		   		 and p.team_id = t.id  
		   		 and g.week_id in (select w.id  
		   		 				   from week w  
		   		 				   where w.season_id in (select s.id from season s where year = '2016') and w.week = 2)  
		   		 and g.winning_team_id = p.team_id  
		  ) as times_picked_to_win_right,  
		  (select count(*)   
		   from pick p join game g on p.game_id = g.id  
		   where p.player_id in (select id from player where name in ('Jonathan', 'Benny boy')) 
		   	     and p.team_id = t.id  
		   	     and g.week_id in (select w.id  
		   	     				   from week w  
		   	     				   where w.season_id in (select s.id from season s where year = '2016') and w.week = 2)  
		   	     and g.winning_team_id != p.team_id  
		   	     and g.winning_team_id != -1 
		  ) as times_picked_to_win_wrong, 
		  (select count(*)   
		   from pick p join game g on p.game_id = g.id  
		   where p.player_id in (select id from player where name in ('Jonathan', 'Benny boy')) 
		     	 and p.team_id != t.id  
		     	 and (g.home_team_id = t.id or g.away_team_id = t.id)  
		     	 and g.week_id in (select w.id  
		     	 				   from week w  
		     	 				   where w.season_id in (select s.id from season s where year = '2016') and w.week = 2)  
		     	 and g.winning_team_id != t.id 
		     	 and g.winning_team_id != -1 
		  ) as times_picked_to_lose_right,  
		  (select count(*)   
		   from pick p join game g on p.game_id = g.id  
		   where p.player_id in (select id from player where name in ('Jonathan', 'Benny boy'))  
		   	 	 and p.team_id != t.id  
		   	 	 and (g.home_team_id = t.id or g.away_team_id = t.id)  
		   	 	 and g.week_id in (select w.id  
		   	 	 				   from week w  
		   	 	 				   where w.season_id in (select s.id from season s where year = '2016') and w.week = 2)  
		   	 	 and g.winning_team_id = t.id  
		   ) as times_picked_to_lose_wrong  
   from team t  
   where t.abbreviation = 'TB'
	) pick_accuracy_summary  
group by team_id, team_name, team_nickname, team_abbreviation, division_id ;

	need to put in sections for:
		players
		years
		weeks
		
	then just do replacements ... if they don't exist, replace them with empty strings
	 
	 */
	public List<CompactPickAccuracyContainer> getCompactPickAccuracies(List<String> years, List<String> weeks, List<String> players, List<String> teams){
		return null;
	}

}
