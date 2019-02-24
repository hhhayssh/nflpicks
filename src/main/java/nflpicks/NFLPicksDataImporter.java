package nflpicks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;

import nflpicks.model.Conference;
import nflpicks.model.Division;
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
	 * A cache from a conference's name to the actual object.  Here so we don't have
	 * to go to the database we need a conference object from its abbreviation.
	 * 
	 */
	protected Map<String, Conference> conferenceCache;
	
	/**
	 * 
	 * A cache from a division's name to the actual object.  Here so we don't have
	 * to go to the database we need a division object from its abbreviation.
	 * 
	 */
	protected Map<String, Division> divisionCache;
	
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
		
		importer.importPicksData(importFilename);
	}
	
	/**
	 * 
	 * If you're making a data importer, you should have the data service already made.
	 * 
	 * @param dataService
	 */
	public NFLPicksDataImporter(NFLPicksDataService dataService){
		this.conferenceCache = new HashMap<String, Conference>();
		this.divisionCache = new HashMap<String, Division>();
		this.teamCache = new HashMap<String, Team>();
		this.seasonCache = new HashMap<String, Season>();
		this.weekCache = new HashMap<String, Week>();
		this.playerCache = new HashMap<String, Player>();
		this.gameCache = new HashMap<String, Game>();
		this.pickCache = new HashMap<String, Pick>();
		this.dataService = dataService;
	}
	
	/**
	 * 
	 * Imports the data for the conferences, divisions, and teams from the given file.  It will create new records
	 * for each if they don't already exist.
	 * 
	 * Each line in the given file should have 14 fields in it:
	 * 
	 * 		conferenceName
	 * 		currentConferenceName
	 * 		conferenceStartYear
	 * 		conferenceEndYear
	 * 		divisionName
	 * 		currentDivisionName
	 * 		divisionStartYear
	 * 		divisionEndYear
	 * 		teamCity
	 * 		teamNickname
	 * 		teamAbbreviation
	 * 		teamStartYear
	 * 		teamEndYear
	 * 		currentTeamAbbreviation
	 * 
	 * The "current" fields are there for if a team moves or if divisions or conferences change.
	 * 
	 * @param filename
	 */
	public void importTeamData(String filename){
		
		//Steps to do:
		//	1. Each line should have all the info we need to make a conference, division,
		//	   and team, so just try to make each one for each line.
		//	2. We should only create each "thing" (conference, division, team) once.
		
		log.info("Importing team data...");
		
		long start = System.currentTimeMillis();
		
		int totalNumberOfLines = Util.getLineCount(filename);
		log.info("Found " + totalNumberOfLines + " lines in " + filename);
		
		log.info("Importing data from lines...");
		BufferedReader reader = null;
		int lineNumber = 0;
		String line = null;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			
			while ((line = reader.readLine() )!= null){
				
				//Skip the header...
				if (lineNumber == 0){
					lineNumber++;
					continue;
				}

				//Turn the csv line into a list so we can handle it easier.
				List<String> values = Util.getCsvValues(line);
				if (values.size() != 14){
					log.error("Bad line!  lineNumber = " + lineNumber + ", line = " + line);
					continue;
				}
				
				String conferenceName = values.get(0);
				String currentConferenceName = values.get(1);
				String conferenceStartYear = values.get(2);
				String conferenceEndYear = values.get(3);
				String divisionName = values.get(4);
				String currentDivisionName = values.get(5);
				String divisionStartYear = values.get(6);
				String divisionEndYear = values.get(7);
				String teamCity = values.get(8);
				String teamNickname = values.get(9);
				String teamAbbreviation = values.get(10);
				String teamStartYear = values.get(11);
				String teamEndYear = values.get(12);
				String currentTeamAbbreviation = values.get(13);
				
				Conference conference = getConference(conferenceName);
				if (conference == null){
					conference = createConference(conferenceName, conferenceStartYear, conferenceEndYear, currentConferenceName);
					log.info("Created conference: " + conference);
				}
				
				Division division = getDivision(conferenceName, divisionName);
				if (division == null){
					division = createDivision(conference, divisionName, divisionStartYear, divisionEndYear, currentDivisionName);
					log.info("Created division: " + division);
				}

				Team team = getTeam(teamAbbreviation);
				if (team == null){
					team = createTeam(division, teamCity, teamNickname, teamAbbreviation, teamStartYear, teamEndYear, currentTeamAbbreviation);
					log.info("Created team: " + team);
				}
			}
		}
		catch (Exception e){
			log.error("Error importing team data!  lineNumber = " + lineNumber + ", filename = " + filename, e);
		}
		finally {
			Util.closeReader(reader);
		}
		
		long elapsed = System.currentTimeMillis() - start;
		
		log.info("Done importing team data.  Took " + elapsed + " ms from file " + filename);
	}
	
	/**
	 * 
	 * This function imports the picks data from the given file name.  It expects
	 * the data in the given file to be like a csv file like this:
	 * 
	 * 		Year,Week,Away,Home,Winner,PlayerName1,PlayerName2,PlayerName3,...
	 * 		2016,1,BUF,BAL,BUF,BUF,BAL,BAL,...
	 * 		2016,1,ATL,GB,GB,GB,ATL,ATL,...
	 * 		...
	 * 
	 * Everything after "Home" field is optional.  If there aren't any player picks, it'll
	 * just make the games.  If isn't a "Winner" column, it won't set the winner of the games.
	 * 
	 * It will create the season, week, and game records if they don't exist.  If they do exist, it'll
	 * update what's there to match what's in the file (like it'll set the winning team and stuff like that).
	 * It'll create the player records if they don't exist too.
	 * 
	 * @param filename
	 */
	public void importPicksData(String filename){
		
		//Steps to do:
		//	1. Get how many lines are in the file so we can show the progress.
		//	2. Make sure the header has the required columns (year, week, away, home).  If it doesn't
		//	   have that many columns, that means the file is bad so we should quit.
		//	3. Check to see if there are any player names in the header.
		//	4. If there are, go through and create records for them in the database if they don't
		//	   already exist.
		//	5. After that', we're ready to go, so just read each line and import it.
		//	6. That's it.
	
		log.info("Importing picks data...");
		
		long start = System.currentTimeMillis();
		
		int totalNumberOfLines = Util.getLineCount(filename);
		log.info("Found " + totalNumberOfLines + " lines in " + filename);
		
		log.info("Reading header...");
		List<String> headerNames = Util.readHeaderValues(filename);
		log.info("Read " + headerNames.size() + " header columns: " + headerNames);

		int numberOfColumns = headerNames.size();
		
		//We need at least the year, week, home, and away columns in order to do anything.
		if (numberOfColumns < 4 ){
			log.error("Bad header in file!  Not enough columns!  headers = " + headerNames);
			return;
		}
		
		List<String> playerNames = new ArrayList<String>();
		
		//If there are more than 5 columns, that means the player picks should be included so we should
		//get out the player names and make sure we have records for each player.
		if (numberOfColumns > 5){
			playerNames = headerNames.subList(5, numberOfColumns);

			log.info("Found " + playerNames.size() + " player names: " + playerNames);
			log.info("Updating players...");
			
			//This function will create player records for each name that doesn't already have a player
			//record.
			createPlayers(playerNames);
			
			log.info("Players updated.");
		}

		//No we're ready to go...
		log.info("Importing data from lines...");
		BufferedReader reader = null;
		int lineNumber = 0;
		String line = null;
		
		int progress = 0;
		int lastProgress = 0;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			
			while ((line = reader.readLine() )!= null){
				
				//Skip the header...
				if (lineNumber == 0){
					lineNumber++;
					continue;
				}
				
				//Turn the csv line into a list so we can handle it easier.
				List<String> values = Util.getCsvValues(line);
				
				int numberOfValues = values.size();
				
				//If we ever read fewer than 4 values, the line is bad, which means the file is bad, so we should just quit.
				if (numberOfValues < 4){
					log.error("Error importing data!  Bad line!  filename = " + filename + ", lineNumber = " + lineNumber + ", line = " + line);
					reader.close();
					return;
				}
				
				//We know we'll have at least the year, week, away team, and home team, so pull those
				//values out.
				String year = values.get(0);
				String weekNumber = values.get(1);
				String awayTeamAbbreviation = values.get(2);
				String homeTeamAbbreviation = values.get(3);
				String winningTeamAbbreviation = null;
				List<String> playerPicks = null;
				
				//Add in the winning team and players if we have them.
				if (numberOfValues >= 5){
					winningTeamAbbreviation = values.get(4);
					
					if (numberOfValues >=6 ){
						playerPicks = values.subList(5, values.size());
					}
				}
				
				//Now we're ready to import the line.
				importData(year, weekNumber, awayTeamAbbreviation, homeTeamAbbreviation, winningTeamAbbreviation, playerNames, playerPicks);
				
				lineNumber++;
				
				//So we can show the progress of the export in the log.
				progress = (int)Math.floor(((double)lineNumber / (double)totalNumberOfLines) * 100.0);
				
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
	
	/**
	 * 
	 * This function will import the given data that should come from a "line" in the csv file.  It will make the records
	 * as it needs them.  For example, if there's no season record for the given year, it'll make a record for the season.
	 * Same thing with the week, game, and picks.
	 * 
	 * @param year
	 * @param weekNumber
	 * @param awayTeamAbbreviation
	 * @param homeTeamAbbreviation
	 * @param winningTeamAbbreviation
	 * @param playerNames
	 * @param playerPicks
	 */
	protected void importData(String year, String weekNumber, String awayTeamAbbreviation, String homeTeamAbbreviation, String winningTeamAbbreviation,
							  List<String> playerNames, List<String> playerPicks){
		
		//Steps to do:
		//	1. Make sure the season and week exist and create them if they don't.
		//	2. Make sure the game exists and create it if it doesn't.
		//	3. If there are player names, go through all of them and make the pick
		//	   records for them.
		
		Season season = getSeason(year);
		if (season == null){
			season = createSeason(year);
		}
		
		Week week = getWeek(year, weekNumber);
		if (week == null){
			week = createWeek(year, weekNumber);
		}
		
		//If there's a winning team, figure out if there's a tie and pass that in too.
		//If there is a tie, the winning team abbreviation should be "TIE".
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
		else {
			Team winningTeam = getTeam(winningTeamAbbreviation);
			game.setWinningTeam(winningTeam);
			dataService.saveGame(game);
		}
		
		for (int index = 0; index < playerNames.size(); index++){
			String playerName = playerNames.get(index);
			String pickAbbreviation = playerPicks.get(index);
			
			//If they didn't make a pick, there's nothing to do, so just keep going.  When people
			//don't make picks, we don't save like an empty record or anything like that.
			if (!Util.hasSomething(pickAbbreviation)){
				continue;
			}
			
			Player player = getPlayer(playerName);
			Team pickedTeam = getTeam(pickAbbreviation);
			Pick pick = getPick(year, weekNumber, awayTeamAbbreviation, homeTeamAbbreviation, playerName);
			
			if (pick == null){
				pick = new Pick();
			}

			//The pick has the game, player, and the team they picked.  We don't care about the result right
			//now because that's "derived" when they get the picks by comparing the winning team and picked team.
			pick.setGame(game);
			pick.setPlayer(player);
			pick.setTeam(pickedTeam);

			dataService.savePick(pick);
		}
	}
	
	/**
	 * 
	 * Gets the conference for the given name.  It'll get it out of the cache if it's
	 * there and go to the database if it's not.
	 * 
	 * @param name
	 * @return
	 */
	protected Conference getConference(String name){
		
		Conference conference = conferenceCache.get(name);
		
		if (conference == null){
			conference = dataService.getConference(name, true);
			conferenceCache.put(name, conference);
		}
		
		return conference;
	}
	
	/**
	 * 
	 * Creates the conference with the given name and other parameters and no divisions.
	 * 
	 * @param name
	 * @param startYear
	 * @param endYear
	 * @param currentName
	 * @return
	 */
	protected Conference createConference(String name, String startYear, String endYear, String currentName){
		
		Conference conference = new Conference(-1, name, null, startYear, endYear, currentName);
		Conference savedConference = dataService.saveConference(conference);
		
		return savedConference;
	}
	
	/**
	 * 
	 * Gets the division for the given name.  It'll get it out of the cache if it's
	 * there and go to the database if it's not.
	 * 
	 * @param conferenceName
	 * @param name
	 * @return
	 */
	protected Division getDivision(String conferenceName, String name){
		
		Division division = divisionCache.get(name);
		
		if (division == null){
			division = dataService.getDivision(conferenceName, name, true);
			divisionCache.put(name, division);
		}
		
		return division;
	}
	
	/**
	 * 
	 * Creates a division in the given conference with given name and no teams.
	 * 
	 * @param conference
	 * @param name
	 * @param startYear
	 * @param endYear
	 * @param currentName
	 * @return
	 */
	protected Division createDivision(Conference conference, String name, String startYear, String endYear, String currentName){
		
		Division division = new Division(-1, conference.getId(), name, null, startYear, endYear, currentName);
		Division savedDivision = dataService.saveDivision(division);
		
		return savedDivision;
	}
	
	
	/**
	 * 
	 * Gets the season for the given year.  It'll get it out of the cache if it's
	 * there and go to the database if it's not.
	 * 
	 * @param year
	 * @return
	 */
	protected Season getSeason(String year){
		
		Season season = seasonCache.get(year);
		
		if (season == null){
			season = dataService.getSeasonByYear(year);
			seasonCache.put(year, season);
		}
		
		return season;
	}
	
	/**
	 * 
	 * Creates a season for the given year and returns what was created.
	 * 
	 * @param year
	 * @return
	 */
	protected Season createSeason(String year){
		
		Season season = new Season(year);
		season = dataService.saveSeason(season);
		
		return season;
	}
	
	/**
	 * 
	 * Gets the week for the given year and week number.  It'll try to get it
	 * from the cache first, and then go to the database to get it if it's not in
	 * the cache.
	 * 
	 * @param year
	 * @param weekNumber
	 * @return
	 */
	protected Week getWeek(String year, String weekNumber){
		
		String seasonAndWeekKey = getSeasonAndWeekKey(year, weekNumber);
		
		Week week = weekCache.get(seasonAndWeekKey);
		
		if (week == null){
			week = dataService.getWeek(year, weekNumber);
			weekCache.put(seasonAndWeekKey, week);
		}
		
		return week;
	}
	
	/**
	 * 
	 * Creates a week record for the given year and week number.  It will make
	 * the "label" for the week based off the given week number and handle
	 * things like calling week 18 "Playoffs - Wild Card" too.
	 * 
	 * @param year
	 * @param weekNumber
	 * @return
	 */
	protected Week createWeek(String year, String weekNumber){
		
		int weekNumberInt = Util.toInteger(weekNumber);
		//The label to use for the week is usually just like "Week" and then the number.
		//If the week number is 18 or over, though, it's like "Playoffs - Divisional".
		String label = ModelUtil.getWeekLabelForWeekNumber(weekNumberInt);
		Season season = getSeason(year);
		Week week = new Week(season.getId(), weekNumberInt, label);
		week = dataService.saveWeek(week);
		
		return week;
	}
	
	/**
	 * 
	 * Gets the "key" we use to store weeks by their year and number in the cache.
	 * 
	 * @param year
	 * @param week
	 * @return
	 */
	protected String getSeasonAndWeekKey(String year, String week){
		return year + "-" + week;
	}
	
	/**
	 * 
	 * Gets the team with the given abbreviation.  If it's not in the cache,
	 * it'll go the database to get it.
	 * 
	 * @param teamAbbreviation
	 * @return
	 */
	protected Team getTeam(String teamAbbreviation){
		
		Team team = teamCache.get(teamAbbreviation);
		
		if (team == null){
			team = dataService.getTeamByAbbreviation(teamAbbreviation);
			teamCache.put(teamAbbreviation, team);
		}
		
		return team;
	}
	
	/**
	 * 
	 * Makes a team in the given division, city, and with the given nickname and all the other parameters.
	 * 
	 * @param division
	 * @param city
	 * @param nickname
	 * @param abbreviation
	 * @param startYear
	 * @param endYear
	 * @param currentAbbreviation
	 * @return
	 */
	protected Team createTeam(Division division, String city, String nickname, String abbreviation, String startYear, String endYear, String currentAbbreviation){
		
		Team team = new Team(-1, division.getId(), city, nickname, abbreviation, startYear, endYear, currentAbbreviation);
		Team savedTeam = dataService.saveTeam(team);
		
		return savedTeam;
	}
	
	/**
	 * 
	 * Gets the player with the given name.  If they're not in the cache, it'll go
	 * to the database to get them.
	 * 
	 * @param playerName
	 * @return
	 */
	protected Player getPlayer(String playerName){
		
		Player player = playerCache.get(playerName);
		
		if (player == null){
			player = dataService.getPlayer(playerName);
			playerCache.put(playerName, player);
		}
		
		return player;
	}
	
	/**
	 * 
	 * Gets the game for the given year and week for the given teams.  If it's not in the cache, it'll go
	 * to the database and get it.
	 * 
	 * @param year
	 * @param week
	 * @param awayTeamAbbreviation
	 * @param homeTeamAbbreviation
	 * @return
	 */
	protected Game getGame(String year, String week, String awayTeamAbbreviation, String homeTeamAbbreviation){
		
		String gameKey = getGameKey(year, week, awayTeamAbbreviation, homeTeamAbbreviation);
		
		Game game = gameCache.get(gameKey);
		
		if (game == null){
			game = dataService.getGame(year, week, awayTeamAbbreviation, homeTeamAbbreviation);
			gameCache.put(gameKey, game);
		}
		
		return game;
	}
	
	/**
	 * 
	 * Gets the key we use in the game cache map so we can identify a game by its "components".
	 * 
	 * @param year
	 * @param week
	 * @param awayTeamAbbreviation
	 * @param homeTeamAbbreviation
	 * @return
	 */
	protected String getGameKey(String year, String week, String awayTeamAbbreviation, String homeTeamAbbreviation){
		return year + "-" + week + "-" + awayTeamAbbreviation + "-" + homeTeamAbbreviation;
	}
	
	/**
	 * 
	 * Creates a game record for the given year and week for the given two teams and with the given result.
	 * 
	 * @param year
	 * @param weekNumber
	 * @param awayTeamAbbreviation
	 * @param homeTeamAbbreviation
	 * @param winningTeamAbbreviation
	 * @param tie
	 * @return
	 */
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
	
	/**
	 * 
	 * Gets the pick for the given game and player.  If it's not in the cache, it'll go to the database and get the pick.
	 * 
	 * @param year
	 * @param week
	 * @param awayTeamAbbreviation
	 * @param homeTeamAbbreviation
	 * @param playerName
	 * @return
	 */
	protected Pick getPick(String year, String week, String awayTeamAbbreviation, String homeTeamAbbreviation, String playerName){
		
		String pickKey = getPickKey(year, week, awayTeamAbbreviation, homeTeamAbbreviation, playerName);
		
		Pick pick = pickCache.get(pickKey);
		
		if (pick == null){
			pick = dataService.getPick(playerName, year, Integer.parseInt(week), homeTeamAbbreviation, awayTeamAbbreviation);
		}
		
		return pick;
	}
	
	/**
	 * 
	 * Gets the key for the given pick (the game plus the player).
	 * 
	 * @param year
	 * @param week
	 * @param awayTeamAbbreviation
	 * @param homeTeamAbbreviation
	 * @param playerName
	 * @return
	 */
	protected String getPickKey(String year, String week, String awayTeamAbbreviation, String homeTeamAbbreviation, String playerName){
		return year + "-" + week + "-" + awayTeamAbbreviation + "-" + homeTeamAbbreviation + "-" + playerName;
	}
	
	/**
	 * 
	 * Creates records in the player table for all the given player names if they
	 * don't exist.  If a player with one of the names already exists, it won't do anything.
	 * 
	 * @param playerNames
	 */
	protected void createPlayers(List<String> playerNames){
		
		log.info("Creating new " + playerNames.size() + " new players: " + playerNames);
		
		for (int index = 0; index < playerNames.size(); index++){
			String playerName = playerNames.get(index);
			
			Player player = dataService.getPlayer(playerName);
			
			if (player == null){
				log.info("Saving new player for " + playerName);
				player = new Player(-1, playerName);
				Player savedPlayer = dataService.savePlayer(player);
				
				if (savedPlayer == null){
					log.error("Error updating player!  playerName = " + playerName + ", playerNames = " + playerNames);
					return;
				}
			}
			else {
				log.info("Not creating player for " + playerName + " because they already exist.");
			}
		}
		
		log.info("Done creating " + playerNames.size() + " players.");
	}
}
