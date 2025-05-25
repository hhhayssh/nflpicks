package nflpicks;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import nflpicks.model.CompactPick;
import nflpicks.model.CompactPlayerPick;
import nflpicks.model.Division;
import nflpicks.model.DivisionRecord;
import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.PickSplit;
import nflpicks.model.Player;
import nflpicks.model.Record;
import nflpicks.model.Season;
import nflpicks.model.Team;
import nflpicks.model.Week;
import nflpicks.model.stats.Championship;
import nflpicks.model.stats.ChampionshipsForPlayer;
import nflpicks.model.stats.CollectivePickAccuracySummary;
import nflpicks.model.stats.CollectiveRecord;
import nflpicks.model.stats.CollectiveRecordSummary;
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
 * This class is here to do all the json specific stuff in one
 * place.  It'll convert to and from json for different
 * domain objects.  Dumb, you say?  Why don't you just use a mapper
 * library like jackson, you say? ... Because I don't want to and
 * this is my project, so I'm doing it old school, the way I want to.
 * 
 * @author albundy
 *
 */
public class JSONUtil {
	
	private static final Log log = LogFactory.getLog(JSONUtil.class);

	/**
	 * 
	 * This function converts the given seasons into a json formatted string.
	 * 
	 * @param seasons
	 * @return
	 */
	public static String seasonsToJSONString(List<Season> seasons){
		
		JSONArray jsonArray = seasonsToJSONArray(seasons);
		
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given seasons into a json array.
	 * 
	 * @param seasons
	 * @return
	 */
	public static JSONArray seasonsToJSONArray(List<Season> seasons){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < seasons.size(); index++){
			Season season = seasons.get(index);
			JSONObject jsonObject = seasonToJSONObject(season);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;

	}
	
	/**
	 * 
	 * This function converts the given season to a json formatted string.
	 * 
	 * @param season
	 * @return
	 */
	public static String seasonToJSONString(Season season){
		
		JSONObject jsonObject = seasonToJSONObject(season);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given season to a json object.  If the season
	 * has weeks in it, it'll convert the weeks too.  Otherwise, it won't set
	 * the weeks variable.
	 * 
	 * @param season
	 * @return
	 */
	public static JSONObject seasonToJSONObject(Season season){
		
		JSONObject jsonObject = new JSONObject();

		jsonObject.put(NFLPicksConstants.JSON_SEASON_ID, season.getId());
		jsonObject.put(NFLPicksConstants.JSON_SEASON_YEAR, season.getYear());
		
		List<Week> weeks = season.getWeeks();
		if (weeks != null){
			JSONArray weeksJSONArray = weeksToJSONArray(weeks);
			jsonObject.put(NFLPicksConstants.JSON_SEASON_WEEKS, weeksJSONArray);
		}
		
		return jsonObject;
	}
	
	/**
	 * 
	 * This function converts the given weeks to a json formatted string.
	 * 
	 * @param weeks
	 * @return
	 */
	public static String weeksToJSONString(List<Week> weeks){
		
		JSONArray jsonArray = weeksToJSONArray(weeks);
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given weeks to a json array.
	 * 
	 * @param weeks
	 * @return
	 */
	public static JSONArray weeksToJSONArray(List<Week> weeks){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < weeks.size(); index++){
			Week week = weeks.get(index);
			JSONObject jsonObject = weekToJSONObject(week);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;

	}
	
	/**
	 * 
	 * This function converts the given week to a json formatted string.
	 * 
	 * @param week
	 * @return
	 */
	public static String weekToJSONString(Week week){
		
		JSONObject jsonObject = weekToJSONObject(week);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given week to a json object.  If the week
	 * has games in it, it'll convert them too.  If it doesn't, it won't
	 * set the games variable.
	 * 
	 * @param week
	 * @return
	 */
	public static JSONObject weekToJSONObject(Week week){
		
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put(NFLPicksConstants.JSON_WEEK_ID, week.getId());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_WEEK_SEQUENCE_NUMBER, week.getSequenceNumber());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_TYPE, week.getType());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_KEY, week.getKey());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_LABEL, week.getLabel());

		List<Game> games = week.getGames();
		if (games != null){
			JSONArray gamesJSONArray = gamesToJSONArray(games);
			jsonObject.put(NFLPicksConstants.JSON_WEEK_GAMES, gamesJSONArray);
		}
		
		return jsonObject;
	}
	
