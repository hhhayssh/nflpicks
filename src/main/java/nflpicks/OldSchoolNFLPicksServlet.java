package nflpicks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.Record;
import nflpicks.model.Team;
import nflpicks.model.Week;

public class OldSchoolNFLPicksServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(NFLPicksServlet.class);
	
	protected NFLPicksDataService dataService;
	
	public void init() throws ServletException {
		log.info("Initializing old school servlet...");
		ApplicationContext.getContext().initialize();
		dataService = new NFLPicksDataService(ApplicationContext.getContext().getDataSource());
		log.info("Done initializing old school servlet.");
    }
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("Processing get request... request = " + req.getRequestURL() + "?" + req.getQueryString());
		
		String type = req.getParameter("type");
		String player = req.getParameter("player");
		String year = req.getParameter("year");
		String week = req.getParameter("week");
		
		try {
			ServletOutputStream output = resp.getOutputStream();
			writeResponse(output, type, player, year, week);
			output.flush();
		}
		catch (Exception e){
			log.error("Error writing get response! type = " + type + ", player = " + player + ", year = " + year + ", week = " + week, e);
		}
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		
		log.info("Processing post request... request = " + req.getRequestURL() + "?" + req.getQueryString());
		
		String type = req.getParameter("type");
		String player = req.getParameter("player");
		String year = req.getParameter("year");
		String week = req.getParameter("week");
		
		try {
			ServletOutputStream output = resp.getOutputStream();
			writeResponse(output, type, player, year, week);
			output.flush();
		}
		catch (Exception e){
			log.error("Error writing post response! type = " + type + ", player = " + player + ", year = " + year + ", week = " + week, e);
		}
	}
	
	protected void writeResponse(ServletOutputStream output, String type, String player, String year, String week) throws Exception {
		
		output.print("<html>");
		writeHead(output);
		writeBody(output, type, player, year, week);
		output.print("</html>");
	}
	
	protected void writeHead(ServletOutputStream output) throws Exception {
		
		output.print("<head>\n" + 
					 "<base href=\"nflpicks\">\n" + 
					 "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/nflpicks.css?1\">\n" + 
					 "<meta name=\"viewport\" content=\"initial-scale=1.0, minimum-scale=0.25, maximum-scale=5.0, user-scalable=yes\">\n" + 
					 "<meta content=\"text/html;charset=utf-8\" http-equiv=\"Content-Type\">\n" + 
					 "<meta content=\"utf-8\" http-equiv=\"encoding\">\n" + 
					"</head>");
	}
	
	protected void writeBody(ServletOutputStream output, String type, String player, String year, String week) throws Exception {
		
		output.print("<body>");
		
		writeTopHeader(output);
		writeSelectorForm(output, type, player, year, week);

		if ("picks".equals(type)){
			
			if (year == null || "all".equals(year)){
				year = Util.getCurrentYear();
			}
			
			int weekInt = Util.parseInt(week, 0);
			
			if (weekInt == 0){
				week = null;
				weekInt = 1;
			}
			
			List<Player> players = null;
			if ("all".equals(player)){
				players = dataService.getPlayers(year);
			}
			else {
				boolean wasPlayerActiveInYear = dataService.wasPlayerActiveInYear(player, year);
				if (wasPlayerActiveInYear){
					players = Arrays.asList(dataService.getPlayer(player));
				}
				else {
					player = "all";
					players = dataService.getPlayers(year);
				}
			}
			
			List<String> playerNames = new ArrayList<String>();
			for (int index = 0; index < players.size(); index++){
				Player currentPlayer = players.get(index);
				playerNames.add(currentPlayer.getName());
			}
			
			List<Game> games = dataService.getGames(year, weekInt);
			
			List<Pick> picks = null;
			if ("all".equals(player)){
				picks = dataService.getPicks(year, weekInt);
			}
			else {
				picks = dataService.getPicks(player, year, weekInt);
			}
			
			List<String> years = year == null ? null : Arrays.asList(year);
			List<String> weeks = week == null ? null : Arrays.asList(week);
			
			List<Record> records = dataService.getRecords(years, weeks, playerNames);
			
			writePicks(output, players, records, picks, games);
			
		}
		else if (type == null || "standings".equals(type)){
			List<String> years = null;
			
			if (year == null || "all".equals(year)){
			}
			else {
				years = Arrays.asList(year);
			}
			
			List<String> weeks = null;
			if (week == null || "all".equals(week)){
				
			}
			else {
				weeks = Arrays.asList(week);
			}
			
			List<Player> players = null;
			if (player == null || "all".equals(player)){
				if (year == null || "all".equals(years)){
					players = dataService.getPlayers();
				}
				else {
					players = dataService.getPlayers(year);
				}
			}
			else {
				players = Arrays.asList(dataService.getPlayer(player));
			}
			
			List<String> playerNames = new ArrayList<String>();
			for (int index = 0; index < players.size(); index++){
				Player currentPlayer = players.get(index);
				playerNames.add(currentPlayer.getName());
			}
			
			List<Record> records = dataService.getRecords(years, weeks, playerNames);
			
			Collections.sort(records, new RecordComparator());
			
			writeRecords(output, records);
		}
		
		writeFooter(output);
		
		output.print("</body>");
	}
	
	protected void writeTopHeader(ServletOutputStream output) throws Exception {
		
		output.print("<div class=\"top-header-container\">\n" + 
					 "<h3 style=\"margin-top: 5px; margin-bottom: 5px;\">NFL Picks</h3>\n" + 
					 "</div>");
	}
	
	protected void writeSelectorForm(ServletOutputStream output, String type, String player, String year, String week) throws Exception {

		List<String[]> typeOptions = Arrays.asList(new String[]{"standings", "Standings"}, new String[]{"picks", "Picks"});
		
		if (type == null){
			type = "standings";
		}
		
		List<Player> players = null;
		if ("picks".equals(type)){
			
			if (year == null || "all".equals(year)){
				year = Util.getCurrentYear();
			}
			
			if (week == null || "all".equals(week)){
				week = "1";
			}
			
			players = dataService.getPlayers(year);
		}
		else if ("standings".equals(type)){
			if (year == null || "all".equals(year)){
				players = dataService.getPlayers();
			}
			else {
				players = dataService.getPlayers(year);
			}
		}
		
		List<String[]> playerOptions = new ArrayList<String[]>();
		playerOptions.add(new String[]{"all", "Everybody"});
		for (int index = 0; index < players.size(); index++){
			Player currentPlayer = players.get(index);
			playerOptions.add(new String[]{currentPlayer.getName(), currentPlayer.getName()});
		}
		
		List<String> years = dataService.getYears();
		List<String[]> yearOptions = new ArrayList<String[]>();
		if ("standings".equals(type)){
			yearOptions.add(new String[]{"all", "All"});
		}
		for (int index = 0; index < years.size(); index++){
			String currentYear = years.get(index);
			yearOptions.add(new String[]{currentYear, currentYear});
		}
		
		List<Week> weeks = dataService.getWeeks(Util.getCurrentYear());
		List<String[]> weekOptions = new ArrayList<String[]>();
		if ("standings".equals(type)){
			weekOptions.add(new String[]{"all", "All"});
		}
		for (int index = 0; index < weeks.size(); index++){
			Week currentWeek = weeks.get(index);
			weekOptions.add(new String[]{String.valueOf(currentWeek.getWeekNumber()), String.valueOf(currentWeek.getLabel())});
		}
		
		String typeSelectHtml = HtmlUtil.createSelectHtml(typeOptions, type, "type", "type", null, "criteria-selector", null);
		String playersSelectHtml = HtmlUtil.createSelectHtml(playerOptions, player, "player", "player", null, "criteria-selector", null);
		String yearsSelectHtml = HtmlUtil.createSelectHtml(yearOptions, year, "year", "year", null, "criteria-selector", null);
		String weeksSelectHtml = HtmlUtil.createSelectHtml(weekOptions, week, "week", "week", null, "criteria-selector", null);
		
		output.print( 
				"		<div class=\"selectors-container\">\n" + 
				"		<form method=\"get\">" +
				"			<div class=\"selector-container\">\n" + 
				"				<div>What</div>\n" + 
								typeSelectHtml + 
				"			</div>\n" + 
				"			<div id=\"playerContainer\" class=\"selector-container\">\n" + 
				"				<div>Who</div>\n" + 
								playersSelectHtml + 
				"			</div>\n" + 
				"			<div id=\"yearContainer\" class=\"selector-container\">\n" + 
				"				<div>Year</div>\n" + 
								yearsSelectHtml + 
				"				</select>\n" + 
				"			</div>\n" + 
				"			<div id=\"weekContainer\" class=\"selector-container\">\n" + 
				"				<div>Week</div>\n" + 
							    weeksSelectHtml + 
				"			</div>\n" +
				"			<div id=\"weekContainer\" class=\"selector-container\">\n" + 
				"		    	<input type=\"submit\" value=\"Update\"/>" +
				"			</div>\n" +
				"</form>" +
				"		</div>");
	}
	
	protected void writePicks(ServletOutputStream output, List<Player> players, List<Record> records, List<Pick> picks, List<Game> games) throws Exception {
		
		writePicksHeader(output, players, records, picks, games);
		writePicksBody(output, players, records, picks, games);
	}
	
	protected void writePicksHeader(ServletOutputStream output, List<Player> players, List<Record> records, List<Pick> picks, List<Game> games) throws Exception {
		
		output.print("<div id=\"contentContainer\" class=\"content-container\">\n" + 
				"    <table class=\"picks-table\" align=\"center\">\n" + 
				"      <thead>\n" + 
				"        <tr>\n" + 
				"          <th class=\"table-header\" align=\"left\">Game</th>\n");
		
		Collections.sort(players, new Comparator<Player>(){

			public int compare(Player player1, Player player2) {
				return player1.getName().compareTo(player2.getName());
			}
		});
		
		for (int index = 0; index < players.size(); index++){
			Player player = players.get(index);
			output.print("<th colspan=\"2\" class=\"table-header\" align=\"left\">" + player.getName() + "</th>");
		}
		
		output.print("</tr></thead>");
		
		output.print("<tbody>");
		output.print("<tr>");
		output.print("<td class=\"last-pick-game\"></td>");
		
		boolean areThereAnyTies = ModelUtil.areThereAnyTies(records);
		
		for (int index = 0; index < players.size(); index++){
			Player player = players.get(index);
			
			Record record = ModelUtil.getRecordForPlayer(records, player.getName());
			
			String cssClass = "pick-record";
			if (index + 1 == players.size()){
				cssClass = "last-pick-record";
			}
			
			String recordHtml = "";
			
			if (record == null){
				if (areThereAnyTies){
					recordHtml = "<td colspan=\"2\" class=\"" + cssClass + "\">0 - 0 - 0</td>";
				}
				else {
					recordHtml = "<td colspan=\"2\" class=\"" + cssClass + "\">0 - 0</td>";
				}
				
			}
			else {

				recordHtml = "<td colspan=\"2\" class=\"" + cssClass + "\">" + record.getWins() + " - " + record.getLosses();

				if (areThereAnyTies){
					recordHtml = recordHtml + " - " + record.getTies();
				}
			}
			
			recordHtml = recordHtml + "</td>";
			
			output.print(recordHtml);
			
		}
		
		output.print("</tr>");
	}
	
	protected void writePicksBody(ServletOutputStream output, List<Player> players, List<Record> records, List<Pick> picks, List<Game> games) throws Exception {
		
		//go through each game ... sort them by id i think
		//get the pick for each game and player
		
		for (int gameIndex = 0; gameIndex < games.size(); gameIndex++){
			Game game = games.get(gameIndex);
			Team awayTeam = game.getAwayTeam();
			Team homeTeam = game.getHomeTeam();
			Team winningTeam = game.getWinningTeam();
			
			String rowCssClass = "even-row";
			if (gameIndex % 2 == 1){
				rowCssClass = "odd-row";
			}
			
			boolean isLastGame = false;
			String gameClass = "pick-game";
			if (gameIndex + 1 == games.size()){
				gameClass = "last-pick-game";
				isLastGame = true;
			}
			
			String awayTeamClass = "";
			String homeTeamClass = "";
			
			if (winningTeam != null){
				int winningTeamId = winningTeam.getId();
				
				if (awayTeam != null && awayTeam.getId() == winningTeamId){
					awayTeamClass = "winner";
				}
				else if (homeTeam != null && homeTeam.getId() == winningTeamId){
					homeTeamClass = "winner";
				}
			}
			else if (game.getTie()){
				awayTeamClass = "tie";
				homeTeamClass = "tie";
			}
			
			output.print("<tr class=\"" + rowCssClass + "\">");
			output.print("<td class=\"" + gameClass + "\">" + 
							"<span class=\"" + awayTeamClass + "\">" + awayTeam.getAbbreviation() + "</span>" + 
							" @ " + 
							"<span class=\"" + homeTeamClass + "\">" + homeTeam.getAbbreviation() + "</span>" + 
						 "</td>");
			
			for (int playerIndex = 0; playerIndex < players.size(); playerIndex++){
				Player player = players.get(playerIndex);
				
				Pick pick = ModelUtil.getPick(picks, player.getName(), game.getHomeTeam().getAbbreviation(), game.getAwayTeam().getAbbreviation(), null);
				Team pickTeam = pick == null ? null : pick.getTeam();
				String abbreviation = "";
				String result = "";
				String pickCssClass = "";
				
				if (pickTeam != null){
					abbreviation = pickTeam.getAbbreviation();
					
					if (winningTeam != null){
						if (pickTeam.getId() == winningTeam.getId()){
							result = "W";
							pickCssClass = "winner";
						}
						else {
							result = "L";
							pickCssClass = "loser";
						}
					}
					else if (game.getTie()){
						result = "T";
						pickCssClass = "tie";
					}
				}
				
				String teamCssClass = "pick-team";
				String resultCssClass = "pick-result";
				
				if (isLastGame){
					teamCssClass = "last-pick-team";
					resultCssClass = "last-pick-result";
				}
				
				output.print("<td class=\"" + teamCssClass + "\"><span class=\"" + pickCssClass + "\">" + abbreviation + "</span></td>");
				output.print("<td class=\"" + resultCssClass + "\"><span class=\"" + pickCssClass + "\">" + result + "</span></td>");  
			}
			
			output.print("</tr>");
		}
		
		output.print("</tbody></table>");
		
	}
	
	protected void writeRecords(ServletOutputStream output, List<Record> records) throws Exception {
		
		Record previousRecord = null;
		Record nextRecord = null;
		int gamesBack = 0;
		int rank = 0;
		int nextRank = 1;
		int previousRank = -1;
		int tieIndependentRank = 1;
		
		//int maxWins = records.stream().map(r -> r.getWins()).reduce(Integer::max);
		
		int topWins = 0;
		int topLosses = 0;
		
		boolean areThereAnyTies = false;
		for (int index = 0; index < records.size(); index++){
			Record record = records.get(0);
			if (record.getWins() > topWins){
				topWins = record.getWins();
			}
			
			if (index == 0 || (record.getLosses() < topLosses)){
				topLosses = record.getLosses();
			}
			
			if (record.getTies() > 0){
				areThereAnyTies = true;
			}
		}
		
		String tableHtml = "<table align=\"center\">\n" + 
		"      <thead>\n" + 
		"        <tr>\n" + 
		"          <th class=\"standings-header\"></th>\n" + 
		"          <th class=\"standings-header\">Wins</th>\n" + 
		"          <th class=\"standings-header\">Losses</th>\n";
		
		if (areThereAnyTies){
			tableHtml = tableHtml + "<th class=\"standings-header\">Ties</th>\n";
		}
		
		tableHtml = tableHtml + "          <th class=\"standings-header\">Win %</th>\n" + 
		"          <th class=\"standings-header\">GB</th>\n" + 
		"        </tr>\n" + 
		"      </thead>\n" + 
		"      <tbody>";
		
		output.print(tableHtml);
		
		for (int index = 0; index < records.size(); index++){
			Record record = records.get(index);
			
			tieIndependentRank = index + 1;
			rank = nextRank;
			
			nextRecord = null;
			
			if (index + 1 < records.size()){
				nextRecord = records.get(index + 1);
				
				//If the next record has the same number of losses, it should have the same rank
				//as this one.
				if (record.getWins() == nextRecord.getWins() && 
						record.getLosses() == nextRecord.getLosses()){
				}
				//If it doesn't, it should start a new "rank" and be whatever the current rank would
				//have been if there were no ties + 1.
				else {
					nextRank = tieIndependentRank + 1;
				}
			}
			
			//Now, we have the rank and next rank so we need to figure out if we need to put a little 't' to indicate
			//there was a tie.
			//There's a tie if:
			//	1. It's the same as the next rank and we're not at the end.
			//	2. The rank is the same as the previous rank.
			//
			//Number 1 should be pretty straight forward.  If this rank is the same as the next one, it's in a tie.
			//Number 2 is there for the last tie in a series of ties.  The last tie will have a "nextRank" that's different from
			//what it is, but we'll still want to show a tie for it.  So, in that case, we can just look to see if it's the same
			//as the previous rank and, if it is, we know there's a tie.
			
			String rankToUse = rank + "";
			
			if ((nextRank == rank && index + 1 < records.size()) || (rank == previousRank)){
				rankToUse = rankToUse + "t";
			}
			
			double wins = (double)record.getWins();
			double losses = (double)record.getLosses();
			
			double percentage = wins / (wins + losses);
			String formattedPercentage = Util.formatNormalDouble(percentage);
			
			String gamesBackString = "";
			if (topWins == record.getWins() && topLosses == record.getLosses()){
				gamesBackString = "-";
			}
			else {
				gamesBackString = String.valueOf(topWins - record.getWins());
			}
			
			String recordHtml = "<tr>" + 
									"<td class=\"records-cell\">" + rankToUse + ". " + record.getPlayer().getName() + "</td>" +
									"<td class=\"records-data-cell\">" + record.getWins() + "</td>" +
									"<td class=\"records-data-cell\">" + record.getLosses() + "</td>";
			
			if (areThereAnyTies){
				recordHtml = recordHtml + "<td class=\"records-data-cell\">" + record.getTies() + "</td>";
			}
			
			recordHtml = recordHtml + "<td class=\"records-data-cell\">" + formattedPercentage + "</td>" +
									  "<td class=\"records-data-cell\">" + gamesBackString + "</td>" +
								"</tr>";
			
			output.print(recordHtml);
			
			previousRecord = record;
			previousRank = rank;
		}
		
		output.print("</tbody></table>");
	}
	
	protected void writeFooter(ServletOutputStream output) throws Exception {

		output.print("<div>" + 
				"<p style=\"font-size: 11px; text-align: center;\">What is <a href=\"http://carsonrails.com\">cars on rails?</a></p>" + 
				"<p style=\"font-size: 11px; text-align: center;\"><a href=\"index.html\">Click here for the regular javascript enabled version</a></p>" + 
				"</div>");
	}
	
	protected int getGamesBack(Record record1, Record record2){
		
		
		int wins1 = record1.getWins();
		int wins2 = record2.getWins();
		
		int gamesBack = wins1 - wins2;
		
		if (gamesBack == 0){
			int losses1 = record1.getLosses();
			int losses2 = record2.getLosses();
			
			gamesBack = losses2 - losses1;
		}
		
		return gamesBack;
		
	}
	
	protected class RecordComparator implements Comparator<Record> {

		public int compare(Record record1, Record record2) {
			
			int wins1 = record1.getWins();
			int wins2 = record2.getWins();
			
			if (wins1 > wins2){
				return -1;
			}
			else if (wins1 < wins2){
				return 1;
			}
			else {
				int losses1 = record1.getLosses();
				int losses2 = record2.getLosses();
				
				
				if (losses1 < losses2){
					return -1;
				}
				else if (losses1 > losses2){
					return 1;
				}
			}

			return 0;
		}
	}
}
