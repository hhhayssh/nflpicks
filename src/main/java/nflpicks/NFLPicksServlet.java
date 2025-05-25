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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nflpicks.model.CompactPick;
import nflpicks.model.Division;
import nflpicks.model.DivisionRecord;
import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.PickSplit;
import nflpicks.model.Player;
import nflpicks.model.Record;
import nflpicks.model.Team;
import nflpicks.model.stats.Championship;
import nflpicks.model.stats.ChampionshipsForPlayer;
import nflpicks.model.stats.CollectivePickAccuracySummary;
import nflpicks.model.stats.CollectiveRecord;
import nflpicks.model.stats.CollectiveRecordSummary;
import nflpicks.model.stats.DivisionTitle;
import nflpicks.model.stats.DivisionTitlesForPlayer;
import nflpicks.model.stats.PickAccuracySummary;
import nflpicks.model.stats.SeasonRecordForPlayer;
import nflpicks.model.stats.WeekRecordForPlayer;
import nflpicks.model.stats.WeekRecordForPlayers;
import nflpicks.model.stats.WeekRecordsForPlayer;


public class NFLPicksServlet extends HttpServlet {
	
	private static final Log log = LogFactory.getLog(NFLPicksServlet.class);
	
	protected static final String TARGET_TEAMS = "teams";
	protected static final String TARGET_GAMES = "games";
	protected static final String TARGET_PLAYERS = "players";
	protected static final String TARGET_DIVISIONS = "divisions";
	protected static final String TARGET_DIVISION_PLAYERS = "divisionPlayers";
	protected static final String TARGET_PICKS = "picks";
	protected static final String TARGET_PICKS_GRID = "picksGrid";
	protected static final String TARGET_COMPACT_PICKS_GRID = "compactPicksGrid";
	protected static final String TARGET_STANDINGS = "standings";
	protected static final String TARGET_DIVISION_STANDINGS = "divisionStandings";
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
	protected static final String STAT_NAME_DIVISION_TITLES = "divisionTitles";
	protected static final String STAT_NAME_DIVISION_TITLE_STANDINGS = "divisionTitleStandings";
	protected static final String STAT_NAME_PICK_ACCURACY = "pickAccuracy";
	protected static final String STAT_NAME_PICK_SPLITS = "pickSplits";
	protected static final String STAT_NAME_SEASON_STANDINGS = "seasonStandings";
	protected static final String STAT_NAME_WEEK_COMPARISON = "weekComparison";
	protected static final String STAT_NAME_SEASON_PROGRESSION = "seasonProgression";
	protected static final String STAT_NAME_COLLECTIVE_RECORDS = "collectiveRecords";
	protected static final String STAT_NAME_COLLECTIVE_RECORD_SUMMARY = "collectiveRecordSummary";
	protected static final String STAT_NAME_COLLECTIVE_PICK_ACCURACY = "collectivePickAccuracy";

	protected static final String PARAMETER_NAME_TARGET = "target";
	protected static final String PARAMETER_NAME_PLAYER = "player";
	protected static final String PARAMETER_NAME_YEAR = "year";
	protected static final String PARAMETER_NAME_WEEK = "week";
	protected static final String PARAMETER_NAME_STAT_NAME = "statName";
	protected static final String PARAMETER_NAME_TEAM = "team";
	protected static final String PARAMETER_NAME_TEAM1 = "team1";
	protected static final String PARAMETER_NAME_TEAM2 = "team2";
	//this should become like "vsmode" or something
	protected static final String PARAMETER_NAME_TEAM1_AT_TEAM2 = "team1AtTeam2";
	protected static final String PARAMETER_NAME_DIVISION = "division";
	
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
	protected static final String PARAMETER_VALUE_TRUE = "true";
	
	protected static final String ERROR_JSON_RESPONSE = "{\"error\": true}";
	
	protected NFLPicksModelDataService modelDataService;
	
	protected NFLPicksStatsDataService statsDataService;
	
	protected NFLPicksDataExporter dataExporter;
	
