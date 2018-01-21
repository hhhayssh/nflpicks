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
	
	public static Pick getPick(List<Pick> picks, String player, String homeTeamAbbreviation, String awayTeamAbbreviation, String winningTeamAbbreviation){
		
		for (int index = 0; index < picks.size(); index++){
			Pick pick = picks.get(index);
			
			if (player.equals(pick.getPlayer().getName())){
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
	
	public static String getPickResult(String winningTeamAbbreviation, String pickAbbreviation){
		
		if (winningTeamAbbreviation == null || pickAbbreviation == null){
			return null;
		}
		
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
	
	public static Record getRecordForPlayer(List<Record> records, String playerName){
		
		if (records == null || playerName == null){
			return null;
		}
		
		for (int index = 0; index < records.size(); index++){
			Record record = records.get(index);
			
			if (playerName.equals(record.getPlayer().getName())){
				return record;
			}
		}
		
		return null;
	}
	
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

}
