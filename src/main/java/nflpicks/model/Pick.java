package nflpicks.model;

public class Pick {
	// id | game_id | player_id | team_id 

	protected int id;
	
	protected Game game;
	
	protected Player player;
	
	protected Team team;
	
	protected String result;
	
	public Pick(){
	}
	
	public Pick(int id, Game game, Player player, Team pickedTeam, String result){
		this.id = id;
		this.game = game;
		this.player = player;
		this.team = pickedTeam;
		this.result = result;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
	
	public String getResult(){
		return result;
	}
	
	public void setResult(String result){
		this.result = result;
	}
}
