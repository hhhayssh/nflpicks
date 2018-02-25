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
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import nflpicks.model.CompactPick;
import nflpicks.model.Conference;
import nflpicks.model.Division;
import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.Record;
import nflpicks.model.Season;
import nflpicks.model.Team;
import nflpicks.model.Week;
import nflpicks.model.WeekRecord;
import nflpicks.model.WeeksWon;

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
public class NFLPicksDataService {
	
	private static final Logger log = Logger.getLogger(NFLPicksDataService.class);

	/**
	 * 
	 * The object that does the talking to the database.
	 * 
	 */
	protected DataSource dataSource;
	
	//Canned select statements for selecting from the different tables.  We don't normally insert
	//into the conference, division, or team tables from java code, so there are only select
	//statements for dealing with those tables.
	
	protected static final String SELECT_CONFERENCE = "select id, " +
													  "name " +
													  "from conference ";
	
	protected static final String SELECT_DIVISION = "select id, " +
													"conference_id, " + 
													"name " + 
													"from division ";
	
	protected static final String SELECT_TEAM = "select division_id, " +
												"id, " +
												"name, " + 
												"nickname, " +
												"abbreviation " +
												"from team ";
	
	//Statements for dealing with the season table.
	protected static final String SELECT_SEASON = "select id, " +
												  "year " +
												  "from season ";
	
	protected static final String INSERT_SEASON = "insert into season (year) values (?) ";
	
	protected static final String UPDATE_SEASON = "update season " + 
												  "set year = ? " +
												  "where id = ? ";
	
	//Statements for dealing with the week table.
	//TODO: change week to week_number
	protected static final String SELECT_WEEK = "select id, " +
												"season_id, " +
												"week, " + 
												"label " + 
												"from week ";
	
	protected static final String INSERT_WEEK = "insert into week (season_id, week, label) values (?, ?, ?) ";
	
	protected static final String UPDATE_WEEK = "update week " + 
												"set season_id = ?, " + 
												"week = ?, " + 
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
	
	//Statments for dealing with the people... still don't quite like that people have ids.
	protected static final String SELECT_PLAYER = "select id, name from player ";
	
	protected static final String INSERT_PLAYER = "insert into player (name) values (?) ";
	
	protected static final String UPDATE_PLAYER = "update player " +
											  	  "set name = ? " + 
											  	  "where id = ? ";

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
												 			 "(case when p.team_id = g.winning_team_id " + 
												 			 	   "then 1 " + 
												 			 	   "else 0 " + 
												 			  "end) as wins, " + 
												 			 //Only count the pick as a loss if there was a pick.  If there wasn't (p.team_id = null), then
												 			 //don't count that.
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
												 			  	   //This will be inserted later so that we only get the records we need.  Makes it
												 			  	   //so we can restrict on stuff like the season, the player, ....
												 			  	   "%s " + 
												 		") pick_totals " + 
												"group by pick_totals.player_id, pick_totals.player_name ";
	
	protected static final String SELECT_WEEK_RECORDS = "select pick_totals.season_id, " + 
													 		"pick_totals.year, " + 
													 		"pick_totals.player_id, " + 
													 		"pick_totals.player_name, " + 
													 		"pick_totals.week_id, " + 
													 		"pick_totals.week, " + 
													 		"pick_totals.week_label, " + 
													 		"sum(pick_totals.wins) as wins, " + 
													 		"sum(pick_totals.losses) as losses, " + 
													 		"sum(pick_totals.ties) as ties " + 
													 "from (select pl.id as player_id, " + 
													 		 	  "pl.name as player_name, " + 
													 		 	  "s.id as season_id, " + 
													 		 	  "s.year as year, " + 
													 		 	  "w.id as week_id, " + 
													 		 	  "w.week as week, " + 
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
													"group by season_id, year, pick_totals.player_id, pick_totals.player_name, week_id, week, week_label " + 
													"order by year, week, player_name ";
	
	/**
	 * 
	 * Usually pays off to have an empty constructor.  If you use this one, you should
	 * set the data source before doing anything.
	 * 
	 */
	public NFLPicksDataService(){
	}
	
	/**
	 * 
	 * Makes a data service that'll use the given data source to pull
	 * the data.
	 * 
	 * @param dataSource
	 */
	public NFLPicksDataService(DataSource dataSource){
		setDataSource(dataSource);
	}
	
	//get all
	//get get all shallow
	//get one id
	//get one id shallow
	//save
	//update
	//insert
	