	/**
	 * 
	 * Converts the given list of games into a json string.
	 * 
	 * @param games
	 * @return
	 */
	public static String gamesToJSONString(List<Game> games){
		
		JSONArray jsonArray = gamesToJSONArray(games);
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given list of games into a json array.
	 * 
	 * @param games
	 * @return
	 */
	public static JSONArray gamesToJSONArray(List<Game> games){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < games.size(); index++){
			Game game = games.get(index);
			JSONObject jsonObject = gameToJSONObject(game);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	/**
	 * 
	 * Converts the given game to a json formatted string.
	 * 
	 * @param game
	 * @return
	 */
	public static String gameToJSONString(Game game){
		
		JSONObject jsonObject = gameToJSONObject(game);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given game to a json object.  It makes a new json object
	 * and just copies in the game's values as values in that object using
	 * the same names.
	 * 
	 * @param game
	 * @return
	 */
	public static JSONObject gameToJSONObject(Game game){
		
		//Steps to do:
		//	1. Just go through and copy all the values and convert
		//	   the ones that are objects into json objects first.
		
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put(NFLPicksConstants.JSON_GAME_ID, game.getId());
		jsonObject.put(NFLPicksConstants.JSON_GAME_WEEK_ID, game.getWeekId());
		
		JSONObject homeTeamJSONObject = teamToJSONObject(game.getHomeTeam());
		jsonObject.put(NFLPicksConstants.JSON_GAME_HOME_TEAM, homeTeamJSONObject);
		
		JSONObject awayTeamJSONObject = teamToJSONObject(game.getAwayTeam());
		jsonObject.put(NFLPicksConstants.JSON_GAME_AWAY_TEAM, awayTeamJSONObject);
		
		Team winningTeam = game.getWinningTeam();
		if (winningTeam != null){
			JSONObject winningTeamJSONObject = teamToJSONObject(game.getWinningTeam());
			jsonObject.put(NFLPicksConstants.JSON_GAME_WINNING_TEAM, winningTeamJSONObject);
		}
		
		jsonObject.put(NFLPicksConstants.JSON_GAME_TIE, game.getTie());
		
		return jsonObject;
	}

	/**
	 * 
	 * Converts the given list of teams to a json string.
	 * 
	 * @param teams
	 * @return
	 */
	public static String teamsToJSONString(List<Team> teams){
		
		JSONArray jsonArray = teamsToJSONArray(teams);
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given list of teams into a json array.
	 * 
	 * @param teams
	 * @return
	 */
	public static JSONArray teamsToJSONArray(List<Team> teams){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < teams.size(); index++){
			Team team = teams.get(index);
			JSONObject jsonObject = teamToJSONObject(team);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	/**
	 * 
	 * Converts the given team into a json string.
	 * 
	 * @param team
	 * @return
	 */
	public static String teamToJSONString(Team team){
		
		JSONObject jsonObject = teamToJSONObject(team);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given team into a json object by just copying
	 * the variables into the json object.
	 * 
	 * @param team
	 * @return
	 */
	public static JSONObject teamToJSONObject(Team team){

		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put(NFLPicksConstants.JSON_TEAM_ID, team.getId());
		jsonObject.put(NFLPicksConstants.JSON_TEAM_DIVISION_ID, team.getDivisionId());
		jsonObject.put(NFLPicksConstants.JSON_TEAM_CITY, team.getCity());
		jsonObject.put(NFLPicksConstants.JSON_TEAM_NICKNAME, team.getNickname());
		jsonObject.put(NFLPicksConstants.JSON_TEAM_ABBREVIATION, team.getAbbreviation());
		
		return jsonObject;
	}
	
	/**
	 * 
	 * Converts the given list of players into a json formatted string.
	 * 
	 * @param players
	 * @return
	 */
	public static String playersToJSONString(List<Player> players){
		
		JSONArray jsonArray = playersToJSONArray(players);
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given list of players into an array of json objects.
	 * 
	 * @param players
	 * @return
	 */
	public static JSONArray playersToJSONArray(List<Player> players){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < players.size(); index++){
			Player playerInfo = players.get(index);
			JSONObject jsonObject = playerToJSONObject(playerInfo);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	/**
	 * 
	 * Converts the given player object into a json formatted string.
	 * 
	 * @param player
	 * @return
	 */
	public static String playerToJSONString(Player player){
		
		JSONObject jsonObject = playerToJSONObject(player);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given player into a json object.
	 * 
	 * @param player
	 * @return
	 */
	public static JSONObject playerToJSONObject(Player player){
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(NFLPicksConstants.JSON_PLAYER_ID, player.getId());
		jsonObject.put(NFLPicksConstants.JSON_PLAYER_NAME, player.getName());
		
		return jsonObject;
	}
	
	/**
	 * 
	 * Converts the given list of divisions into a json formatted string.
	 * 
	 * @param divisions
	 * @return
	 */
	public static String divisionsToJSONString(List<Division> divisions){
		
		JSONArray jsonArray = divisionsToJSONArray(divisions);
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given list of divisions into an array of json objects.
	 * 
	 * @param divisions
	 * @return
	 */
	public static JSONArray divisionsToJSONArray(List<Division> divisions){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < divisions.size(); index++){
			Division division = divisions.get(index);
			JSONObject jsonObject = divisionToJSONObject(division);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	/**
	 * 
	 * Converts the given division object into a json formatted string.
	 * 
	 * @param division
	 * @return
	 */
	public static String divisionToJSONString(Division division){
		
		JSONObject jsonObject = divisionToJSONObject(division);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given division into a json object.
	 * 
	 * @param player
	 * @return
	 */
	public static JSONObject divisionToJSONObject(Division division){
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(NFLPicksConstants.JSON_DIVISION_ID, division.getId());
		jsonObject.put(NFLPicksConstants.JSON_DIVISION_NAME, division.getName());
		jsonObject.put(NFLPicksConstants.JSON_DIVISION_ABBREVIATION, division.getAbbreviation());
		
		List<Player> players = division.getPlayers();
		if (players != null){
			JSONArray playersJSONArray = playersToJSONArray(players);
			jsonObject.put(NFLPicksConstants.JSON_DIVISION_PLAYERS, playersJSONArray);
		}
		
		return jsonObject;
	}
	
	/**
	 * 
	 * Converts the given list of division records to a json array.
	 * 
	 * @param divisionRecords
	 * @return
	 */
	public static JSONArray divisionRecordsToJSONArray(List<DivisionRecord> divisionRecords){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < divisionRecords.size(); index++){
			DivisionRecord divisionRecord = divisionRecords.get(index);
			JSONObject jsonObject = divisionRecordToJSONObject(divisionRecord);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	/**
	 * 
	 * Converts the given division record to a json formatted string.
	 * 
	 * @param record
	 * @return
	 */
	public static String divisionRecordToJSONString(DivisionRecord divisionRecord){
		
		JSONObject jsonObject = divisionRecordToJSONObject(divisionRecord);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given division record to a json object.
	 * 
	 * @param divisionRecord
	 * @return
	 */
	public static JSONObject divisionRecordToJSONObject(DivisionRecord divisionRecord){
		
		JSONObject jsonObject = new JSONObject();
		
		Division division = divisionRecord.getDivision();
		JSONObject divisionJSONObject = divisionToJSONObject(division);
		
		List<Record> records = divisionRecord.getRecords();
		JSONArray recordsJSONArray = null;
		if (records != null){
			recordsJSONArray = recordsToJSONArray(records);
		}
		
		jsonObject.put(NFLPicksConstants.JSON_DIVISION_RECORD_DIVISION, divisionJSONObject);
		jsonObject.put(NFLPicksConstants.JSON_DIVISION_RECORD_RECORDS, recordsJSONArray);
		
		return jsonObject;
	}
	
	/**
	 * 
	 * Converts the given list of picks into a json formatted string.
	 * 
	 * @param picks
	 * @return
	 */
	public static String picksToJSONString(List<Pick> picks){
		
		JSONArray jsonArray = picksToJSONArray(picks);
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given list of picks to a json array of json objects.
	 * 
	 * @param picks
	 * @return
	 */
	public static JSONArray picksToJSONArray(List<Pick> picks){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < picks.size(); index++){
			Pick pick = picks.get(index);
			JSONObject jsonObject = pickToJSONObject(pick);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	/**
	 * 
	 * Converts the given pick to a json formatted string.
	 * 
	 * @param pick
	 * @return
	 */
	public static String pickToJSONString(Pick pick){
		
		JSONObject jsonObject = pickToJSONObject(pick);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given pick into a json object.
	 * 
	 * @param pick
	 * @return
	 */
	public static JSONObject pickToJSONObject(Pick pick){
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(NFLPicksConstants.JSON_PICK_ID, pick.getId());
		
		JSONObject gameJSONObject = gameToJSONObject(pick.getGame());
		jsonObject.put(NFLPicksConstants.JSON_PICK_GAME, gameJSONObject);
		
		JSONObject playerJSONObject = playerToJSONObject(pick.getPlayer());
		jsonObject.put(NFLPicksConstants.JSON_PICK_PLAYER, playerJSONObject);
		
		Team team = pick.getTeam();
		if (team != null){
			JSONObject teamJSONObject = teamToJSONObject(team);
			jsonObject.put(NFLPicksConstants.JSON_PICK_TEAM, teamJSONObject);
			
			String result = pick.getResult();
			if (result != null){
				jsonObject.put(NFLPicksConstants.JSON_PICK_RESULT, result);
			}
		}
		
		return jsonObject;
	}
	
	/**
	 * 
	 * Converts the given list of records to a json array.
	 * 
	 * @param records
	 * @return
	 */
	public static JSONArray recordsToJSONArray(List<Record> records){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < records.size(); index++){
			Record record = records.get(index);
			JSONObject jsonObject = recordToJSONObject(record);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	/**
	 * 
	 * Converts the given record to a json formatted string.
	 * 
	 * @param record
	 * @return
	 */
	public static String recordToJSONString(Record record){
		
		JSONObject jsonObject = recordToJSONObject(record);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given record to a json object.
	 * 
	 * @param record
	 * @return
	 */
	public static JSONObject recordToJSONObject(Record record){
		
		JSONObject jsonObject = new JSONObject();
		
		JSONObject playerJSONObject = playerToJSONObject(record.getPlayer());
		jsonObject.put(NFLPicksConstants.JSON_RECORD_PLAYER, playerJSONObject);
		jsonObject.put(NFLPicksConstants.JSON_RECORD_WINS, record.getWins());
		jsonObject.put(NFLPicksConstants.JSON_RECORD_LOSSES, record.getLosses());
		jsonObject.put(NFLPicksConstants.JSON_RECORD_TIES, record.getTies());
		
		return jsonObject;
	}
	
	/**
	 * 
	 * This function converts the given week record objects into a json formatted
	 * string.
	 * 
	 * @param weeksWon
	 * @return
	 */
	public static String weekRecordsToJSONString(List<WeekRecord> weekRecord){
		
		JSONArray jsonArray = weekRecordsToJSONArray(weekRecord);
		
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given week records into a json array.
	 * 
	 * @param seasons
	 * @return
	 */
	public static JSONArray weekRecordsToJSONArray(List<WeekRecord> weekRecords){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < weekRecords.size(); index++){
			WeekRecord weekRecord = weekRecords.get(index);
			JSONObject jsonObject = weekRecordToJSONObject(weekRecord);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;

	}
	
	/**
	 * 
	 * This function converts the given week record to a json formatted string.
	 * 
	 * @param season
	 * @return
	 */
	public static String weekRecordToJSONString(WeekRecord weekRecord){
		
		JSONObject jsonObject = weekRecordToJSONObject(weekRecord);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given week record into a json object.
	 * 
	 * @param weekWon
	 * @return
	 */
	public static JSONObject weekRecordToJSONObject(WeekRecord weekRecord){
		
		JSONObject jsonObject = new JSONObject();
		
		JSONObject seasonJSONObject = seasonToJSONObject(weekRecord.getSeason());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORD_SEASON, seasonJSONObject);
		
		JSONObject weekJSONObject = weekToJSONObject(weekRecord.getWeek());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORD_WEEK, weekJSONObject);
		
		JSONObject recordJSONObject = recordToJSONObject(weekRecord.getRecord());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORD_RECORD, recordJSONObject);
		
		
		return jsonObject;
	}
	
	/**
	 * 
	 * This function converts the given week record for player list into a json formatted
	 * string.
	 * 
	 * @param weeksWon
	 * @return
	 */
	public static String weekRecordForPlayerListToJSONString(List<WeekRecordForPlayer> weekRecordForPlayerList){
		
		JSONArray jsonArray = weekRecordForPlayerListToJSONArray(weekRecordForPlayerList);
		
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given week record for player list into a json array.
	 * 
	 * @param seasons
	 * @return
	 */
	public static JSONArray weekRecordForPlayerListToJSONArray(List<WeekRecordForPlayer> weekRecordForPlayerList){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < weekRecordForPlayerList.size(); index++){
			WeekRecordForPlayer weekRecordForPlayer = weekRecordForPlayerList.get(index);
			JSONObject jsonObject = weekRecordForPlayerToJSONObject(weekRecordForPlayer);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;

	}
	
	/**
	 * 
	 * This function converts the given week record for player to a json formatted string.
	 * 
	 * @param season
	 * @return
	 */
	public static String weekRecordForPlayerToJSONString(WeekRecordForPlayer weekRecordForPlayer){
		
		JSONObject jsonObject = weekRecordForPlayerToJSONObject(weekRecordForPlayer);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given week record for player into a json object.
	 * 
	 * @param weekWon
	 * @return
	 */
	public static JSONObject weekRecordForPlayerToJSONObject(WeekRecordForPlayer weekRecordForPlayer){
		
		JSONObject jsonObject = new JSONObject();
		
		JSONObject playerJSONObject = playerToJSONObject(weekRecordForPlayer.getPlayer());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORD_FOR_PLAYER_PLAYER, playerJSONObject);
		
		JSONObject seasonJSONObject = seasonToJSONObject(weekRecordForPlayer.getSeason());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORD_FOR_PLAYER_SEASON, seasonJSONObject);
		
		JSONObject weekJSONObject = weekToJSONObject(weekRecordForPlayer.getWeek());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORD_FOR_PLAYER_WEEK, weekJSONObject);
		
		JSONObject recordJSONObject = recordToJSONObject(weekRecordForPlayer.getRecord());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORD_FOR_PLAYER_RECORD, recordJSONObject);
		
		
		return jsonObject;
	}
	
	/**
	 * 
	 * This function converts the given week record for players list into a json formatted
	 * string.
	 * 
	 * @param weeksWon
	 * @return
	 */
	public static String weekRecordForPlayersListToJSONString(List<WeekRecordForPlayers> weekRecordForPlayersList){
		
		JSONArray jsonArray = weekRecordForPlayersListToJSONArray(weekRecordForPlayersList);
		
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given week record for players list into a json array.
	 * 
	 * @param seasons
	 * @return
	 */
	public static JSONArray weekRecordForPlayersListToJSONArray(List<WeekRecordForPlayers> weekRecordForPlayersList){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < weekRecordForPlayersList.size(); index++){
			WeekRecordForPlayers weekRecordForPlayers = weekRecordForPlayersList.get(index);
			JSONObject jsonObject = weekRecordForPlayersToJSONObject(weekRecordForPlayers);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;

	}
	
	/**
	 * 
	 * This function will convert the given week record for players object into a json formatted
	 * string.
	 * 
	 * @param weekRecordForPlayers
	 * @return
	 */
	public static String playersWeekRecordToJSONString(WeekRecordForPlayers weekRecordForPlayers){
		
		JSONObject jsonObject = weekRecordForPlayersToJSONObject(weekRecordForPlayers);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function will convert the given week record for players object into a json object.
	 * 
	 * @param weekRecordForPlayers
	 * @return
	 */
	public static JSONObject weekRecordForPlayersToJSONObject(WeekRecordForPlayers weekRecordForPlayers){
		
		JSONObject jsonObject = new JSONObject();
		
		JSONArray playersJSONObject = playersToJSONArray(weekRecordForPlayers.getPlayers());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORD_FOR_PLAYERS_PLAYERS, playersJSONObject);
		
		JSONObject seasonJSONObject = seasonToJSONObject(weekRecordForPlayers.getSeason());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORD_FOR_PLAYERS_SEASON, seasonJSONObject);
		
		JSONObject weekJSONObject = weekToJSONObject(weekRecordForPlayers.getWeek());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORD_FOR_PLAYERS_WEEK, weekJSONObject);
		
		JSONObject recordJSONObject = recordToJSONObject(weekRecordForPlayers.getRecord());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORD_FOR_PLAYERS_RECORD, recordJSONObject);
		
		
		return jsonObject;
	}
	
	/**
	 * 
	 * Converts the given week records for player list into a json formatted string.
	 * 
	 * @param weekRecordsForPlayerList
	 * @return
	 */
	public static String weekRecordsForPlayerListToJSONString(List<WeekRecordsForPlayer> weekRecordsForPlayerList){
		
		JSONArray jsonArray = weekRecordsForPlayerListToJSONArray(weekRecordsForPlayerList);
		
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given week records for player list into a json array.
	 * 
	 * @param seasons
	 * @return
	 */
	public static JSONArray weekRecordsForPlayerListToJSONArray(List<WeekRecordsForPlayer> weekRecordsForPlayerList){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < weekRecordsForPlayerList.size(); index++){
			WeekRecordsForPlayer weekRecordsForPlayer = weekRecordsForPlayerList.get(index);
			JSONObject jsonObject = weekRecordsForPlayerToJSONObject(weekRecordsForPlayer);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;

	}
	
	/**
	 * 
	 * This function converts the given week records for player to a json formatted string.
	 * 
	 * @param season
	 * @return
	 */
	public static String weekRecordsForPlayerToJSONString(WeekRecordsForPlayer weekRecordsForPlayer){
		
		JSONObject jsonObject = weekRecordsForPlayerToJSONObject(weekRecordsForPlayer);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given week records for player into a json object.
	 * 
	 * @param weekWon
	 * @return
	 */
	public static JSONObject weekRecordsForPlayerToJSONObject(WeekRecordsForPlayer weeksWon){
		
		JSONObject jsonObject = new JSONObject();
		
		JSONObject playerJSONObject = playerToJSONObject(weeksWon.getPlayer());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORDS_FOR_PLAYER_PLAYER, playerJSONObject);
		
		JSONArray weekRecordsJSONArray = weekRecordsToJSONArray(weeksWon.getWeekRecords());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORDS_FOR_PLAYER_RECORDS, weekRecordsJSONArray);
		
		
		return jsonObject;
	}
	
	/**
	 * 
	 * This function converts the given championship objects into a json formatted
	 * string.
	 * 
	 * @param weeksWon
	 * @return
	 */
	public static String championshipsToJSONString(List<Championship> championships){
		
		JSONArray jsonArray = championshipsToJSONArray(championships);
		
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given championships into a json array.
	 * 
	 * @param seasons
	 * @return
	 */
	public static JSONArray championshipsToJSONArray(List<Championship> championships){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < championships.size(); index++){
			Championship championship = championships.get(index);
			JSONObject jsonObject = championshipToJSONObject(championship);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;

	}
	
	/**
	 * 
	 * This function converts the given championship to a json formatted string.
	 * 
	 * @param season
	 * @return
	 */
	public static String championshipToJSONString(Championship championship){
		
		JSONObject jsonObject = championshipToJSONObject(championship);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given championship into a json object.
	 * 
	 * @param weekWon
	 * @return
	 */
	public static JSONObject championshipToJSONObject(Championship championship){
		
		JSONObject jsonObject = new JSONObject();
		
		JSONObject playerJSONObject = playerToJSONObject(championship.getPlayer());
		jsonObject.put(NFLPicksConstants.JSON_CHAMPIONSHIP_PLAYER, playerJSONObject);
		
		JSONObject seasonJSONObject = seasonToJSONObject(championship.getSeason());
		jsonObject.put(NFLPicksConstants.JSON_CHAMPIONSHIP_SEASON, seasonJSONObject);
		
		JSONObject recordJSONObject = recordToJSONObject(championship.getRecord());
		jsonObject.put(NFLPicksConstants.JSON_CHAMPIONSHIP_RECORD, recordJSONObject);
		
		
		return jsonObject;
	}
	
	/**
	 * 
	 * This function converts the given championships for player list into a json formatted
	 * string.
	 * 
	 * @param weeksWon
	 * @return
	 */
	public static String championshipsForPlayerListToJSONString(List<ChampionshipsForPlayer> championshipsForPlayerList){
		
		JSONArray jsonArray = championshipsForPlayerListToJSONArray(championshipsForPlayerList);
		
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given championships for player list into a json array.
	 * 
	 * @param seasons
	 * @return
	 */
	public static JSONArray championshipsForPlayerListToJSONArray(List<ChampionshipsForPlayer> championshipsForPlayerList){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < championshipsForPlayerList.size(); index++){
			ChampionshipsForPlayer championshipsForPlayer = championshipsForPlayerList.get(index);
			JSONObject jsonObject = championshipsForPlayerToJSONObject(championshipsForPlayer);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;

	}
	
	/**
	 * 
	 * This function converts the given championships for player to a json formatted string.
	 * 
	 * @param season
	 * @return
	 */
	public static String championshipsForPlayerToJSONString(ChampionshipsForPlayer championshipsForPlayer){
		
		JSONObject jsonObject = championshipsForPlayerToJSONObject(championshipsForPlayer);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given championships for player into a json object.
	 * 
	 * @param championshipsForPlayer
	 * @return
	 */
	public static JSONObject championshipsForPlayerToJSONObject(ChampionshipsForPlayer championshipsForPlayer){
		
		JSONObject jsonObject = new JSONObject();
		
		JSONObject playerJSONObject = playerToJSONObject(championshipsForPlayer.getPlayer());
		jsonObject.put(NFLPicksConstants.JSON_CHAMPIONSHIPS_FOR_PLAYER_PLAYER, playerJSONObject);
		
		JSONArray championshipsJSONArray = championshipsToJSONArray(championshipsForPlayer.getChampionships());
		jsonObject.put(NFLPicksConstants.JSON_CHAMPIONSHIPS_FOR_PLAYER_CHAMPIONSHIPS, championshipsJSONArray);
		
		
		return jsonObject;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * This function converts the given division title objects into a json formatted
	 * string.
	 * 
	 * @param weeksWon
	 * @return
	 */
	public static String divisionTitlesToJSONString(List<DivisionTitle> divisionTitles){
		
		JSONArray jsonArray = divisionTitlesToJSONArray(divisionTitles);
		
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given division titles into a json array.
	 * 
	 * @param seasons
	 * @return
	 */
	public static JSONArray divisionTitlesToJSONArray(List<DivisionTitle> divisionTitles){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < divisionTitles.size(); index++){
			DivisionTitle divisionTitle = divisionTitles.get(index);
			JSONObject jsonObject = divisionTitleToJSONObject(divisionTitle);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;

	}
	
	/**
	 * 
	 * This function converts the given division title to a json formatted string.
	 * 
	 * @param season
	 * @return
	 */
	public static String divisionTitleToJSONString(DivisionTitle divisionTitle){
		
		JSONObject jsonObject = divisionTitleToJSONObject(divisionTitle);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given division title into a json object.
	 * 
	 * @param weekWon
	 * @return
	 */
	public static JSONObject divisionTitleToJSONObject(DivisionTitle divisionTitle){
		
		JSONObject jsonObject = new JSONObject();
		
		JSONObject divisionJSONObject = divisionToJSONObject(divisionTitle.getDivision());
		jsonObject.put(NFLPicksConstants.JSON_DIVISION_TITLE_DIVISION, divisionJSONObject);
		
		JSONObject playerJSONObject = playerToJSONObject(divisionTitle.getPlayer());
		jsonObject.put(NFLPicksConstants.JSON_DIVISION_TITLE_PLAYER, playerJSONObject);
		
		JSONObject seasonJSONObject = seasonToJSONObject(divisionTitle.getSeason());
		jsonObject.put(NFLPicksConstants.JSON_DIVISION_TITLE_SEASON, seasonJSONObject);
		
		JSONObject recordJSONObject = recordToJSONObject(divisionTitle.getRecord());
		jsonObject.put(NFLPicksConstants.JSON_DIVISION_TITLE_RECORD, recordJSONObject);
		
		return jsonObject;
	}
	
	/**
	 * 
	 * This function converts the given championships for player list into a json formatted
	 * string.
	 * 
	 * @param weeksWon
	 * @return
	 */
	public static String divisionTitlesForPlayerListToJSONString(List<DivisionTitlesForPlayer> divisionTitlesForPlayerList){
		
		JSONArray jsonArray = divisionTitlesForPlayerListToJSONArray(divisionTitlesForPlayerList);
		
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given division titles for player list into a json array.
	 * 
	 * @param seasons
	 * @return
	 */
	public static JSONArray divisionTitlesForPlayerListToJSONArray(List<DivisionTitlesForPlayer> divisionTitlesForPlayerList){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < divisionTitlesForPlayerList.size(); index++){
			DivisionTitlesForPlayer divisionTitlesForPlayer = divisionTitlesForPlayerList.get(index);
			JSONObject jsonObject = divisionTitlesForPlayerToJSONObject(divisionTitlesForPlayer);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;

	}
	
	/**
	 * 
	 * This function converts the given division titles for player to a json formatted string.
	 * 
	 * @param season
	 * @return
	 */
	public static String divisionTitlesForPlayerToJSONString(DivisionTitlesForPlayer divisionTitlesForPlayer){
		
		JSONObject jsonObject = divisionTitlesForPlayerToJSONObject(divisionTitlesForPlayer);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given division titles for player into a json object.
	 * 
	 * @param divisionTitlesForPlayer
	 * @return
	 */
	public static JSONObject divisionTitlesForPlayerToJSONObject(DivisionTitlesForPlayer divisionTitlesForPlayer){
		
		JSONObject jsonObject = new JSONObject();
		
		JSONObject playerJSONObject = playerToJSONObject(divisionTitlesForPlayer.getPlayer());
		jsonObject.put(NFLPicksConstants.JSON_DIVISION_TITLES_FOR_PLAYER_PLAYER, playerJSONObject);
		
		JSONArray divisionTitlesJSONArray = divisionTitlesToJSONArray(divisionTitlesForPlayer.getDivisionTitles());
		jsonObject.put(NFLPicksConstants.JSON_DIVISION_TITLES_FOR_PLAYER_DIVISION_TITLES, divisionTitlesJSONArray);
		
		
		return jsonObject;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * This function converts the given pick accuracy summaries list into a json formatted
	 * string.
	 * 
	 * @param weeksWon
	 * @return
	 */
	public static String pickAccuracySummariesListToJSONString(List<PickAccuracySummary> pickAccuracySummaries){
		
		JSONArray jsonArray = pickAccuracySummariesToJSONArray(pickAccuracySummaries);
		
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given pick accuracy summaries into a json array.
	 * 
	 * @param seasons
	 * @return
	 */
	public static JSONArray pickAccuracySummariesToJSONArray(List<PickAccuracySummary> pickAccuracySummaries){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < pickAccuracySummaries.size(); index++){
			PickAccuracySummary pickAccuracySummary = pickAccuracySummaries.get(index);
			JSONObject jsonObject = pickAccuracySummaryToJSONObject(pickAccuracySummary);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;

	}
	
	/**
	 * 
	 * This function converts the given pick accuracy summary to a json formatted string.
	 * 
	 * @param pickAccuracySummary
	 * @return
	 */
	public static String pickAccuracySummaryToJSONString(PickAccuracySummary pickAccuracySummary){
		
		JSONObject jsonObject = pickAccuracySummaryToJSONObject(pickAccuracySummary);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given pick accuracy summary into a json object.
	 * 
	 * @param pickAccuracySummary
	 * @return
	 */
	public static JSONObject pickAccuracySummaryToJSONObject(PickAccuracySummary pickAccuracySummary){
		
		JSONObject jsonObject = new JSONObject();
		
		JSONObject playerJSONObject = playerToJSONObject(pickAccuracySummary.getPlayer());
		jsonObject.put(NFLPicksConstants.JSON_PICK_ACCURACY_SUMMARY_PLAYER, playerJSONObject);
		
		JSONObject teamJSONObject = teamToJSONObject(pickAccuracySummary.getTeam());
		jsonObject.put(NFLPicksConstants.JSON_PICK_ACCURACY_SUMMARY_TEAM, teamJSONObject);
		
		jsonObject.put(NFLPicksConstants.JSON_PICK_ACCURACY_SUMMARY_ACTUAL_WINS, pickAccuracySummary.getActualWins());
		jsonObject.put(NFLPicksConstants.JSON_PICK_ACCURACY_SUMMARY_ACTUAL_LOSSES, pickAccuracySummary.getActualLosses());
		jsonObject.put(NFLPicksConstants.JSON_PICK_ACCURACY_SUMMARY_ACTUAL_TIES, pickAccuracySummary.getActualTies());
		jsonObject.put(NFLPicksConstants.JSON_PICK_ACCURACY_SUMMARY_PREDICTED_WINS, pickAccuracySummary.getPredictedWins());
		jsonObject.put(NFLPicksConstants.JSON_PICK_ACCURACY_SUMMARY_PREDICTED_LOSSES, pickAccuracySummary.getPredictedLosses());
		jsonObject.put(NFLPicksConstants.JSON_PICK_ACCURACY_SUMMARY_TIMES_RIGHT, pickAccuracySummary.getTimesRight());
		jsonObject.put(NFLPicksConstants.JSON_PICK_ACCURACY_SUMMARY_TIMES_WRONG, pickAccuracySummary.getTimesWrong());
		jsonObject.put(NFLPicksConstants.JSON_PICK_ACCURACY_SUMMARY_TIMES_PICKED_TO_WIN_RIGHT, pickAccuracySummary.getTimesPickedToWinRight());
		jsonObject.put(NFLPicksConstants.JSON_PICK_ACCURACY_SUMMARY_TIMES_PICKED_TO_WIN_WRONG, pickAccuracySummary.getTimesPickedToWinWrong());
		jsonObject.put(NFLPicksConstants.JSON_PICK_ACCURACY_SUMMARY_TIMES_PICKED_TO_LOSE_RIGHT, pickAccuracySummary.getTimesPickedToLoseRight());
		jsonObject.put(NFLPicksConstants.JSON_PICK_ACCURACY_SUMMARY_TIMES_PICKED_TO_LOSE_WRONG, pickAccuracySummary.getTimesPickedToLoseWrong());
		
		return jsonObject;
	}

	
	/**
	 * 
	 * Converts the given list of compact picks into a json string.
	 * 
	 * @param games
	 * @return
	 */
	public static String compactPicksToJSONString(List<CompactPick> picks){
		
		JSONArray jsonArray = compactPicksToJSONArray(picks);
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given list of picks into a json array.
	 * 
	 * @param games
	 * @return
	 */
	public static JSONArray compactPicksToJSONArray(List<CompactPick> picks){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < picks.size(); index++){
			CompactPick pick = picks.get(index);
			JSONObject jsonObject = compactPickToJSONObject(pick);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	/**
	 * 
	 * Converts the given compact pick to a json formatted string.
	 * 
	 * @param game
	 * @return
	 */
	public static String compactPickToJSONString(CompactPick compactPick){
		
		JSONObject jsonObject = compactPickToJSONObject(compactPick);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given compact pick to a json object.  Not much to it.
	 * 
	 * @param game
	 * @return
	 */
	public static JSONObject compactPickToJSONObject(CompactPick pick){
		
		//Steps to do:
		//	1. Just go through and copy all the values and convert
		//	   the ones that are objects into json objects first.
		
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put(NFLPicksConstants.JSON_COMPACT_PICK_YEAR, pick.getYear());
		jsonObject.put(NFLPicksConstants.JSON_COMPACT_PICK_WEEK_SEQUENCE_NUMBER, pick.getWeekSequenceNumber());
		jsonObject.put(NFLPicksConstants.JSON_COMPACT_PICK_WEEK_TYPE, pick.getWeekType());
		jsonObject.put(NFLPicksConstants.JSON_COMPACT_PICK_WEEK_KEY, pick.getWeekKey());
		jsonObject.put(NFLPicksConstants.JSON_COMPACT_PICK_WEEK_LABEL, pick.getWeekLabel());
		jsonObject.put(NFLPicksConstants.JSON_COMPACT_PICK_HOME_TEAM_ABBREVIATION, pick.getHomeTeamAbbreviation());
		jsonObject.put(NFLPicksConstants.JSON_COMPACT_PICK_AWAY_TEAM_ABBREVIATION, pick.getAwayTeamAbbreviation());
		jsonObject.put(NFLPicksConstants.JSON_COMPACT_PICK_WINNING_TEAM_ABBREVIATION, pick.getWinningTeamAbbreviation());
		
		JSONArray playerPicksArray = compactPlayerPicksToJSONArray(pick.getPlayerPicks());

		jsonObject.put(NFLPicksConstants.JSON_COMPACT_PICK_PLAYER_PICKS, playerPicksArray);
		
		return jsonObject;
	}
	
	
	/**
	 * 
	 * Converts the given list of compact picks into a json string.
	 * 
	 * @param games
	 * @return
	 */
	public static String compactPlayerPicksToJSONString(List<CompactPlayerPick> playerPicks){
		
		JSONArray jsonArray = compactPlayerPicksToJSONArray(playerPicks);
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given list of player picks into a json array.
	 * 
	 * @param games
	 * @return
	 */
	public static JSONArray compactPlayerPicksToJSONArray(List<CompactPlayerPick> playerPicks){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < playerPicks.size(); index++){
			CompactPlayerPick pick = playerPicks.get(index);
			JSONObject jsonObject = compactPlayerPickToJSONObject(pick);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	/**
	 * 
	 * Converts the given compact player pick to a json formatted string.
	 * 
	 * @param game
	 * @return
	 */
	public static String compactPlayerPickToJSONString(CompactPlayerPick playerPick){
		
		JSONObject jsonObject = compactPlayerPickToJSONObject(playerPick);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given compact player pick to a json object.  Not much to it.
	 * 
	 * @param game
	 * @return
	 */
	public static JSONObject compactPlayerPickToJSONObject(CompactPlayerPick playerPick){
		
		//Steps to do:
		//	1. Just go through and copy all the values and convert
		//	   the ones that are objects into json objects first.
		
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put(NFLPicksConstants.JSON_COMPACT_PLAYER_PICK_PLAYER, playerPick.getPlayer());
		jsonObject.put(NFLPicksConstants.JSON_COMPACT_PLAYER_PICK_PICK, playerPick.getPick());
		
		return jsonObject;
	}
	
	/**
	 * 
	 * Converts the given list of pick splits into a json string.
	 * 
	 * @param games
	 * @return
	 */
	public static String pickSplitsToJSONString(List<PickSplit> pickSplits){
		
		JSONArray jsonArray = pickSplitsToJSONArray(pickSplits);
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given list of pick splits into a json array.
	 * 
	 * @param pickSplits
	 * @return
	 */
	public static JSONArray pickSplitsToJSONArray(List<PickSplit> pickSplits){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < pickSplits.size(); index++){
			PickSplit pickSplit = pickSplits.get(index);
			JSONObject jsonObject = pickSplitToJSONObject(pickSplit);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	/**
	 * 
	 * Converts the given pick split to a json formatted string.
	 * 
	 * @param pickSplit
	 * @return
	 */
	public static String pickSplitToJSONString(PickSplit pickSplit){
		
		JSONObject jsonObject = pickSplitToJSONObject(pickSplit);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given pick split to a json object.  Not much to it.
	 * 
	 * @param pickSplit
	 * @return
	 */
	public static JSONObject pickSplitToJSONObject(PickSplit pickSplit){
		
		//Steps to do:
		//	1. Just go through and copy all the values and convert
		//	   the ones that are objects into json objects first.
		
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put(NFLPicksConstants.JSON_PICK_SPLIT_YEAR, pickSplit.getYear());
		jsonObject.put(NFLPicksConstants.JSON_PICK_SPLIT_WEEK_SEQUENCE_NUMBER, pickSplit.getWeekSequenceNumber());
		jsonObject.put(NFLPicksConstants.JSON_PICK_SPLIT_WEEK_TYPE, pickSplit.getWeekType());
		jsonObject.put(NFLPicksConstants.JSON_PICK_SPLIT_WEEK_KEY, pickSplit.getWeekKey());
		jsonObject.put(NFLPicksConstants.JSON_PICK_SPLIT_WEEK_LABEL, pickSplit.getWeekLabel());
		jsonObject.put(NFLPicksConstants.JSON_PICK_SPLIT_HOME_TEAM_ABBREVIATION, pickSplit.getHomeTeamAbbreviation());
		jsonObject.put(NFLPicksConstants.JSON_PICK_SPLIT_AWAY_TEAM_ABBREVIATION, pickSplit.getAwayTeamAbbreviation());
		jsonObject.put(NFLPicksConstants.JSON_PICK_SPLIT_WINNING_TEAM_ABBREVIATION, pickSplit.getWinningTeamAbbreviation());
		jsonObject.put(NFLPicksConstants.JSON_PICK_SPLIT_HOME_TEAM_PLAYERS, pickSplit.getHomeTeamPlayers());
		jsonObject.put(NFLPicksConstants.JSON_PICK_SPLIT_AWAY_TEAM_PLAYERS, pickSplit.getAwayTeamPlayers());
		
		return jsonObject;
	}

	/**
	 * 
	 * Converts the given list of season records into a json string.
	 * 
	 * @param games
	 * @return
	 */
	public static String seasonRecordsForPlayersToJSONString(List<SeasonRecordForPlayer> seasonRecords){
		
		JSONArray jsonArray = seasonRecordsForPlayersToJSONArray(seasonRecords);
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given list of season records into a json array.
	 * 
	 * @param pickSplits
	 * @return
	 */
	public static JSONArray seasonRecordsForPlayersToJSONArray(List<SeasonRecordForPlayer> seasonRecords){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < seasonRecords.size(); index++){
			SeasonRecordForPlayer seasonRecord = seasonRecords.get(index);
			JSONObject jsonObject = seasonRecordForPlayerToJSONObject(seasonRecord);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	/**
	 * 
	 * Converts the given season record for player to a json formatted string.
	 * 
	 * @param pickSplit
	 * @return
	 */
	public static String seasonRecordForPlayerToJSONString(SeasonRecordForPlayer seasonRecord){
		
		JSONObject jsonObject = seasonRecordForPlayerToJSONObject(seasonRecord);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given season record for player to a json object.  Not much to it.
	 * 
	 * @param pickSplit
	 * @return
	 */
	public static JSONObject seasonRecordForPlayerToJSONObject(SeasonRecordForPlayer seasonRecord){
		
		//Steps to do:
		//	1. Just go through and copy all the values and convert
		//	   the ones that are objects into json objects first.
		
		JSONObject jsonObject = new JSONObject();

		JSONObject playerJSONObject = playerToJSONObject(seasonRecord.getPlayer());
		jsonObject.put(NFLPicksConstants.JSON_SEASON_RECORD_FOR_PLAYER_PLAYER, playerJSONObject);
		JSONObject seasonJSONObject = seasonToJSONObject(seasonRecord.getSeason());
		jsonObject.put(NFLPicksConstants.JSON_SEASON_RECORD_FOR_PLAYER_SEASON, seasonJSONObject);
		JSONObject recordJSONObject = recordToJSONObject(seasonRecord.getRecord());
		jsonObject.put(NFLPicksConstants.JSON_SEASON_RECORD_FOR_PLAYER_RECORD, recordJSONObject);
		jsonObject.put(NFLPicksConstants.JSON_SEASON_RECORD_FOR_PLAYER_CHAMPIONSHIP, seasonRecord.getChampionship());
		
		return jsonObject;
	}
	
	/**
	 * 
	 * Converts the given list of collective records into a json string.
	 * 
	 * @param collectiveRecords
	 * @return
	 */
	public static String collectiveRecordsToJSONString(List<CollectiveRecord> collectiveRecords){
		
		JSONArray jsonArray = collectiveRecordsToJSONArray(collectiveRecords);
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given list of collective records into a json array.
	 * 
	 * @param collectiveRecords
	 * @return
	 */
	public static JSONArray collectiveRecordsToJSONArray(List<CollectiveRecord> collectiveRecords){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < collectiveRecords.size(); index++){
			CollectiveRecord collectiveRecord = collectiveRecords.get(index);
			JSONObject jsonObject = collectiveRecordToJSONObject(collectiveRecord);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	/**
	 * 
	 * Converts the given collective record to a json formatted string.
	 * 
	 * @param collectiveRecord
	 * @return
	 */
	public static String collectiveRecordToJSONString(CollectiveRecord collectiveRecord){
		
		JSONObject jsonObject = collectiveRecordToJSONObject(collectiveRecord);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given collective record to a json object.  Not much to it.
	 * 
	 * @param collectiveRecord
	 * @return
	 */
	public static JSONObject collectiveRecordToJSONObject(CollectiveRecord collectiveRecord){
		
		//Steps to do:
		//	1. Just go through and copy all the values and convert
		//	   the ones that are objects into json objects first.
		
		JSONObject jsonObject = new JSONObject();
		
		Season season = collectiveRecord.getSeason();
		JSONObject seasonJSONObject = seasonToJSONObject(season);
		
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_RECORD_SEASON, seasonJSONObject);
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_RECORD_WINS, collectiveRecord.getWins());
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_RECORD_LOSSES, collectiveRecord.getLosses());
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_RECORD_TIES, collectiveRecord.getTies());
		
		return jsonObject;
	}
	
	/**
	 * 
	 * Converts the given list of collective record summaries into a json string.
	 * 
	 * @param collectiveRecordSummaries
	 * @return
	 */
	public static String collectiveRecordSummariesToJSONString(List<CollectiveRecordSummary> collectiveRecordSummaries){
		
		JSONArray jsonArray = collectiveRecordSummariesToJSONArray(collectiveRecordSummaries);
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given list of collective record summaries into a json array.
	 * 
	 * @param collectiveRecordsSummaries
	 * @return
	 */
	public static JSONArray collectiveRecordSummariesToJSONArray(List<CollectiveRecordSummary> collectiveRecordSummaries){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < collectiveRecordSummaries.size(); index++){
			CollectiveRecordSummary collectiveRecordSummary = collectiveRecordSummaries.get(index);
			JSONObject jsonObject = collectiveRecordSummaryToJSONObject(collectiveRecordSummary);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	/**
	 * 
	 * Converts the given collective record summary to a json formatted string.
	 * 
	 * @param collectiveRecordSummary
	 * @return
	 */
	public static String collectiveRecordSummaryToJSONString(CollectiveRecordSummary collectiveRecordSummary){
		
		JSONObject jsonObject = collectiveRecordSummaryToJSONObject(collectiveRecordSummary);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * Converts the given collective record summary to a json object.  Not much to it.
	 * 
	 * @param collectiveRecordSummary
	 * @return
	 */
	public static JSONObject collectiveRecordSummaryToJSONObject(CollectiveRecordSummary collectiveRecordSummary){
		
		//Steps to do:
		//	1. Just go through and copy all the values and convert
		//	   the ones that are objects into json objects first.
		
		JSONObject jsonObject = new JSONObject();
		
		List<CollectiveRecord> collectiveRecords = collectiveRecordSummary.getCollectiveRecords();
		
		JSONArray collectiveRecordsJSONArray = collectiveRecordsToJSONArray(collectiveRecords);
		
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_RECORD_SUMMARY_COLLECTIVE_RECORDS, collectiveRecordsJSONArray);
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_RECORD_WINS, collectiveRecordSummary.getWins());
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_RECORD_LOSSES, collectiveRecordSummary.getLosses());
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_RECORD_TIES, collectiveRecordSummary.getTies());
		
		return jsonObject;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * This function converts the given collective pick accuracy summaries list into a json formatted
	 * string.
	 * 
	 * @param collectivePickAccuracySummaries
	 * @return
	 */
	public static String collectivePickAccuracySummariesListToJSONString(List<CollectivePickAccuracySummary> collectivePickAccuracySummaries){
		
		JSONArray jsonArray = collectivePickAccuracySummariesToJSONArray(collectivePickAccuracySummaries);
		
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given collective pick accuracy summaries into a json array.
	 * 
	 * @param collectivePickAccuracySummaries
	 * @return
	 */
	public static JSONArray collectivePickAccuracySummariesToJSONArray(List<CollectivePickAccuracySummary> collectivePickAccuracySummaries){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < collectivePickAccuracySummaries.size(); index++){
			CollectivePickAccuracySummary collectivePickAccuracySummary = collectivePickAccuracySummaries.get(index);
			JSONObject jsonObject = collectivePickAccuracySummaryToJSONObject(collectivePickAccuracySummary);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;

	}
	
	/**
	 * 
	 * This function converts the given collective pick accuracy summary to a json formatted string.
	 * 
	 * @param collectivePickAccuracySummary
	 * @return
	 */
	public static String collectivePickAccuracySummaryToJSONString(CollectivePickAccuracySummary collectivePickAccuracySummary){
		
		JSONObject jsonObject = collectivePickAccuracySummaryToJSONObject(collectivePickAccuracySummary);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given collective pick accuracy summary into a json object.
	 * 
	 * @param collectivePickAccuracySummary
	 * @return
	 */
	public static JSONObject collectivePickAccuracySummaryToJSONObject(CollectivePickAccuracySummary collectivePickAccuracySummary){
		
		JSONObject jsonObject = new JSONObject();
		
		JSONObject teamJSONObject = teamToJSONObject(collectivePickAccuracySummary.getTeam());
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_PICK_ACCURACY_SUMMARY_TEAM, teamJSONObject);
		
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_PICK_ACCURACY_SUMMARY_ACTUAL_WINS, collectivePickAccuracySummary.getActualWins());
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_PICK_ACCURACY_SUMMARY_ACTUAL_LOSSES, collectivePickAccuracySummary.getActualLosses());
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_PICK_ACCURACY_SUMMARY_ACTUAL_TIES, collectivePickAccuracySummary.getActualTies());
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_PICK_ACCURACY_SUMMARY_PREDICTED_WINS, collectivePickAccuracySummary.getPredictedWins());
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_PICK_ACCURACY_SUMMARY_PREDICTED_LOSSES, collectivePickAccuracySummary.getPredictedLosses());
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_PICK_ACCURACY_SUMMARY_TIMES_RIGHT, collectivePickAccuracySummary.getTimesRight());
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_PICK_ACCURACY_SUMMARY_TIMES_WRONG, collectivePickAccuracySummary.getTimesWrong());
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_PICK_ACCURACY_SUMMARY_TIMES_PICKED_TO_WIN_RIGHT, collectivePickAccuracySummary.getTimesPickedToWinRight());
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_PICK_ACCURACY_SUMMARY_TIMES_PICKED_TO_WIN_WRONG, collectivePickAccuracySummary.getTimesPickedToWinWrong());
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_PICK_ACCURACY_SUMMARY_TIMES_PICKED_TO_LOSE_RIGHT, collectivePickAccuracySummary.getTimesPickedToLoseRight());
		jsonObject.put(NFLPicksConstants.JSON_COLLECTIVE_PICK_ACCURACY_SUMMARY_TIMES_PICKED_TO_LOSE_WRONG, collectivePickAccuracySummary.getTimesPickedToLoseWrong());
		
		return jsonObject;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * Creates a new json object from the given string (which should be in the
	 * json format).
	 * 
	 * @param json
	 * @return
	 */
	public static JSONObject createJSONObjectFromString(String json){
		
		JSONObject object = null;
		
		try {
			object = new JSONObject(json);
		}
		catch (Exception e){
			log.error("Error creating json object from string! json = " + json, e);
		}
		
		return object;
	}
}
