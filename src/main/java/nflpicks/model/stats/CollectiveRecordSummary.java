package nflpicks.model.stats;

import java.util.List;

public class CollectiveRecordSummary {
	
	protected List<CollectiveRecord> collectiveRecords;
	
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

	public CollectiveRecordSummary(){
	}

	public List<CollectiveRecord> getCollectiveRecords() {
		return collectiveRecords;
	}

	public void setCollectiveRecords(List<CollectiveRecord> collectiveRecords) {
		this.collectiveRecords = collectiveRecords;
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
		
		String thisObjectAsAString = "collectiveRecords = " + collectiveRecords + 
									 ", wins = " + wins + 
									 ", losses = " + losses +
									 ", ties = " + ties;
		
		return thisObjectAsAString;
	}
}
