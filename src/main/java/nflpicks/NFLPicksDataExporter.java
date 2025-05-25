package nflpicks;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nflpicks.model.CompactPick;
import nflpicks.model.CompactPlayerPick;
import nflpicks.model.Division;
import nflpicks.model.Player;
import nflpicks.model.PlayerDivision;
import nflpicks.model.Season;
import nflpicks.model.Team;
import nflpicks.model.TeamConference;
import nflpicks.model.TeamDivision;

/**
 * 
 * This class will export the picks from the database.  It's made so that it
 * can be ran on its own (with a main function) or ran by another class.
 * 
 * It exports them in the format they were in back when I used to write them
 * on paper, where it was just the game, winner, and then each person's picks.
 * 
 * It's here so we can have an archive of the picks outside of the database so 
 * everything can be rebuilt if it needs to be.
 * 
 * @author albundy
 *
 */
public class NFLPicksDataExporter {
	
	private static final Log log = LogFactory.getLog(NFLPicksDataExporter.class);
	
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
	 * Here so you can run this outside of any webapp or anything.  To do that, you have to:
	 * 
	 * 		1. Have the nflpicks jar and dependencies on the class path.
	 * 		2. Call this function.
	 * 
	 * The arguments it expects are:
	 * 
	 * 		1. required - The full path to the properties file.
	 * 		2. optional - The full path to where you want it to write the output file.
	 * 		   If this isn't given, it defaults to the current directory with the filename
	 * 		   "nflpicks-export-yyyy-mm-dd-HH-mm-ss.csv".
	 * 
	 * If no arguments are given, it'll ask for those two things on the command line.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		//Steps to do:
		//	1. If we weren't given any arguments, ask for them on the command line.
		//	2. Otherwise, pull them from what we were given.
		//	3. If we don't hvae a properties file, we can't do anything.
		//	4. If we don't have an output file, just use the default.
		//	5. Run it!
		
		String propertiesFilename = null;
		String outputFilename = null;
		
		if (args.length == 0){
			Scanner scanner = new Scanner(System.in);
			
			System.out.println("Properties file:");
			propertiesFilename = scanner.nextLine();
			
			System.out.println("Output filename:");
			outputFilename = scanner.nextLine();
			
			scanner.close();
		}
		else {
			if (args.length >= 1){
				propertiesFilename = args[0];

				if (args.length == 2){
					outputFilename = args[1];
				}
			}
		}

		//Can't do anything without the properties file, which has the connection to the database.
		if (!Util.hasSomething(propertiesFilename)){
			System.out.println("No properties file given!  Unable to export!");
			return;
		}
		
		//If we don't have anything for the output file, just set it to the default in the current directory.
		if (!Util.hasSomething(outputFilename)){
			String currentDate = DateUtil.formatDate(new Date(), DateUtil.DEFAULT_DATE_FORMAT_WITH_TIME);
			outputFilename = "nflpicks-export-" + currentDate + ".csv";
		}
		
		//Now that we're here, we just have to initialize all the variables in the application context,
		//create the object that'll let us pull out the data, and run it.

		ApplicationContext.getContext().initialize(propertiesFilename);
		
		NFLPicksModelDataService modelDataService = new NFLPicksModelDataService(ApplicationContext.getContext().getDataSource());
		
		NFLPicksStatsDataService statsDataService = new NFLPicksStatsDataService(ApplicationContext.getContext().getDataSource(), modelDataService);
		
		NFLPicksDataExporter exporter = new NFLPicksDataExporter(modelDataService, statsDataService);
		
