package nflpicks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nflpicks.model.Division;
import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.PlayerDivision;
import nflpicks.model.Season;
import nflpicks.model.Team;
import nflpicks.model.TeamConference;
import nflpicks.model.TeamDivision;
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
	
	private static final Log log = LogFactory.getLog(NFLPicksDataImporter.class);
	
	/**
	 *
	 * The service that lets it connect to the database and pull stuff out.
	 *
	 */
	protected NFLPicksModelDataService modelDataService;
	
	/**
	 * 
	 * Because we need to use functions defined in the stats data service too.
	 * 
	 */
	protected NFLPicksStatsDataService statsDataService;
	
	/**
	 * 
	 * A cache from a conference's name to the actual object.  Here so we don't have
	 * to go to the database we need a conference object from its abbreviation.
	 * 
	 */
	protected Map<String, TeamConference> teamConferenceCache;
	
	/**
	 * 
	 * A cache from a division's name to the actual object.  Here so we don't have
	 * to go to the database we need a division object from its abbreviation.
	 * 
	 */
	protected Map<String, TeamDivision> teamDivisionCache;
	
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
	 * A cache from the division name to the division object.  Here so we won't have to go to
	 * the database every time we need a division by its name.
	 * 
	 */
	protected Map<String, Division> divisionCache;
	
	/**
	 * 
	 * A cache from the player and division key combo to the player division object.  Here so we don't
	 * have to go to the database every time we need to get a player division object.
	 * 
	 */
	protected Map<String, PlayerDivision> playerDivisionCache;
	
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
		
		NFLPicksModelDataService modelDataService = new NFLPicksModelDataService(ApplicationContext.getContext().getDataSource());
		
		NFLPicksStatsDataService statsDataService = new NFLPicksStatsDataService(ApplicationContext.getContext().getDataSource(), modelDataService);
		
		NFLPicksDataImporter importer = new NFLPicksDataImporter(modelDataService, statsDataService);
		
		importer.importPicksData(importFilename);
	}
	
	/**
	 * 
	 * If you're making a data importer, you should have the data service already made.
	 * 
	 * @param dataService
	 */
	public NFLPicksDataImporter(NFLPicksModelDataService modelDataService, NFLPicksStatsDataService statsDataService){
		this.teamConferenceCache = new HashMap<String, TeamConference>();
		this.teamDivisionCache = new HashMap<String, TeamDivision>();
		this.teamCache = new HashMap<String, Team>();
		this.seasonCache = new HashMap<String, Season>();
		this.weekCache = new HashMap<String, Week>();
		this.playerCache = new HashMap<String, Player>();
		this.divisionCache = new HashMap<String, Division>();
		this.playerDivisionCache = new HashMap<String, PlayerDivision>();
		this.gameCache = new HashMap<String, Game>();
		this.pickCache = new HashMap<String, Pick>();
		this.modelDataService = modelDataService;
		this.statsDataService = statsDataService;
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
					log.info("Skipping header: " + line);
					lineNumber++;
					continue;
				}

				//Turn the csv line into a list so we can handle it easier.
				List<String> values = Util.getCsvValues(line, false);
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
				
				TeamConference conference = getTeamConference(conferenceName);
				if (conference == null){
					conference = createTeamConference(conferenceName, conferenceStartYear, conferenceEndYear, currentConferenceName);
					log.info("Created conference: " + conference);
				}
				
				TeamDivision division = getTeamDivision(conferenceName, divisionName);
				if (division == null){
					division = createTeamDivision(conference, divisionName, divisionStartYear, divisionEndYear, currentDivisionName);
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
	 * This function will import the divisions from the give filename.  It expects it
	 * to be in a csv file like this: division name, abbreviation
	 * 
	 * i should make a "multi csv file" that has multiple csvs in it ... there should
	 * be like the picks csv with the players, games, and picks and then the "model" csv
	 * that has the teams, player divisions, and everything else in it
	 * 
	 * there should be a certain number of newlines or some kind of delimiter that separates
	 * each csv in the multi csv
	 * 
	 * maybe 
	 * 
	 * ==========end csv: csv name==================
	 * 
	 * ================start csv: csv name==================
	 * 
	 * @param filename
	 */
	public void importDivisionData(String filename){
		
		log.info("Importing division data ...");
		
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
				List<String> values = Util.getCsvValues(line, false);
				if (values.size() != 2){
					log.error("Bad line!  lineNumber = " + lineNumber + ", line = " + line);
					continue;
				}
				
				String divisionName = values.get(0);
				String abbreviation = values.get(1);
				
				Division division = getDivision(divisionName);
				if (division == null){
					division = createDivision(divisionName, abbreviation);
					
					log.info("Created division: " + division);
				}
			}
		}
		catch (Exception e){
			log.error("Error importing division data!  lineNumber = " + lineNumber + ", filename = " + filename, e);
		}
		finally {
			Util.closeReader(reader);
		}
		
		long elapsed = System.currentTimeMillis() - start;
		
		log.info("Done importing division data.  Took " + elapsed + " ms from file " + filename);
	}
	
	/**
	 * 
	 * This function will import the "player division data" from the given file.  Each line in the file should
	 * have the division name, player name, and year for the player/division "relationship", in that order.
	 * 
	 * It pretty much just does what the other ones do.  Reads each line, tries to turn it into an object, and then
	 * saves the object (inserts or updates).
	 * 
	 * @param filename
	 */
	public void importPlayerDivisionData(String filename){
		
		log.info("Importing player division data ...");
		
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
				List<String> values = Util.getCsvValues(line, false);
				if (values.size() != 3){
					log.error("Bad line!  lineNumber = " + lineNumber + ", line = " + line);
					continue;
				}
				
				String divisionName = values.get(0);
				String playerName = values.get(1);
				String year = values.get(2);
				
				PlayerDivision playerDivision = getPlayerDivision(divisionName, playerName, year);
				if (playerDivision == null){
					Division division = getDivision(divisionName);
					Player player = getPlayer(playerName);
					Season season = getSeason(year);
					playerDivision = createPlayerDivision(division, player, season);
					
					log.info("Created player division: " + playerDivision);
				}
			}
		}
		catch (Exception e){
			log.error("Error importing player division data!  lineNumber = " + lineNumber + ", filename = " + filename, e);
		}
		finally {
			Util.closeReader(reader);
		}
		
		long elapsed = System.currentTimeMillis() - start;
		
		log.info("Done importing player division data.  Took " + elapsed + " ms from file " + filename);
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
				List<String> values = Util.getCsvValues(line, false);
				
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
				String weekSequenceNumber = values.get(1);
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
				importData(year, weekSequenceNumber, awayTeamAbbreviation, homeTeamAbbreviation, winningTeamAbbreviation, playerNames, playerPicks);
				
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
	 * @param weekSequenceNumber
	 * @param awayTeamAbbreviation
	 * @param homeTeamAbbreviation
	 * @param winningTeamAbbreviation
	 * @param playerNames
	 * @param playerPicks
	 */
	protected void importData(String year, String weekSequenceNumber, String awayTeamAbbreviation, String homeTeamAbbreviation, String winningTeamAbbreviation,
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
		
		Week week = getWeek(year, weekSequenceNumber);
		if (week == null){
			week = createWeek(year, weekSequenceNumber);
		}
		
		//If there's a winning team, figure out if there's a tie and pass that in too.
		//If there is a tie, the winning team abbreviation should be "TIE".
		boolean tie = false;
		if (winningTeamAbbreviation != null){
			if (NFLPicksConstants.TIE_TEAM_ABBREVIATION.equals(winningTeamAbbreviation)){
				tie = true;
			}
		}

		Game game = getGame(year, weekSequenceNumber, awayTeamAbbreviation, homeTeamAbbreviation);
		if (game == null){
			game = createGame(year, weekSequenceNumber, awayTeamAbbreviation, homeTeamAbbreviation, winningTeamAbbreviation, tie);
		}
		else {
			Team winningTeam = getTeam(winningTeamAbbreviation);
			game.setWinningTeam(winningTeam);
			//If there was no winning team, it'll be null, but the abbreviation for it will be "TIE".
			//If that's the case, flip the "tie" switch on the game so it's treated like a tie the
			//rest of the way.
			if (winningTeam == null) {
				if (NFLPicksConstants.TIE_TEAM_ABBREVIATION.equals(winningTeamAbbreviation)) {
					game.setTie(true);
				}
			}
			modelDataService.saveGame(game);
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
			Pick pick = getPick(year, weekSequenceNumber, awayTeamAbbreviation, homeTeamAbbreviation, playerName);
			
			if (pick == null){
				pick = new Pick();
			}

			//The pick has the game, player, and the team they picked.  We don't care about the result right
			//now because that's "derived" when they get the picks by comparing the winning team and picked team.
			pick.setGame(game);
			pick.setPlayer(player);
			pick.setTeam(pickedTeam);

			modelDataService.savePick(pick);
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
	protected TeamConference getTeamConference(String name){
		
		TeamConference conference = teamConferenceCache.get(name);
		
		if (conference == null){
			conference = modelDataService.getTeamConference(name, true);
			teamConferenceCache.put(name, conference);
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
	protected TeamConference createTeamConference(String name, String startYear, String endYear, String currentName){
		
		TeamConference conference = new TeamConference(-1, name, null, startYear, endYear, currentName);
		TeamConference savedConference = modelDataService.saveTeamConference(conference);
		
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
	protected TeamDivision getTeamDivision(String conferenceName, String name){
		
		TeamDivision division = teamDivisionCache.get(name);
		
		if (division == null){
			division = modelDataService.getTeamDivision(conferenceName, name, true);
			teamDivisionCache.put(name, division);
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
	protected TeamDivision createTeamDivision(TeamConference conference, String name, String startYear, String endYear, String currentName){
		
		TeamDivision division = new TeamDivision(-1, conference.getId(), name, null, startYear, endYear, currentName);
		TeamDivision savedDivision = modelDataService.saveTeamDivision(division);
		
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
			season = modelDataService.getSeasonByYear(year, true);
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
		season = modelDataService.saveSeason(season);
		
		return season;
	}
	
	/**
	 * 
	 * Gets the week for the given year and week number.  It'll try to get it
	 * from the cache first, and then go to the database to get it if it's not in
	 * the cache.
	 * 
	 * @param year
	 * @param weekSequenceNumber
	 * @return
	 */
	protected Week getWeek(String year, String weekSequenceNumber){
		
		String seasonAndWeekKey = getSeasonAndWeekKey(year, weekSequenceNumber);
		
		Week week = weekCache.get(seasonAndWeekKey);
		
		if (week == null){
			int weekSequenceNumberInteger = Util.toInteger(weekSequenceNumber);
			week = modelDataService.getWeekBySequenceNumber(year, weekSequenceNumberInteger);
			weekCache.put(seasonAndWeekKey, week);
		}
		
		return week;
	}
	
	/**
	 * 
	 * Creates a week record for the given year and week number.  It will make the
	 * type, key, and label based off the year and week number and it'll handle
	 * the change in week 18 from 2020 to 2021.
	 * 
	 * @param year
	 * @param weekSequenceNumber
	 * @return
	 */
	protected Week createWeek(String year, String weekSequenceNumber){
		
		int yearNumberInt = Util.toInteger(year);
		int weekSequenceNumberInt = Util.toInteger(weekSequenceNumber);
		
		//The label to use for the week is usually just like "Week" and then the number.
		//If the week number is 18 or over, though, it's like "Playoffs - Divisional".
		//this needs the year now too.
		
		String type = ModelUtil.getWeekType(yearNumberInt, weekSequenceNumberInt);
		String key = ModelUtil.getWeekKey(yearNumberInt, weekSequenceNumberInt);
		String label = ModelUtil.getWeekLabel(yearNumberInt, weekSequenceNumberInt);
		
		Season season = getSeason(year);
		Week week = new Week(season.getId(), weekSequenceNumberInt, type, key, label);
		week = modelDataService.saveWeek(week);
		
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
			team = modelDataService.getTeamByAbbreviation(teamAbbreviation);
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
	protected Team createTeam(TeamDivision division, String city, String nickname, String abbreviation, String startYear, String endYear, String currentAbbreviation){
		
		Team team = new Team(-1, division.getId(), city, nickname, abbreviation, startYear, endYear, currentAbbreviation);
		Team savedTeam = modelDataService.saveTeam(team);
		
		return savedTeam;
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
	protected Division getDivision(String name){
		
		Division division = divisionCache.get(name);
		
		if (division == null){
			division = modelDataService.getDivisionByName(name, true);
			divisionCache.put(name, division);
		}
		
		return division;
	}
	
	/**
	 * 
	 * Creates a division with the given name and abbreviation.
	 * 
	 * @param name
	 * @param abbreviation
	 * @return
	 */
	protected Division createDivision(String name, String abbreviation){
		
		Division division = new Division(-1, name, abbreviation, null);
		Division savedDivision = modelDataService.saveDivision(division);
		
		return savedDivision;
	}
	
	/**
	 * 
	 * Gets the player division for the division, player name, and year.  It'll get it out of the cache if it's
	 * there and go to the database if it's not.
	 * 
	 * @param conferenceName
	 * @param name
	 * @return
	 */
	protected PlayerDivision getPlayerDivision(String divisionName, String playerName, String year){
		
		String playerDivisionKey = this.getPlayerDivisionKey(divisionName, playerName, year);
		PlayerDivision playerDivision = playerDivisionCache.get(playerDivisionKey);
		
		if (playerDivision == null){
			playerDivision = modelDataService.getPlayerDivision(divisionName, playerName, year);
			playerDivisionCache.put(playerDivisionKey, playerDivision);
		}
		
		return playerDivision;
	}
	
	/**
	 * 
	 * Creates a player division with the given division, player, and season.
	 * 
	 * @param division
	 * @param player
	 * @param season
	 * @return
	 */
	protected PlayerDivision createPlayerDivision(Division division, Player player, Season season){
		
		PlayerDivision playerDivision = new PlayerDivision(-1, division, player, season);
		PlayerDivision savedPlayerDivision = modelDataService.savePlayerDivision(playerDivision);
		
		return savedPlayerDivision;
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
			player = modelDataService.getPlayer(playerName);
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
			game = modelDataService.getGame(year, week, awayTeamAbbreviation, homeTeamAbbreviation);
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
	 * @param weekSequenceNumber
	 * @param awayTeamAbbreviation
	 * @param homeTeamAbbreviation
	 * @param winningTeamAbbreviation
	 * @param tie
	 * @return
	 */
	protected Game createGame(String year, String weekSequenceNumber, String awayTeamAbbreviation, String homeTeamAbbreviation, 
							  String winningTeamAbbreviation, boolean tie){
		
		Week week = getWeek(year, weekSequenceNumber);
		Team homeTeam = getTeam(homeTeamAbbreviation);
		Team awayTeam = getTeam(awayTeamAbbreviation);
		Team winningTeam = getTeam(winningTeamAbbreviation);

		Game game = new Game(-1, week.getId(), homeTeam, awayTeam, tie, winningTeam);
		
		game = modelDataService.saveGame(game);
		
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
			pick = modelDataService.getPick(playerName, year, Integer.parseInt(week), homeTeamAbbreviation, awayTeamAbbreviation);
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
	 * Gets the key for the given player division.
	 * 
	 * @param divisionName
	 * @param playerName
	 * @param year
	 * @return
	 */
	protected String getPlayerDivisionKey(String divisionName, String playerName, String year){
		return divisionName + "-" + playerName + "-" + year;
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
			
			Player player = modelDataService.getPlayer(playerName);
			
			if (player == null){
				log.info("Saving new player for " + playerName);
				player = new Player(-1, playerName);
				Player savedPlayer = modelDataService.savePlayer(player);
				
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
