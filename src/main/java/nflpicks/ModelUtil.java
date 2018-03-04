package nflpicks;

import java.util.ArrayList;
import java.util.List;

import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.Record;

/**
 * 
 * Here to hold "utility" functions for working with different kinds of model objects.
 * Here so that the "utility" functions for different model classes can be referenced
 * in one place and so they can easily refer to each other without needing an instance.
 * 
 * @author albundy
 *
 */
public class ModelUtil {
	
	/**
	 * 
	 * Gets the pick that the given player name for the given game from the given list.
	 * If it doesn't find the game in the list of picks or the player didn't make a pick
	 * for the game, it'll return null.
	 * 
	 * @param picks
	 * @param playerName
	 * @param gameId
	 * @return
	 */
	public static Pick getPick(List<Pick> picks, String playerName, int gameId){
		
		//Steps to do:
		//	1. Go through all the picks and look for ones made by the given player.
		//	2. If we find one that's for the game and was picked by the given player,
		//	   that's the one we're looking for.
		//	3. If we go through it all, that means the pick doesn't exist in the list.
		
		if (picks == null || playerName == null){
			return null;
		}
		
		for (int index = 0; index < picks.size(); index++){
			Pick pick = picks.get(index);
			
			int currentGameId = pick.getGame().getId();
			String currentPlayerName = pick.getPlayer().getName();
			
			if (gameId == currentGameId && playerName.equals(currentPlayerName)){
				return pick;
			}
		}
		
		return null;
	}

