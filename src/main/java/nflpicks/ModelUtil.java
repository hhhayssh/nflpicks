package nflpicks;

import java.util.ArrayList;
import java.util.List;

import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.Record;

public class ModelUtil {
	
	public static Pick getPick(List<Pick> picks, String playerName, int gameId){
		
		if (picks == null){
			return null;
		}
		
		for (int index = 0; index < picks.size(); index++){
			Pick pick = picks.get(index);
			
			String currentPlayerName = pick.getPlayer().getName();
			if (!playerName.equals(currentPlayerName)){
				continue;
			}
			
			if (pick.getGame().getId() == gameId){
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
