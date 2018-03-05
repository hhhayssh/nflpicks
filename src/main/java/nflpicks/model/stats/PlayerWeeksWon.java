package nflpicks.model.stats;

import java.util.List;

import nflpicks.model.Player;

//the level we want the player at changes
//it should be at the top level of whatever we have
//
//	record - wins, losses, ties
//	week record - season, week, and record
//	player week record - player and a week record
//
//don't want to duplicate a player but it would be nice to
//don't like the name PlayerWeekRecord
//then this should be PlayerWeeksWon

//maybe WeekRecord, ties a week to a record
//		season, week, record
//YearRecord ties a year to a record
//		season, record
//PlayerRecord ties a player to a week record where week could be null
//should there be reuse?  PlayerWeekRecord - player, WeekRecord
//	could make it easier to do TeamWeekRecord - team, WeekRecord
//	choices:
//		playerRecord.weekRecord.season
//		playerRecord.season
//		playerRecord.week
//		playerRecord.player
//	... better if it's not nested
//PlayerYearRecord
//		player, season, record
//PlayerWeekRecord
//		player, season, week, record
//PlayerPlayoffRecord
//		player, season, record
public class PlayerWeeksWon {
	
	protected Player player;
	
	protected List<WeekRecord> weekRecords;

	public PlayerWeeksWon(){
	}
	
	public PlayerWeeksWon(Player player){
		this(player, null);
	}
	
	public PlayerWeeksWon(Player player, List<WeekRecord> weekRecords){
		this.player = player;
		this.weekRecords = weekRecords;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public List<WeekRecord> getWeekRecords() {
		return weekRecords;
	}

	public void setWeekRecords(List<WeekRecord> weekRecords) {
		this.weekRecords = weekRecords;
	}
	
	public String toString(){
		
		String thisObjectAsAString = "player = " + player + 
								     ", weekRecords = " + weekRecords;
		
		return thisObjectAsAString;
	}
}