	public void init() throws ServletException {
		log.info("Initializing servlet...");
		
		ApplicationContext.getContext().initialize();
		
		modelDataService = new NFLPicksModelDataService(ApplicationContext.getContext().getDataSource());
		
		statsDataService = new NFLPicksStatsDataService(ApplicationContext.getContext().getDataSource(), modelDataService);
		
		dataExporter = new NFLPicksDataExporter(modelDataService, statsDataService);
		
		log.info("Done initializing servlet.");
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		log.info("Processing request... request = " + request.getRequestURL() + "?" + request.getQueryString());
		
		String target = getParameter(request, PARAMETER_NAME_TARGET);
		String json = "";
		
		if (TARGET_TEAMS.equals(target)){
			List<Team> teams = modelDataService.getTeams();
			json = JSONUtil.teamsToJSONString(teams);
		}
		else if (TARGET_GAMES.equals(target)){
			String year = getParameter(request, PARAMETER_NAME_YEAR);
			String weekKey = getParameter(request, PARAMETER_NAME_WEEK);
			
			List<Game> games = modelDataService.getGames(year, weekKey);
			json = JSONUtil.gamesToJSONString(games);
		}
		else if (TARGET_PLAYERS.equals(target)){
			List<Player> players = modelDataService.getPlayers();
			json = JSONUtil.playersToJSONString(players);
		}
		else if (TARGET_DIVISIONS.equals(target)){
			List<Division> divisions = modelDataService.getDivisions();
			json = JSONUtil.divisionsToJSONString(divisions);
		}
		else if (TARGET_PICKS.equals(target)){
			String playerName = getParameter(request, PARAMETER_NAME_PLAYER);
			String year = getParameter(request, PARAMETER_NAME_YEAR);
			String weekKey = getParameter(request, PARAMETER_NAME_WEEK);
			
			List<Pick> picks = modelDataService.getPicks(playerName, year, weekKey);
			
			json = JSONUtil.picksToJSONString(picks);
		}
		else if (TARGET_COMPACT_PICKS_GRID.equals(target)){
			long start = System.currentTimeMillis();
			
			String yearParameter = getParameter(request, PARAMETER_NAME_YEAR);
			String weekKeyParameter = getParameter(request, PARAMETER_NAME_WEEK);
			String playerParameter = Util.replaceUrlCharacters(request.getParameter(PARAMETER_NAME_PLAYER));
			String team1Parameter = getParameter(request, PARAMETER_NAME_TEAM1);
			String team2Parameter = getParameter(request, PARAMETER_NAME_TEAM2);
			String team1AtTeam2Parameter = getParameter(request, PARAMETER_NAME_TEAM1_AT_TEAM2);
			
			List<String> years = Util.delimitedStringToList(yearParameter, ",");
			List<String> weekKeys = Util.delimitedStringToList(weekKeyParameter, ",");
			List<String> team1Teams = Util.delimitedStringToList(team1Parameter, ",");
			List<String> team2Teams = Util.delimitedStringToList(team2Parameter, ",");
			List<String> playerNames = Util.delimitedStringToList(playerParameter, ",");
			
			boolean team1AtTeam2 = false;
			if (team1AtTeam2Parameter != null && PARAMETER_VALUE_TRUE.equals(team1AtTeam2Parameter)){
				team1AtTeam2 = true;
			}
			
			boolean isAllYears = isAllParameterValue(years);
			boolean isAllWeeks = isAllParameterValue(weekKeys);
			boolean isAllPlayers = isAllParameterValue(playerNames);
			boolean isAllTeams1 = isAllParameterValue(team1Teams);
			boolean isAllTeams2 = isAllParameterValue(team2Teams);
			
			if (isAllYears){
				years = null;
			}
			
			if (isAllWeeks){
				weekKeys = null;
			}
			
			if (isAllPlayers){
				playerNames = null;
			}
			
			if (isAllTeams1){
				team1Teams = null;
			}
			
			if (isAllTeams2){
				team2Teams = null;
			}
			
			List<Player> players = null;

			if (isAllPlayers){

				if (isAllYears){
					players = modelDataService.getPlayers();
				}
				else {
					players = modelDataService.getActivePlayers(years);
					
					if (players == null || players.size() == 0){
						players = modelDataService.getPlayers();
					}
				}
				
				playerNames = new ArrayList<String>();
				
				for (int index = 0; index < players.size(); index++){
					Player player = players.get(index);
					playerNames.add(player.getName());
				}
			}
			else {
				players = modelDataService.getPlayers(playerNames);
			}
			
			List<CompactPick> picks = statsDataService.getCompactPicks(years, weekKeys, playerNames, team1Teams, team2Teams, team1AtTeam2);
			
			JSONObject gridJSONObject = new JSONObject();
			gridJSONObject.put(NFLPicksConstants.JSON_COMPACT_PICK_GRID_PLAYERS, playerNames);
			gridJSONObject.put(NFLPicksConstants.JSON_COMPACT_PICK_GRID_PICKS, JSONUtil.compactPicksToJSONArray(picks));
			
			json = gridJSONObject.toString();
			
			long elapsed = System.currentTimeMillis() - start;
			
			log.info("Time to get compact grid =  " + elapsed);
			
		}
		//Still used by the edit...
		else if (TARGET_PICKS_GRID.equals(target)){
			String yearParameter = getParameter(request, PARAMETER_NAME_YEAR);
			String weekKeyParameter = getParameter(request, PARAMETER_NAME_WEEK);
			String playerParameter = Util.replaceUrlCharacters(request.getParameter(PARAMETER_NAME_PLAYER));
			String teamParameter = getParameter(request, PARAMETER_NAME_TEAM);
			String team1Parameter = getParameter(request, PARAMETER_NAME_TEAM1);
			String team2Parameter = getParameter(request, PARAMETER_NAME_TEAM2);
			String team1AtTeam2Parameter = getParameter(request, PARAMETER_NAME_TEAM1_AT_TEAM2);
			
			List<String> years = Util.delimitedStringToList(yearParameter, ",");
			List<String> weekKeys = Util.delimitedStringToList(weekKeyParameter, ",");
			List<String> team1Teams = Util.delimitedStringToList(team1Parameter, ",");
			List<String> team2Teams = Util.delimitedStringToList(team2Parameter, ",");
			List<String> playerNames = Util.delimitedStringToList(playerParameter, ",");
			
			boolean team1AtTeam2 = false;
			if (team1AtTeam2Parameter != null && PARAMETER_VALUE_TRUE.equals(team1AtTeam2Parameter)){
				team1AtTeam2 = true;
			}
			
			boolean isAllYears = isAllParameterValue(years);
			boolean isAllWeeks = isAllParameterValue(weekKeys);
			boolean isAllPlayers = isAllParameterValue(playerNames);
			boolean isAllTeams1 = isAllParameterValue(team1Teams);
			boolean isAllTeams2 = isAllParameterValue(team2Teams);
			
			if (isAllYears){
				years = null;
			}
			
			if (isAllWeeks){
				weekKeys = null;
			}
			
			if (isAllPlayers){
				playerNames = null;
			}
			
			if (isAllTeams1){
				team1Teams = null;
			}
			
			if (isAllTeams2){
				team2Teams = null;
			}
			
			List<Player> players = null;
			if (isAllPlayers){
				players = modelDataService.getPlayers();
			}
			else {
				players = modelDataService.getPlayers(playerNames);
			}
			
			List<Game> games = modelDataService.getGames(years, weekKeys, null);
			
			List<Pick> picks = modelDataService.getPicks(years, weekKeys, playerNames, null);
			
			JSONObject gridJSONObject = new JSONObject();

			gridJSONObject.put(NFLPicksConstants.JSON_PICK_GRID_PLAYERS, JSONUtil.playersToJSONArray(players));
			gridJSONObject.put(NFLPicksConstants.JSON_PICK_GRID_GAMES, JSONUtil.gamesToJSONArray(games));
			gridJSONObject.put(NFLPicksConstants.JSON_PICK_GRID_PICKS, JSONUtil.picksToJSONArray(picks));
			
			json = gridJSONObject.toString();
		}
		else if (TARGET_STANDINGS.equals(target)){
			String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
			
			List<String> playerNames = null;
			if (!PARAMETER_VALUE_ALL.equals(playersString)){
				playerNames = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
			}

			String weekKeysString = request.getParameter(PARAMETER_NAME_WEEK);
			List<String> weekKeys = null;
			if (!PARAMETER_VALUE_ALL.equals(weekKeysString)){
				weekKeys = Util.delimitedStringToList(weekKeysString, PARAMETER_VALUE_DELIMITER);
			}
			
			String yearsString = request.getParameter(PARAMETER_NAME_YEAR);
			List<String> years = null; 
			if (!PARAMETER_VALUE_ALL.equals(yearsString)){
				years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
			}
			
			String teamsString = getParameter(request, PARAMETER_NAME_TEAM);
			List<String> teams = null;
			if (!PARAMETER_VALUE_ALL.equals(teamsString)){ 
				teams = Util.delimitedStringToList(teamsString, PARAMETER_VALUE_DELIMITER);
			}
			
			String team1TeamsString = getParameter(request, PARAMETER_NAME_TEAM1);
			List<String> team1Teams = null;
			if (!PARAMETER_VALUE_ALL.equals(team1TeamsString)){
				team1Teams = Util.delimitedStringToList(team1TeamsString, PARAMETER_VALUE_DELIMITER);
			}
			
			String team2TeamsString = getParameter(request, PARAMETER_NAME_TEAM2);
			List<String> team2Teams = null;
			if (!PARAMETER_VALUE_ALL.equals(team2TeamsString)){
				team2Teams = Util.delimitedStringToList(team2TeamsString, PARAMETER_VALUE_DELIMITER);
			}
			
			String team1AtTeam2String = getParameter(request, PARAMETER_NAME_TEAM1_AT_TEAM2);
			boolean team1AtTeam2 = false;
			if (team1AtTeam2String != null && PARAMETER_VALUE_TRUE.equals(team1AtTeam2String)){
				team1AtTeam2 = true;
			}
			
			List<Record> records = statsDataService.getRecords(years, weekKeys, playerNames, teams, team1Teams, team2Teams, team1AtTeam2);
			
			JSONObject recordsJSONObject = new JSONObject();
			recordsJSONObject.put(NFLPicksConstants.JSON_STANDINGS_RECORDS, JSONUtil.recordsToJSONArray(records));
			
			json = recordsJSONObject.toString();
		}
		else if (TARGET_DIVISION_STANDINGS.equals(target)){
			String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
			List<String> playerNames = null;
			if (!PARAMETER_VALUE_ALL.equals(playersString)){
				//escape all of these...
				playerNames = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
			}

			String weekKeysString = request.getParameter(PARAMETER_NAME_WEEK);
			List<String> weekKeys = null;
			if (!PARAMETER_VALUE_ALL.equals(weekKeysString)){
				weekKeys = Util.delimitedStringToList(weekKeysString, PARAMETER_VALUE_DELIMITER);
			}
			
			String yearsString = request.getParameter(PARAMETER_NAME_YEAR);
			List<String> years = null; 
			if (!PARAMETER_VALUE_ALL.equals(yearsString)){
				years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
			}
			
//			String teamsString = getParameter(request, PARAMETER_NAME_TEAM);
//			List<String> teams = null;
//			if (!PARAMETER_VALUE_ALL.equals(teamsString)){ 
//				teams = Util.delimitedStringToList(teamsString, PARAMETER_VALUE_DELIMITER);
//			}
			
			String team1TeamsString = getParameter(request, PARAMETER_NAME_TEAM1);
			List<String> team1Teams = null;
			if (!PARAMETER_VALUE_ALL.equals(team1TeamsString)){
				team1Teams = Util.delimitedStringToList(team1TeamsString, PARAMETER_VALUE_DELIMITER);
			}
			
			String team2TeamsString = getParameter(request, PARAMETER_NAME_TEAM2);
			List<String> team2Teams = null;
			if (!PARAMETER_VALUE_ALL.equals(team2TeamsString)){
				team2Teams = Util.delimitedStringToList(team2TeamsString, PARAMETER_VALUE_DELIMITER);
			}
			
			String team1AtTeam2String = getParameter(request, PARAMETER_NAME_TEAM1_AT_TEAM2);
			boolean team1AtTeam2 = false;
			if (team1AtTeam2String != null && PARAMETER_VALUE_TRUE.equals(team1AtTeam2String)){
				team1AtTeam2 = true;
			}
			
			String divisionsString = getParameter(request, PARAMETER_NAME_DIVISION);
			List<String> divisionAbbreviations = null;
			if (!PARAMETER_VALUE_ALL.equals(divisionsString)){
				//escape all of these...
				divisionAbbreviations = Util.delimitedStringToList(divisionsString, PARAMETER_VALUE_DELIMITER);
			}
			
			List<DivisionRecord> divisionRecords = statsDataService.getDivisionRecords(divisionAbbreviations, years, weekKeys, playerNames, team1Teams, team2Teams, team1AtTeam2);
			
			JSONObject recordsJSONObject = new JSONObject();
			recordsJSONObject.put(NFLPicksConstants.JSON_STANDINGS_DIVISION_RECORDS, JSONUtil.divisionRecordsToJSONArray(divisionRecords));
			
			json = recordsJSONObject.toString();
		}
		else if (TARGET_SELECTION_CRITERIA.equals(target)){
			
			JSONObject selectionCriteriaJSONObject = new JSONObject();
			
			List<String> years = modelDataService.getYearsForCriteria();
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_SELECTION_CRITERIA_YEARS, years);
			
			List<Player> players = modelDataService.getPlayers();
			List<String> playerNames = new ArrayList<String>();
			
			for (int index = 0; index < players.size(); index++){
				Player player = players.get(index);
				playerNames.add(player.getName());
			}
			
			Collections.sort(playerNames);
			
			//this should be players, not just the names....
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_SELECTION_CRITERIA_PLAYERS, playerNames);
			
