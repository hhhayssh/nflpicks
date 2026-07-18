package nflpicks.model.stats;

import nflpicks.model.Player;

/*
season_id,
	   week_id,
	   player_id,
	   sum(chalk_count) as total_chalk_count,
	   sum(chalk_right_count) as chalk_right_count,
	   sum(chalk_wrong_count) as chalk_wrong_count,
	   sum(genius_count) as total_genius_count,
	   sum(genius_right_count) as genius_right_count,
	   sum(genius_wrong_count) as genius_wrong_count
 */

public class ChalkGeniusRecord {
	
	protected Player player;
	
	protected int totalChalkCount;
	
	protected int chalkRightCount;
	
	protected int chalkWrongCount;
	
	protected int totalGeniusCount;
	
	protected int geniusRightCount;
	
	protected int geniusWrongCount;
	
	public ChalkGeniusRecord() {
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getTotalChalkCount() {
		return totalChalkCount;
	}

	public void setTotalChalkCount(int totalChalkCount) {
		this.totalChalkCount = totalChalkCount;
	}

	public int getChalkRightCount() {
		return chalkRightCount;
	}

	public void setChalkRightCount(int chalkRightCount) {
		this.chalkRightCount = chalkRightCount;
	}

	public int getChalkWrongCount() {
		return chalkWrongCount;
	}

	public void setChalkWrongCount(int chalkWrongCount) {
		this.chalkWrongCount = chalkWrongCount;
	}

	public int getTotalGeniusCount() {
		return totalGeniusCount;
	}

	public void setTotalGeniusCount(int totalGeniusCount) {
		this.totalGeniusCount = totalGeniusCount;
	}

	public int getGeniusRightCount() {
		return geniusRightCount;
	}

	public void setGeniusRightCount(int geniusRightCount) {
		this.geniusRightCount = geniusRightCount;
	}

	public int getGeniusWrongCount() {
		return geniusWrongCount;
	}

	public void setGeniusWrongCount(int geniusWrongCount) {
		this.geniusWrongCount = geniusWrongCount;
	}
	
	public String toString() {
		
		String thisObjectAsAString = "player = " + player +
									 ", totalChalkCount = " + totalChalkCount + 
									 ", chalkRightCount = " + chalkRightCount + 
									 ", chalkWrongCount = " + chalkWrongCount + 
									 ", totalGeniusCount = " + totalGeniusCount +
									 ", geniusRightCount = " + geniusRightCount +
									 ", geniusWrongCount = " + geniusWrongCount;
		
		return thisObjectAsAString;
	}

}
