package nflpicks;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.Record;
import nflpicks.model.Season;
import nflpicks.model.Team;
import nflpicks.model.Week;
import nflpicks.model.stats.Championship;
import nflpicks.model.stats.PlayerWeekRecord;
import nflpicks.model.stats.PlayerWeeksWon;
import nflpicks.model.stats.WeekRecord;

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
	
	private static final Logger log = Logger.getLogger(JSONUtil.class);

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
		jsonObject.put(NFLPicksConstants.JSON_WEEK_WEEK_NUMBER, week.getWeekNumber());
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
		jsonObject.put(NFLPicksConstants.JSON_TEAM_NAME, team.getName());
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
	 * This function converts the given week record objects into a json formatted
	 * string.
	 * 
	 * @param weeksWon
	 * @return
	 */
	public static String playerWeekRecordsToJSONString(List<PlayerWeekRecord> playerWeekRecords){
		
		JSONArray jsonArray = playerWeekRecordsToJSONArray(playerWeekRecords);
		
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
	public static JSONArray playerWeekRecordsToJSONArray(List<PlayerWeekRecord> playerWeekRecords){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < playerWeekRecords.size(); index++){
			PlayerWeekRecord playerWeekRecord = playerWeekRecords.get(index);
			JSONObject jsonObject = playerWeekRecordToJSONObject(playerWeekRecord);
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
	public static String playerWeekRecordToJSONString(PlayerWeekRecord playerWeekRecord){
		
		JSONObject jsonObject = playerWeekRecordToJSONObject(playerWeekRecord);
		
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
	public static JSONObject playerWeekRecordToJSONObject(PlayerWeekRecord playerWeekRecord){
		
		JSONObject jsonObject = new JSONObject();
		
		JSONObject playerJSONObject = playerToJSONObject(playerWeekRecord.getPlayer());
		jsonObject.put(NFLPicksConstants.JSON_PLAYER_WEEK_RECORD_PLAYER, playerJSONObject);
		
		JSONObject seasonJSONObject = seasonToJSONObject(playerWeekRecord.getSeason());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORD_SEASON, seasonJSONObject);
		
		JSONObject weekJSONObject = weekToJSONObject(playerWeekRecord.getWeek());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORD_WEEK, weekJSONObject);
		
		JSONObject recordJSONObject = recordToJSONObject(playerWeekRecord.getRecord());
		jsonObject.put(NFLPicksConstants.JSON_WEEK_RECORD_RECORD, recordJSONObject);
		
		
		return jsonObject;
	}
	
	/**
	 * 
	 * Converts the given weeks one list into a json formatted string.
	 * 
	 * @param weeksWon
	 * @return
	 */
	public static String weeksWonToJSONString(List<PlayerWeeksWon> weeksWon){
		
		JSONArray jsonArray = weeksWonToJSONArray(weeksWon);
		
		String json = jsonArray.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given weeks won into a json array.
	 * 
	 * @param seasons
	 * @return
	 */
	public static JSONArray weeksWonToJSONArray(List<PlayerWeeksWon> weeksWonList){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < weeksWonList.size(); index++){
			PlayerWeeksWon weeksWon = weeksWonList.get(index);
			JSONObject jsonObject = weeksWonToJSONObject(weeksWon);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;

	}
	
	/**
	 * 
	 * This function converts the given weeks won to a json formatted string.
	 * 
	 * @param season
	 * @return
	 */
	public static String weeksWonToJSONString(PlayerWeeksWon weeksWon){
		
		JSONObject jsonObject = weeksWonToJSONObject(weeksWon);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	/**
	 * 
	 * This function converts the given weeks won into a json object.
	 * 
	 * @param weekWon
	 * @return
	 */
	public static JSONObject weeksWonToJSONObject(PlayerWeeksWon weeksWon){
		
		JSONObject jsonObject = new JSONObject();
		
		JSONObject playerJSONObject = playerToJSONObject(weeksWon.getPlayer());
		jsonObject.put(NFLPicksConstants.JSON_WEEKS_WON_PLAYER, playerJSONObject);
		
		JSONArray weekRecordsJSONArray = weekRecordsToJSONArray(weeksWon.getWeekRecords());
		jsonObject.put(NFLPicksConstants.JSON_WEEKS_WON_WEEK_RECORDS, weekRecordsJSONArray);
		
		
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
			log.error("Error reading json! json = " + json, e);
		}
		
		return object;
	}
}
