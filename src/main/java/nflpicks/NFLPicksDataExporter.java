package nflpicks;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;

import nflpicks.model.CompactPick;
import nflpicks.model.Player;

public class NFLPicksDataExporter {
	
	private static final Logger log = Logger.getLogger(NFLPicksDataExporter.class);
	
	protected NFLPicksDataService dataService;
	
	public static void main(String[] args) throws Exception {
		
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
		
		if (!Util.hasSomething(propertiesFilename)){
			System.out.println("No properties file given!  Unable to export!");
			return;
		}
		
		if (!Util.hasSomething(outputFilename)){
			String currentDate = DateUtil.formatDate(new Date(), DateUtil.DEFAULT_DATE_FORMAT_WITH_TIME);
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
	
	public void export(String filename){
		
		log.info("Exporting to " + filename + " ...");
		long start = System.currentTimeMillis();
		
		PrintWriter writer = null;
		
		try {
			log.info("Getting players...");
			List<Player> players = dataService.getPlayers();
			List<String> playerNames = ModelUtil.getPlayerNames(players);
			log.info("Got " + playerNames.size() + " players.");
			log.info("Getting picks...");
			long picksStart = System.currentTimeMillis();
			List<CompactPick> compactPicks = dataService.getCompactPicks();
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
	
	public String export(){
		
		List<Player> players = dataService.getPlayers();
		List<String> playerNames = ModelUtil.getPlayerNames(players);
		List<CompactPick> compactPicks = dataService.getCompactPicks();
		
		StringWriter writer = new StringWriter();
		
		writeAsCSV(playerNames, compactPicks, writer);
		
		String exportedPicks = writer.toString();
		
		return exportedPicks;
	}
	
	protected int writeAsCSV(List<String> playerNames, List<CompactPick> compactPicks, Writer writer){
		
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
				
				if (index > 0 && index % 20 == 0){
					writer.flush();
				}
				
				progress = (int)Math.ceil(((double)index / (double)totalLines) * 100.0);
				
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
	
	protected void writePickLine(Writer writer, CompactPick compactPick, List<String> playerNames) throws Exception {
		String year = compactPick.getYear();
		int weekNumber = compactPick.getWeekNumber();
		String awayTeamAbbreviation = compactPick.getAwayTeamAbbreviation();
		String homeTeamAbbreviation = compactPick.getHomeTeamAbbreviation();
		String winningTeamAbbreviation = compactPick.getWinningTeamAbbreviation();
		Map<String, String> playerPicksMap = compactPick.getPlayerPicks();
		
		List<String> playerPicks = new ArrayList<String>();
		
		for (int playerNameIndex = 0; playerNameIndex < playerNames.size(); playerNameIndex++){
			String playerName = playerNames.get(playerNameIndex);
			String playerPick = playerPicksMap.get(playerName);
			playerPicks.add(playerPick);
		}
		
		writeLine(writer, year, weekNumber, awayTeamAbbreviation, homeTeamAbbreviation, winningTeamAbbreviation, playerPicks);
	}
	
	protected void writeLine(Writer writer, String year, int weekNumber, String awayTeamAbbreviation, String homeTeamAbbreviation, 
							 String winningTeamAbbreviation, List<String> playerPicks) throws Exception {
		
		writer.write(Util.unNull(year));
		writer.write(',');
		writer.write(String.valueOf(weekNumber));
		writer.write(',');
		writer.write(Util.unNull(awayTeamAbbreviation));
		writer.write(',');
		writer.write(Util.unNull(homeTeamAbbreviation));
		writer.write(',');
		writer.write(Util.unNull(winningTeamAbbreviation));
		
		for (int index = 0; index < playerPicks.size(); index++){
			String playerPick = playerPicks.get(index);
			writer.write(',');
			writer.write(Util.unNull(playerPick));
		}
		
		writer.write('\n');
	}
}
