package nflpicks;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.Record;
import nflpicks.model.Team;

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
