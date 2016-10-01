package nflpicks.model;

public class Game {
	
	protected int id;
	
	protected int weekId;
	//  id  | week_id | home_team_id | away_team_id | home_team_score | away_team_score 
	protected Team homeTeam;
	
	protected Team awayTeam;

	protected int winningTeamId;
	
	public Game(){
	}
	
	public Game(int id, int weekId, Team homeTeam, Team awayTeam, int winningTeamId){
		this.id = id;
		this.weekId = weekId;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.winningTeamId = winningTeamId;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getWeekId(){
		return weekId;
	}
	
	public void setWeekId(int weekId){
		this.weekId = weekId;
	}

	public Team getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(Team homeTeam) {
		this.homeTeam = homeTeam;
	}

	public Team getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(Team awayTeam) {
		this.awayTeam = awayTeam;
	}

	public int getWinningTeamId(){
		return winningTeamId;
	}
	
	public void setWinningTeamId(int winningTeamId){
		this.winningTeamId = winningTeamId;
	}
}
