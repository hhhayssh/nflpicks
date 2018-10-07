package nflpicks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
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

import nflpicks.model.CompactPick;
import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.PickSplit;
import nflpicks.model.Player;
import nflpicks.model.Record;
import nflpicks.model.Team;
import nflpicks.model.Week;
import nflpicks.model.stats.Championship;
import nflpicks.model.stats.ChampionshipsForPlayer;
import nflpicks.model.stats.PickAccuracySummary;
import nflpicks.model.stats.WeekRecordForPlayer;
import nflpicks.model.stats.WeekRecordForPlayers;
import nflpicks.model.stats.WeekRecordsForPlayer;


public class NFLPicksServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(NFLPicksServlet.class);
	
	protected static final String TARGET_TEAMS = "teams";
	protected static final String TARGET_GAMES = "games";
	protected static final String TARGET_PLAYERS = "players";
	protected static final String TARGET_PICKS = "picks";
	protected static final String TARGET_PICKS_GRID = "picksGrid";
	protected static final String TARGET_COMPACT_PICKS_GRID = "compactPicksGrid";
	protected static final String TARGET_STANDINGS = "standings";
	protected static final String TARGET_SELECTION_CRITERIA = "selectionCriteria";
	protected static final String TARGET_EDIT_SELECTION_CRITERIA = "editSelectionCriteria";
	protected static final String TARGET_EXPORT_PICKS = "exportPicks";
	protected static final String TARGET_STATS = "stats";
	protected static final String TARGET_MAKE_PICKS = "makePicks";
	
	protected static final String STAT_NAME_WEEKS_WON_STANDINGS = "weeksWonStandings";
	protected static final String STAT_NAME_WEEKS_WON_BY_WEEK = "weeksWonByWeek";
	protected static final String STAT_NAME_WEEK_RECORDS_BY_PLAYER = "weekRecordsByPlayer";
	protected static final String STAT_NAME_WEEK_STANDINGS = "weekStandings";
	protected static final String STAT_NAME_CHAMPIONS = "champions";
	protected static final String STAT_NAME_CHAMPIONSHIP_STANDINGS = "championshipStandings";
	protected static final String STAT_NAME_PICK_ACCURACY = "pickAccuracy";
	protected static final String STAT_NAME_PICK_SPLITS = "pickSplits";

	protected static final String PARAMETER_NAME_TARGET = "target";
	protected static final String PARAMETER_NAME_PLAYER = "player";
	protected static final String PARAMETER_NAME_YEAR = "year";
	protected static final String PARAMETER_NAME_WEEK = "week";
	protected static final String PARAMETER_NAME_STAT_NAME = "statName";
	protected static final String PARAMETER_NAME_TEAM = "team";
	
	protected static final String PARAMETER_NAME_GAMES = "games";
	protected static final String PARAMETER_NAME_ID = "id";
	protected static final String PARAMETER_NAME_WINNING_TEAM_ID = "winningTeamId";
	
	protected static final String PARAMETER_NAME_PICKS = "picks";
	protected static final String PARAMETER_NAME_GAME_ID = "gameId";
	protected static final String PARAMETER_NAME_TEAM_ID = "teamId";
	
	protected static final String PARAMETER_VALUE_ALL = "all";
	protected static final String PARAMETER_VALUE_DELIMITER = ",";
	protected static final String PARAMETER_VALUE_CURRENT = "current";
	protected static final String PARAMETER_VALUE_NEXT = "next";
	
	protected static final String ERROR_JSON_RESPONSE = "{\"error\": true}";
	
	protected NFLPicksDataService dataService;
	
	protected NFLPicksDataExporter dataExporter;
	
	public void init() throws ServletException {
		log.info("Initializing servlet...");
		ApplicationContext.getContext().initialize();
		dataService = new NFLPicksDataService(ApplicationContext.getContext().getDataSource());
		dataExporter = new NFLPicksDataExporter(dataService);
		log.info("Done initializing servlet.");
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("Processing request... request = " + request.getRequestURL() + "?" + request.getQueryString());
		
		String target = getParameter(request, PARAMETER_NAME_TARGET);
		String json = "";
		
		if (TARGET_TEAMS.equals(target)){
			List<Team> teams = dataService.getTeams();
			json = JSONUtil.teamsToJSONString(teams);
		}
		else if (TARGET_GAMES.equals(target)){
			String year = getParameter(request, PARAMETER_NAME_YEAR);
			String week = getParameter(request, PARAMETER_NAME_WEEK);
			
			int weekInt = Util.parseInt(week, 0);
			
			List<Game> games = dataService.getGames(year, weekInt);
			json = JSONUtil.gamesToJSONString(games);
		}
		else if (TARGET_PLAYERS.equals(target)){
			List<Player> players = dataService.getPlayers();
			json = JSONUtil.playersToJSONString(players);
		}
		else if (TARGET_PICKS.equals(target)){
//			String playerName = getParameter(request, PARAMETER_NAME_PLAYER);
//			String year = getParameter(request, PARAMETER_NAME_YEAR);
//			String weekString = getParameter(request, PARAMETER_NAME_WEEK);
//			int week = Util.parseInt(weekString, 0);
//			
//			List<Pick> picks = null;
//			if (PARAMETER_VALUE_ALL.equals(playerName)){
//				picks = dataService.getPicks(year, week);
//			}
//			else {
//				picks = dataService.getPicks(playerName, year, week);
//			}
//			
//			json = JSONUtil.picksToJSONString(picks);
		}
		else if (TARGET_COMPACT_PICKS_GRID.equals(target)){
			long start = System.currentTimeMillis();
			
			String yearParameter = getParameter(request, PARAMETER_NAME_YEAR);
			String weekParameter = getParameter(request, PARAMETER_NAME_WEEK);
			String playerParameter = Util.replaceUrlCharacters(request.getParameter(PARAMETER_NAME_PLAYER));
			String teamParameter = getParameter(request, PARAMETER_NAME_TEAM);
			
			List<String> years = Util.delimitedStringToList(yearParameter, ",");
			List<String> weeks = Util.delimitedStringToList(weekParameter, ",");
			List<String> teams = Util.delimitedStringToList(teamParameter, ",");
			List<String> playerNames = Util.delimitedStringToList(playerParameter, ",");
			
			boolean isAllYears = isAllParameterValue(years);
			boolean isAllWeeks = isAllParameterValue(weeks);
			boolean isAllPlayers = isAllParameterValue(playerNames);
			boolean isAllTeams = isAllParameterValue(teams);
			
			if (isAllYears){
				years = null;
			}
			
			if (isAllWeeks){
				weeks = null;
			}
			
			if (isAllPlayers){
				playerNames = null;
			}
			
			if (isAllTeams){
				teams = null;
			}
			
			List<Player> players = null;
			if (isAllPlayers){
				if (isAllYears){
					players = dataService.getPlayers();
				}
				else {
					players = dataService.getActivePlayers(years);
					
					if (players.size() == 0){
						players = dataService.getPlayers();
					}
				}
				
				playerNames = new ArrayList<String>();
				
				for (int index = 0; index < players.size(); index++){
					Player player = players.get(index);
					playerNames.add(player.getName());
				}
			}
			else {
				players = dataService.getPlayers(playerNames);
			}

			List<CompactPick> picks = dataService.getCompactPicks(years, weeks, playerNames, teams);
			
			JSONObject gridJSONObject = new JSONObject();
			gridJSONObject.put(NFLPicksConstants.JSON_COMPACT_PICK_GRID_PLAYERS, playerNames);
			gridJSONObject.put(NFLPicksConstants.JSON_COMPACT_PICK_GRID_PICKS, JSONUtil.compactPicksToJSONArray(picks));
			
			json = gridJSONObject.toString();
			
			long elapsed = System.currentTimeMillis() - start;
			
			System.out.println("Time to get compact grid =  " + elapsed);
			
		}
		else if (TARGET_PICKS_GRID.equals(target)){
			/*
			 
			 	selected = year = all, week = all, player = all, team = all ... should a team selection be forced when year = all or week = all?  No, that doesn't make sense.
			 	would be confusing for people ... better to just put out a huge grid
			 	year	week	game	benny	bruce	chance
			 	
			 	selected = year = 2016, week = all, player = all
			 	week	game	benny	bruce	chance
			 	
			 	
			 	The all parameter shouldn't get outside of here
			 	
			 	dataService.getPicks(year, week, team, player)
			 	
			 	This should do it so it's the most flexible
			 	dataService.getPicks(years, weeks, teams, players)
			 	
			 	Would also need to get the games for the years, weeks, and teams
			
				When rendering, just check whether all is selected for years. if it is, show the year column
				if week is all, show the week column
			 
			 */
			
			//dataService.getCompactPicks(years, weekNumbers, playerNames)?
			
			String yearParameter = getParameter(request, PARAMETER_NAME_YEAR);
			String weekParameter = getParameter(request, PARAMETER_NAME_WEEK);
			String playerParameter = Util.replaceUrlCharacters(request.getParameter(PARAMETER_NAME_PLAYER));
			String teamParameter = getParameter(request, PARAMETER_NAME_TEAM);
			
			List<String> years = Util.delimitedStringToList(yearParameter, ",");
			List<String> weeks = Util.delimitedStringToList(weekParameter, ",");
			List<String> teams = Util.delimitedStringToList(teamParameter, ",");
			List<String> playerNames = Util.delimitedStringToList(playerParameter, ",");
			
			boolean isAllYears = isAllParameterValue(years);
			boolean isAllWeeks = isAllParameterValue(weeks);
			boolean isAllPlayers = isAllParameterValue(playerNames);
			boolean isAllTeams = isAllParameterValue(teams);
			
			if (isAllYears){
				years = null;
			}
			
			if (isAllWeeks){
				weeks = null;
			}
			
			if (isAllPlayers){
				playerNames = null;
			}
			
			if (isAllTeams){
				teams = null;
			}
			
			List<Player> players = null;
			if (isAllPlayers){
				players = dataService.getPlayers();
			}
			else {
				players = dataService.getPlayers(playerNames);
			}
			
			List<Game> games = dataService.getGames(years, weeks, teams);
			
			List<Pick> picks = dataService.getPicks(years, weeks, playerNames, teams);
			
			JSONObject gridJSONObject = new JSONObject();
			gridJSONObject.put(NFLPicksConstants.JSON_PICK_GRID_PLAYERS, JSONUtil.playersToJSONArray(players));
			gridJSONObject.put(NFLPicksConstants.JSON_PICK_GRID_GAMES, JSONUtil.gamesToJSONArray(games));
			gridJSONObject.put(NFLPicksConstants.JSON_PICK_GRID_PICKS, JSONUtil.picksToJSONArray(picks));
			
			json = gridJSONObject.toString();
		}
		else if (TARGET_STANDINGS.equals(target)){
			String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
			
			List<String> players = null;
			if (!PARAMETER_VALUE_ALL.equals(playersString)){
				//escape all of these...
				players = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
			}

			String weeksString = request.getParameter(PARAMETER_NAME_WEEK);
			List<String> weeks = null;
			if (!PARAMETER_VALUE_ALL.equals(weeksString)){
				weeks = Util.delimitedStringToList(weeksString, PARAMETER_VALUE_DELIMITER);
			}
			
			String yearsString = request.getParameter(PARAMETER_NAME_YEAR);
			List<String> years = null; 
			if (!PARAMETER_VALUE_ALL.equals(yearsString)){
				years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
			}

			List<Record> records = dataService.getRecords(years, weeks, players);
			
			JSONObject recordsJSONObject = new JSONObject();
			recordsJSONObject.put(NFLPicksConstants.JSON_STANDINGS_RECORDS, JSONUtil.recordsToJSONArray(records));
			
			json = recordsJSONObject.toString();
		}
		else if (TARGET_SELECTION_CRITERIA.equals(target)){
			
			List<String> years = dataService.getYears();
			
			JSONObject selectionCriteriaJSONObject = new JSONObject();
			
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_SELECTION_CRITERIA_YEARS, years);
			
			List<Player> players = dataService.getPlayers();
			List<String> playerNames = new ArrayList<String>();
			
			for (int index = 0; index < players.size(); index++){
				Player player = players.get(index);
				playerNames.add(player.getName());
			}
			
			Collections.sort(playerNames);
			
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_SELECTION_CRITERIA_PLAYERS, playerNames);
			
			List<Team> teams = dataService.getTeams();
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_SELECTION_CRITERIA_TEAMS, teams);
			
			int currentWeekNumber = dataService.getCurrentWeekNumber();
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_CURRENT_WEEK_NUMBER, currentWeekNumber);
			
			String currentYear = dataService.getCurrentYear();
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_CURRENT_YEAR, currentYear);
			
			json = selectionCriteriaJSONObject.toString();
		}
		else if (TARGET_EDIT_SELECTION_CRITERIA.equals(target)){
			
			String key = getParameter(request, "key");
			
			boolean editKeyCheck = checkEditKey(key);
			
			if (!editKeyCheck){
				log.error("Error getting selection criteria to edit!  Invalid key!  key = " + key);
				writeErrorResponse(response);
				return;
			}
			
			List<String> years = dataService.getYears();
			
			JSONObject selectionCriteriaJSONObject = new JSONObject();
			
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_SELECTION_CRITERIA_YEARS, years);
			
			List<Player> players = dataService.getPlayers();
			List<String> playerNames = new ArrayList<String>();
			
			for (int index = 0; index < players.size(); index++){
				Player player = players.get(index);
				playerNames.add(player.getName());
			}
			
			Collections.sort(playerNames);
			
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_SELECTION_CRITERIA_PLAYERS, playerNames);
			
			json = selectionCriteriaJSONObject.toString();
		}
		else if (TARGET_EXPORT_PICKS.equals(target)){
			String exportedPicks = dataExporter.exportData();
			
			String exportDate = DateUtil.formatDateAsISODate(new Date());
			
			String filename = "picks-export-" + exportDate + ".csv";
			
			response.setContentType("text/csv");
	        response.setContentLength(exportedPicks.length());

			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			
			OutputStream outputStream = response.getOutputStream();
			
			Util.writeBufferedBytes(exportedPicks.getBytes(), outputStream);
			
			return;
		}
		else if (TARGET_STATS.equals(target)){
			
			String statName = getParameter(request, PARAMETER_NAME_STAT_NAME);
			
			if (STAT_NAME_WEEKS_WON_STANDINGS.equals(statName)){
				
				String year = getParameter(request, PARAMETER_NAME_YEAR);
				if (PARAMETER_VALUE_ALL.equals(year)){
					year = null;
				}
				
				List<WeekRecordsForPlayer> weeksWon = dataService.getWeekRecordsForPlayer(year);
				
				json = JSONUtil.weekRecordsForPlayerListToJSONString(weeksWon);
			}
			else if (STAT_NAME_WEEKS_WON_BY_WEEK.equals(statName)){
				List<String> players = null;

				String weeksString = getParameter(request, PARAMETER_NAME_WEEK);
				List<String> weeks = null;
				if (!PARAMETER_VALUE_ALL.equals(weeksString)){
					weeks = Util.delimitedStringToList(weeksString, PARAMETER_VALUE_DELIMITER);
				}
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<WeekRecordForPlayers> weekRecordForPlayersList = dataService.getWeekRecordForPlayers(years, weeks, players);
				
				json = JSONUtil.weekRecordForPlayersListToJSONString(weekRecordForPlayersList);
			}
			else if (STAT_NAME_WEEK_RECORDS_BY_PLAYER.equals(statName)){
				
				List<String> years = null;
				String year = getParameter(request, PARAMETER_NAME_YEAR);
				if (!PARAMETER_VALUE_ALL.equals(year)){
					years = Util.delimitedStringToList(year, PARAMETER_VALUE_DELIMITER);
				}
				
				List<String> players = null;
				String player = getParameter(request, PARAMETER_NAME_PLAYER);
				if (!PARAMETER_VALUE_ALL.equals(player)){
					players = Util.delimitedStringToList(player, PARAMETER_VALUE_DELIMITER);
				}
				
				List<String> weeks = null;
				String week = getParameter(request, PARAMETER_NAME_WEEK);
				if (!PARAMETER_VALUE_ALL.equals(week)){
					weeks = Util.delimitedStringToList(week, PARAMETER_VALUE_DELIMITER);
				}
				
				List<WeekRecordForPlayer> playerWeekRecords = dataService.getPlayerWeekRecords(years, weeks, players);
				
				json = JSONUtil.weekRecordForPlayerListToJSONString(playerWeekRecords);
			}
			else if (STAT_NAME_WEEK_STANDINGS.equals(statName)){
				
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> players = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					players = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}

				String weeksString = getParameter(request, PARAMETER_NAME_WEEK);
				List<String> weeks = null;
				if (!PARAMETER_VALUE_ALL.equals(weeksString)){
					weeks = Util.delimitedStringToList(weeksString, PARAMETER_VALUE_DELIMITER);
				}
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<WeekRecordForPlayer> bestWeeks = dataService.getWeekRecordForPlayer(years, weeks, players);
				
				json = JSONUtil.weekRecordForPlayerListToJSONString(bestWeeks);
			}
			else if (STAT_NAME_CHAMPIONS.equals(statName)){
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<Championship> championships = dataService.getChampionships(years);
				
				json = JSONUtil.championshipsToJSONString(championships);
			}
			else if (STAT_NAME_CHAMPIONSHIP_STANDINGS.equals(statName)){

				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<ChampionshipsForPlayer> playerChampionships = dataService.getPlayerChampionships(years);
				
				json = JSONUtil.championshipsForPlayerListToJSONString(playerChampionships);
			}
			else if (STAT_NAME_PICK_ACCURACY.equals(statName)){
				//getPickAccuracySummaries
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> players = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					players = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}
				
				String teamsString = getParameter(request, PARAMETER_NAME_TEAM);
				List<String> teams = null;
				if (!PARAMETER_VALUE_ALL.equals(teamsString)){ 
					teams = Util.delimitedStringToList(teamsString, PARAMETER_VALUE_DELIMITER);
				}

				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<PickAccuracySummary> pickAccuracySummaries = dataService.getPickAccuracySummaries(years, players, teams);
				
				json = JSONUtil.pickAccuracySummariesListToJSONString(pickAccuracySummaries);
			}
			else if (STAT_NAME_PICK_SPLITS.equals(statName)){
				
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> players = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					players = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}

				String weeksString = getParameter(request, PARAMETER_NAME_WEEK);
				List<String> weeks = null;
				if (PARAMETER_VALUE_CURRENT.equals(weeksString)){
					int currentWeekNumber = dataService.getCurrentWeekNumber();
					weeks = new ArrayList<String>();
					weeks.add(String.valueOf(currentWeekNumber));
				}
				else if (PARAMETER_VALUE_NEXT.equals(weeksString)){
					int nextWeekNumber = dataService.getNextWeekNumber();
					weeks = new ArrayList<String>();
					weeks.add(String.valueOf(nextWeekNumber));
				}
				else if (!PARAMETER_VALUE_ALL.equals(weeksString)){
					weeks = Util.delimitedStringToList(weeksString, PARAMETER_VALUE_DELIMITER);
				}
				
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				String teamsString = getParameter(request, PARAMETER_NAME_TEAM);
				List<String> teams = null;
				if (!PARAMETER_VALUE_ALL.equals(teamsString)){ 
					teams = Util.delimitedStringToList(teamsString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<PickSplit> pickSplits = dataService.getPickSplits(years, weeks, players, teams);
				
				json = JSONUtil.pickSplitsToJSONString(pickSplits);
			}
		}
		else if (TARGET_MAKE_PICKS.equals(target)){
			
			List<Game> gamesForNextWeek = dataService.getGamesForNextWeek();
			
			json = JSONUtil.gamesToJSONString(gamesForNextWeek);
		}
		
		writeJSONResponse(response, json);
	}
	
	protected void writeErrorResponse(HttpServletResponse response) throws IOException {
		writeJSONResponse(response, ERROR_JSON_RESPONSE);
		
	}
	
	protected void writeJSONResponse(HttpServletResponse response, String json) throws IOException {
		response.setContentType("text/plain; charset=UTF-8");

		PrintWriter writer = response.getWriter();
		writer.println(json);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse respsonse){
		
		log.info("Processing request... request = " + request.getRequestURL() + "?" + request.getQueryString());
		
		String target = getParameter(request, PARAMETER_NAME_TARGET);
		
		if (TARGET_GAMES.equals(target)){
			String body = readBody(request);
			
			if ("".equals(body)){
				log.error("Error reading body!");
				return;
			}
			
			JSONObject gamesToSave = JSONUtil.createJSONObjectFromString(body);
			
			if (gamesToSave == null){
				log.error("Error creating json object from body! body = " + body);
				return;
			}
			
			JSONArray games = gamesToSave.optJSONArray(PARAMETER_NAME_GAMES);
			
			for (int index = 0; index < games.length(); index++){
				JSONObject gameJSONObject = games.optJSONObject(index);
				String gameIdString = gameJSONObject.optString(PARAMETER_NAME_ID);
				String winningTeamIdString = gameJSONObject.optString(PARAMETER_NAME_WINNING_TEAM_ID);
				
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
			String body = readBody(request);
			
			if ("".equals(body)){
				log.error("Error reading body!");
				return;
			}
			
			JSONObject picksToSave = JSONUtil.createJSONObjectFromString(body);
			
			String playerString = picksToSave.optString(PARAMETER_NAME_PLAYER);
			JSONArray picks = picksToSave.optJSONArray(PARAMETER_NAME_PICKS);
			
			for (int index = 0; index < picks.length(); index++){
				JSONObject pick = picks.optJSONObject(index);
				String gameId = pick.optString(PARAMETER_NAME_GAME_ID);
				String teamId = pick.optString(PARAMETER_NAME_TEAM_ID);
				
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
	
	protected boolean checkEditKey(String key){
		
		LocalDateTime ldt = LocalDateTime.now();

		int month = ldt.getMonthValue();
		int day = ldt.getDayOfMonth();
		
		int sum = month + day;
		
		if (key.equals(String.valueOf(sum))){
			return true;
		}
		
		return false;
		
	}
	
	protected String getParameter(HttpServletRequest request, String parameterName){
		
		String value = request.getParameter(parameterName);
		
		String unescapedValue = Util.replaceUrlCharacters(value);
		
		return unescapedValue;
	}
	
	protected boolean isAllParameterValue(List<String> parameterValues){
		
		if (parameterValues == null){
			return false;
		}
		
		if (parameterValues.size() != 1){
			return false;
		}
		
		String parameterValue = parameterValues.get(0);
		
		if (PARAMETER_VALUE_ALL.equals(parameterValue)){
			return true;
		}
		
		return false;
		
	}
	
	public void destroy() {
		log.info("Destroying servlet...");
    }
}
