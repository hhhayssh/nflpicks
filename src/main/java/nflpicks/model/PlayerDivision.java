package nflpicks.model;

/**
 * 
 * This represents the relationship between a player and a division.  I assume that'll
 * change a decent number of times, but shouldn't change (hopefully) within a season, so
 * I'm basically saying a player should be in a division for a full year.
 * 
 * @author albundy
 *
 */
public class PlayerDivision {
	
	protected int id;
	
	protected Division division;
	
	protected Player player;
	
	protected Season season;
	
	public PlayerDivision(){
	}
	
	public PlayerDivision(int id, Division division, Player player, Season season){
		this.id = id;
		this.division = division;
		this.player = player;
		this.season = season;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		result = primeNumber * result + (player == null ? 0 : player.hashCode());
		result = primeNumber * result + (division == null ? 0 : division.hashCode());
		result = primeNumber * result + (season == null ? 0 : season.hashCode());
		
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
		
		if (object == null || !(object instanceof PlayerDivision)){
			return false;
		}
		
		PlayerDivision otherPlayerDivision = (PlayerDivision)object;
		
		int otherId = otherPlayerDivision.getId();
		
		if (id != otherId){
			return false;
		}
		
		Division otherDivision = otherPlayerDivision.getDivision();
		
		if (division != null){
			if (!division.equals(otherPlayerDivision)){
				return false;
			}
		}
		else {
			if (otherPlayerDivision != null){
				return false;
			}
		}
		
		Player otherPlayer = otherPlayerDivision.getPlayer();
		
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
		
		Season otherSeason = otherPlayerDivision.getSeason();
		
		if (season != null){
			if (!season.equals(otherSeason)){
				return false;
			}
		}
		else {
			if (otherSeason != null){
				return false;
			}
		} 
		
		return true;
	}
	
	/**
	 * 
	 * Returns the string version of this object.
	 * 
	 */
	@Override
	public String toString(){
		
		String thisObjectAsAString = "id = " + id + 
									 ", division = " + division + 
									 ", player = " + player + 
									 ", season = " + season;
		
		return thisObjectAsAString;
	}

}