			List<Team> teams = modelDataService.getTeams();
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_SELECTION_CRITERIA_TEAMS, teams);
			
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_SELECTION_CRITERIA_DIVISIONS_ENABLED, ApplicationContext.getContext().getDivisionsEnabled());
			
			String currentWeekKey = statsDataService.getCurrentWeekKey();
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_CURRENT_WEEK_KEY, currentWeekKey);
			
			String currentYear = statsDataService.getCurrentYear();
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
			
			List<String> years = modelDataService.getYearsForCriteria();
			
			JSONObject selectionCriteriaJSONObject = new JSONObject();
			
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_SELECTION_CRITERIA_YEARS, years);
			
			List<Player> players = modelDataService.getPlayers();
			List<String> playerNames = new ArrayList<String>();
			
			for (int index = 0; index < players.size(); index++){
				Player player = players.get(index);
				playerNames.add(player.getName());
			}
			
			Collections.sort(playerNames);
			
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_SELECTION_CRITERIA_PLAYERS, playerNames);
			
			String currentWeekKey = statsDataService.getCurrentWeekKey();
			String currentYear = statsDataService.getCurrentYear();
			
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_CURRENT_WEEK_KEY, currentWeekKey);
			selectionCriteriaJSONObject.put(NFLPicksConstants.JSON_CURRENT_YEAR, currentYear);
			
			json = selectionCriteriaJSONObject.toString();
		}
		else if (TARGET_EXPORT_PICKS.equals(target)){
			String exportedPicks = dataExporter.exportPicksData();
			
			String exportDate = DateUtil.formatDateAsDefaultDateWithTime(new Date());
			
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
				
				String weekKeysString = getParameter(request, PARAMETER_NAME_WEEK);
				List<String> weekKeys = null;
				if (!PARAMETER_VALUE_ALL.equals(weekKeysString)){
					weekKeys = Util.delimitedStringToList(weekKeysString, PARAMETER_VALUE_DELIMITER);
				}
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<String> playerNames = null;
				String player = getParameter(request, PARAMETER_NAME_PLAYER);
				if (!PARAMETER_VALUE_ALL.equals(player)){
					playerNames = Util.delimitedStringToList(player, PARAMETER_VALUE_DELIMITER);
				}
				
				List<WeekRecordsForPlayer> weeksWon = statsDataService.getWeeksWon(years, weekKeys, playerNames, true);
				
				json = JSONUtil.weekRecordsForPlayerListToJSONString(weeksWon);
			}
			else if (STAT_NAME_WEEKS_WON_BY_WEEK.equals(statName)){
				String weekKeysString = getParameter(request, PARAMETER_NAME_WEEK);
				List<String> weekKeys = null;
				if (!PARAMETER_VALUE_ALL.equals(weekKeysString)){
					weekKeys = Util.delimitedStringToList(weekKeysString, PARAMETER_VALUE_DELIMITER);
				}
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<String> playerNames = null;
				String player = getParameter(request, PARAMETER_NAME_PLAYER);
				if (!PARAMETER_VALUE_ALL.equals(player)){
					playerNames = Util.delimitedStringToList(player, PARAMETER_VALUE_DELIMITER);
				}
				
				List<WeekRecordForPlayers> weekRecordForPlayersList = statsDataService.getWeeksWonByWeek(years, weekKeys, playerNames, true);
				
				json = JSONUtil.weekRecordForPlayersListToJSONString(weekRecordForPlayersList);
			}
			else if (STAT_NAME_WEEK_RECORDS_BY_PLAYER.equals(statName)){
				
				List<String> years = null;
				String year = getParameter(request, PARAMETER_NAME_YEAR);
				if (!PARAMETER_VALUE_ALL.equals(year)){
					years = Util.delimitedStringToList(year, PARAMETER_VALUE_DELIMITER);
				}
				
				List<String> playerNames = null;
				String player = getParameter(request, PARAMETER_NAME_PLAYER);
				if (!PARAMETER_VALUE_ALL.equals(player)){
					playerNames = Util.delimitedStringToList(player, PARAMETER_VALUE_DELIMITER);
				}
				
				List<String> weekKeys = null;
				String weekKey = getParameter(request, PARAMETER_NAME_WEEK);
				if (!PARAMETER_VALUE_ALL.equals(weekKey)){
					weekKeys = Util.delimitedStringToList(weekKey, PARAMETER_VALUE_DELIMITER);
				}
				
				List<WeekRecordForPlayer> playerWeekRecords = statsDataService.getPlayerWeekRecords(years, weekKeys, playerNames, true);
				
				json = JSONUtil.weekRecordForPlayerListToJSONString(playerWeekRecords);
			}
			else if (STAT_NAME_WEEK_STANDINGS.equals(statName)){
				
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> playerNames = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					playerNames = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}

				String weekKeysString = getParameter(request, PARAMETER_NAME_WEEK);
				List<String> weekKeys = null;
				if (!PARAMETER_VALUE_ALL.equals(weekKeysString)){
					weekKeys = Util.delimitedStringToList(weekKeysString, PARAMETER_VALUE_DELIMITER);
				}
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<WeekRecordForPlayer> bestWeeks = statsDataService.getWeekStandings(years, weekKeys, playerNames);
				
				json = JSONUtil.weekRecordForPlayerListToJSONString(bestWeeks);
			}
			else if (STAT_NAME_SEASON_STANDINGS.equals(statName)){
				
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> playerNames = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					playerNames = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}
				
				String weekKeysString = getParameter(request, PARAMETER_NAME_WEEK);
				List<String> weekKeys = null;
				if (!PARAMETER_VALUE_ALL.equals(weekKeysString)){
					weekKeys = Util.delimitedStringToList(weekKeysString, PARAMETER_VALUE_DELIMITER);
				}
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<SeasonRecordForPlayer> seasonRecords = statsDataService.getSeasonRecords(years, weekKeys, playerNames);
				
				json = JSONUtil.seasonRecordsForPlayersToJSONString(seasonRecords);
			}
			else if (STAT_NAME_CHAMPIONS.equals(statName)){
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> playerNames = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					playerNames = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<Championship> championships = statsDataService.getChampionships(years, playerNames);
				
				json = JSONUtil.championshipsToJSONString(championships);
			}
			else if (STAT_NAME_CHAMPIONSHIP_STANDINGS.equals(statName)){

				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> playerNames = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					playerNames = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<ChampionshipsForPlayer> playerChampionships = statsDataService.getPlayerChampionships(years, playerNames);
				
				json = JSONUtil.championshipsForPlayerListToJSONString(playerChampionships);
			}
			else if (STAT_NAME_DIVISION_TITLES.equals(statName)){
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> playerNames = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					playerNames = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<DivisionTitle> divisionTitles = statsDataService.getDivisionTitles(years, playerNames);
				
				json = JSONUtil.divisionTitlesToJSONString(divisionTitles);
			}
			else if (STAT_NAME_DIVISION_TITLE_STANDINGS.equals(statName)){

				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> playerNames = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					playerNames = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}
				
				List<DivisionTitlesForPlayer> playerChampionships = statsDataService.getPlayerDivisionTitles(years, playerNames);
					
				json = JSONUtil.divisionTitlesForPlayerListToJSONString(playerChampionships);
			}
			else if (STAT_NAME_PICK_ACCURACY.equals(statName)){
				//getPickAccuracySummaries
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> playerNames = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					playerNames = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}
				
				String weekKeysString = getParameter(request, PARAMETER_NAME_WEEK);
				List<String> weekKeys = null;
				if (!PARAMETER_VALUE_ALL.equals(weekKeysString)){
					weekKeys = Util.delimitedStringToList(weekKeysString, PARAMETER_VALUE_DELIMITER);
				}
				
