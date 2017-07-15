package nflpicks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.Team;

public class NFLPicksDataUpdater {
	
	private static final Logger log = Logger.getLogger(NFLPicksServlet.class);
	
	protected NFLPicksDataService dataService;
	
	public static void main(String[] args){
		BasicConfigurator.configure();
		
		if (args.length != 1){
			System.out.println("bad");
			return;
		}
		
		String propertiesFilename = args[0];
		
		NFLPicksDataUpdater updater = new NFLPicksDataUpdater();
		
		updater.initialize(propertiesFilename);
		
		updater.update();
	}
	
	public void initialize(String propertiesFilename){
		log.info("Initializing updater...");
		ApplicationContext.getContext().initialize(propertiesFilename);
		dataService = new NFLPicksDataService(ApplicationContext.getContext().getDataSource());
		log.info("Done initializing updater.");
	}
	
	protected String readOption(String prompt, String errorPrompt, List<String> acceptableInputs, String quitInput, BufferedReader reader) throws Exception {
		
		System.out.print(prompt);
		
		String option = reader.readLine();
		
		if (acceptableInputs != null){
			while (!(acceptableInputs.contains(option) || quitInput.equals(option))){
				System.out.println(errorPrompt);
				System.out.print(prompt);
				
				option = reader.readLine();
			}
		}
		
		if (quitInput.equals(option)){
			option = null;
		}
		
		return option;
	}
	
	public void update(){
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(System.in));
			
			List<String> acceptableTypes = Arrays.asList(new String[]{"results", "picks"});
			String type = readOption("Type (results or picks): ", "Nice try.", acceptableTypes, "quit", reader);
			
			if (type == null){
				System.out.println("Bye");
				return;
			}
			
			if ("results".equals(type)){
				//year
				//week
				List<String> acceptableYears = Arrays.asList(new String[]{"2016", "2017"});
				String year = readOption("Year: ", "Blah", acceptableYears, "quit", reader);
				
				if (year == null){
					System.out.println("done");
					return;
				}
				
				List<String> acceptableWeeks = Arrays.asList(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17"});
				String week = readOption("Week: ", "x", acceptableWeeks, "quit", reader);
				
				if (week == null){
					System.out.println("quitting...");
					return;
				}
				
			
				int weekInt = Integer.parseInt(week);
				
				String winnersString = readOption("Winners: ", "none", null, "quit", reader);
				
				List<String> winners = Util.delimitedStringToList(winnersString, ",");
				
				for (int index = 0; index < winners.size(); index++){
					String winner = winners.get(index);
					
					Game game = dataService.getGame(year, weekInt, winner);
					
					Team winningTeam = dataService.getTeam(winner);
					
					if (game != null){
						System.out.println("blah");
					}
					else {
						System.out.println("q");
					}
					
					if (winningTeam != null){
						System.out.println("asdf");
					}
					else {
						System.out.println("asdfwer");
					}
					
					game.setWinningTeam(winningTeam);
					
					dataService.saveGame(game);
				}
				//Now we have the year, week, and winners.
				//For each winner, we just need to find the team, then find the game and update the winner.
			}
			else if ("picks".equals(type)){
				//year
				//week
				//player name
				//	if all, do a comma separated order
				
				List<String> acceptableYears = Arrays.asList(new String[]{"2016"});
				String year = readOption("Year: ", "Blah", acceptableYears, "quit", reader);
				
				if (year == null){
					System.out.println("done");
					return;
				}
				
				List<String> acceptableWeeks = Arrays.asList(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17"});
				String week = readOption("Week: ", "x", acceptableWeeks, "quit", reader);
				
				if (week == null){
					System.out.println("quitting...");
					return;
				}
				
				int weekInt = Integer.parseInt(week);
				
				String playersNamesString = readOption("Players (enter comma separated list of players): ", "y", null, "quit", reader);
				List<String> playerNames = Util.delimitedStringToList(playersNamesString, ",");
				
				List<Player> players = new ArrayList<Player>();
				
				for (int index = 0; index < playerNames.size(); index++){
					String playerName = playerNames.get(index);
					
					Player player = dataService.getPlayer(playerName);
					
					if (player == null){
						log.error("Couldn't find player with name = " + playerName);
						return;
					}
					
					players.add(player);
				}
				
				List<Game> games = new ArrayList<Game>();
				List<Team> teams = new ArrayList<Team>();
				String picksString = readOption("Picks (enter comma separated list of picks in the same order as the players): ", "y", null, "quit", reader);
				
				List<String> pickedTeamAbbreviations = Util.delimitedStringToList(picksString, ",");
				
				for (int index = 0; index < pickedTeamAbbreviations.size(); index++){
					String teamAbbreviation = pickedTeamAbbreviations.get(index);
					
					Game game = dataService.getGame(year, weekInt, teamAbbreviation);
					
					if (game == null){
						log.error("Couldn't find game! year = " + year + ", week = " + week + ", teamAbbreviation = " + teamAbbreviation);
						return;
					}
					games.add(game);
					
					Team team = dataService.getTeam(teamAbbreviation);
					teams.add(team);
				}
				
				for (int index = 0; index < players.size(); index++){
					Player player = players.get(index);
					Game game = games.get(index);
					Team team = teams.get(index);
					
					Pick pick = dataService.getPick(player.getName(), year, weekInt, game.getHomeTeam().getAbbreviation(), game.getAwayTeam().getAbbreviation());
					
					if (pick == null){
						pick = new Pick();
					}
					
					pick.setPlayer(player);
					pick.setGame(game);
					pick.setTeam(team);
					
					Pick savedPick = dataService.savePick(pick);
					
					if (savedPick != null){
						System.out.println("wow");
					}
				}
			}
			
		}
		catch (Exception e){
			log.error("Error reading options!", e);
		}
	}

}
