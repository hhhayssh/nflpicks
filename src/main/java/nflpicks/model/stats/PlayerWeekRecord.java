package nflpicks.model.stats;

import nflpicks.model.Player;
import nflpicks.model.Record;
import nflpicks.model.Season;
import nflpicks.model.Week;

public class PlayerWeekRecord {
	
	protected Player player;
	
	protected Season season;
	
	protected Week week;
	
	protected Record record;
	
	public PlayerWeekRecord(){
	}
	
	public PlayerWeekRecord(Player player, Season season, Week week, Record record){
		this.player = player;
		this.season = season;
		this.week = week;
		this.record = record;
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

	public Week getWeek() {
		return week;
	}

	public void setWeek(Week week) {
		this.week = week;
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}
	
	public String toString(){
		
		String thisObjectAsAString = "player = " + player +
									 ", season = " + season + 
									 ", week = " + week + 
									 ", record = " + record;
		
		return thisObjectAsAString;
	}

}