//				String teamsString = getParameter(request, PARAMETER_NAME_TEAM);
//				List<String> teams = null;
//				if (!PARAMETER_VALUE_ALL.equals(teamsString)){ 
//					teams = Util.delimitedStringToList(teamsString, PARAMETER_VALUE_DELIMITER);
//				}
				
				String teams1String = getParameter(request, PARAMETER_NAME_TEAM1);
				List<String> teams1 = null;
				if (!PARAMETER_VALUE_ALL.equals(teams1String)){ 
					teams1 = Util.delimitedStringToList(teams1String, PARAMETER_VALUE_DELIMITER);
				}
				
				String teams2String = getParameter(request, PARAMETER_NAME_TEAM2);
				List<String> teams2 = null;
				if (!PARAMETER_VALUE_ALL.equals(teams2String)){ 
					teams2 = Util.delimitedStringToList(teams2String, PARAMETER_VALUE_DELIMITER);
				}
				
				String team1AtTeam2Parameter = getParameter(request, PARAMETER_NAME_TEAM1_AT_TEAM2);
				boolean team1AtTeam2 = false;
				if (team1AtTeam2Parameter != null && PARAMETER_VALUE_TRUE.equals(team1AtTeam2Parameter)){
					team1AtTeam2 = true;
				}

				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				log.info("Getting pick accuracy summaries...");
				long start = System.currentTimeMillis();
				//List<PickAccuracySummary> pickAccuracySummaries = statsDataService.getPickAccuracySummaries(years, weekKeys, playerNames, teams);
				//getPickAccuracySummaries3
				List<PickAccuracySummary> pickAccuracySummaries = statsDataService.getPickAccuracySummaries3(years, weekKeys, playerNames, teams1, teams2, team1AtTeam2);
				
				long elapsed = System.currentTimeMillis() - start;
				log.info("Done getting pick accuracy summaries. elapsed = " + elapsed);
				
				json = JSONUtil.pickAccuracySummariesListToJSONString(pickAccuracySummaries);
			}
			else if (STAT_NAME_PICK_SPLITS.equals(statName)){
				
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> playerNames = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					playerNames = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}

				String weekKeysString = getParameter(request, PARAMETER_NAME_WEEK);
				List<String> weekKeys = null;
				if (PARAMETER_VALUE_CURRENT.equals(weekKeysString)){
					int currentWeekSequenceNumber = statsDataService.getCurrentWeekSequenceNumber();
					String weekKey = ModelUtil.createWeekKey(currentWeekSequenceNumber);
					weekKeys = new ArrayList<String>();
					weekKeys.add(weekKey);
				}
				else if (PARAMETER_VALUE_NEXT.equals(weekKeysString)){
					int nextWeekSequenceNumber = statsDataService.getNextWeekSequenceNumber();
					String weekKey = ModelUtil.createWeekKey(nextWeekSequenceNumber);
					weekKeys = new ArrayList<String>();
					weekKeys.add(weekKey);
				}
				else if (!PARAMETER_VALUE_ALL.equals(weekKeysString)){
					weekKeys = Util.delimitedStringToList(weekKeysString, PARAMETER_VALUE_DELIMITER);
				}
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
//				String teamsString = getParameter(request, PARAMETER_NAME_TEAM);
//				List<String> teams = null;
//				if (!PARAMETER_VALUE_ALL.equals(teamsString)){ 
//					teams = Util.delimitedStringToList(teamsString, PARAMETER_VALUE_DELIMITER);
//				}
				
				String teams1String = getParameter(request, PARAMETER_NAME_TEAM1);
				List<String> teams1 = null;
				if (!PARAMETER_VALUE_ALL.equals(teams1String)){ 
					teams1 = Util.delimitedStringToList(teams1String, PARAMETER_VALUE_DELIMITER);
				}
				
				String teams2String = getParameter(request, PARAMETER_NAME_TEAM2);
				List<String> teams2 = null;
				if (!PARAMETER_VALUE_ALL.equals(teams2String)){ 
					teams2 = Util.delimitedStringToList(teams2String, PARAMETER_VALUE_DELIMITER);
				}
				
				String team1AtTeam2Parameter = getParameter(request, PARAMETER_NAME_TEAM1_AT_TEAM2);
				boolean team1AtTeam2 = false;
				if (team1AtTeam2Parameter != null && PARAMETER_VALUE_TRUE.equals(team1AtTeam2Parameter)){
					team1AtTeam2 = true;
				}
				
				List<PickSplit> pickSplits = statsDataService.getPickSplits(years, weekKeys, playerNames, teams1, teams2, team1AtTeam2);
				
				json = JSONUtil.pickSplitsToJSONString(pickSplits);
			}
			else if (STAT_NAME_WEEK_COMPARISON.equals(statName)){
				List<String> years = null;
				String year = getParameter(request, PARAMETER_NAME_YEAR);
				if (!PARAMETER_VALUE_ALL.equals(year)){
					years = Util.delimitedStringToList(year, PARAMETER_VALUE_DELIMITER);
				}
				
				List<String> playerNames = null;
				String player = getParameter(request, PARAMETER_NAME_PLAYER);
				if (!PARAMETER_VALUE_ALL.equals(player)){
					playerNames = Util.delimitedStringToList(player, PARAMETER_VALUE_DELIMITER);
				}
				
				List<String> weekKeys = null;
				String weekKey = getParameter(request, PARAMETER_NAME_WEEK);
				if (!PARAMETER_VALUE_ALL.equals(weekKey)){
					weekKeys = Util.delimitedStringToList(weekKey, PARAMETER_VALUE_DELIMITER);
				}
				
				List<WeekRecordForPlayer> playerWeekRecords = statsDataService.getPlayerWeekRecords(years, weekKeys, playerNames, true);
				
				json = JSONUtil.weekRecordForPlayerListToJSONString(playerWeekRecords);
			}
			else if (STAT_NAME_SEASON_PROGRESSION.equals(statName)){
				List<String> years = null;
				String year = getParameter(request, PARAMETER_NAME_YEAR);
				if (!PARAMETER_VALUE_ALL.equals(year)){
					years = Util.delimitedStringToList(year, PARAMETER_VALUE_DELIMITER);
				}
				
				List<String> playerNames = null;
				String player = getParameter(request, PARAMETER_NAME_PLAYER);
				if (!PARAMETER_VALUE_ALL.equals(player)){
					playerNames = Util.delimitedStringToList(player, PARAMETER_VALUE_DELIMITER);
				}
				
				List<String> weekKeys = null;
				String weekKey = getParameter(request, PARAMETER_NAME_WEEK);
				if (!PARAMETER_VALUE_ALL.equals(weekKey)){
					weekKeys = Util.delimitedStringToList(weekKey, PARAMETER_VALUE_DELIMITER);
				}
				
				List<WeekRecordForPlayer> playerWeekRecords = statsDataService.getPlayerWeekRecords(years, weekKeys, playerNames, true);
				
				json = JSONUtil.weekRecordForPlayerListToJSONString(playerWeekRecords);
			}
			else if (STAT_NAME_COLLECTIVE_RECORDS.equals(statName)){
				
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> playerNames = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					playerNames = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}
				
				String weekKeysString = getParameter(request, PARAMETER_NAME_WEEK);
				List<String> weekKeys = null;
				if (!PARAMETER_VALUE_ALL.equals(weekKeysString)){
					weekKeys = Util.delimitedStringToList(weekKeysString, PARAMETER_VALUE_DELIMITER);
				}
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				String teams1String = getParameter(request, PARAMETER_NAME_TEAM1);
				List<String> teams1 = null;
				if (!PARAMETER_VALUE_ALL.equals(teams1String)){ 
					teams1 = Util.delimitedStringToList(teams1String, PARAMETER_VALUE_DELIMITER);
				}
				
				String teams2String = getParameter(request, PARAMETER_NAME_TEAM2);
				List<String> teams2 = null;
				if (!PARAMETER_VALUE_ALL.equals(teams2String)){ 
					teams2 = Util.delimitedStringToList(teams2String, PARAMETER_VALUE_DELIMITER);
				}
				
				String team1AtTeam2Parameter = getParameter(request, PARAMETER_NAME_TEAM1_AT_TEAM2);
				boolean team1AtTeam2 = false;
				if (team1AtTeam2Parameter != null && PARAMETER_VALUE_TRUE.equals(team1AtTeam2Parameter)){
					team1AtTeam2 = true;
				}
				
				List<CollectiveRecord> collectiveRecords = statsDataService.getCollectiveRecords(years, weekKeys, playerNames, teams1, teams2, team1AtTeam2);
				
				json = JSONUtil.collectiveRecordsToJSONString(collectiveRecords);
			}
			else if (STAT_NAME_COLLECTIVE_RECORD_SUMMARY.equals(statName)){
				
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> playerNames = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					playerNames = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}
				
				String weekKeysString = getParameter(request, PARAMETER_NAME_WEEK);
				List<String> weekKeys = null;
				if (!PARAMETER_VALUE_ALL.equals(weekKeysString)){
					weekKeys = Util.delimitedStringToList(weekKeysString, PARAMETER_VALUE_DELIMITER);
				}
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				String teams1String = getParameter(request, PARAMETER_NAME_TEAM1);
				List<String> teams1 = null;
				if (!PARAMETER_VALUE_ALL.equals(teams1String)){ 
					teams1 = Util.delimitedStringToList(teams1String, PARAMETER_VALUE_DELIMITER);
				}
				
				String teams2String = getParameter(request, PARAMETER_NAME_TEAM2);
				List<String> teams2 = null;
				if (!PARAMETER_VALUE_ALL.equals(teams2String)){ 
					teams2 = Util.delimitedStringToList(teams2String, PARAMETER_VALUE_DELIMITER);
				}
				
				String team1AtTeam2Parameter = getParameter(request, PARAMETER_NAME_TEAM1_AT_TEAM2);
				boolean team1AtTeam2 = false;
				if (team1AtTeam2Parameter != null && PARAMETER_VALUE_TRUE.equals(team1AtTeam2Parameter)){
					team1AtTeam2 = true;
				}
				
				CollectiveRecordSummary collectiveRecordSummary = statsDataService.getCollectiveRecordSummary(years, weekKeys, playerNames, teams1, teams2, team1AtTeam2);
				
				json = JSONUtil.collectiveRecordSummaryToJSONString(collectiveRecordSummary);
			}
			else if (STAT_NAME_COLLECTIVE_PICK_ACCURACY.equals(statName)){
				
				String playersString = getParameter(request, PARAMETER_NAME_PLAYER);
				List<String> playerNames = null;
				if (!PARAMETER_VALUE_ALL.equals(playersString)){
					playerNames = Util.delimitedStringToList(playersString, PARAMETER_VALUE_DELIMITER);
				}
				
				String weekKeysString = getParameter(request, PARAMETER_NAME_WEEK);
				List<String> weekKeys = null;
				if (!PARAMETER_VALUE_ALL.equals(weekKeysString)){
					weekKeys = Util.delimitedStringToList(weekKeysString, PARAMETER_VALUE_DELIMITER);
				}
				
				String yearsString = getParameter(request, PARAMETER_NAME_YEAR);
				List<String> years = null; 
				if (!PARAMETER_VALUE_ALL.equals(yearsString)){
					years = Util.delimitedStringToList(yearsString, PARAMETER_VALUE_DELIMITER);
				}
				
				String teams1String = getParameter(request, PARAMETER_NAME_TEAM1);
				List<String> teams1 = null;
				if (!PARAMETER_VALUE_ALL.equals(teams1String)){ 
					teams1 = Util.delimitedStringToList(teams1String, PARAMETER_VALUE_DELIMITER);
				}
				
				String teams2String = getParameter(request, PARAMETER_NAME_TEAM2);
				List<String> teams2 = null;
				if (!PARAMETER_VALUE_ALL.equals(teams2String)){ 
					teams2 = Util.delimitedStringToList(teams2String, PARAMETER_VALUE_DELIMITER);
				}
				
				String team1AtTeam2Parameter = getParameter(request, PARAMETER_NAME_TEAM1_AT_TEAM2);
				boolean team1AtTeam2 = false;
				if (team1AtTeam2Parameter != null && PARAMETER_VALUE_TRUE.equals(team1AtTeam2Parameter)){
					team1AtTeam2 = true;
				}
				
				log.info("Getting collective pick accuracy...");
				
				long start = System.currentTimeMillis();
				
				//List<CollectivePickAccuracySummary> collectivePickAccuracies = statsDataService.getCollectivePickAccuracy(years, weekKeys, playerNames, teams1);
				List<CollectivePickAccuracySummary> collectivePickAccuracies = statsDataService.getCollectivePickAccuracy0(years, weekKeys, playerNames, teams1, teams2, team1AtTeam2);
				
				json = JSONUtil.collectivePickAccuracySummariesListToJSONString(collectivePickAccuracies);
				
				long elapsed = System.currentTimeMillis() - start;
				
				log.info("Got collective pick accuracy in " + elapsed + " ms");
			}
		}
		else if (TARGET_MAKE_PICKS.equals(target)){
			
			List<Game> gamesForNextWeek = statsDataService.getGamesForCurrentWeek();
			
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
				
				Game existingGame = modelDataService.getGame(gameId);
				Team winningTeam = modelDataService.getTeam(winningTeamId);
				
				if (existingGame == null){
					log.error("Error saving games!  Could not get game with id = " + gameIdString);
					return;
				}
				
				existingGame.setWinningTeam(winningTeam);
				
				//If nobody won, the winning team will be null and so we have to flip the tie switch.
				if (winningTeamId == -1){
					existingGame.setTie(true);
				}
				
				modelDataService.saveGame(existingGame);
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
			
			Player player = modelDataService.getPlayer(playerString);
			
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
					Pick pickToDelete = modelDataService.getPick(gameIdInt, player.getId());
					
					//If they made a pick before, delete it.  If they didn't, there's nothing to do, so we should just
					//go to the next game and pick.
					if (pickToDelete != null){
						modelDataService.deletePick(pickToDelete);
					}
					
					continue;
				}
				
				Game game = modelDataService.getGame(gameIdInt);
				Team team = modelDataService.getTeam(teamIdInt);

				Pick pickToSave = modelDataService.getPick(gameIdInt, player.getId());
				//If there is no pick, we'll be doing an insert.  Otherwise, it'll be an update.
				if (pickToSave == null){
					pickToSave = new Pick();
				}
				
				pickToSave.setGame(game);
				pickToSave.setPlayer(player);
				pickToSave.setTeam(team);
				modelDataService.savePick(pickToSave);
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
		
		String editKey = ApplicationContext.getContext().getEditKey();
		
		LocalDateTime ldt = LocalDateTime.now();

		int month = ldt.getMonthValue();
		int day = ldt.getDayOfMonth();
		
		int sum = month + day;
		
		String expectedEditKey = String.valueOf(sum);
		if (editKey != null){
			expectedEditKey = editKey + "-" + sum;
		}
		
		if (key.equals(expectedEditKey)){
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