		exporter.exportPicksData(outputFilename);
	}
	
	/**
	 * 
	 * Creates an exporter that'll pull the data using the given service.  This is the only
	 * constructor because this class doesn't make sense without a data service right now.
	 * 
	 * @param modelDataService
	 * @param statsDataService
	 */
	public NFLPicksDataExporter(NFLPicksModelDataService modelDataService, NFLPicksStatsDataService statsDataService){
		this.modelDataService = modelDataService;
		this.statsDataService = statsDataService;
	}
	
	/**
	 * 
	 * This function will export the conference, division, and team data to the given file.
	 * The exported file will have all the data it needs to rebuild all the stuff again.
	 * 
	 * @param filename
	 */
	public void exportTeamData(String filename){
		
		//Steps to do:
		//	1. To export the team data, we should just be able to do a "deep" retrieve
		//	   on the conferences and they should have everything in them.
		//	2. Each line should be a team and so that's the level where we'll print at.
		
		log.info("Exporting team data to " + filename + " ...");
		long start = System.currentTimeMillis();
		
		PrintWriter writer = null;
		int lineNumber = 0;
		
		try {
			writer = new PrintWriter(filename);
			
			String header = "conference_name,current_conference_name,conference_start_year,conference_end_year,division_name,current_division_name,division_start_year,division_end_year,team_city,team_nickname,team_abbreviation,team_start_year,team_end_year,current_team_abbreviation";

			writer.print(header);
			writer.print('\n');
			
			List<TeamConference> conferences = modelDataService.getTeamConferences(false);

			for (int conferenceIndex = 0; conferenceIndex < conferences.size(); conferenceIndex++){
				TeamConference conference = conferences.get(conferenceIndex);

				String conferenceName = conference.getName();
				String currentConferenceName = conference.getCurrentName();
				String conferenceStartYear = conference.getStartYear();
				String conferenceEndYear = conference.getEndYear();
				
				List<TeamDivision> divisions = conference.getDivisions();
				
				for (int divisionIndex = 0; divisionIndex < divisions.size(); divisionIndex++){
					TeamDivision division = divisions.get(divisionIndex);

					String divisionName = division.getName();
					String currentDivisionName = division.getCurrentName();
					String divisionStartYear = division.getStartYear();
					String divisionEndYear = division.getEndYear();
					
					List<Team> teams = division.getTeams();

					for (int teamIndex = 0; teamIndex < teams.size(); teamIndex++){
						Team team = teams.get(teamIndex);
						
						String teamCity = team.getCity();
						String teamNickname = team.getNickname();
						String teamAbbreviation = team.getAbbreviation();
						String teamStartYear = team.getStartYear();
						String teamEndYear = team.getEndYear();
						String currentTeamAbbreviation = team.getCurrentAbbreviation();
						
						List<String> values = Arrays.asList(new String[]{conferenceName, currentConferenceName, conferenceStartYear, conferenceEndYear,
																		 divisionName, currentDivisionName, divisionStartYear, divisionEndYear,
																		 teamCity, teamNickname, teamAbbreviation, teamStartYear, teamEndYear, currentTeamAbbreviation});
						
						String line = Util.toCsvString(values);
						writer.print(line);
						writer.print('\n');
						lineNumber++;
					}
					
					writer.flush();
				}
				
				writer.flush();
			}
			
			writer.flush();
		}
		catch (Exception e){
			log.error("Error exporting team data!  lineNumber = " + lineNumber + ", filename = " + filename, e);
		}
		finally {
			Util.closeWriter(writer);
		}
		
		long elapsed = System.currentTimeMillis() - start;
		log.info("Done exporting team data.  Took " + elapsed + " ms to export to " + filename);
	}
	
	/**
	 * 
	 * This function will export the picks to the given file.  Not much to it!
	 * 
	 * @param filename
	 */
	public void exportPicksData(String filename){
		
		//Steps to do:
		//	1. To export, we need two things: the players and the picks.
		//	2. The players say what will be in the header (the game teams,
		//	   the winning team, and then all the player names).
		//	   The picks say what the games are, the winning team, and what
		//	   each player picked.
		//	3. We can make everything we need for the csv using those two things, so
		//	   get them from the database.
		//	4. Use them to write the csv file.
		
		log.info("Exporting picks to " + filename + " ...");
		long start = System.currentTimeMillis();
		
		PrintWriter writer = null;
		
		try {
			log.info("Getting players...");
			List<Player> players = modelDataService.getPlayers();
			List<String> playerNames = ModelUtil.getPlayerNames(players);
			log.info("Got " + playerNames.size() + " players.");
			
			log.info("Getting picks...");
			long picksStart = System.currentTimeMillis();
			List<CompactPick> compactPicks = statsDataService.getCompactPicks();
			long picksElapsed = System.currentTimeMillis() - picksStart;
			log.info("Got " + compactPicks.size() + " picks.  Took " + picksElapsed + " ms.");
			
			writer = new PrintWriter(filename);
			
			log.info("Writing picks...");
			long writeStart = System.currentTimeMillis();
			int numberOfLines = writeAsCSV(playerNames, compactPicks, writer);
			long writeElapsed = System.currentTimeMillis() - writeStart;
			log.info("Wrote " + numberOfLines + " lines.  Took " + writeElapsed + " ms.");
		}
		catch (Exception e){
			log.error("Error exporting data to file!  filename = " + filename, e);
		}
		finally {
			Util.closeWriter(writer);
		}
		
		long elapsed = System.currentTimeMillis() - start;
		log.info("Done exporting.  Took " + elapsed + " ms to export to " + filename);
	}
	
	/**
	 * 
	 * This function will export all the pick data to a string and return that string.
	 * It's here so another class can get the csv string that we write to a file as a string
	 * (so it can send it back to a browser or something).
	 * 
	 * @return
	 */
	public String exportPicksData(){
		
		//Steps to do:
		//	1. Just like with the other export function, we just need the players
		//	   and picks to do the export.
		//	2. Once we get those, we can do the export.
		
		long start = System.currentTimeMillis();
		
		log.info("Starting export to a string...");
		
		log.info("Getting players...");
		long playersStart = System.currentTimeMillis();
		List<Player> players = modelDataService.getPlayers();
		long playersElapsed = System.currentTimeMillis() - playersStart;
		log.info("Got " + players.size() + " players in " + playersElapsed + " ms.");
		List<String> playerNames = ModelUtil.getPlayerNames(players);

		log.info("Getting picks...");
		long picksStart = System.currentTimeMillis();
		List<CompactPick> compactPicks = statsDataService.getCompactPicks();
		long picksElapsed = System.currentTimeMillis() - picksStart;
		log.info("Got " + compactPicks.size() + " in " + picksElapsed + " ms.");

		log.info("Writing picks to a string...");
		long writeStart = System.currentTimeMillis();
		StringWriter writer = new StringWriter();
		int numberOfLines = writeAsCSV(playerNames, compactPicks, writer);
		String exportedPicks = writer.toString();
		long writeElapsed = System.currentTimeMillis() - writeStart;
		log.info("Wrote " + numberOfLines + " lines to a string in " + writeElapsed + " ms.");
		
		long elapsed = System.currentTimeMillis() - start;
		log.info("Finished exporting picks to a string in " + elapsed + " ms.");
		
		return exportedPicks;
	}
	
	/**
	 * 
	 * This function will write the picks for the given players using the given writer.  The writer is passed
	 * in so we can use the same function to write to a file or to a string (so we can handle writing to a file
	 * or writing to something we're going to send back as a download with the same function).
	 * 
	 * It'll return the number of lines it writes.  If there's an error, it'll return -1.
	 * 
	 * The csv will be like this:
	 * 
	 * 		Year,Week,Away,Home,Winner,PlayerName1,PlayerName2,PlayerName3,...
	 * 		2017,1,BUF,NE,BUF,BUF,NE,NE
	 * 		2017,1,CHI,IND,CHI,IND,CHI,CHI
	 * 		...
	 * 
	 * It will have the year, week, teams, and picks of each player for each game.  We can rebuild
	 * everything again off that information.
	 * 
	 * @param playerNames
	 * @param compactPicks
	 * @param writer
	 * @return
	 */
	protected int writeAsCSV(List<String> playerNames, List<CompactPick> compactPicks, Writer writer){
		
		if (playerNames == null || compactPicks == null){
			return -1;
		}
		
		//Steps to do:
		//	1. Write the header with the "standard" columns and the player names.
		//	2. Go through each pick and write a line for it.
		//	3. That's it.
		
		int lineCount = 0;
		
		try {
			StringBuilder headerStringBuilder = new StringBuilder("Year,Week,Away,Home,Winner");

			for (int index = 0; index < playerNames.size(); index++){
				String playerName = playerNames.get(index);
				headerStringBuilder.append(",").append(playerName);
			}

			String header = headerStringBuilder.toString();

			lineCount++;
			writer.write(header);
			writer.write('\n');
			writer.flush();
			
			int totalLines = compactPicks.size();
			int progress = 0;
			int lastProgress = 0;
			
			for (int index = 0; index < compactPicks.size(); index++){
				CompactPick compactPick = compactPicks.get(index);
				
				writePickLine(writer, compactPick, playerNames);
				lineCount++;
				
				//Flush every so often ... 20 lines seems to be ok.
				if (index > 0 && index % 20 == 0){
					writer.flush();
				}
				
				//So we can show the progress of the export in the log.
				progress = (int)Math.floor(((double)index / (double)totalLines) * 100.0);
				
				//Only show the progress every 5%.
				if (progress >= (lastProgress + 5)){
					log.info("Wrote " + index + " of " + totalLines + " (" + progress + "%)");
					lastProgress = progress;
				}
			}
			
			writer.flush();
			Util.closeWriter(writer);
		}
		catch (Exception e){
			log.error("Error writing compact pick data as csv!", e);
		}
		
		return lineCount;
	}
	
	/**
	 * 
	 * This function will write a single line using the given writer for the given pick.
	 * 
	 * The line will have these "columns": Year, week, away team, home team, winning team
	 * and then each of the picks from each of the players in the order that they're in
	 * in the given playerNames list.
	 * 
	 * @param writer
	 * @param compactPick
	 * @param playerNames
	 * @throws Exception
	 */
	protected void writePickLine(Writer writer, CompactPick compactPick, List<String> playerNames) throws Exception {
		
		//Steps to do:
		//	1. Pull out all the info we need.
		//	2. Get the picks in the order that we're given the player names.
		//	3. Write it out.
		
		String year = compactPick.getYear();
		int weekSequenceNumber = compactPick.getWeekSequenceNumber();
		String awayTeamAbbreviation = compactPick.getAwayTeamAbbreviation();
		String homeTeamAbbreviation = compactPick.getHomeTeamAbbreviation();
		String winningTeamAbbreviation = compactPick.getWinningTeamAbbreviation();
		List<CompactPlayerPick> playerPicks = compactPick.getPlayerPicks();
		
		List<String> picks = new ArrayList<String>();
		
		for (int playerNameIndex = 0; playerNameIndex < playerNames.size(); playerNameIndex++){
			String playerName = playerNames.get(playerNameIndex);
			
			for (int pickIndex = 0; pickIndex < playerPicks.size(); pickIndex++){
				CompactPlayerPick playerPick = playerPicks.get(pickIndex);
				
				if (playerName.equals(playerPick.getPlayer())){
					picks.add(playerPick.getPick());
				}
			}
		}
		
		writeLine(writer, year, weekSequenceNumber, awayTeamAbbreviation, homeTeamAbbreviation, winningTeamAbbreviation, picks);
	}
	
	/**
	 * 
	 * This function will write out all the info using the given writer.  It'll write the year, week,
	 * away team, home team, winning team, and then all the player picks (in the order they're given.
	 * 
	 * @param writer
	 * @param year
	 * @param weekSequenceNumber
	 * @param awayTeamAbbreviation
	 * @param homeTeamAbbreviation
	 * @param winningTeamAbbreviation
	 * @param playerPicks
	 * @throws Exception
	 */
	protected void writeLine(Writer writer, String year, int weekSequenceNumber, String awayTeamAbbreviation, String homeTeamAbbreviation, 
							 String winningTeamAbbreviation, List<String> playerPicks) throws Exception {
		
		//Steps to do:
		//	1. Just write all the stuff we were given.
		//	2. Write the player picks in the order we got them.
		
		//unNull because I'm a moron.
		writer.write(Util.toEmptyStringIfNull(year));
		writer.write(',');
		writer.write(String.valueOf(weekSequenceNumber));
		writer.write(',');
		writer.write(Util.toEmptyStringIfNull(awayTeamAbbreviation));
		writer.write(',');
		writer.write(Util.toEmptyStringIfNull(homeTeamAbbreviation));
		writer.write(',');
		writer.write(Util.toEmptyStringIfNull(winningTeamAbbreviation));
		
		for (int index = 0; index < playerPicks.size(); index++){
			String playerPick = playerPicks.get(index);
			writer.write(',');
			writer.write(Util.toEmptyStringIfNull(playerPick));
		}
		
		writer.write('\n');
	}
	
	/**
	 * 
	 * This function will export the division data to the given file.
	 * The exported file will have all the data it needs to rebuild all the stuff again.
	 * 
	 * @param filename
	 */
	public void exportDivisionData(String filename){
		
		//Steps to do:
		//	1. To export the team data, we should just be able to do a "deep" retrieve
		//	   on the conferences and they should have everything in them.
		//	2. Each line should be a team and so that's the level where we'll print at.
		
		log.info("Exporting division data to " + filename + " ...");
		long start = System.currentTimeMillis();
		
		PrintWriter writer = null;
		int lineNumber = 0;
		
		try {
			writer = new PrintWriter(filename);
			
			String header = "division,abbreviation";

			writer.print(header);
			writer.print('\n');
			
			List<Division> divisions = modelDataService.getDivisions();

			for (int index = 0; index < divisions.size(); index++){
				Division division = divisions.get(index);

				String divisionName = division.getName();
				String abbreviation = division.getAbbreviation();

				List<String> values = Arrays.asList(new String[]{divisionName, abbreviation});

				String line = Util.toCsvString(values);
				writer.print(line);
				writer.print('\n');
				lineNumber++;

				writer.flush();
			}
			
			writer.flush();
		}
		catch (Exception e){
			log.error("Error exporting division data!  lineNumber = " + lineNumber + ", filename = " + filename, e);
		}
		finally {
			Util.closeWriter(writer);
		}
		
		long elapsed = System.currentTimeMillis() - start;
		log.info("Done exporting division data.  Took " + elapsed + " ms to export to " + filename);
	}
	
	/**
	 * 
	 * This function will export the player division data to the given file.
	 * The exported file will have all the data it needs to rebuild all the stuff again.
	 * 
	 * @param filename
	 */
	public void exportPlayerDivisionData(String filename){
		
		//Steps to do:
		//	1. To export the team data, we should just be able to do a "deep" retrieve
		//	   on the conferences and they should have everything in them.
		//	2. Each line should be a team and so that's the level where we'll print at.
		
		log.info("Exporting player division data to " + filename + " ...");
		long start = System.currentTimeMillis();
		
		PrintWriter writer = null;
		int lineNumber = 0;
		
		try {
			writer = new PrintWriter(filename);
			
			String header = "division,player,year";

			writer.print(header);
			writer.print('\n');
			
			List<PlayerDivision> playerDivisions = modelDataService.getPlayerDivisions();

			for (int index = 0; index < playerDivisions.size(); index++){
				PlayerDivision playerDivision = playerDivisions.get(index);

				Division division = playerDivision.getDivision();
				Player player = playerDivision.getPlayer();
				Season season = playerDivision.getSeason();
				
				String divisionName = division.getName();
				String playerName = player.getName();
				String year = season.getYear();

				List<String> values = Arrays.asList(new String[]{divisionName, playerName, year});

				String line = Util.toCsvString(values);
				writer.print(line);
				writer.print('\n');
				lineNumber++;

				writer.flush();
			}
			
			writer.flush();
		}
		catch (Exception e){
			log.error("Error exporting player division data!  lineNumber = " + lineNumber + ", filename = " + filename, e);
		}
		finally {
			Util.closeWriter(writer);
		}
		
		long elapsed = System.currentTimeMillis() - start;
		log.info("Done exporting player division data.  Took " + elapsed + " ms to export to " + filename);
	}
}
