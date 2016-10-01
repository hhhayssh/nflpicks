package nflpicks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import nflpicks.model.Conference;
import nflpicks.model.Division;
import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.Record;
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
	protected static final String SELECT_WEEK = "select id, " +
												"season_id, " +
												"week, " + 
												"week_description " + 
												"from week ";
	
	protected static final String SELECT_GAME = "select id, " +
											    "week_id, " +
											    "home_team_id, " + 
											    "away_team_id, " + 
											    "winning_team_id " +
											    "from game ";
	
	protected static final String SELECT_PICK = "select id, " +
												"game_id, " + 
												"player_id, " + 
												"team_id " + 
												"from pick ";
	
	protected static final String SELECT_PLAYER = "select id, name from player ";

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
												 			 "(case when g.winning_team_id != -1 and (p.team_id is null or p.team_id != g.winning_team_id) " + 
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
	
	protected Team getTeam(int id){
		
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
	
	public List<Week> getWeeks(String year){
		
		List<Week> weeks = new ArrayList<Week>();
		
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
				Week week = mapWeek(results);
				weeks.add(week);
			}
		}
		catch (Exception e){
			log.error("Error getting weeks! year = " + year, e);
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
	
	public List<Week> getWeeks(){
		
		List<Week> weeks = new ArrayList<Week>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement(SELECT_WEEK);
			results = statement.executeQuery();
			
			while (results.next()){
				Week week = mapWeek(results);
				weeks.add(week);
			}
		}
		catch (Exception e){
			log.error("Error getting weeks!", e);
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
		
		Week week = new Week();
		int weekId = result.getInt("week_id");
		week.setId(weekId);
		week.setSeasonId(result.getInt("season_id"));
		week.setWeek(result.getInt("week"));
		
		List<Game> games = getGames(weekId);
		week.setGames(games);
		
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
						   					  					      "where year = ?)) ";
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
	
	protected Game getGame(int id){
		
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
		game.setWinningTeamId(winningTeamId);

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
	
	protected Pick getPick(int id){
		
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
			int winningTeamId = game.getWinningTeamId();
			if (winningTeamId != 0){
				if (winningTeamId == -1){
					pick.setResult("T");
				}
				else if (winningTeamId == pickedTeamId){
					pick.setResult("W");
				}
				else {
					pick.setResult("L");
				}
			}
		}
		
		return pick;
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
	
	protected void close(ResultSet results, PreparedStatement statement, Connection connection){
		closeResults(results);
		closeStatement(statement);
		closeConnection(connection);
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
	
	protected void closeResults(ResultSet results){
		try {
			if (results != null){
				results.close();
			}
		}
		catch (Exception e){
			log.error("Error closing results!", e);
		}
	}
	
	protected void closeStatement(PreparedStatement statement){
		try {
			if (statement != null){
				statement.close();
			}
		}
		catch (Exception e){
			log.error("Error closing statement!", e);
		}
	}
	
	protected void closeConnection(Connection connection){
		try {
			if (connection != null){
				connection.close();	
			}
		}
		catch (Exception e){
			log.error("Error closing connection!", e);
		}
	}
	
	public DataSource getDataSource(){
		return dataSource;
	}
	
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}
}