	//season
	//conference
	//division
	//team
	//game
	//player
	//pick

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
		}
		finally {
			close(results, statement, connection);
		}
		
		return seasons;
	}
	
	public Season getSeason(int id){
		
		Season season = getSeason(id, false);
		
		return season;
	}
	
	public Season getSeason(int id, boolean shallow){
		
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return season;
	}
	
	/**
	 * 
	 * This function will get all the conferences with them all
	 * filled in with the divisions and teams.
	 * 
	 * @return
	 */
	public List<Conference> getConferences(){
		
		List<Conference> conferences = getConferences(false);
		
		return conferences;
	}
	
	public List<Conference> getConferences(boolean shallow){
		
		List<Conference> conferences = new ArrayList<Conference>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement(SELECT_CONFERENCE);
			results = statement.executeQuery();
			
			while (results.next()){
				Conference conference = mapConferenceResult(results, shallow);
				conferences.add(conference);
			}
		}
		catch (Exception e){
			log.error("Error getting conferences!", e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return conferences;
	}
	
	protected Conference mapConferenceResult(ResultSet results, boolean shallow) throws SQLException {
		
		Conference conference = new Conference();
		conference.setId(results.getInt("id"));
		conference.setName(results.getString("name"));
		
		if (!shallow){
			int conferenceId = results.getInt("id");
			List<Division> divisions = getDivisions(conferenceId, shallow);
			conference.setDivisions(divisions);
		}
		
		return conference;
	}
	
	public List<Division> getDivisions(){
		
		List<Division> divisions = new ArrayList<Division>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement(SELECT_CONFERENCE);
			results = statement.executeQuery();
			
			while (results.next()){
				Division division = mapDivisionResult(results);
				divisions.add(division);
			}
		}
		catch (Exception e){
			log.error("Error getting divisions!", e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return divisions;
	}
	
	public List<Division> getDivisions(int conferenceId, boolean shallow){
		
		List<Division> divisions = new ArrayList<Division>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		try {
			String query = SELECT_DIVISION + " where conference_id = ? ";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, conferenceId);
			results = statement.executeQuery();
			
			while (results.next()){
				Division division = mapDivisionResult(results);
				divisions.add(division);
			}
		}
		catch (Exception e){
			log.error("Error getting divisions!", e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return divisions;
		
	}
	
	protected Division mapDivisionResult(ResultSet results) throws SQLException {
		
		Division division = new Division();
		division.setId(results.getInt("id"));
		division.setConferenceId(results.getInt("conferenceId"));
		division.setName(results.getString("name"));
		
		return division;
	}
	
	public List<Team> getTeams(){
		
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return teams;
	}
	
	public Team getTeam(String abbreviation){
		
		Team team = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

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
		}
		finally {
			close(results, statement, connection);
		}
		
		return team;
	}
	
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return team;
	}
	
	protected Team mapTeamsResult(ResultSet result) throws SQLException{
		Team team = new Team();
		team.setId(result.getInt("id"));
		team.setDivisionId(result.getInt("division_id"));
		team.setName(result.getString("name"));
		team.setNickname(result.getString("nickname"));
		team.setAbbreviation(result.getString("abbreviation"));
		
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return seasons;
	}
	
	public Season getSeason(String year){
		
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
				season = mapSeason(results, false);
			}
		}
		catch (Exception e){
			log.error("Error getting season! year = " + year, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return season;
	}
	
	protected Season mapSeason(ResultSet result) throws SQLException {
		
		Season season = mapSeason(result, false);
		
		return season;
	}
	
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	///////////////////////////////////////////////////////////////
	
	
	
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return years;
	}
	
	
	
	
	
	//we need to:
	//	1. create the right number of "in (?, ?)" question marks
	//	2. add those parameters to the prepared statement.
	//	1. the creation of the string has to happen before the creation of the prepared
	//	   statement ... it has to be separate
	
	public List<String[]> getWeeksAndLabels(String year){
		
		List<String[]> weeksAndLabels = new ArrayList<String[]>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			String query = SELECT_WEEK + 
						   "where season_id in (select season_id " +
						   				  	   "from season " + 
						   				  	   "where year = ? )";
			statement = connection.prepareStatement(query);
			statement.setString(1, year);
			results = statement.executeQuery();
			
			while (results.next()){
				int week = results.getInt("week");
				String label = results.getString("label");
				String[] weekAndLabel = new String[]{String.valueOf(week), label};
				weeksAndLabels.add(weekAndLabel);
			}
		}
		catch (Exception e){
			log.error("Error getting weeks! year = " + year, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return weeksAndLabels;
	}

	public List<Week> getWeeks(){
		
		List<Week> weeks = getWeeks(null, null, false);
		
		return weeks;
	}
	
	public List<Week> getWeeks(String year){
		
		List<String> years = new ArrayList<String>();
		years.add(year);
		
		List<Week> weeks = getWeeks(years, null, false);
		
		return weeks;
	}
	
	public List<Week> getWeeks(List<String> years, List<String> weekNumbers){
	
		List<Week> weeks = getWeeks(years, weekNumbers, false);
		
		return weeks;
	}
	
	public List<Week> getWeeks(List<String> years, List<String> weekNumbers, boolean shallow){
		
		List<Week> weeks = new ArrayList<Week>();
		
		List<Integer> weekNumberIntegers = Util.toIntegers(weekNumbers);
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			StringBuilder stringBuilder = new StringBuilder(SELECT_WEEK);
			
			boolean addedWhere = false;
			
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
			
			if (weekNumberIntegers != null && weekNumberIntegers.size() > 0){
				if (!addedWhere){
					 stringBuilder.append(" where ");
					 addedWhere = true;
				}
				else {
					stringBuilder.append(" and ");
				}
				
				String inParameterString = DatabaseUtil.createInClauseParameterString(weekNumberIntegers.size());
			
				//this should be changed to week_number
				stringBuilder.append("week in ")
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
			
			if (weekNumberIntegers != null && weekNumberIntegers.size() > 0){
				for (int index = 0; index < weeks.size(); index++){
					Integer weekNumber = weekNumberIntegers.get(index);
					statement.setInt(parameterIndex, weekNumber);
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
			log.error("Error getting weeks!  years = " + years + ", weeks = " + weeks + ", shallow = " + shallow, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return weeks;
	}
	
	public List<Week> getWeeks(int seasonId){
		
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return weeks;
	}
	
	public Week getWeek(int id){
		
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return week;
	}
	
	public Season saveSeason(Season season){
		
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
			savedSeason = getSeason(season.getYear());
		}
		
		return savedSeason;
	}
	
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
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
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
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	public Player savePlayer(Player player){
		
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
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
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
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	public Week saveWeek(Week week){
		
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
			savedWeek = getWeek(week.getSeasonId(), week.getWeekNumber());
		}
		
		return savedWeek;
	}
	
	protected int insertWeek(Week week){
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(INSERT_WEEK);
			statement.setInt(1, week.getSeasonId());
			statement.setInt(2, week.getWeekNumber());
			statement.setString(3, week.getLabel());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error inserting week! week = " + week, e);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
	protected int updateWeek(Week week){
		
		int numberOfAffectedRows = 0;
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(UPDATE_WEEK);
			statement.setInt(1, week.getSeasonId());
			statement.setInt(2, week.getWeekNumber());
			statement.setString(3, week.getLabel());
			statement.setInt(4, week.getId());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error inserting week! week = " + week, e);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
		
	}
	
	public Week getWeek(int seasonId, int week){
		
		Week retrievedWeek = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			String query = SELECT_WEEK + 
						   "where season_id = ?" +
						   		 "and week = ? ";
			statement = connection.prepareStatement(query);
			statement.setInt(1, seasonId);
			statement.setInt(2, week);
			results = statement.executeQuery();
			
			if (results.next()){
				retrievedWeek = mapWeek(results);
			}
		}
		catch (Exception e){
			log.error("Error getting week! seasonId = " + seasonId + ", week = " + week, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return retrievedWeek;
	}
	
	public Week getWeek(String year, String week){
		
		int weekInt = Integer.parseInt(week);
		
		Week weekObject = getWeek(year, weekInt);
		
		return weekObject;
	}
	
	public Week getWeek(String year, int week){
		
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
						   		 "and week = ? ";
			statement = connection.prepareStatement(query);
			statement.setString(1, year);
			statement.setInt(2, week);
			results = statement.executeQuery();
			
			if (results.next()){
				retrievedWeek = mapWeek(results);
			}
		}
		catch (Exception e){
			log.error("Error getting week! year = " + year + ", week = " + week, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return retrievedWeek;
	}
	
	protected Week mapWeek(ResultSet result) throws SQLException {
		
		Week week = mapWeek(result, false);
		
		return week;
	}
	
	protected Week mapWeek(ResultSet result, boolean shallow) throws SQLException {
		
		Week week = new Week();
		int weekId = result.getInt("id");
		week.setId(weekId);
		int seasonId = result.getInt("season_id");
		week.setSeasonId(seasonId);
		week.setWeekNumber(result.getInt("week"));
		week.setLabel(result.getString("label"));
		
		if (!shallow){
			List<Game> games = getGames(weekId);
			week.setGames(games);
		}
		
		return week;
	}
	
	public List<Game> getGames(int weekId){
		
		List<Game> games = new ArrayList<Game>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			String query = SELECT_GAME + 
						   " where week_id = ? "; 
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return games;
	}
	
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return games;
	}
	
	public List<Game> getGames(String year, int week){
		
		List<Game> games = new ArrayList<Game>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			String query = SELECT_GAME + 
						   " where week_id in (select w.id " +
						   					  "from week w " + 
						   					  "where week = ? " + 
						   					  	    "and season_id in (select id " +
						   					  					      "from season " + 
						   					  					      "where year = ?)) " +
						   "order by id asc ";
			statement = connection.prepareStatement(query);
			statement.setInt(1, week);
			statement.setString(2, year);
			results = statement.executeQuery();
			
			while (results.next()){
				Game game = mapGame(results);
				games.add(game);
			}
		}
		catch (Exception e){
			log.error("Error getting games! week = " + week + ", year = " + year, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return games;
	}
	
	public Game getGame(String year, String week, String awayTeamAbbreviation, String homeTeamAbbreviation){
		
		Game game = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_GAME + 
						   "where week_id in (select w.id " +
						   					 "from week w " + 
						   					 "where w.week = ? " + 
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
			statement.setInt(1, Integer.parseInt(week));
			statement.setString(2, year);
			statement.setString(3, awayTeamAbbreviation);
			statement.setString(4, homeTeamAbbreviation);
			
			results = statement.executeQuery();
			
			while (results.next()){
				
				if (game == null){
					game = mapGame(results);
				}
				else {
					log.error("Found more than one game for input! year = " + year + ", week = " + week + ", awayTeamAbbreviation = " + awayTeamAbbreviation + 
							  ", homeTeamAbbreviation = " + homeTeamAbbreviation);
					return null;
				}
			}
		}
		catch (Exception e){
			log.error("Error getting game! year = " + year + ", week = " + week + ", awayTeamAbbreviation = " + awayTeamAbbreviation + 
					  ", homeTeamAbbreviation = " + homeTeamAbbreviation, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return game;
	}
	
	public Game getGame(String year, int week, String teamAbbreviation){
		
		Game game = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			String query = SELECT_GAME + 
						   "where week_id in (select w.id " +
						   					 "from week w " + 
						   					 "where w.week = ? " + 
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
			statement.setInt(1, week);
			statement.setString(2, year);
			statement.setString(3, teamAbbreviation);
			statement.setString(4, teamAbbreviation);
			
			results = statement.executeQuery();
			
			while (results.next()){
				
				if (game == null){
					game = mapGame(results);
				}
				else {
					log.error("Found more than one game for input! year = " + year + ", week = " + week + ", teamAbbreviation = " + teamAbbreviation);
					return null;
				}
			}
		}
		catch (Exception e){
			log.error("Error getting game! year = " + year + ", week = " + week + ", teamAbbreviation = " + teamAbbreviation, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return game;
	}
	
	public Game getGame(int weekId, int homeTeamId, int awayTeamId){
		
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return game;
		
	}
	
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return game;
	}
	
	public Game saveGame(Game game){
		
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
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
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
			
			statement.setInt(5, game.getId());
			
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			log.error("Error updating game! game = " + game, e);
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
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
		if (winningTeamId == -1){
			game.setTie(true);
		}
		else if (winningTeamId > 0) {
			Team winningTeam = getTeam(winningTeamId);
			game.setWinningTeam(winningTeam);
		}

		return game;
	}
	
	public Pick getPick(String playerName, String year, int week, String homeTeamAbbreviation, String awayTeamAbbreviation){
		
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
							   					 				   		  "and week = ? ) " +
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
			statement.setInt(3, week);
			statement.setString(4, homeTeamAbbreviation);
			statement.setString(5, awayTeamAbbreviation);
			results = statement.executeQuery();
			
			if (results.next()){
				pick = mapPick(results);
			}
		}
		catch (Exception e){
			log.error("Error getting picks! playerName = " + playerName + ", year = " + year + ", week = " + week + 
					  ", homeTeamAbbreviation = " + homeTeamAbbreviation + ", awayTeamAbbreviation = " + awayTeamAbbreviation, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return pick;
	}
	
	public List<Pick> getPicks(String playerName, String year, int week){
		
		List<Pick> picks = new ArrayList<Pick>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
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
							   					 				   		  "and week = ? ))";
			statement = connection.prepareStatement(query);
			statement.setString(1, playerName);
			statement.setString(2, year);
			statement.setInt(3, week);
			results = statement.executeQuery();
			
			while (results.next()){
				Pick pick = mapPick(results);
				picks.add(pick);
			}
		}
		catch (Exception e){
			log.error("Error getting picks! playerName = " + playerName + ", year = " + year + ", week = " + week, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return picks;
	}
	
	public List<Pick> getPicks(int playerId, String year, int week){
		
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
							   					 				   		  "and week = ? ";
			statement = connection.prepareStatement(query);
			statement.setInt(1, playerId);
			statement.setString(2, year);
			statement.setInt(3, week);
			results = statement.executeQuery();
			
			while (results.next()){
				Pick pick = mapPick(results);
				picks.add(pick);
			}
		}
		catch (Exception e){
			log.error("Error getting picks! playerId = " + playerId + ", year = " + year + ", week = " + week, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return picks;
	}
	
	public List<Pick> getPicks(List<String> years, List<String> weekNumbers, List<String> playerNames){
		
		List<Pick> picks = new ArrayList<Pick>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			
			List<Integer> weekNumberIntegers = Util.toIntegers(weekNumbers);
			
			//another programming language idea ... where if you "add" something, it just adds to the data structure.
			
			boolean hasWeeks = false;
			if (weekNumberIntegers != null && weekNumberIntegers.size() > 0){
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
			
			String query = SELECT_PICK +
					   "where game_id in (select id " +
					   					 "from game " + 
					   					 "where week_id in (select id " + 
					   					 				   "from week ";
			
			//could have weeks without years, years without weeks, or both
			/*
			 select *
			 from pick
			 where game_id in (select id
			 				   from game 
			 				   where week_id in (select id
			 				   					 from week
			 				   					 where week in (?, ?, ?, ?)
			 				   					       and season_id in (select id
			 				   					       				 	 from season
			 				   					       				 	 where year in (?, ?, ?,)
			 				   					       				 	)
			 				   					)
			 				   )
			 	  and player_id in (select id
			 	  					from player
			 	  					where player_name in (?, ?, ?))
			 */
			if (hasWeeks){
				query = query + " where week in " + DatabaseUtil.createInClauseParameterString(weekNumberIntegers.size());
			}
			
			if (hasYears){
				
				if (hasWeeks){
					query = query + " and ";
				}
				else {
					query = query + " where ";
				}
				
				query = query + " season_id in (select id from season where year in " + DatabaseUtil.createInClauseParameterString(years.size()) + " ) ";
			}
			
			query = query + ")";
			
			query = query + ")";
			
			if (hasPlayers){
				query = query + " and player_id in (select id from player where name in " + DatabaseUtil.createInClauseParameterString(playerNames.size()) + " ) ";
			}
			
			connection = getConnection();
			statement = connection.prepareStatement(query);
			
			if (hasWeeks){
				int parameterIndex = 1;
				for (int index = 0; index < weekNumberIntegers.size(); index++){
					Integer weekNumber = weekNumberIntegers.get(index);
					statement.setInt(parameterIndex, weekNumber);
					parameterIndex++;
				}
			}
			
			if (hasYears){
				int parameterIndex = 1;
				for (int index = 0; index < years.size(); index++){
					String year = years.get(index);
					statement.setString(parameterIndex, year);
					parameterIndex++;
				}
			}
			
			if (hasPlayers){
				int parameterIndex = 1;
				for (int index = 0; index < playerNames.size(); index++){
					String playerName = playerNames.get(index);
					statement.setString(parameterIndex, playerName);
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
			log.error("Error getting picks! years = " + years + ", weekNumbers = " + weekNumbers + ", playerNames = " + playerNames, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return picks;
	}
	
	public List<Pick> getPicks(String year, int week){
		
		List<Pick> picks = new ArrayList<Pick>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			log.info("Getting picks... year = " + year + ", week = " + week);
			connection = getConnection();
			String query = SELECT_PICK +
						   "where game_id in (select id " +
						   					 "from game " + 
						   					 "where week_id in (select id " + 
						   					 				   "from week " + 
						   					 				   "where season_id in (select id " +
						   					 				   					   "from season " +
						   					 				   					   "where year = ?) " +
						   					 				   		 "and week = ? ))";
			statement = connection.prepareStatement(query);
			statement.setString(1, year);
			statement.setInt(2, week);
			results = statement.executeQuery();
			
			while (results.next()){
				Pick pick = mapPick(results);
				picks.add(pick);
			}
		}
		catch (Exception e){
			log.error("Error getting picks! year = " + year + ", week = " + week, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return picks;
	}
	
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return picks;
	}
	
	public Pick savePick(Pick pick){
		
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
			savedPick = getPick(pick.getGame().getId(), pick.getPlayer().getId());
		}
		
		return savedPick;
	}
	
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
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
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
		}
		finally {
			close(null, statement, connection);
		}
		
		return numberOfAffectedRows;
	}
	
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return pick;
	}
	
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return pick;
	}
	
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
				pick.setResult("T");
			}
			else if (winningTeam != null){
				int winningTeamId = winningTeam.getId();
				
				if (winningTeamId == pickedTeamId){
					pick.setResult("W");
				}
				else {
					pick.setResult("L");
				}
			}
		}
		
		return pick;
	}
	
//	public boolean wasPlayerActiveInYear(String year, String player){
//		
//		if (year == null || player == null){
//			return false;
//		}
//		
//		List<Player> players = getPlayers(year);
//		
//		for (int index = 0; index < players.size(); index++){
//			Player currentPlayer = players.get(index);
//			if (player.equals(currentPlayer.getName())){
//				return true;
//			}
//		}
//		
//		return false;
//	}
	
	//somebody should make a programming language where you can't change the
	//variable names.......
	//call it "global"
	
	public List<Player> getPlayers(List<String> playerNames){
		
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
			
			while (results.next()){
				Player playerInfo = mapPlayer(results);
				players.add(playerInfo);
			}
		}
		catch (Exception e){
			log.error("Error getting players!", e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return players;
	}
	
	public List<Player> getPlayers(String year){
		
		List<Player> players = new ArrayList<Player>();
		
		if (year == null){
			players = getPlayers();
			return players;
		}
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			//where they have at least one pick.
			/*
			 
			 select *
			 from player
			 where id in (select player_id
			 			  from pick
			 			  where game_id in (select id
			 			  				    from game
			 			  				    where week_id in (select id
			 			  				    				  from week
			 			  				    				  where season_id in (select id
			 			  				    				  					  from season
			 			  				    				  					  where year = ?))));
			 
			 */
			String query = SELECT_PLAYER + 
						   "where id in (select player_id " + 
						   				"from pick " + 
						   				"where game_id in (select id " + 
						   								  "from game " + 
						   								  "where week_id in (select id " + 
						   								  					"from week " + 
						   								  					"where season_id in (select id " + 
						   								  										"from season " + 
						   								  										"where year = ?))))";
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, year);
			results = statement.executeQuery();
			
			while (results.next()){
				Player playerInfo = mapPlayer(results);
				players.add(playerInfo);
			}
		}
		catch (Exception e){
			log.error("Error getting players!", e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return players;
	}
	
	
	
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return players;
	}
	
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return player;
	}
	
	public boolean wasPlayerActiveInYear(String player, String year){
		
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
		}
		finally {
			close(results, statement, connection);
		}
		
		if (numberOfPicksInYear > 0){
			return true;
		}
		
		return false;
	}
	
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
		}
		finally {
			close(results, statement, connection);
		}
		
		return player;
	}
	
	protected Player mapPlayer(ResultSet results) throws SQLException {
		Player player = new Player();
		player.setId(results.getInt("id"));
		player.setName(results.getString("name"));
		return player;
	}
	
	public List<Record> getRecords(List<String> years, List<String> weeks, List<String> players){
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		List<Record> records = new ArrayList<Record>();
		
		try {
			connection = dataSource.getConnection();
			
			String recordsCriteria = createRecordsCriteria(years, weeks, players);
			
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
			if (weeks != null && weeks.size() > 0){
				for (int index = 0; index < weeks.size(); index++){
					String week = weeks.get(index);
					int weekInt = Integer.parseInt(week);
					statement.setInt(parameterIndex, weekInt);
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
				Record record = mapRecord(results);
				records.add(record);
			}
		}
		catch (Exception e){
			log.error("Error getting records! years = " + years + ", weeks = " + weeks + ", players = " + players, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return records;
	}
	
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
	
	protected String createRecordsCriteria(List<String> years, List<String> weeks, List<String> players){
		
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
		boolean hasWeeks = weeks != null && weeks.size() > 0;
		
		if (hasYears || hasWeeks){
			
			if (!addedWhere){
				whereClause.append("where ");
			}
			else {
				whereClause.append(" and ");
			}
			
			whereClause.append("g.week_id in (select w.id from week w where ");
		
			if (hasWeeks){
				whereClause.append("w.week in (");
				for (int index = 0; index < weeks.size(); index++){
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
		
		return whereClause.toString();
	}
	
	/*
	select s.year as year,
       w.week as week,
       home_team.abbreviation as home_team_abbreviation,
       away_team.abbreviation as away_team_abbreviation,
       winning_team.abbreviation as winning_team_abbreviation,
       
       (select picked_team.abbreviation
        from pick p join team picked_team on p.team_id = picked_team.id
        where p.game_id = g.id
              and p.player_id = 1) as x_pick
              
              
from season s join week w on s.id = w.season_id
     join game g on w.id = g.week_id
     join team home_team on g.home_team_id = home_team.id
     join team away_team on g.away_team_id = away_team.id
     left outer join team winning_team on g.winning_team_id = winning_team.id
order by s.year asc, w.week asc, g.id asc;
	 */
	public List<CompactPick> getCompactPicks(){
		
		List<CompactPick> compactPicks = getCompactPicks(null, null, null);
		
		return compactPicks;
		
	}
	public List<CompactPick> getCompactPicks(List<String> years, List<Integer> weekNumbers, List<String> playerNames) {
		
		List<CompactPick> compactPicks = new ArrayList<CompactPick>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = dataSource.getConnection();
			
			String selectBase = "select s.year as year, " + 
								"w.week as week, " + 
								"home_team.abbreviation as home_team_abbreviation, " + 
								"away_team.abbreviation as away_team_abbreviation, " +
								"(case when g.winning_team_id = -1 then 'TIE' " +
									  "else winning_team.abbreviation " +
							    "end) as winning_team_abbreviation "; 
			
			List<Player> players = null;
			
			if (playerNames == null){
				players = getPlayers();
				playerNames = ModelUtil.getPlayerNames(players);
				//Yeah, I could do it like this...
				//playerNames = players.stream().map(p -> p.getName()).collect(Collectors.toList());
			}
			else {
				players = getPlayers(playerNames);
			}
			
			
			List<String> playerNamesToUse = new ArrayList<String>();
			List<Integer> playerIdsToUse = new ArrayList<Integer>();
			
			for (int index = 0; index < players.size(); index++){
				Player player = players.get(index);
				String playerName = player.getName();
				Integer playerId = player.getId();
				
				String playerNameToUse = playerName.toLowerCase().replaceAll("\\s+", "_") + "_pick";
				playerNamesToUse.add(playerNameToUse);
				playerIdsToUse.add(playerId);
				
				String playerPickSelect = "(select picked_team.abbreviation " + 
										  "from pick p join team picked_team on p.team_id = picked_team.id " + 
										  "where p.game_id = g.id " + 
										  "and p.player_id = ?) as " + playerNameToUse;
				
				selectBase = selectBase + ", " + playerPickSelect;
			}
			
			String fromBase = "from season s join week w on s.id = w.season_id " + 
							  "join game g on w.id = g.week_id " + 
							  "join team home_team on g.home_team_id = home_team.id " + 
							  "join team away_team on g.away_team_id = away_team.id " + 
							  "left outer join team winning_team on g.winning_team_id = winning_team.id ";
			
			String whereBase = "";
			
			boolean addedWhere = false;
			boolean hasYears = Util.hasSomething(years);
			boolean hasWeeks = Util.hasSomething(weekNumbers);
			
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
				}
				
				String inParameterString = DatabaseUtil.createInClauseParameterString(weekNumbers.size());
				whereBase = whereBase + " w.week in " + inParameterString;
			}
			
			String orderBy = "order by s.year asc, w.week asc, g.id asc ";
			
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
				for (int index = 0; index < weekNumbers.size(); index++){
					Integer weekNumber = weekNumbers.get(index);
					statement.setInt(parameterIndex, weekNumber);
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
		}
		finally {
			DatabaseUtil.close(results, statement, connection);
		}
		
		return compactPicks;
	}
	
	protected CompactPick mapCompactPick(ResultSet results, List<String> playerNamesToUse, List<String> playerNames) throws SQLException {
		
		CompactPick compactPick = new CompactPick();
		
		/*
		 select s.year as year,
       w.week as week,
       home_team.abbreviation as home_team_abbreviation,
       away_team.abbreviation as away_team_abbreviation,
       winning_team.abbreviation as winning_team_abbreviation,
       
       (select picked_team.abbreviation
        from pick p join team picked_team on p.team_id = picked_team.id
        where p.game_id = g.id
              and p.player_id = 1) as x_pick
		 */
		
		String year = results.getString("year");
		int weekNumber = results.getInt("week");
		String homeTeamAbbreviation = results.getString("home_team_abbreviation");
		String awayTeamAbbreviation = results.getString("away_team_abbreviation");
		String winningTeamAbbreviation = results.getString("winning_team_abbreviation");
		
		Map<String, String> playerPicks = new HashMap<String, String>();
		for (int index = 0; index < playerNamesToUse.size(); index++){
			String playerNameToUse = playerNamesToUse.get(index);
			String playerName = playerNames.get(index);
			
			String playerPick = results.getString(playerNameToUse);
			
			playerPicks.put(playerName, playerPick);
		}
		
		compactPick.setYear(year);
		compactPick.setWeekNumber(weekNumber);
		compactPick.setHomeTeamAbbreviation(homeTeamAbbreviation);
		compactPick.setAwayTeamAbbreviation(awayTeamAbbreviation);
		compactPick.setWinningTeamAbbreviation(winningTeamAbbreviation);
		compactPick.setPlayerPicks(playerPicks);
		
		return compactPick;
	}
	
	protected class WeekRecordComparator implements Comparator<WeekRecord> {

		public int compare(WeekRecord weekRecord1, WeekRecord weekRecord2) {
			
			Season season1 = weekRecord1.getSeason();
			String year1 = season1.getYear();
			Season season2 = weekRecord2.getSeason();
			String year2 = season2.getYear();
			
			int seasonResult = year1.compareTo(year2);
			
			if (seasonResult != 0){
				return seasonResult;
			}
			
			Week week1 = weekRecord1.getWeek();
			Week week2 = weekRecord2.getWeek();
			
			int weekNumber1 = week1.getWeekNumber();
			int weekNumber2 = week2.getWeekNumber();
			
			if (weekNumber1 < weekNumber2){
				return -1;
			}
			else if (weekNumber1 > weekNumber2){
				return 1;
			}
			
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
			
			int losses1 = record1.getLosses();
			int losses2 = record2.getLosses();
			
			if (losses1 < losses2){
				return -1;
			}
			else if (losses1 > losses2){
				return 1;
			}
			
			return 0;
		}
	}
	
	public List<WeeksWon> getWeeksWon(String year){
		
		List<WeekRecord> weekRecords = getWeekRecords(year);
		
		Collections.sort(weekRecords, new WeekRecordComparator());
		//sort by year and week before going through them!
		
		List<WeeksWon> weeksWon = new ArrayList<WeeksWon>();
		
		List<Player> playersForYear = getPlayers(year);
		//go through and group them...
		//sort by year, and week
		//for each week record, get the year and week
		//compare
		//map of player name to their week
		//or
		//it could be like this
		//	year	week	winners
		//also want the other way
		//	player	weeks won
		//
		//group them in java or in javascript?
		//java is better i think
		//how should they be grouped?
		
		int currentSeasonId = -1;
		int currentWeekId = -1;
		boolean isNewWeek = false;
		
		//season	week	player		wins	losses
		//2017		8		tim			12		4
		
		List<Record> currentWinnersForTheWeek = new ArrayList<Record>();
		
		Map<Integer, WeeksWon> playerToWeeksWonMap = new HashMap<Integer, WeeksWon>();
		
		for (int index = 0; index < weekRecords.size(); index++){
			WeekRecord weekRecord = weekRecords.get(index);
			
			Season season = weekRecord.getSeason();
			int seasonId = season.getId();
			
			Week week = weekRecord.getWeek();
			int weekId = week.getId();
			
			if (index == 0){
				currentSeasonId = seasonId;
				currentWeekId = weekId;
			}
			
			isNewWeek = false;
			
			if (seasonId != currentSeasonId || weekId != currentWeekId){
				//it's a new week and season
				//handle the current first
				
				for (int recordIndex = 0; recordIndex < currentWinnersForTheWeek.size(); recordIndex++){
					Record winningRecord = currentWinnersForTheWeek.get(recordIndex);
					Player winningPlayer = winningRecord.getPlayer();
					WeeksWon currentWeeksWon = playerToWeeksWonMap.get(winningPlayer.getId());
					
					WeekRecord winningWeekRecord = new WeekRecord(season, week, winningRecord);
					
					List<WeekRecord> winningWeekRecordsForPlayer = null;
					
					if (currentWeeksWon == null){
						currentWeeksWon = new WeeksWon(winningPlayer);
						winningWeekRecordsForPlayer = new ArrayList<WeekRecord>();
					}
					else {
						winningWeekRecordsForPlayer = currentWeeksWon.getWeekRecords();
					}
					
					winningWeekRecordsForPlayer.add(winningWeekRecord);
					currentWeeksWon.setWeekRecords(winningWeekRecordsForPlayer);
					
					playerToWeeksWonMap.put(winningPlayer.getId(), currentWeeksWon);
				}
				
				currentSeasonId = seasonId;
				currentWeekId = weekId;
				isNewWeek = true;
				currentWinnersForTheWeek = new ArrayList<Record>();
			}
			
			Record record = weekRecord.getRecord();
			
			int wins = record.getWins();
			int losses = record.getLosses();
			
			boolean isBetterThanCurrentWinners = false;
			boolean isTieWithCurrentWinners = false;
			
			for (int recordIndex = 0; recordIndex < currentWinnersForTheWeek.size(); recordIndex++){
				Record winningRecord = currentWinnersForTheWeek.get(recordIndex);
				
				int winningRecordWins = winningRecord.getWins();
				int winningRecordLosses = winningRecord.getLosses();
				
				if (wins > winningRecordWins){
					isBetterThanCurrentWinners = true;
				}
				else if (wins == winningRecordWins){
					if (losses < winningRecordLosses){
						isBetterThanCurrentWinners = true;
					}
					else if (losses == winningRecordLosses){
						isTieWithCurrentWinners = true;
					}
				}
				
				if (isBetterThanCurrentWinners || isTieWithCurrentWinners){
					break;
				}
			}
			
			if (index == 0 || isNewWeek){
				isBetterThanCurrentWinners = true;
			}
			
			if (isBetterThanCurrentWinners){
				currentWinnersForTheWeek = new ArrayList<Record>();
				currentWinnersForTheWeek.add(record);
			}
			else if (isTieWithCurrentWinners){
				currentWinnersForTheWeek.add(record);
			}
		}
		
		for (int index = 0; index < playersForYear.size(); index++){
			Player player = playersForYear.get(index);
			
			WeeksWon weeksWonForPlayer = playerToWeeksWonMap.get(player.getId());
			
			if (weeksWonForPlayer == null){
				weeksWonForPlayer = new WeeksWon(player, new ArrayList<WeekRecord>());
			}
			
			weeksWon.add(weeksWonForPlayer);
		}
		
		return weeksWon;
		
		//playerToWeeksWonMap
	}
	
	//the weeks won will usually be for a single year and all players... should just need to get the records and then
	//make the weeks won off that.
	//	group the records by year and week
	//	go through each group and see who won that week
	//	make a WeekWon for that week.
	public List<WeekRecord> getWeekRecords(String year){
		//List<String> years, List<String> weeks, List<String> players
		//the record does the calculating of wins and losses in sql
		//will need to get the record for each player and each week of each year
		//could get this as a result set:
		//	year, week, record, player_name
		//
		//would have to get it once
		List<String> years = null;
		if (year != null){
			years = Arrays.asList(year);
		}
		
		List<WeekRecord> weekRecords = getWeekRecords(years, null, null);
		
		return weekRecords;
	}
	
	public List<WeekRecord> getWeekRecords(List<String> years, List<String> weeks, List<String> players){
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		List<WeekRecord> weekRecords = new ArrayList<WeekRecord>();
		
		try {
			connection = dataSource.getConnection();
			
			String weekRecordsCriteria = createWeekRecordsCriteria(years, weeks, players);
			
			String query = String.format(SELECT_WEEK_RECORDS, weekRecordsCriteria);
			
			statement = connection.prepareStatement(query);
			
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
			if (weeks != null && weeks.size() > 0){
				for (int index = 0; index < weeks.size(); index++){
					String week = weeks.get(index);
					int weekInt = Integer.parseInt(week);
					statement.setInt(parameterIndex, weekInt);
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
				WeekRecord weekRecord = mapWeekRecord(results);
				weekRecords.add(weekRecord);
			}
		}
		catch (Exception e){
			log.error("Error getting records! years = " + years + ", weeks = " + weeks + ", players = " + players, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return weekRecords;
		
	}
	
	protected String createWeekRecordsCriteria(List<String> years, List<String> weeks, List<String> players){
		
		/*
		 protected static final String SELECT_WEEKS_WON = "select pick_totals.season_id, " + 
													 		"pick_totals.year, " + 
													 		"pick_totals.player_id, " + 
													 		"pick_totals.player_name, " + 
													 		"pick_totals.week_id, " + 
													 		"pick_totals.week, " + 
													 		"pick_totals.week_label, " + 
													 		"sum(pick_totals.wins) as wins, " + 
													 		"sum(pick_totals.losses) as losses, " + 
													 		"sum(pick_totals.ties) as ties " + 
													 "from (select pl.id as player_id, " + 
													 		 	  "pl.name as player_name, " + 
													 		 	  "s.id as season_id, " + 
													 		 	  "s.year as year, " + 
													 		 	  "w.id as week_id, " + 
													 		 	  "w.week as week, " + 
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
													"group by season_id, year, pick_totals.player_id, pick_totals.player_name, week_id, week, week_label " + 
													"order by year, week, player_name ";
		 */
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
		boolean hasWeeks = weeks != null && weeks.size() > 0;
		
		if (hasYears || hasWeeks){
			
			if (!addedWhere){
				whereClause.append("where ");
			}
			else {
				whereClause.append(" and ");
			}
			
			if (hasWeeks){
				whereClause.append("w.week in (");
				for (int index = 0; index < weeks.size(); index++){
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
		
		return whereClause.toString();
	}
	
	protected WeekRecord mapWeekRecord(ResultSet results) throws SQLException {
		
		int seasonId = results.getInt("season_id");
		String year = results.getString("year");
		Season season = new Season(seasonId, year);

		int weekId = results.getInt("week_id");
		int weekNumber = results.getInt("week");
		String weekLabel = results.getString("week_label");
		//public Week(int id, int seasonId, int weekNumber, String label, List<Game> games)
		Week week = new Week(weekId, seasonId, weekNumber, weekLabel);
		
		int playerId = results.getInt("player_id");
		String playerName = results.getString("player_name");
		Player player = new Player(playerId, playerName);
		
		int wins = results.getInt("wins");
		int losses = results.getInt("losses");
		int ties = results.getInt("ties");
		Record record = new Record(player, wins, losses, ties);
		
		WeekRecord weekRecord = new WeekRecord(season, week, record);
		
		
		return weekRecord;
	}
	
	protected void close(ResultSet results, PreparedStatement statement, Connection connection){
		DatabaseUtil.close(results, statement, connection);
	}
	
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
}
