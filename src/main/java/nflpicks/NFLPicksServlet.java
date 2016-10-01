package nflpicks;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.Record;
import nflpicks.model.Team;


public class NFLPicksServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(NFLPicksServlet.class);
	
	protected static final String TARGET_TEAMS = "teams";
	protected static final String TARGET_GAMES = "games";
	protected static final String TARGET_PLAYERS = "players";
	protected static final String TARGET_PICKS = "picks";
	protected static final String TARGET_PICKS_GRID = "picksGrid";
	protected static final String TARGET_RECORDS = "records";
	
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
		
		String json = "";
		
		if (TARGET_TEAMS.equals(target)){
			List<Team> teams = dataService.getTeams();
			json = JSONUtil.teamsToJSONString(teams);
		}
		else if (TARGET_GAMES.equals(target)){
			List<Game> games = dataService.getGames();
			json = JSONUtil.gamesToJSONString(games);
		}
		else if (TARGET_PLAYERS.equals(target)){
			List<Player> players = dataService.getPlayers();
			json = JSONUtil.playersToJSONString(players);
		}
		else if (TARGET_PICKS.equals(target)){
			String playerName = req.getParameter("player");
			String year = req.getParameter("year");
			String weekString = req.getParameter("week");
			int week = Integer.parseInt(weekString);
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
			String playerName = req.getParameter("player");
			String year = req.getParameter("year");
			String weekString = req.getParameter("week");
			int week = Integer.parseInt(weekString);
			
			List<Player> players = null;
			if ("all".equals(playerName)){
				players = dataService.getPlayers();
			}
			else {
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
		else if (TARGET_RECORDS.equals(target)){
			String playersString = req.getParameter("players");
			List<String> players = null;
			if (!"all".equals(playersString)){
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
		
		PrintWriter writer = resp.getWriter();
		writer.println(json);
	}
	
	public void destroy() {
		log.info("Destroying servlet...");
    }
}
