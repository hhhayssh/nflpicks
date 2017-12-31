package nflpicks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.Season;
import nflpicks.model.Team;
import nflpicks.model.Week;

public class NFLPicksDataImporter2 {
	
	private static final Logger log = Logger.getLogger(NFLPicksDataImporter2.class);
	
	protected NFLPicksDataService dataService;
	
	protected Map<String, Season> seasonCache;
	
	protected Map<String, Week> weekCache;
	
	protected Map<String, Team> teamCache;
	
	protected Map<String, Player> playerCache;
	
	protected Map<String, Game> gameCache;
	
	protected Map<String, Pick> pickCache;
	
	public static void main(String[] args){
		
		NFLPicksDataImporter2 importer = new NFLPicksDataImporter2();
		
		String propertiesFilename = null;
		
		if (args.length == 1){
			propertiesFilename = args[0];
		}
		else {
			propertiesFilename = System.getProperty(NFLPicksConstants.NFL_PICKS_PROPERTIES_FILENAME_PROPERTY);
		}
		
		if (propertiesFilename == null){
			propertiesFilename = NFLPicksConstants.DEFAULT_NFL_PICKS_PROPERTIES_FILENAME;
		}
		
		importer.initialize(propertiesFilename);
	}
	
	public NFLPicksDataImporter2(){
		this.seasonCache = new HashMap<String, Season>();
		this.weekCache = new HashMap<String, Week>();
		this.teamCache = new HashMap<String, Team>();
		this.playerCache = new HashMap<String, Player>();
	}
	
	public void initialize(String propertiesFilename){
		
		ApplicationContext.getContext().initialize(propertiesFilename);
		
		this.dataService = new NFLPicksDataService(ApplicationContext.getContext().getDataSource());
	}
	
