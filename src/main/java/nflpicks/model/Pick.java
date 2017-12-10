package nflpicks.model;

/**
 * 
 * Represents a pick a player made for a game.  Pretty much the whole point
 * of all this crap.  If the team is null, that means the player hasn't made a
 * pick for the game yet.
 * 
 * @author albundy
 *
 */
public class Pick {

	/**
	 * 
	 * The id of the pick.
	 * 
	 */
	protected int id;
	
	/**
	 * 
	 * The game the pick is for.
	 * 
	 */
	protected Game game;
	
	/**
	 * 
	 * The player who's making the pick.
	 * 
	 */
	protected Player player;
	
	/**
	 * 
	 * The team they picked.
	 * 
	 */
	protected Team team;
	
	/**
	 * 
	 * The result of the pick ... this could be derived by comparing the 
	 * team to the winning team of the game.  Here for convenience, like everything
	 * else in programming.
	 * 
	 * I chose not to make it derived because I figured it's usually best if the model
	 * objects are "dumb" so that they don't carry logic around when we might want that
	 * logic to be different in different situations.
	 * 
	 */
	protected String result;
	
	public Pick(){
	}
	
	/**
	 * 
	 * Lets you make a Pick object without calling all the setters.
	 * 
	 * @param id
	 * @param game
	 * @param player
	 * @param pickedTeam
	 * @param result
	 */
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
	
	/**
	 * 
	 * The hash code should turn this object into a relatively unique number
	 * so that it can be identified by that number easily and so (hopefully)
	 * there aren't that many "collisions" with other objects.  
	 * 
	 * It starts at a prime number repeatedly multiplies and adds the hash codes
	 * of the variables of the objects in this class.  I don't have that great of a handle
	 * on why it's done this way (check the internet if you care) but I know
	 * what it's trying to do.
	 * 
	 */
	@Override
	public int hashCode(){
		
		int primeNumber = 31;
		
		int result = 1;
		
		result = primeNumber * result + Integer.valueOf(id).hashCode();
		result = primeNumber * result + (game == null ? 0 : game.hashCode());
		result = primeNumber * result + (player == null ? 0 : player.hashCode());
		result = primeNumber * result + (team == null ? 0 : team.hashCode());
		result = primeNumber * result + (this.result == null ? 0 : this.result.hashCode());
		
		return result;
	}
	
	/**
	 * 
	 * Returns true if the given object has all the same values for all
	 * the variables in this object.
	 * 
	 */
	@Override
	public boolean equals(Object object){
		
		//Steps to do:
		//	1. If the given object is this object, it's equal.
		//	2. If it's null or isn't an instance of this class, it's not equal.
		//	3. Otherwise, just go down through each variable and return
		//	   false if it's not equal.
		//	4. If we get to the end, then all the variables "weren't not equal"
		//	   so that means the object is equal to this one.
		
		if (object == this){
			return true;
		}
		
		if (object == null || !(object instanceof Pick)){
			return false;
		}
		
		Pick otherPick = (Pick)object;
		
		int otherId = otherPick.getId();
		
		if (id != otherId){
			return false;
		}
		
		Game otherGame = otherPick.getGame();
		
		if (game != null){
			if (!game.equals(otherGame)){
				return false;
			}
		}
		else {
			if (otherGame != null){
				return false;
			}
		}
		
		Player otherPlayer = otherPick.getPlayer();
		
		if (player != null){
			if (!player.equals(otherPlayer)){
				return false;
			}
		}
		else {
			if (otherPlayer != null){
				return false;
			}
		}
		
		Team otherTeam = otherPick.getTeam();
		
		if (team != null){
			if (!team.equals(otherTeam)){
				return false;
			}
		}
		else {
			if (otherTeam != null){
				return false;
			}
		}
		
		String otherResult = otherPick.getResult();
		
		if (result != null){
			if (!result.equals(otherResult)){
				return false;
			}
		}
		else {
			if (otherResult != null){
				return false;
			}
		}
		
		return true;
	}

	/**
	 * 
	 * Blah blah blah...
	 * 
	 */
	@Override
	public String toString(){
		
		String thisObjectAsAString = "id = " + id + 
									 ", game = " + game +
									 ", player = " + player + 
									 ", team = " + team +
									 ", result = " + result;
		
		return thisObjectAsAString;
	}
}
