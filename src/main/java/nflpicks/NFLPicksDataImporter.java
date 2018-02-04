package nflpicks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;

import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.Season;
import nflpicks.model.Team;
import nflpicks.model.Week;

/**
 * 
 * This class will import picks from the "regular" format into the
 * database.  It's here because I wanted to be able to take the output
 * of the exporter and basically rebuild everything off of that.
 * 
 * It expects the data to be in the same format the exporter uses.  It'll
 * create the players and games as it processes them.  If a game or player
 * already exists, it'll update what's there.  It's almost like it "synchronizes"
 * the data in the database to what's in the exported file.
 * 
 * @author albundy
 *
 */
public class NFLPicksDataImporter {
	
	private static final Logger log = Logger.getLogger(NFLPicksDataImporter.class);
	
	/**
	 * 
	 * The thing that lets us get stuff from the database and save it.
	 * 
	 */
	protected NFLPicksDataService dataService;
	
	/**
	 * 
	 * A cache from year to season so we don't have to hit up the database
	 * each time we need to get a season by its year.
	 * 
	 */
	protected Map<String, Season> seasonCache;
	
	/**
	 * 
	 * A cache from the week number to the week object so we don't have to go
	 * to the database every time we need to get a week by its number.
	 * 
	 */
	protected Map<String, Week> weekCache;
	
	/**
	 * 
	 * A cache from a team's abbreviation to the actual team object.  Here so 
	 * we don't have to go to the database every time we need a team object
	 * but only have its abbreviation.
	 * 
	 */
	protected Map<String, Team> teamCache;
	
	/**
	 * 
	 * A cache from a player name to the player object.  Here so we don't have to
	 * go to the database every time we need to get a player from their name.
	 * 
	 */
	protected Map<String, Player> playerCache;
	
	/**
	 * 
	 * A cache of games so we don't have to go to the database to get a game
	 * over and over.  The key is defined by the "getGameKey" function (it's
	 * the year, week, home team, and away team).
	 * 
	 */
	protected Map<String, Game> gameCache;
	
	/**
	 * 
	 * A cache of picks so we don't have to go to the database to get a pick
	 * every time we need one.  The key is defined by the "getPickKey" function
	 * (it's the year, week, home team, away team, and player name). 
	 * 
	 */
	protected Map<String, Pick> pickCache;
	
	/**
	 * 
	 * Here so we can run this from the command line.  If you put in arguments, it'll use them.
	 * If not, it'll get them from the command line.  The arguments are:
	 * 
	 * 		1. The full path to the nflpicks.properties file.
	 * 		2. The full path to the picks file to import.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		String propertiesFilename = null;
		String importFilename = null;
		
		if (args.length == 0){
			Scanner scanner = new Scanner(System.in);
			System.out.println("Properties file:");
			propertiesFilename = scanner.nextLine();
			
			System.out.println("Import file:");
			importFilename = scanner.nextLine();
			
			scanner.close();
		}
		else if (args.length == 2){
			propertiesFilename = args[0];
			importFilename = args[1];
		}
		else {
			System.out.println("Bad arguments, man!");
			return;
		}
		
		ApplicationContext.getContext().initialize(propertiesFilename);
		
		NFLPicksDataService dataService = new NFLPicksDataService(ApplicationContext.getContext().getDataSource());
		
		NFLPicksDataImporter importer = new NFLPicksDataImporter(dataService);
		
		importer.importData(importFilename);
	}
	
	public NFLPicksDataImporter(NFLPicksDataService dataService){
		this.seasonCache = new HashMap<String, Season>();
		this.weekCache = new HashMap<String, Week>();
		this.teamCache = new HashMap<String, Team>();
		this.playerCache = new HashMap<String, Player>();
		this.gameCache = new HashMap<String, Game>();
		this.pickCache = new HashMap<String, Pick>();
		this.dataService = dataService;
	}
	
	public void importData(String filename){
		
		//What should happen when we import?
		
		//1. read the header and check each player to see if they're in there
		//it should be ....
		/*
		 Year,Week,Away,Home,Winner,Benny boy,Bruce,Chance,Jonathan,Mark,Teddy,Tim,Bookey,Jerry,Josh,Doodle,Var
2017,1,KC,NE,KC,KC,NE,,KC,KC,NE,NE,NE,NE,NE,NE,
		 */
		
		long start = System.currentTimeMillis();
		
		int totalNumberOfLines = Util.getLineCount(filename);
		
		log.info("Found " + totalNumberOfLines + " lines in " + filename);
		
		log.info("Reading header...");
		List<String> headerNames = readHeaderNames(filename);
		log.info("Read " + headerNames.size() + " header columns: " + headerNames);
		int numberOfColumns = headerNames.size();
		
