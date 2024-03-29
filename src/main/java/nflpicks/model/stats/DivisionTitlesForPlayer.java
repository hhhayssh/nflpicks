package nflpicks.model.stats;

import java.util.List;

import nflpicks.model.Player;

/**
 * 
 * Holds the division titles a player has so it's easy to show them.  Just like the
 * championships for player class.
 * 
 * @author albundy
 *
 */
public class DivisionTitlesForPlayer {

	protected Player player;
	
	protected List<DivisionTitle> divisionTitles;
	
	public DivisionTitlesForPlayer(){
	}
	
	public DivisionTitlesForPlayer(Player player, List<DivisionTitle> divisionTitles){
		this.player = player;
		this.divisionTitles = divisionTitles;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public List<DivisionTitle> getDivisionTitles() {
		return divisionTitles;
	}

	public void setDivisionTitles(List<DivisionTitle> divisionTitles) {
		this.divisionTitles = divisionTitles;
	}
	
	public String toString(){
		
		String thisObjectAsAString = "player = " + player + 
								     ", divisionTitles = " + divisionTitles;
		
		return thisObjectAsAString;
	}
}
