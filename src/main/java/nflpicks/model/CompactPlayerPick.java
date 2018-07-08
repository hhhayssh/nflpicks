package nflpicks.model;

public class CompactPlayerPick {

	protected String player;
	
	protected String pick;
	
	public CompactPlayerPick(){
	}
	
	public CompactPlayerPick(String player, String pick){
		this.player = player;
		this.pick = pick;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getPick() {
		return pick;
	}

	public void setPick(String pick) {
		this.pick = pick;
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
		
		if (object == null || !(object instanceof CompactPickGrid)){
			return false;
		}
		
		CompactPlayerPick otherCompactPick = (CompactPlayerPick)object;
		
		String otherPlayer = otherCompactPick.getPlayer();
		
		if (player != null){
			if (!player.equals(otherPlayer)){
				return false;
			}
		}
		else {
			if (player != null){
				return false;
			}
		}
		
		String otherPick = otherCompactPick.getPick();
		
		if (pick != null){
			if (!pick.equals(otherPick)){
				return false;
			}
		}
		else {
			if (pick != null){
				return false;
			}
		}
		
		return true;
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
		result = primeNumber * result + (pick == null ? 0 : pick.hashCode());
		
		return result;
	}
	
	public String toString(){
		
		String thisObjectAsAString = "player = " + player + 
									 ", pick = " + pick;
		
		return thisObjectAsAString;
	}
}