	/**
	 * 
	 * Gets the pick from the given list that's from the given player and matches either the home team, away team,
	 * or winning team abbreviation (it checks in that order).  If it can't find a pick that matches that, it'll return
	 * null.
	 * 
	 * @param picks
	 * @param playerName
	 * @param homeTeamAbbreviation
	 * @param awayTeamAbbreviation
	 * @param winningTeamAbbreviation
	 * @return
	 */
	public static Pick getPick(List<Pick> picks, String playerName, String homeTeamAbbreviation, String awayTeamAbbreviation, String winningTeamAbbreviation){
		
		if (picks == null || playerName == null){
			return null;
		}
		
		//Steps to do:
		//	1. Go through all the picks and check who made each one.
		//	2. If the player matches what we were given, then check if the game
		//	   has one of the teams and return it if it does.
		//	3. If we go through the whole list, then that means it wasn't in the list.
		
		for (int index = 0; index < picks.size(); index++){
			Pick pick = picks.get(index);
			Player player = pick.getPlayer();
			String pickPlayerName = player.getName();
			
			if (playerName.equals(pickPlayerName)){
				Game game = pick.getGame();
				
				String pickHomeTeam = game.getHomeTeam().getAbbreviation();
				String pickAwayTeam = game.getAwayTeam().getAbbreviation();
				String winningTeam = pick.getTeam() != null ? pick.getTeam().getAbbreviation() : null;
				
				if (homeTeamAbbreviation != null && homeTeamAbbreviation.equals(pickHomeTeam)){
					return pick;
				}
				
				if (awayTeamAbbreviation != null && awayTeamAbbreviation.equals(pickAwayTeam)){
					return pick;
				}
				
				if (winningTeamAbbreviation != null && winningTeamAbbreviation.equals(winningTeam)){
					return pick;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * This function will get the "result" we should use based on the winning team
	 * and the team that got picked. ... Yeah, completely stupid.  It's here so I only
	 * have to do this in one place and can "derive" whether it's a win or a loss based on
	 * the pick and the winning team.  Probably a stupid decision to do it like that, but, hey,
	 * what can I say, I'm a stupid person.
	 * 
	 * If they match, it'll return W.  If they don't, it'll return L.  If the winning team abbreviation
	 * is "TIE", it'll return T.  If either one is null, it'll return null.
	 * 
	 * @param winningTeamAbbreviation
	 * @param pickAbbreviation
	 * @return
	 */
	public static String getPickResult(String winningTeamAbbreviation, String pickAbbreviation){
		
		if (winningTeamAbbreviation == null || pickAbbreviation == null){
			return null;
		}
		
		//Steps to do:
		//	1. If the winning team was the "tie" team, then it was a tie.
		//	2. Otherwise, if they're the same, the result was a win.
		//	3. And if they're different, it was a loss.
		
		String pickResult = null;
		
		if (NFLPicksConstants.TIE_TEAM_ABBREVIATION.equals(winningTeamAbbreviation)){
			pickResult = NFLPicksConstants.RESULT_TIE;
		}
		else {
			if (winningTeamAbbreviation.equals(pickAbbreviation)){
				pickResult = NFLPicksConstants.RESULT_WIN;
			}
			else {
				pickResult = NFLPicksConstants.RESULT_LOSS;
			}
		}
		
		return pickResult;
	}
	
	/**
	 * 
	 * This function checks to see whether there are any ties in the list
	 * of records.  Kind of dumb, but sometimes we need to add a column or
	 * do something if we're showing records and there are ties.  If there
	 * aren't ties, we usually leave the tie part off.
	 * 
	 * @param records
	 * @return
	 */
	public static boolean areThereAnyTies(List<Record> records){
		
		if (records == null){
			return false;
		}
		
		for (int index = 0; index < records.size(); index++){
			Record record = records.get(index);
			
			if (record.getTies() > 0){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * This function gets the largest number of wins for the given records.
	 * Not much to it.
	 * 
	 * @param records
	 * @return
	 */
	public static int getTopWins(List<Record> records){
		
		if (records == null){
			return -1;
		}
		
		int topWins = -1;
		
		for (int index = 0; index < records.size(); index++){
			Record record = records.get(index);
			
			if (record.getWins() > topWins){
				topWins = record.getWins();
			}
		}
		
		return topWins;
	}
	
	/**
	 * 
	 * A stupid function that gets the record for the player with the give name
	 * from the list of records.  Here because I'm a moron.
	 * 
	 * And, yeah, I realize I could be using java 8 streams to do it like this:
	 * 
	 * 	records.stream().filter(p -> p.getName() == playerName).collect(Collectors.toList());
	 * 
	 * But I don't feel like doing it that way and, since it's my project, I'm not going to.
	 * 
	 * @param records
	 * @param playerName
	 * @return
	 */
	public static Record getRecordForPlayer(List<Record> records, String playerName){
		
		if (records == null || playerName == null){
			return null;
		}
		
		for (int index = 0; index < records.size(); index++){
			Record record = records.get(index);
			Player player = record.getPlayer();
			String recordPlayerName = player.getName();
			
			if (playerName.equals(recordPlayerName)){
				return record;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * Another dumb function that should be a one liner using java 8 streams.  It
	 * gets the names of all the players in the given list.  
	 * 
	 * @param players
	 * @return
	 */
	public static List<String> getPlayerNames(List<Player> players){
		
		if (players == null){
			return null;
		}
		
		List<String> playerNames = new ArrayList<String>();
		
		for (int index = 0; index < players.size(); index++){
			Player player = players.get(index);
			String playerName = player.getName();
			playerNames.add(playerName);
		}
		
		return playerNames;
		
		//need a programming language that only deals with the basic data structures and types like int, string, long...
	}
	
	public static String getWeekLabelForWeekNumber(int weekNumber){
		
		String weekLabel = null;
		
		if (weekNumber <= 17){
			weekLabel = "Week " + weekNumber;
		}
		else {
			if (weekNumber == 18){
				weekLabel = "Wildcard";
			}
			else if (weekNumber == 19){
				weekLabel = "Divisional";
			}
			else if (weekNumber == 20){
				weekLabel = "Conference Championship";
			}
			else if (weekNumber == 21){
				weekLabel = "Superbowl";
			}
		}
		
		return weekLabel;
		
	}
}
