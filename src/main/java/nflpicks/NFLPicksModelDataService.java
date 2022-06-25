package nflpicks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import nflpicks.model.Division;
import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.PlayerDivision;
import nflpicks.model.Season;
import nflpicks.model.Team;
import nflpicks.model.TeamConference;
import nflpicks.model.TeamDivision;
import nflpicks.model.Week;

public class NFLPicksModelDataService {

	private static final Logger log = Logger.getLogger(NFLPicksModelDataService.class);

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

	/**
	 * 
	 * Usually pays off to have an empty constructor.  If you use this one, you should
	 * set the data source before doing anything.
	 * 
	 */
	public NFLPicksModelDataService(){
	}
	
	/**
	 * 
	 * Makes a data service that'll use the given data source to pull
	 * the data.
	 * 
	 * @param dataSource
	 */
	public NFLPicksModelDataService(DataSource dataSource){
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
}
