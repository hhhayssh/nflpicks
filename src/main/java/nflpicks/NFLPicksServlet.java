package nflpicks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.Record;
import nflpicks.model.Season;
import nflpicks.model.Team;


public class NFLPicksServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(NFLPicksServlet.class);
	
	protected static final String TARGET_TEAMS = "teams";
	protected static final String TARGET_GAMES = "games";
	protected static final String TARGET_PLAYERS = "players";
	protected static final String TARGET_PICKS = "picks";
	protected static final String TARGET_PICKS_GRID = "picksGrid";
	protected static final String TARGET_STANDINGS = "standings";
	protected static final String TARGET_SELECTION_CRITERIA = "selectionCriteria";
	
	protected NFLPicksDataService dataService;
	
	public void init() throws ServletException {
		log.info("Initializing servlet...");
		ApplicationContext.getContext().initialize();
		dataService = new NFLPicksDataService(ApplicationContext.getContext().getDataSource());
		log.info("Done initializing servlet.");
    }

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("Processing request... request = " + req.getRequestURL() + "?" + req.getQueryString());
		
		String target = req.getParameter("target");
		resp.setContentType("text/plain; charset=UTF-8");
		
		String json = "";
		
		if (TARGET_TEAMS.equals(target)){
			List<Team> teams = dataService.getTeams();
			json = JSONUtil.teamsToJSONString(teams);
		}
		else if (TARGET_GAMES.equals(target)){
			String year = req.getParameter("year");
			String week = req.getParameter("week");
			
			int weekInt = Util.parseInt(week, 0);
			
			List<Game> games = dataService.getGames(year, weekInt);
			json = JSONUtil.gamesToJSONString(games);
		}
		else if (TARGET_PLAYERS.equals(target)){
			List<Player> players = dataService.getPlayers();
			json = JSONUtil.playersToJSONString(players);
		}
		else if (TARGET_PICKS.equals(target)){
			//Escape this name
			String playerName = Util.replaceUrlCharacters(req.getParameter("player"));
			String year = req.getParameter("year");
			String weekString = req.getParameter("week");
			int week = Util.parseInt(weekString, 0);
			
			List<Pick> picks = null;
			if ("all".equals(playerName)){
				picks = dataService.getPicks(year, week);
			}
			else {
				picks = dataService.getPicks(playerName, year, week);
			}
			
			json = JSONUtil.picksToJSONString(picks);
		}
		else if (TARGET_PICKS_GRID.equals(target)){
			//Escape this name
			String playerName = Util.replaceUrlCharacters(req.getParameter("player"));
			String year = req.getParameter("year");
			String weekString = req.getParameter("week");
			int week = Util.parseInt(weekString, 0);
			
			List<Player> players = null;
			if ("all".equals(playerName)){
				players = dataService.getPlayers();
			}
			else {
				//TODO: only include a player if they have a pick in that year.
				Player player = dataService.getPlayer(playerName);
				players = new ArrayList<Player>();
				players.add(player);
			}
			
			List<Game> games = dataService.getGames(year, week);
			
			List<Pick> picks = null;
			if ("all".equals(playerName)){
				picks = dataService.getPicks(year, week);
			}
			else {
				picks = dataService.getPicks(playerName, year, week);
			}
			
			JSONObject gridJSONObject = new JSONObject();
			gridJSONObject.put("players", JSONUtil.playersToJSONArray(players));
			gridJSONObject.put("games", JSONUtil.gamesToJSONArray(games));
			gridJSONObject.put("picks", JSONUtil.picksToJSONArray(picks));
			
			json = gridJSONObject.toString();
		}
		else if (TARGET_STANDINGS.equals(target)){
			String playersString = req.getParameter("players");
			List<String> players = null;
			if (!"all".equals(playersString)){
				//escape all of these...
				players = Util.delimitedStringToList(playersString, ",");
			}

			String weeksString = req.getParameter("weeks");
			List<String> weeks = null;
			if (!"all".equals(weeksString)){
				weeks = Util.delimitedStringToList(weeksString, ",");
			}
			
			String yearsString = req.getParameter("years");
			List<String> years = null; 
			if (!"all".equals(yearsString)){
				years = Util.delimitedStringToList(yearsString, ",");
			}

			List<Record> records = dataService.getRecords(years, weeks, players);
			
			JSONObject recordsJSONObject = new JSONObject();
			recordsJSONObject.put("records", JSONUtil.recordsToJSONArray(records));
			
			json = recordsJSONObject.toString();
		}
		else if (TARGET_SELECTION_CRITERIA.equals(target)){
			
			List<String> years = dataService.getYears();
			
			JSONArray selectionJSONArray = new JSONArray();
			
			for (int index = 0; index < years.size(); index++){
				JSONObject selectionJSONObject = new JSONObject();
				String year = years.get(index);
				
				selectionJSONObject.put("year", year);
				
				List<Player> playersForSeason = dataService.getPlayers(year);
				
				List<String[]> weeksAndLabels = dataService.getWeeksAndLabels(year);
				
				JSONArray weeksArray = new JSONArray();
				
				for (int w = 0; w < weeksAndLabels.size(); w++){
					String[] weekAndLabel = weeksAndLabels.get(w);
					JSONObject weekJSONObject = new JSONObject();
					weekJSONObject.put("week", weekAndLabel[0]);
					weekJSONObject.put("label", weekAndLabel[1]);
					weeksArray.put(weekJSONObject);
				}
				
				selectionJSONObject.put("weeks", weeksArray);
				
				JSONArray playersArray = new JSONArray();
				
				for (int p = 0; p < playersForSeason.size(); p++){
					Player player = playersForSeason.get(p);
					JSONObject playerJSONObject = new JSONObject();
					playerJSONObject.put("id", player.getId());
					playerJSONObject.put("name", player.getName());
					playersArray.put(playerJSONObject);
				}
				
				selectionJSONObject.put("players", playersArray);
				
				selectionJSONArray.put(selectionJSONObject);
			}
			
			json = selectionJSONArray.toString();
		}
		
		PrintWriter writer = resp.getWriter();
		writer.println(json);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		
		log.info("Processing request... request = " + req.getRequestURL() + "?" + req.getQueryString());
		
		String target = req.getParameter("target");
		
		if (TARGET_GAMES.equals(target)){
			String body = readBody(req);
			
			if ("".equals(body)){
				log.error("Error reading body!");
				return;
			}
			
			JSONObject gamesToSave = JSONUtil.createJSONObjectFromString(body);
			
			if (gamesToSave == null){
				log.error("Error creating json object from body! body = " + body);
				return;
			}
			
			String yearString = gamesToSave.optString("year");
			String weekString = gamesToSave.optString("week");
			JSONArray games = gamesToSave.optJSONArray("games");
			
			for (int index = 0; index < games.length(); index++){
				JSONObject gameJSONObject = games.optJSONObject(index);
				String gameIdString = gameJSONObject.optString("id");
				String winningTeamIdString = gameJSONObject.optString("winningTeamId");
				
				int gameId = Util.parseInt(gameIdString, 0);
				int winningTeamId = Util.parseInt(winningTeamIdString, 0);
				
				Game existingGame = dataService.getGame(gameId);
				Team winningTeam = dataService.getTeam(winningTeamId);
				
				if (existingGame == null){
					log.error("Error saving games!  Could not get game with id = " + gameIdString);
					return;
				}
				
				existingGame.setWinningTeam(winningTeam);
				
				if (winningTeamId == -1){
					existingGame.setTie(true);
				}
				
				dataService.saveGame(existingGame);
			}
		}
		else if (TARGET_PICKS.equals(target)){
			String body = readBody(req);
			
			if ("".equals(body)){
				log.error("Error reading body!");
				return;
			}
			
			JSONObject picksToSave = JSONUtil.createJSONObjectFromString(body);
			
			String yearString = picksToSave.optString("year");
			String weekString = picksToSave.optString("week");
			String playerString = picksToSave.optString("player");
			JSONArray picks = picksToSave.optJSONArray("picks");
			
			for (int index = 0; index < picks.length(); index++){
				JSONObject pick = picks.optJSONObject(index);
				String gameId = pick.optString("gameId");
				String teamId = pick.optString("teamId");
				
				int gameIdInt = Util.parseInt(gameId, 0);
				int teamIdInt = Util.parseInt(teamId, 0);
				
				Player player = dataService.getPlayer(playerString);
				Pick pickToSave = dataService.getPick(gameIdInt, player.getId());
				
				Game game = dataService.getGame(gameIdInt);
				Team team = dataService.getTeam(teamIdInt);
				
				if (pickToSave == null){
					pickToSave = new Pick();
				}
				
				pickToSave.setGame(game);
				pickToSave.setPlayer(player);
				pickToSave.setTeam(team);
				dataService.savePick(pickToSave);
			}
		}
	}
	
	protected String readBody(HttpServletRequest request){
		
		BufferedReader reader = null;
		StringBuilder bodyStringBuilder = new StringBuilder();
		try {
			reader = request.getReader();
			
			String line = "";
			while ((line = reader.readLine()) != null){
				bodyStringBuilder.append(line);
			}
		}
		catch (Exception e){
			log.error("Error reading body!", e);
		}
		
		return bodyStringBuilder.toString();
	}
	
	public void destroy() {
		log.info("Destroying servlet...");
    }
}