	public void importData(String filename){
		
		//What should happen when we import?
		
		//1. read the header and check each player to see if they're in there
		//it should be ....
		/*
		 Year,Week,Away,Home,Winner,Benny boy,Bruce,Chance,Jonathan,Mark,Teddy,Tim,Bookey,Jerry,Josh,Doodle,Var
2017,1,KC,NE,KC,KC,NE,,KC,KC,NE,NE,NE,NE,NE,NE,
		 */
		
		List<String> headerNames = readHeaderNames(filename);
		
		int numberOfColumns = headerNames.size();
		
		if (numberOfColumns < 4 ){
			//Error!
			log.error("Bad header in file!  Not enough columns!  headers = " + headerNames);
			return;
		}
		
		List<String> playerNames = new ArrayList<String>();
		if (numberOfColumns > 5){
			playerNames = headerNames.subList(5, numberOfColumns);
			updatePlayers(playerNames);
		}
		
		BufferedReader reader = null;
		int lineNumber = 0;
		String line = null;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			
			while ((line = reader.readLine() )!= null){
				List<String> values = Util.getCsvValues(line);
				
				int numberOfValues = values.size();
				
				if (numberOfValues < 4){
					log.error("Error importing data!  Bad line!  filename = " + filename + ", lineNumber = " + lineNumber + ", line = " + line);
				}
				
				String year = values.get(0);
				String weekNumber = values.get(1);
				String awayTeamAbbreviation = values.get(2);
				String homeTeamAbbreviation = values.get(3);
				String winningTeamAbbreviation = null;
				List<String> playerPicks = null;
				
				if (numberOfValues >= 5){
					winningTeamAbbreviation = values.get(4);
					
					if (numberOfValues >=6 ){
						playerPicks = values.subList(5, values.size());
					}
				}
				
				importData(year, weekNumber, awayTeamAbbreviation, homeTeamAbbreviation, winningTeamAbbreviation, playerNames, playerPicks);
			}
		}
		catch (Exception e){
			log.error("Error importing data!  lineNumber = " + lineNumber + ", filename = " + filename, e);
		}
		finally {
			Util.closeReader(reader);
		}
	}
	
	protected void importData(String year, String weekNumber, String awayTeamAbbreviation, String homeTeamAbbreviation, String winningTeamAbbreviation,
							  List<String> playerNames, List<String> playerPicks){
		
		Week week = getWeek(year, weekNumber);
		Team awayTeam = getTeam(awayTeamAbbreviation);
		Team homeTeam = getTeam(homeTeamAbbreviation);
		Team winningTeam = null;
		Game game = getGame(year, weekNumber, awayTeamAbbreviation, homeTeamAbbreviation);
		
		boolean tie = false;
		
		if (winningTeamAbbreviation != null){
			if (NFLPicksConstants.TIE_TEAM_ABBREVIATION.equals(winningTeamAbbreviation)){
				tie = true;
			}
			else {
				winningTeam = getTeam(winningTeamAbbreviation);
			}
		}
		
		if (game == null){
			game = new Game();
		}
		game.setWeekId(week.getId());
		game.setAwayTeam(awayTeam);
		game.setHomeTeam(homeTeam);
		game.setTie(tie);
		game.setWinningTeam(winningTeam);
		
		game = dataService.saveGame(game);
		
		for (int index = 0; index < playerNames.size(); index++){
			String playerName = playerNames.get(index);
			String pickAbbreviation = playerPicks.get(index);
			
			Player player = getPlayer(playerName);
			Team pickedTeam = getTeam(pickAbbreviation);
			Pick pick = getPick(year, weekNumber, awayTeamAbbreviation, homeTeamAbbreviation, playerName);
			
			if (pick == null){
				pick = new Pick();
			}
			
			pick.setGame(game);
			pick.setPlayer(player);
			pick.setTeam(pickedTeam);
			
			String result = PickUtil.getPickResult(winningTeamAbbreviation, pickAbbreviation);
			pick.setResult(result);

			dataService.savePick(pick);
		}
	}
	
	protected Season getSeason(String year){
		
		Season season = this.seasonCache.get(year);
		
		if (season == null){
			season = dataService.getSeason(year);
			this.seasonCache.put(year, season);
		}
		
		return season;
	}
	
	protected Week getWeek(String year, String weekNumber){
		
		String seasonAndWeekKey = getSeasonAndWeekKey(year, weekNumber);
		
		Week week = weekCache.get(seasonAndWeekKey);
		
		if (week == null){
			week = dataService.getWeek(year, weekNumber);
			weekCache.put(seasonAndWeekKey, week);
		}
		
		return week;
	}
	
	protected String getSeasonAndWeekKey(String year, String week){
		return year + "-" + week;
	}
	
	protected Team getTeam(String teamAbbreviation){
		
		Team team = teamCache.get(teamAbbreviation);
		
		if (team == null){
			team = dataService.getTeam(teamAbbreviation);
			teamCache.put(teamAbbreviation, team);
		}
		
		return team;
	}
	
	protected Player getPlayer(String playerName){
		
		Player player = playerCache.get(playerName);
		
		if (player == null){
			player = dataService.getPlayer(playerName);
			playerCache.put(playerName, player);
		}
		
		return player;
	}
	
	protected Game getGame(String year, String week, String awayTeamAbbreviation, String homeTeamAbbreviation){
		
		String gameKey = getGameKey(year, week, awayTeamAbbreviation, homeTeamAbbreviation);
		
		Game game = gameCache.get(gameKey);
		
		if (game == null){
			game = dataService.getGame(year, week, awayTeamAbbreviation, homeTeamAbbreviation);
			gameCache.put(gameKey, game);
		}
		
		return game;
	}
	
	protected String getGameKey(String year, String week, String awayTeamAbbreviation, String homeTeamAbbreviation){
		return year + "-" + week + "-" + awayTeamAbbreviation + "-" + homeTeamAbbreviation;
	}
	
	protected Pick getPick(String year, String week, String awayTeamAbbreviation, String homeTeamAbbreviation, String playerName){
		
		String pickKey = getPickKey(year, week, awayTeamAbbreviation, homeTeamAbbreviation, playerName);
		
		Pick pick = pickCache.get(pickKey);
		
		if (pick == null){
			pick = dataService.getPick(playerName, year, Integer.parseInt(week), homeTeamAbbreviation, awayTeamAbbreviation);
		}
		
		return pick;
	}
	
	protected String getPickKey(String year, String week, String awayTeamAbbreviation, String homeTeamAbbreviation, String playerName){
		return year + "-" + week + "-" + awayTeamAbbreviation + "-" + homeTeamAbbreviation + "-" + playerName;
	}
	
	protected void updatePlayers(List<String> playerNames){
		
		for (int index = 0; index < playerNames.size(); index++){
			String playerName = playerNames.get(index);
			
			Player player = dataService.getPlayer(playerName);
			
			if (player == null){
				player = new Player(-1, playerName);
				Player savedPlayer = dataService.savePlayer(player);
				
				if (savedPlayer == null){
					log.error("Error updating player!  playerName = " + playerName + ", playerNames = " + playerNames);
					return;
				}
			}
		}
	}
	
	protected List<String> readHeaderNames(String filename){
		
		List<String> headerNames = Util.readHeaderValues(filename);
		
		return headerNames;
		//Files.lines(Paths.get(nflPicksCsvFilename)).findFirst().get();
	}

}
