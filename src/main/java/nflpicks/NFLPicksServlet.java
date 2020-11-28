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
import nflpicks.model.stats.Championship;
import nflpicks.model.stats.ChampionshipsForPlayer;
import nflpicks.model.stats.PickAccuracySummary;
import nflpicks.model.stats.SeasonRecordForPlayer;
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
	protected static final String STAT_NAME_SEASON_STANDINGS = "seasonStandings";
	protected static final String STAT_NAME_WEEK_COMPARISON = "weekComparison";
	protected static final String STAT_NAME_SEASON_PROGRESSION = "seasonProgression";

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
			String playerName = getParameter(request, PARAMETER_NAME_PLAYER);
			String year = getParameter(request, PARAMETER_NAME_YEAR);
			String weekString = getParameter(request, PARAMETER_NAME_WEEK);
			int week = Util.parseInt(weekString, 0);
			
			List<Pick> picks = null;
			if (PARAMETER_VALUE_ALL.equals(playerName)){
				picks = dataService.getPicks(year, week);
			}
			else {
				picks = dataService.getPicks(playerName, year, week);
			}
			
			json = JSONUtil.picksToJSONString(picks);
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
					
					if (players == null || players.size() == 0){
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
		//Still used by the edit...
		else if (TARGET_PICKS_GRID.equals(target)){
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
			
			JSONObject selectionCriteriaJSONObject = new JSONObject();
			
			List<String> years = dataService.getYearsForCriteria();
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_SELECTION_CRITERIA_YEARS, years);
			
			List<Player> players = dataService.getPlayers();
			List<String> playerNames = new ArrayList<String>();
			
			for (int index = 0; index < players.size(); index++){
				Player player = players.get(index);
				playerNames.add(player.getName());
			}
			
			Collections.sort(playerNames);
			
			//this should be players, not just the names....
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
			
			List<String> years = dataService.getYearsForCriteria();
			
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
			String exportedPicks = dataExporter.exportPicksData();
			
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
				
				List<String> players = null;
				String player = getParameter(request, PARAMETER_NAME_PLAYER);
				if (!PARAMETER_VALUE_ALL.equals(player)){
					players = Util.delimitedStringToList(player, PARAMETER_VALUE_DELIMITER);
				}
				
				List<WeekRecordsForPlayer> weeksWon = dataService.getWeeksWon(years, weeks, players, true);
				
				json = JSONUtil.weekRecordsForPlayerListToJSONString(weeksWon);
			}
			else if (STAT_NAME_WEEKS_WON_BY_WEEK.equals(statName)){
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
				
				List<String> players = null;
				String player = getParameter(request, PARAMETER_NAME_PLAYER);
				if (!PARAMETER_VALUE_ALL.equals(player)){
					players = Util.delimitedStringToList(player, PARAMETER_VALUE_DELIMITER);
				}
				
				List<WeekRecordForPlayers> weekRecordForPlayersList = dataService.getWeeksWonByWeek(years, weeks, players, true);
				
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
				
				List<WeekRecordForPlayer> playerWeekRecords = dataService.getPlayerWeekRecords(years, weeks, players, true);
				
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
				
				List<WeekRecordForPlayer> bestWeeks = dataService.getWeekStandings(years, weeks, players);
				
				json = JSONUtil.weekRecordForPlayerListToJSONString(bestWeeks);
			}
			else if (STAT_NAME_SEASON_STANDINGS.equals(statName)){
				
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> players = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					players = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<SeasonRecordForPlayer> seasonRecords = dataService.getSeasonRecords(years, players);
				
				json = JSONUtil.seasonRecordsForPlayersToJSONString(seasonRecords);
			}
			else if (STAT_NAME_CHAMPIONS.equals(statName)){
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> players = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					players = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<Championship> championships = dataService.getChampionships(years, players);
				
				json = JSONUtil.championshipsToJSONString(championships);
			}
			else if (STAT_NAME_CHAMPIONSHIP_STANDINGS.equals(statName)){

				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> players = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					players = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<ChampionshipsForPlayer> playerChampionships = dataService.getPlayerChampionships(years, players);
				
				json = JSONUtil.championshipsForPlayerListToJSONString(playerChampionships);
			}
			else if (STAT_NAME_PICK_ACCURACY.equals(statName)){
				//getPickAccuracySummaries
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
				
				List<PickAccuracySummary> pickAccuracySummaries = dataService.getPickAccuracySummaries(years, weeks, players, teams);
				
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
			else if (STAT_NAME_WEEK_COMPARISON.equals(statName)){
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
				
				List<WeekRecordForPlayer> playerWeekRecords = dataService.getPlayerWeekRecords(years, weeks, players, true);
				
				json = JSONUtil.weekRecordForPlayerListToJSONString(playerWeekRecords);
			}
			else if (STAT_NAME_SEASON_PROGRESSION.equals(statName)){
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
				
				List<WeekRecordForPlayer> playerWeekRecords = dataService.getPlayerWeekRecords(years, weeks, players, true);
				
				json = JSONUtil.weekRecordForPlayerListToJSONString(playerWeekRecords);
			}
		}
		else if (TARGET_MAKE_PICKS.equals(target)){
			
			List<Game> gamesForNextWeek = dataService.getGamesForCurrentWeek();
			
			json = JSONUtil.gamesToJSONString(gamesForNextWeek);
		}
		
		writeJSONResponse(response, json);
	}

	/**
	 * 
	 * A convenience function so we can use the same error message as a response if we need to.
	 * Basically, here so whatever catches the error won't have to think about what to do with it.
	 * 
	 * @param response
	 * @throws IOException
	 */
	protected void writeErrorResponse(HttpServletResponse response) throws IOException {
		writeJSONResponse(response, ERROR_JSON_RESPONSE);
	}
	
	/**
	 * 
	 * A convenience function for writing json to an http response.  Makes sure the content is right
	 * on the response and then writes it using the response's writer.
	 * 
	 * @param response
	 * @param json
	 * @throws IOException
	 */
	protected void writeJSONResponse(HttpServletResponse response, String json) throws IOException {
		response.setContentType("text/plain; charset=UTF-8");

		PrintWriter writer = response.getWriter();
		
		writer.println(json);
	}
	
	/**
	 * 
	 * This function will handle processing either game or pick updates.  The "target" parameter determines what it does.
	 * It's mainly here to update the winners and who people picked, so that's all it'll do.
	 * 
	 * It expects the body of the given request to be json with the games or picks to save.
	 * 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse respsonse){
		
		//Steps to do:
		//	1. Pull out the target and use it to decide what to do.
		//	I'll comment on the lines instead of here...
		
		log.info("Processing request... request = " + request.getRequestURL() + "?" + request.getQueryString());
		
		String target = getParameter(request, PARAMETER_NAME_TARGET);
		
		if (TARGET_GAMES.equals(target)){
			String body = readBody(request);
			
			if ("".equals(body)){
				log.error("Error reading body!");
				return;
			}
			
			//The body should be json and we just have to pull it out.
			JSONObject gamesToSave = JSONUtil.createJSONObjectFromString(body);
			
			if (gamesToSave == null){
				log.error("Error creating json object from body! body = " + body);
				return;
			}
			
			//Just have to go through and update all the games and who won each.
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
				
				//If nobody won, the winning team will be null and so we have to flip the tie switch.
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
			
			//Same deal with the picks as with the games.
			JSONObject picksToSave = JSONUtil.createJSONObjectFromString(body);
			if (picksToSave == null){
				log.error("Error reading picks from request body!  body = " + body);
				return;
			}
			
			//We have the picks and person who made them, so pull them out.
			String playerString = picksToSave.optString(PARAMETER_NAME_PLAYER);
			JSONArray picks = picksToSave.optJSONArray(PARAMETER_NAME_PICKS);
			
			Player player = dataService.getPlayer(playerString);
			
			if (player == null){
				log.error("Error finding player to update picks!  player = " + playerString + ", body = " + body);
			}
			
			//Now we just have to go through all the picks they made and add each one.
			//If there's one that has a blank team, then we'll want to delete that pick because
			//it's like they didn't make one.
			for (int index = 0; index < picks.length(); index++){
				JSONObject pick = picks.optJSONObject(index);
				String gameId = pick.optString(PARAMETER_NAME_GAME_ID);
				String teamId = pick.optString(PARAMETER_NAME_TEAM_ID);

				int gameIdInt = Util.parseInt(gameId, 0);
				int teamIdInt = Util.parseInt(teamId, 0);

				//If no team was given, that means they didn't make a pick, so we should delete
				//one if it's already there.
				if (Util.isBlank(teamId) || teamIdInt == 0){
					Pick pickToDelete = dataService.getPick(gameIdInt, player.getId());
					
					//If they made a pick before, delete it.  If they didn't, there's nothing to do, so we should just
					//go to the next game and pick.
					if (pickToDelete != null){
						dataService.deletePick(pickToDelete);
					}
					
					continue;
				}
				
				Game game = dataService.getGame(gameIdInt);
				Team team = dataService.getTeam(teamIdInt);

				Pick pickToSave = dataService.getPick(gameIdInt, player.getId());
				//If there is no pick, we'll be doing an insert.  Otherwise, it'll be an update.
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
	
	/**
	 * 
	 * A convenience function for reading the body of a request.  Just reads all the lines
	 * that were sent with it.
	 * 
	 * @param request
	 * @return
	 */
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

	/**
	 * 
	 * A little bit of security.  Checks to see if the key that was sent is right.
	 * 
	 * @param key
	 * @return
	 */
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
	
	/**
	 * 
	 * A convenience function for getting a parameter value from a request.  It'll replace
	 * the "url" characters in the value before returning it.
	 * 
	 * @param request
	 * @param parameterName
	 * @return
	 */
	protected String getParameter(HttpServletRequest request, String parameterName){
		
		String value = request.getParameter(parameterName);
		
		String unescapedValue = Util.replaceUrlCharacters(value);
		
		return unescapedValue;
	}
	
	/**
	 * 
	 * Checks to see whether the given "parameter values" list is really
	 * just a single item list with "ALL" in it.
	 * 
	 * 
	 * @param parameterValues
	 * @return
	 */
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
