package nflpicks.model;

import java.util.ArrayList;
import java.util.List;

public class CompactPickGrid {

	//Should make a class called CompactPickGrid
			//has:
			//	players
			//		playerId
			//		name
			//	games
			//		game
			//			year
			//			weekNumber
			//			awayTeam
			//			homeTeam
			//			winningTeam
			//			picks
			//				playerId
			//				pickedTeam
	
	protected List<String> playerNames;
	
	protected List<CompactPick> picks;
	
	public CompactPickGrid(){
		this.playerNames = new ArrayList<String>();
		this.picks = new ArrayList<CompactPick>();
	}

	public List<CompactPick> getPicks() {
		return picks;
	}

	public void setPicks(List<CompactPick> picks) {
		this.picks = picks;
	}

	public List<String> getPlayerNames() {
		return playerNames;
	}

	public void setPlayerNames(List<String> playerNames) {
		this.playerNames = playerNames;
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
		
		CompactPickGrid otherCompactPickGrid = (CompactPickGrid)object;
		
		List<String> otherPlayerNames = otherCompactPickGrid.getPlayerNames();
		
		if (playerNames != null){
			if (!playerNames.equals(otherPlayerNames)){
				return false;
			}
		}
		else {
			if (playerNames != null){
				return false;
			}
		}
		
		List<CompactPick> otherPicks = otherCompactPickGrid.getPicks();
		
		if (picks != null){
			if (!picks.equals(otherPicks)){
				return false;
			}
		}
		else {
			if (picks != null){
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
		
		result = primeNumber * result + (playerNames == null ? 0 : playerNames.hashCode());
		result = primeNumber * result + (picks == null ? 0 : picks.hashCode());
		
		return result;
	}
	
	public String toString(){
		
		String thisObjectAsAString = "playerNames = " + playerNames + 
									 ", picks = " + picks;
		
		return thisObjectAsAString;
	}
}
