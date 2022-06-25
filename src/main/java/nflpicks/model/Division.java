package nflpicks.model;

import java.util.List;

/**
 * 
 * This represents a division with players in it, not a division with teams.
 * Because I assume people will change divisions more than teams, I made a separate
 * "PlayerDivision" object to link players, divisions, and seasons.
 * 
 * @author albundy
 *
 */
public class Division {

	protected int id;
	
	protected String name;
	
	protected String abbreviation;
	
	protected List<Player> players;
	
	public Division(){
	}
	
	public Division(int id, String name, String abbreviation){
		this(id, name, abbreviation, null);
	}
	
	public Division(int id, String name, String abbreviation, List<Player> players){
		this.id = id;
		this.name = name;
		this.abbreviation = abbreviation;
		this.players = players;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
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
		result = primeNumber * result + (name == null ? 0 : name.hashCode());
		result = primeNumber * result + (abbreviation == null ? 0 : abbreviation.hashCode());
		result = primeNumber * result + (players == null ? 0 : players.hashCode());
		
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
		
		if (object == null || !(object instanceof TeamDivision)){
			return false;
		}
		
		Division otherDivision = (Division)object;
		
		int otherId = otherDivision.getId();
		
		if (id != otherId){
			return false;
		}
		
		String otherName = otherDivision.getName();
		
		if (name != null){
			if (!name.equals(otherName)){
				return false;
			}
		}
		else {
			if (otherName != null){
				return false;
			}
		}
		
		String otherAbbreviation = otherDivision.getAbbreviation();
		
		if (abbreviation != null){
			if (!abbreviation.equals(otherAbbreviation)){
				return false;
			}
		}
		else {
			if (otherAbbreviation != null){
				return false;
			}
		}
		
		List<Player> otherPlayers = otherDivision.getPlayers();
		
		if (players != null){
			if (!players.equals(otherPlayers)){
				return false;
			}
		}
		else {
			if (otherPlayers != null){
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
									 ", name = " + name + 
									 ", abbreviation = " + abbreviation + 
									 ", players = " + players;
		
		return thisObjectAsAString;
	}
}
