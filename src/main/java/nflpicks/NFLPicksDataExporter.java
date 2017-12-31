package nflpicks;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.Team;
import nflpicks.model.Week;

public class NFLPicksDataExporter {
	
	private static final Logger log = Logger.getLogger(NFLPicksDataExporter.class);
	
	protected NFLPicksDataService dataService;
	
	public static void main(String[] args){
		
		String propertiesFilename = null;
	
		String outputFilename = null;
		
		if (args.length >= 1){
			propertiesFilename = args[0];
			
			if (args.length == 2){
				outputFilename = args[1];
			}
		}
		else {
			propertiesFilename = System.getProperty(NFLPicksConstants.NFL_PICKS_PROPERTIES_FILENAME_PROPERTY);
		}
		
		if (propertiesFilename == null){
			propertiesFilename = NFLPicksConstants.DEFAULT_NFL_PICKS_PROPERTIES_FILENAME;
		}
		
		if (outputFilename == null){
			String currentDate = DateUtil.formatDate(new Date());
			outputFilename = "nflpicks-export-" + currentDate + ".csv";
		}
		
		ApplicationContext.getContext().initialize(propertiesFilename);
		
		NFLPicksDataService dataService = new NFLPicksDataService(ApplicationContext.getContext().getDataSource());
		
		NFLPicksDataExporter exporter = new NFLPicksDataExporter(dataService);
		
		exporter.export(outputFilename);
	}
	
	public NFLPicksDataExporter(NFLPicksDataService dataService){
		this.dataService = dataService;
	}
	
	public String export(){
		
		ExportData exportData = getExportData(null, null, null);
		
		StringWriter writer = new StringWriter();
		
		writeExportDataAsCSV(exportData, writer);
		
		String exportedData = writer.toString();
		
		return exportedData;
	}
	
	public void export(String filename){
		ExportData exportData = getExportData(null, null, null);
		
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(filename);
			writeExportDataAsCSV(exportData, writer);
		}
		catch (Exception e){
			log.error("Error exporting data to file!  filename = " + filename, e);
		}
		finally {
			Util.closeWriter(writer);
		}
		
	}
	
	protected class ExportData {
		
		protected List<Week> weeks;
		protected List<Player> players;
		protected List<Pick> picks;
		
		public ExportData(){
			this.weeks = new ArrayList<Week>();
			this.players = new ArrayList<Player>();
			this.picks = new ArrayList<Pick>();
		}
		
		public ExportData(List<Week> weeks, List<Player> players, List<Pick> picks){
			this.weeks = weeks;
			this.players = players;
			this.picks = picks;
		}

		public List<Week> getWeeks() {
			return weeks;
		}

		public void setWeeks(List<Week> weeks) {
			this.weeks = weeks;
		}

		public List<Player> getPlayers() {
			return players;
		}

		public void setPlayers(List<Player> players) {
			this.players = players;
		}

		public List<Pick> getPicks() {
			return picks;
		}

		public void setPicks(List<Pick> picks) {
			this.picks = picks;
		}
	}
	
	public ExportData getExportData(List<String> years, List<String> weekNumbers, List<String> playerNames){
		
		List<Week> weeks = dataService.getWeeks(years, weekNumbers);
		
		List<Player> players = dataService.getPlayers(playerNames);
		
		List<Pick> picks = dataService.getPicks(years, weekNumbers, playerNames);
		
		ExportData exportData = new ExportData(weeks, players, picks);
		
		return exportData;
	}
	
	protected void writeExportDataAsCSV(ExportData exportData, Writer writer){
		
		try {
			List<Player> players = exportData.getPlayers();
			
			StringBuilder headerStringBuilder = new StringBuilder("Year,Week,Away,Home,Winner");
			
			for (int index = 0; index < players.size(); index++){
				Player player = players.get(index);
				
				headerStringBuilder.append(",").append(player.getName());
			}
			
			String header = headerStringBuilder.toString();
			
			writer.write(header);
			writer.write('\n');
			
			List<Week> weeks = exportData.getWeeks();
			List<Pick> picks = exportData.getPicks();
			
			for (int index = 0; index < weeks.size(); index++){
				Week week = weeks.get(index);
				
				String year = week.getYear();
				int weekNumber = week.getWeekNumber();
				
				StringBuilder weekStringBuilder = new StringBuilder();
				
				List<Game> games = week.getGames();
				
				for (int gameIndex = 0; gameIndex < games.size(); gameIndex++){
					Game game = games.get(gameIndex);
					
					int gameId = game.getId();
					
					String awayTeamAbbreviation = null;
					Team awayTeam = game.getAwayTeam();
					if (awayTeam != null){
						awayTeamAbbreviation = awayTeam.getAbbreviation();
					}
					
					String homeTeamAbbreviation = null;
					Team homeTeam = game.getHomeTeam();
					if (homeTeam != null){
						homeTeamAbbreviation = homeTeam.getAbbreviation();
					}
					
					boolean tie = game.getTie();
					String winningTeamAbbreviation = null;
					Team winningTeam = game.getWinningTeam();
					if (winningTeam != null){
						winningTeamAbbreviation = winningTeam.getAbbreviation();
					}
					else {
						if (tie){
							winningTeamAbbreviation = NFLPicksConstants.TIE_TEAM_ABBREVIATION;
						}
					}
					
					weekStringBuilder.append(year).append(",")
									 .append(weekNumber).append(",")
									 .append(Util.unNull(awayTeamAbbreviation)).append(",")
									 .append(Util.unNull(homeTeamAbbreviation)).append(",")
									 .append(Util.unNull(winningTeamAbbreviation));
					
					for (int playerIndex = 0; playerIndex < players.size(); playerIndex++){
						Player player = players.get(playerIndex);
						String playerName = player.getName();
						
						Pick pick = PickUtil.getPick(picks, playerName, gameId);
						
						String pickAbbreviation = null;
						
						if (pick != null){
							Team pickedTeam = pick.getTeam();
							
							if (pickedTeam != null){
								pickAbbreviation = pickedTeam.getAbbreviation();
							}
						}
						
						weekStringBuilder.append(",").append(Util.unNull(pickAbbreviation));
					}
					
					writer.write(weekStringBuilder.toString());
					writer.write('\n');
					
					writer.flush();
					
					weekStringBuilder = new StringBuilder();
				}
			}
			
			writer.flush();
		}
		catch (Exception e){
			log.error("Error writing export data!", e);
		}
	}
}
