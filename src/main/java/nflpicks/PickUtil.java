package nflpicks;

import java.util.List;

import nflpicks.model.Game;
import nflpicks.model.Pick;

public class PickUtil {
	
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

}
