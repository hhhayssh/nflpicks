package nflpicks.model;

public class Record {
	
	protected Player player;
	
	protected int wins;
	
	protected int losses;
	
	protected int ties;
	
	public Record(){
	}
	
	public Record(Player player, int wins, int losses, int ties){
		this.player = player;
		this.wins = wins;
		this.ties = ties;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getLosses() {
		return losses;
	}

	public void setLosses(int losses) {
		this.losses = losses;
	}

	public int getTies() {
		return ties;
	}

	public void setTies(int ties) {
		this.ties = ties;
	}
	
	public String toString(){
		
		String thisObjectAsAString = "player = " + player + 
									 ", wins = " + wins +
									 ", losses = " + losses + 
									 ", ties = " + ties;
		
		return thisObjectAsAString;
	}
}
