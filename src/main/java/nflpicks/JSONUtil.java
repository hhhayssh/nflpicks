package nflpicks;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.Record;
import nflpicks.model.Team;

public class JSONUtil {

	public static String gamesToJSONString(List<Game> games){
		
		JSONArray jsonArray = gamesToJSONArray(games);
		String json = jsonArray.toString();
		
		return json;
	}
	
	public static JSONArray gamesToJSONArray(List<Game> games){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < games.size(); index++){
			Game game = games.get(index);
			JSONObject jsonObject = gameToJSONObject(game);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	public static String gameToJSONString(Game game){
		
		JSONObject jsonObject = gameToJSONObject(game);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	public static JSONObject gameToJSONObject(Game game){
		
		JSONObject jsonObject = new JSONObject();
		/*
		 protected int id;
	protected int weekId;
	protected Team homeTeam;
	protected Team awayTeam;
	protected int homeTeamScore;
	protected int awayTeamScore;
		 */
		jsonObject.put("id", game.getId());
		jsonObject.put("weekId", game.getWeekId());
		
		JSONObject homeTeamJSONObject = teamToJSONObject(game.getHomeTeam());
		jsonObject.put("homeTeam", homeTeamJSONObject);
		
		JSONObject awayTeamJSONObject = teamToJSONObject(game.getAwayTeam());
		jsonObject.put("awayTeam", awayTeamJSONObject);
		
		jsonObject.put("winningTeamId", game.getWinningTeamId());
		
		return jsonObject;
	}

	public static String teamsToJSONString(List<Team> teams){
		
		JSONArray jsonArray = teamsToJSONArray(teams);
		String json = jsonArray.toString();
		
		return json;
	}
	
	public static JSONArray teamsToJSONArray(List<Team> teams){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < teams.size(); index++){
			Team team = teams.get(index);
			JSONObject jsonObject = teamToJSONObject(team);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	public static String teamToJSONString(Team teamInfo){
		
		JSONObject jsonObject = teamToJSONObject(teamInfo);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	public static JSONObject teamToJSONObject(Team team){

		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("id", team.getId());
		jsonObject.put("divisionId", team.getDivisionId());
		jsonObject.put("name", team.getName());
		jsonObject.put("nickname", team.getNickname());
		jsonObject.put("abbreviation", team.getAbbreviation());
		
		return jsonObject;
	}
	
	public static String playersToJSONString(List<Player> playerInfos){
		
		JSONArray jsonArray = playersToJSONArray(playerInfos);
		String json = jsonArray.toString();
		
		return json;
	}
	
	public static JSONArray playersToJSONArray(List<Player> players){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < players.size(); index++){
			Player playerInfo = players.get(index);
			JSONObject jsonObject = playerToJSONObject(playerInfo);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	public static String playerToJSONString(Player playerInfo){
		
		JSONObject jsonObject = playerToJSONObject(playerInfo);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	public static JSONObject playerToJSONObject(Player playerInfo){
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", playerInfo.getId());
		jsonObject.put("name", playerInfo.getName());
		
		return jsonObject;
	}
	
	
	
	
	
	
	
	
	
	
	public static String picksToJSONString(List<Pick> picks){
		
		JSONArray jsonArray = picksToJSONArray(picks);
		String json = jsonArray.toString();
		
		return json;
	}
	
	public static JSONArray picksToJSONArray(List<Pick> picks){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < picks.size(); index++){
			Pick pick = picks.get(index);
			JSONObject jsonObject = pickToJSONObject(pick);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	public static String pickToJSONString(Pick pick){
		
		JSONObject jsonObject = pickToJSONObject(pick);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	public static JSONObject pickToJSONObject(Pick pick){
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", pick.getId());
		
		JSONObject gameJSONObject = gameToJSONObject(pick.getGame());
		jsonObject.put("game", gameJSONObject);
		
		JSONObject playerJSONObject = playerToJSONObject(pick.getPlayer());
		jsonObject.put("player", playerJSONObject);
		
		Team team = pick.getTeam();
		if (team != null){
			JSONObject teamJSONObject = teamToJSONObject(team);
			jsonObject.put("team", teamJSONObject);
			
			jsonObject.put("result", pick.getResult());
		}
		
		return jsonObject;
	}
	
	public static JSONArray recordsToJSONArray(List<Record> records){
		
		JSONArray jsonArray = new JSONArray();
		
		for (int index = 0; index < records.size(); index++){
			Record record = records.get(index);
			JSONObject jsonObject = recordToJSONObject(record);
			jsonArray.put(jsonObject);
		}
		
		return jsonArray;
	}
	
	public static String recordToJSONString(Record record){
		
		JSONObject jsonObject = recordToJSONObject(record);
		
		String json = jsonObject.toString();
		
		return json;
	}
	
	public static JSONObject recordToJSONObject(Record record){
		
		JSONObject jsonObject = new JSONObject();
		
		JSONObject playerJSONObject = playerToJSONObject(record.getPlayer());
		jsonObject.put("player", playerJSONObject);
		
		jsonObject.put("wins", record.getWins());
		jsonObject.put("losses", record.getLosses());
		jsonObject.put("ties", record.getTies());
		
		return jsonObject;
	}
	
	
}
