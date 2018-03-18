package nflpicks.model.stats;

import java.util.List;

import nflpicks.model.Player;

public class PlayerChampionships {

	protected Player player;
	
	protected List<Championship> championships;
	
	public PlayerChampionships(){
	}
	
	public PlayerChampionships(Player player, List<Championship> championships){
		this.player = player;
		this.championships = championships;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public List<Championship> getChampionships() {
		return championships;
	}

	public void setChampionships(List<Championship> championships) {
		this.championships = championships;
	}
	
	public String toString(){
		
		String thisObjectAsAString = "player = " + player + 
								     ", championships = " + championships;
		
		return thisObjectAsAString;
	}
}
