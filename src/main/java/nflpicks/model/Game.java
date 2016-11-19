package nflpicks.model;

public class Game {
	
	protected int id;
	
	protected int weekId;
	//  id  | week_id | home_team_id | away_team_id | home_team_score | away_team_score 
	protected Team homeTeam;
	
	protected Team awayTeam;
	
	protected boolean tie;

	protected Team winningTeam;
	
	public Game(){
	}
	
	public Game(int id, int weekId, Team homeTeam, Team awayTeam, boolean tie, Team winningTeam){
		this.id = id;
		this.weekId = weekId;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.tie = tie;
		this.winningTeam = winningTeam;
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
	
	public boolean getTie(){
		return tie;
	}
	
	public void setTie(boolean tie){
		this.tie = tie;
	}
	
	public Team getWinningTeam(){
		return winningTeam;
	}
	
	public void setWinningTeam(Team winningTeam){
		this.winningTeam = winningTeam;
	}

	
	public String toString(){
		
		String thisObjectAsAString = "id = " + id + 
									 ", weekId = " + weekId +
									 ", homeTeam = " + homeTeam + 
									 ", awayTeam = " + awayTeam +
									 ", tie = " + tie + 
									 ", winningTeam = " + winningTeam;
		
		return thisObjectAsAString;
	}
}
