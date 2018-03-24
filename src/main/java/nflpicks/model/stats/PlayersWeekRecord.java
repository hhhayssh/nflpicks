package nflpicks.model.stats;

import java.util.List;

import nflpicks.model.Player;
import nflpicks.model.Record;
import nflpicks.model.Season;
import nflpicks.model.Week;

public class PlayersWeekRecord {
	
	protected List<Player> players;
	
	protected Season season;
	
	protected Week week;
	
	protected Record record;
	
	public PlayersWeekRecord(){
	}
	
	public PlayersWeekRecord(List<Player> players, Season season, Week week, Record record){
		this.players = players;
		this.season = season;
		this.week = week;
		this.record = record;
	}

	public List<Player> getPlayer() {
		return players;
	}

	public void setPlayer(List<Player> players) {
		this.players = players;
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
		
		String thisObjectAsAString = "players = " + players +
									 ", season = " + season + 
									 ", week = " + week + 
									 ", record = " + record;
		
		return thisObjectAsAString;
	}
}
