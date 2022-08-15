package nflpicks.model.stats;

import nflpicks.model.Season;

/**
 * 
 * Here for holding the record of a "collective group"
 * 
 * @author albundy
 *
 */
public class CollectiveRecord {
	
	protected Season season;
	
	/**
	 * How many wins they have.
	 */
	protected int wins;
	
	/**
	 * How many L's they took.
	 */
	protected int losses;
	
	/**
	 * How many times they got lucky.
	 */
	protected int ties;
	
	
	public CollectiveRecord(){
	}

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
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
		
		String thisObjectAsAString = "wins = " + wins +
								     ", losses = " + losses + 
								     ", ties = " + ties;
		
		return thisObjectAsAString;
	}

}
