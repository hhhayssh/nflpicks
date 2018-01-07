package nflpicks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

public class NFLPicksDataService {
	
	private static final Logger log = Logger.getLogger(NFLPicksDataService.class);

	protected DataSource dataSource;
	
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
	
	protected static final String SELECT_SEASON = "select id, " +
												  "year " +
												  "from season ";
	
	protected static final String INSERT_SEASON = "insert into season (year) values (?) ";
	
	protected static final String UPDATE_SEASON = "update season " + 
												  "set year = ? " +
												  "where id = ? ";
	
	
	protected static final String SELECT_WEEK = "select id, " +
												"season_id, " +
												"week, " + 
												"label " + 
												"from week ";
	
	//insert into week (season_id, week, label) values ((select s.id from season s where s.year = '2016'), 1, 'Week 1');
	protected static final String INSERT_WEEK = "insert into week (season_id, week, label) values (?, ?, ?) ";
	
	protected static final String UPDATE_WEEK = "update week " + 
												"set season_id = ?, " + 
												"week = ?, " + 
												"label = ? " + 
												"where id = ? ";
	
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
	
	protected static final String SELECT_PLAYER = "select id, name from player ";
	
	protected static final String INSERT_PLAYER = "insert into player (name) values (?) ";
	
	protected static final String UPDATE_PLAYER = "update player " +
											  	  "set name = ? " + 
											  	  "where id = ? ";

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
												 			  	   "%s " + 
												 		") pick_totals " + 
												"group by pick_totals.player_id, pick_totals.player_name ";
	
	public NFLPicksDataService(){
	}
	
	public NFLPicksDataService(DataSource dataSource){
		setDataSource(dataSource);
	}
	
	public String getYear(int seasonId){
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		String year = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement("select year from season where id = ? ");
			statement.setInt(1, seasonId);
			results = statement.executeQuery();
			
			while (results.next()){
				year = results.getString(1);
			}
		}
		catch (Exception e){
			log.error("Error getting seasons!", e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return year;
	}
	
	public List<String> getYears(){
		
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
			log.error("Error getting seasons!", e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return years;
	}
	
	public List<Season> getSeasons(List<String> years){
		
		List<Season> seasons = getSeasons(years, false);
		
		return seasons;
	}
	
	public List<Season> getSeasons(List<String> years, boolean shallow){
		
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
	
	public List<Season> getSeasons(){
		
		List<Season> seasons = new ArrayList<Season>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(SELECT_SEASON);
			results = statement.executeQuery();
			
			while (results.next()){
				Season season = mapSeason(results);
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
	
	public List<Conference> getConferences(){
		
		List<Conference> conferences = new ArrayList<Conference>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement(SELECT_CONFERENCE);
			results = statement.executeQuery();
			
			while (results.next()){
				Conference conference = mapConferenceResult(results);
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
	
	public Conference mapConferenceResult(ResultSet results) throws SQLException {
		
		Conference conference = new Conference();
		conference.setId(results.getInt("id"));
		conference.setName(results.getString("name"));
		
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
	
	public Division mapDivisionResult(ResultSet results) throws SQLException {
		
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
	
	public Season getSeason(int id){
		
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
				season = mapSeason(results);
			}
		}
		catch (Exception e){
			log.error("Error getting season! id = " + id, e);
		}
		finally {
			close(results, statement, connection);
		}
		
		return season;
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
				season = mapSeason(results);
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
				
				String inParameterString = DatabaseUtil.createInParameterString(years.size());
				
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
				
				String inParameterString = DatabaseUtil.createInParameterString(weekNumberIntegers.size());
			
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
		String year = getYear(seasonId);
		week.setYear(year);
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
				query = query + " where week in " + DatabaseUtil.createInParameterString(weekNumberIntegers.size());
			}
			
			if (hasYears){
				
				if (hasWeeks){
					query = query + " and ";
				}
				else {
					query = query + " where ";
				}
				
				query = query + " season_id in (select id from season where year in " + DatabaseUtil.createInParameterString(years.size()) + " ) ";
			}
			
			query = query + ")";
			
			query = query + ")";
			
			if (hasPlayers){
				query = query + " and player_id in (select id from player where name in " + DatabaseUtil.createInParameterString(playerNames.size()) + " ) ";
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
				String inParameterString = DatabaseUtil.createInParameterString(playerNames.size());
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
				String inParameterString = DatabaseUtil.createInParameterString(years.size());
				whereBase = "where s.year in " + inParameterString;
			}
			
			if (hasWeeks){
				
				if (addedWhere){
					whereBase = whereBase + " and ";
				}
				else {
					whereBase = "where ";
				}
				
				String inParameterString = DatabaseUtil.createInParameterString(weekNumbers.size());
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