		if (numberOfColumns < 4 ){
			log.error("Bad header in file!  Not enough columns!  headers = " + headerNames);
			return;
		}
		
		List<String> playerNames = new ArrayList<String>();
		if (numberOfColumns > 5){
			playerNames = headerNames.subList(5, numberOfColumns);
			log.info("Found " + playerNames.size() + " player names: " + playerNames);
			log.info("Updating players...");
			updatePlayers(playerNames);
			log.info("Players updated.");
		}
	
		log.info("Importing data from lines...");
		BufferedReader reader = null;
		int lineNumber = 0;
		String line = null;
		
		int progress = 0;
		int lastProgress = 0;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			
			while ((line = reader.readLine() )!= null){
				
				if (lineNumber == 0){
					lineNumber++;
					continue;
				}
				
				List<String> values = Util.getCsvValues(line);
				
				int numberOfValues = values.size();
				
				if (numberOfValues < 4){
					log.error("Error importing data!  Bad line!  filename = " + filename + ", lineNumber = " + lineNumber + ", line = " + line);
					reader.close();
					return;
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
				
				lineNumber++;
				
				//So we can show the progress of the export in the log.
				progress = (int)Math.ceil(((double)lineNumber / (double)totalNumberOfLines) * 100.0);
				
				//Only show the progress every 5%.
				if (progress >= (lastProgress + 5)){
					log.info("Imported " + lineNumber + " of " + totalNumberOfLines + " (" + progress + "%)");
					lastProgress = progress;
				}
			}
		}
		catch (Exception e){
			log.error("Error importing data!  lineNumber = " + lineNumber + ", filename = " + filename, e);
		}
		finally {
			Util.closeReader(reader);
		}
		
		long elapsed = System.currentTimeMillis() - start;
		
		log.info("Done importing data from file.  Took " + elapsed + " ms to import " + totalNumberOfLines + " lines from file " + filename);
	}
	
	protected void importData(String year, String weekNumber, String awayTeamAbbreviation, String homeTeamAbbreviation, String winningTeamAbbreviation,
							  List<String> playerNames, List<String> playerPicks){
		
		Season season = getSeason(year);
		if (season == null){
			season = createSeason(year);
		}
		
		Week week = getWeek(year, weekNumber);
		if (week == null){
			week = createWeek(year, weekNumber);
		}
		
		boolean tie = false;
		if (winningTeamAbbreviation != null){
			if (NFLPicksConstants.TIE_TEAM_ABBREVIATION.equals(winningTeamAbbreviation)){
				tie = true;
			}
		}

		Game game = getGame(year, weekNumber, awayTeamAbbreviation, homeTeamAbbreviation);
		if (game == null){
			game = createGame(year, weekNumber, awayTeamAbbreviation, homeTeamAbbreviation, winningTeamAbbreviation, tie);
		}
		
		for (int index = 0; index < playerNames.size(); index++){
			String playerName = playerNames.get(index);
			String pickAbbreviation = playerPicks.get(index);
			
			if (!Util.hasSomething(pickAbbreviation)){
				continue;
			}
			
			Player player = getPlayer(playerName);
			Team pickedTeam = getTeam(pickAbbreviation);
			Pick pick = getPick(year, weekNumber, awayTeamAbbreviation, homeTeamAbbreviation, playerName);
			
			if (pick == null){
				pick = new Pick();
			}
			
			pick.setGame(game);
			pick.setPlayer(player);
			pick.setTeam(pickedTeam);
			
			String result = ModelUtil.getPickResult(winningTeamAbbreviation, pickAbbreviation);
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
	
	protected Season createSeason(String year){
		
		Season season = new Season(year);
		season = dataService.saveSeason(season);
		
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
	
	protected Week createWeek(String year, String weekNumber){
		
		int weekNumberInt = Util.toInteger(weekNumber);
		String label = ModelUtil.getWeekLabelForWeekNumber(weekNumberInt);
		Season season = getSeason(year);
		Week week = new Week(season.getId(), weekNumberInt, label);
		week = dataService.saveWeek(week);
		
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
	
	protected Game createGame(String year, String weekNumber, String awayTeamAbbreviation, String homeTeamAbbreviation, 
							  String winningTeamAbbreviation, boolean tie){
		
		Week week = getWeek(year, weekNumber);
		Team homeTeam = getTeam(homeTeamAbbreviation);
		Team awayTeam = getTeam(awayTeamAbbreviation);
		Team winningTeam = getTeam(winningTeamAbbreviation);
		Game game = new Game(-1, week.getId(), homeTeam, awayTeam, tie, winningTeam);
		
		game = dataService.saveGame(game);
		
		return game;
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
