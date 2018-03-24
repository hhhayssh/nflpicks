package nflpicks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import nflpicks.model.Team;
import nflpicks.model.stats.Championship;
import nflpicks.model.stats.PlayerChampionships;
import nflpicks.model.stats.PlayerWeekRecord;
import nflpicks.model.stats.PlayerWeeksWon;
import nflpicks.model.stats.PlayersWeekRecord;


public class NFLPicksServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(NFLPicksServlet.class);
	
	protected static final String TARGET_TEAMS = "teams";
	protected static final String TARGET_GAMES = "games";
	protected static final String TARGET_PLAYERS = "players";
	protected static final String TARGET_PICKS = "picks";
	protected static final String TARGET_PICKS_GRID = "picksGrid";
	protected static final String TARGET_STANDINGS = "standings";
	protected static final String TARGET_SELECTION_CRITERIA = "selectionCriteria";
	protected static final String TARGET_EDIT_SELECTION_CRITERIA = "editSelectionCriteria";
	protected static final String TARGET_EXPORT_PICKS = "exportPicks";
	protected static final String TARGET_STATS = "stats";
	
	protected static final String STAT_NAME_WEEKS_WON_STANDINGS = "weeksWonStandings";
	protected static final String STAT_NAME_WEEKS_WON_BY_WEEK = "weeksWonByWeek";
	protected static final String STAT_NAME_WEEK_RECORDS_BY_PLAYER = "weekRecordsByPlayer";
	protected static final String STAT_NAME_BEST_WEEKS = "bestWeeks";
	protected static final String STAT_NAME_CHAMPIONS = "champions";
	protected static final String STAT_NAME_CHAMPIONSHIP_STANDINGS = "championshipStandings";
	
	protected NFLPicksDataService dataService;
	
	protected NFLPicksDataExporter dataExporter;
	
	public void init() throws ServletException {
		log.info("Initializing servlet...");
		ApplicationContext.getContext().initialize();
		dataService = new NFLPicksDataService(ApplicationContext.getContext().getDataSource());
		dataExporter = new NFLPicksDataExporter(dataService);
		log.info("Done initializing servlet.");
    }

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("Processing request... request = " + req.getRequestURL() + "?" + req.getQueryString());
		
		String target = req.getParameter("target");
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
			//let this be multiple players
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
			
			JSONObject selectionCriteriaJSONObject = new JSONObject();
			
			selectionCriteriaJSONObject.put("years", years);
			
			List<Player> players = dataService.getPlayers();
			List<String> playerNames = new ArrayList<String>();
			
			for (int index = 0; index < players.size(); index++){
				Player player = players.get(index);
				playerNames.add(player.getName());
			}
			
			Collections.sort(playerNames);
			
			selectionCriteriaJSONObject.put("players", playerNames);
			
			json = selectionCriteriaJSONObject.toString();
		}
		else if (TARGET_EDIT_SELECTION_CRITERIA.equals(target)){
			
			String key = req.getParameter("key");
			
			if (!"2017BradySucks".equals(key)){
				return;
			}
			
			List<String> years = dataService.getYears();
			
			JSONObject selectionCriteriaJSONObject = new JSONObject();
			
			selectionCriteriaJSONObject.put("years", years);
			
			List<Player> players = dataService.getPlayers();
			List<String> playerNames = new ArrayList<String>();
			
			for (int index = 0; index < players.size(); index++){
				Player player = players.get(index);
				playerNames.add(player.getName());
			}
			
			Collections.sort(playerNames);
			
			selectionCriteriaJSONObject.put("players", playerNames);
			
			json = selectionCriteriaJSONObject.toString();
		}
		else if (TARGET_EXPORT_PICKS.equals(target)){
			String exportedPicks = dataExporter.exportData();
			
			String exportDate = DateUtil.formatDateAsISODate(new Date());
			
			String filename = "picks-export-" + exportDate + ".csv";
			
			resp.setContentType("text/csv");
	        resp.setContentLength(exportedPicks.length());

			resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			
			OutputStream outputStream = resp.getOutputStream();
			
			Util.writeBufferedBytes(exportedPicks.getBytes(), outputStream);
			
			return;
		}
		else if (TARGET_STATS.equals(target)){
			
			String statName = req.getParameter("statName");
			
			if ("weeksWonStandings".equals(statName)){
				String year = req.getParameter("year");
				if ("all".equals(year)){
					year = null;
				}
				List<PlayerWeeksWon> weeksWon = this.dataService.getWeeksWonStandings(year);
				
				json = JSONUtil.weeksWonToJSONString(weeksWon);
			}
			else if (STAT_NAME_WEEKS_WON_BY_WEEK.equals(statName)){
				//want it sorted in chronological order...
				String playersString = req.getParameter("player");
				List<String> players = null;
				if (!"all".equals(playersString)){
					//escape all of these...
					players = Util.delimitedStringToList(playersString, ",");
				}

				String weeksString = req.getParameter("week");
				List<String> weeks = null;
				if (!"all".equals(weeksString)){
					weeks = Util.delimitedStringToList(weeksString, ",");
				}
				
				String yearsString = req.getParameter("year");
				List<String> years = null; 
				if (!"all".equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, ",");
				}
				
				List<PlayersWeekRecord> x = dataService.getWeeksWonByWeek(years, weeks, players);
				
				json = JSONUtil.playersWeekRecordsToJSONString(x);
			}
			else if (STAT_NAME_WEEK_RECORDS_BY_PLAYER.equals(statName)){
				String year = req.getParameter("year");
				if ("all".equals(year)){
					year = null;
				}
				String player = req.getParameter("player");
				
				String week = req.getParameter("week");
				if ("all".equals(week)){
					week = null;
				}
				
				List<PlayerWeekRecord> playerWeekRecords = this.dataService.getPlayerWeekRecords(year, week, player);
				
				json = JSONUtil.playerWeekRecordsToJSONString(playerWeekRecords);
			}
			else if ("weekRecords".equals(statName)){
				String year = req.getParameter("year");
				if ("all".equals(year)){
					year = null;
				}
				String player = req.getParameter("player");
				List<PlayerWeekRecord> weekRecords = this.dataService.getPlayerWeekRecords(year, null, player);
				
				json = JSONUtil.playerWeekRecordsToJSONString(weekRecords);
			}
			else if (STAT_NAME_BEST_WEEKS.equals(statName)){
				
				String playersString = req.getParameter("player");
				List<String> players = null;
				if (!"all".equals(playersString)){
					//escape all of these...
					players = Util.delimitedStringToList(playersString, ",");
				}

				String weeksString = req.getParameter("week");
				List<String> weeks = null;
				if (!"all".equals(weeksString)){
					weeks = Util.delimitedStringToList(weeksString, ",");
				}
				
				String yearsString = req.getParameter("year");
				List<String> years = null; 
				if (!"all".equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, ",");
				}
				
				List<PlayerWeekRecord> bestWeeks = dataService.getBestWeeks(years, weeks, players);
				
				json = JSONUtil.playerWeekRecordsToJSONString(bestWeeks);
			}
			else if (STAT_NAME_CHAMPIONS.equals(statName)){
				
				String yearsString = req.getParameter("year");
				List<String> years = null; 
				if (!"all".equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, ",");
				}
				
				List<Championship> championships = dataService.getChampionships(years);
				
				json = JSONUtil.championshipsToJSONString(championships);
			}
			else if (STAT_NAME_CHAMPIONSHIP_STANDINGS.equals(statName)){
				String yearsString = req.getParameter("year");
				List<String> years = null; 
				if (!"all".equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, ",");
				}
				
				//need to have player -> list of championships
				//either group here or group in javascript
				//better to group here.
				List<PlayerChampionships> playerChampionships = dataService.getPlayerChampionships(years);
				
				json = JSONUtil.playerChampionshipsToJSONString(playerChampionships);
			}
			
			//what stats do we want to show:
			//
			//	object:
			//	year needs to be selected
			//		playerName, id
			//		list of week records for weeks they won
			//
			//	week record:
			//		week number, record
			
			//	weeks won
			//		player	number of weeks won		weeks
			//		doodle					3		2 (9-7), 4 (12-4), 9
			//
			//	could we just calculate this on the browser?
			//	the records come in wins and losses from the server and the ranking is done
			//	on the client... games back and percentage are calculated on the client.
			//	to figure it out in the browser, we'd need the record for each week in the selection
			//	would have to group them and sort them and the count up the W's with a comparison.
			//
			//	java object needs to have:
			//		player
			//		number of weeks they've won
			//		each week
			//		WeekWon
			//			year
			//			week
			//			tie
			//
			//	team predicted record by picks
			//		selectors:
			//			player (all)
			//			team 
			//			year
			//	do we want to have a grid where it's players on the top and teams as the rows?
			//	is it easier if the players are the rows?
			//
			//			car @ tb
			//	benny	car
			//	bruce	tb
			//
			//	times right, times wrong
			//		one player, multiple teams
			//			team	times picked to win (record) times picked to lose (record) 
			//doodle	bal
			//	record over a time period
			//	playoff record
			//	pick comparisons
			//		pick two players, show picks in common and different
			//		pick year and week
		}
		
		resp.setContentType("text/plain; charset=UTF-8");

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
