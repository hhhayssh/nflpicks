package nflpicks.model;

/**
 * 
 * Represents a what Al Bundy scored 4 touchdowns in.  
 * 
 * A game happens between two teams on a certain week and ends up in a 
 * winning team.  If there's no winning team and the "tie" flag is false, that means the 
 * game hasn't happened yet.  If the "tie" flag is true, that means there was a tie.
 * 
 * The week holds the reference to the season and year and ties it to the rest of the games.
 * 
 * @author albundy who scored 4 touchdowns in one game for polk high.
 *
 */
public class Game {
	
	/**
	 * 
	 * The id of the game.
	 * 
	 */
	protected int id;
	
	/**
	 * 
	 * The id of the week that the game happened in.
	 * 
	 */
	protected int weekId;

	/**
	 * 
	 * The team that was at home.
	 * 
	 */
	protected Team homeTeam;
	
	/**
	 * 
	 * The away team.
	 * 
	 */
	protected Team awayTeam;
	
	/**
	 * 
	 * Whether there was a tie or not.  If this is true, there was and if it's
	 * false, there wasn't.
	 * 
	 */
	protected boolean tie;

	/**
	 * 
	 * The team that won.  If this is null, and tie is false, that means the game
	 * hasn't happened yet.  Otherwise, if it's not null, this is the team that won.
	 * 
	 */
	protected Team winningTeam;
	
	public Game(){
	}
	
	/**
	 * 
	 * A convenience constructor for if you want to make a game but it doesn't
	 * have a result yet.
	 * 
	 * @param weekId
	 * @param homeTeam
	 * @param awayTeam
	 */
	public Game(int weekId, Team homeTeam, Team awayTeam){
		this(-1, weekId, homeTeam, awayTeam, false, null);
	}
	
	/**
	 * 
	 * Lets you make a game object without calling all the setters.
	 * 
	 * @param id
	 * @param weekId
	 * @param homeTeam
	 * @param awayTeam
	 * @param tie
	 * @param winningTeam
	 */
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
	
	/**
	 * 
	 * This function says whether this game has a result or not.
	 * It does if it's a tie or there's a winning team.  Otherwise, it doesn't.
	 * 
	 * @return
	 */
	public boolean hasResult(){
		
		if (tie || winningTeam != null){
			return true;
		}
		
		return false;
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
		result = primeNumber * result + Integer.valueOf(weekId).hashCode();
		result = primeNumber * result + (homeTeam == null ? 0 : homeTeam.hashCode());
		result = primeNumber * result + (awayTeam == null ? 0 : awayTeam.hashCode());
		result = primeNumber * result + Boolean.valueOf(tie).hashCode();
		result = primeNumber * result + (winningTeam == null ? 0 : winningTeam.hashCode());
		
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
		
		if (object == null || !(object instanceof Game)){
			return false;
		}
		
		Game otherGame = (Game)object;
		
		int otherId = otherGame.getId();
		
		if (id != otherId){
			return false;
		}
		
		int otherWeekId = otherGame.getWeekId();
		
		if (weekId != otherWeekId){
			return false;
		}
		
		Team otherHomeTeam = otherGame.getHomeTeam();
		
		if (homeTeam != null){
			if (!homeTeam.equals(otherHomeTeam)){
				return false;
			}
		}
		else {
			if (otherHomeTeam != null){
				return false;
			}
		}
		
		Team otherAwayTeam = otherGame.getAwayTeam();
		
		if (awayTeam != null){
			if (!awayTeam.equals(otherAwayTeam)){
				return false;
			}
		}
		else {
			if (otherAwayTeam != null){
				return false;
			}
		}
		
		boolean otherTie = otherGame.getTie();
		
		if (tie != otherTie){
			return false;
		}
		
		Team otherWinningTeam = otherGame.getWinningTeam();
		
		if (winningTeam != null){
			if (!winningTeam.equals(otherWinningTeam)){
				return false;
			}
		}
		else {
			if (otherWinningTeam != null){
				return false;
			}
		}
		
		return true;
	}

	/**
	 * 
	 * Just sends back all the variables in this object as a string.
	 * 
	 */
	@Override
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
