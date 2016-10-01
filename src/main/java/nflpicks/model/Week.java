package nflpicks.model;

import java.util.List;

public class Week {

	protected int id;
	
	protected int seasonId;
	
	protected int week;
	
	protected String description;
	
	protected List<Game> games;
	
	public Week(){
	}
	
	public Week(int id, int seasonId, int week, String description, List<Game> games){
		this.id = id;
		this.seasonId = seasonId;
		this.week = week;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Game> getGames() {
		return games;
	}

	public void setGames(List<Game> games) {
		this.games = games;
	}
}
