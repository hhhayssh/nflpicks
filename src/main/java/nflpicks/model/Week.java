package nflpicks.model;

import java.util.List;

public class Week {

	protected int id;
	
	protected int seasonId;
	
	protected int week;
	
	protected String label;
	
	protected List<Game> games;
	
	public Week(){
	}
	
	public Week(int id, int seasonId, int week, String label, List<Game> games){
		this.id = id;
		this.seasonId = seasonId;
		this.week = week;
		this.label = label;
		this.games = games;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(int seasonId) {
		this.seasonId = seasonId;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Game> getGames() {
		return games;
	}

	public void setGames(List<Game> games) {
		this.games = games;
	}
	
	public String toString(){
		
		String thisObjectAsAString = "id = " + id + 
									 ", seasonId = " + seasonId +
									 ", week = " + week +
									 ", label = " + label +
									 ", games = " + games;
		
		return thisObjectAsAString;
	}
}
