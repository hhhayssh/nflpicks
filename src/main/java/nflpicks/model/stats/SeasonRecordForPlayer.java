package nflpicks.model.stats;

import nflpicks.model.Player;
import nflpicks.model.Record;
import nflpicks.model.Season;

public class SeasonRecordForPlayer {
	
	protected Player player;
	
	protected Season season;
	
	protected Record record;
	
	protected boolean championship;
	
	public SeasonRecordForPlayer(){
	}
	
	public SeasonRecordForPlayer(Player player, Season season, Record record, boolean championship){
		this.player = player;
		this.season = season;
		this.record = record;
		this.championship = championship;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}

	public boolean getChampionship() {
		return championship;
	}

	public void setChampionship(boolean championship) {
		this.championship = championship;
	}
	
	public String toString(){
		
		String thisObjectAsAString = "player = " + player + 
									 ", season = " + season + 
									 ", record = " + record +
									 ", championship = " + championship;
		
		return thisObjectAsAString;
	}
}
