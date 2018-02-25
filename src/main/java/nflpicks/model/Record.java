package nflpicks.model;

/**
 * 
 * Represents a the record that a player has.  It's pretty generic.  It isn't tied
 * to any "time period", just has wins, losses, and ties.  That way we can have records
 * for different time periods.
 * 
 * @author albundy
 *
 */
public class Record {
	
	/**
	 * 
	 * Who the record is for.
	 * 
	 */
	protected Player player;
	
	/**
	 * How many wins they have.
	 */
	protected int wins;
	
	/**
	 * How many L's they took.
	 */
	protected int losses;
	
	/**
	 * How many times they got lucky.
	 */
	protected int ties;
	
	public Record(){
	}
	
	/**
	 * 
	 * A convenience function so you don't have to use all the setters.
	 * 
	 * @param player
	 * @param wins
	 * @param losses
	 * @param ties
	 */
	public Record(Player player, int wins, int losses, int ties){
		this.player = player;
		this.wins = wins;
		this.losses = losses;
		this.ties = ties;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getLosses() {
		return losses;
	}

	public void setLosses(int losses) {
		this.losses = losses;
	}

	public int getTies() {
		return ties;
	}

	public void setTies(int ties) {
		this.ties = ties;
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
		
		result = primeNumber * result + (player == null ? 0 : player.hashCode());
		result = primeNumber * result + Integer.valueOf(wins).hashCode();
		result = primeNumber * result + Integer.valueOf(losses).hashCode();
		result = primeNumber * result + Integer.valueOf(ties).hashCode();
		
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
		
		if (object == null || !(object instanceof Record)){
			return false;
		}
		
		Record otherRecord = (Record)object;
		
		Player otherPlayer = otherRecord.getPlayer();
		
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
		
		int otherWins = otherRecord.getWins();
		if (wins != otherWins){
			return false;
		}
		
		int otherLosses = otherRecord.getLosses();
		if (losses != otherLosses){
			return false;
		}
		
		int otherTies = otherRecord.getTies();
		if (ties != otherTies){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * The record written out.
	 * 
	 */
	@Override
	public String toString(){
		
		String thisObjectAsAString = "player = " + player + 
									 ", wins = " + wins +
									 ", losses = " + losses + 
									 ", ties = " + ties;
		
		return thisObjectAsAString;
	}
}
