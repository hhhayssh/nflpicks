package nflpicks.model.stats;

import nflpicks.model.Division;
import nflpicks.model.Player;
import nflpicks.model.Record;
import nflpicks.model.Season;

/**
 * 
 * Here to hold the fact that a player won a particular division in a
 * particular year.
 * 
 * @author albundy
 *
 */
public class DivisionTitle {
	
	protected Division division;
	
	protected Player player;
	
	protected Season season;
	
	protected Record record;
	
	public DivisionTitle(){
	}
	
	public DivisionTitle(Division division, Player player, Season season, Record record){
		this.division = division;
		this.player = player;
		this.season = season;
		this.record = record;
	}

	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
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

	public String toString(){
		
		String thisObjectAsAString = "division = " + division + 
									 ", player = " + player + 
									 ", season = " + season + 
									 ", record = " + record;
		
		return thisObjectAsAString;
	}
}
