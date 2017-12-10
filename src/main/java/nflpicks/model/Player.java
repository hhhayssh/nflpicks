package nflpicks.model;

/**
 * 
 * Represents a player ... Like Spare Tire.  Look it up.
 * 
 * @author albundy
 *
 */
public class Player {
	
	/**
	 * 
	 * The id of the player ... kind of sucks having a player with
	 * an id because it reduces the person but I went with it because it makes
	 * more sense.  I might change it back to being player name so that people never
	 * get an id assigned to them.
	 * 
	 */
	protected int id;
	
	/**
	 * 
	 * The name of the player blah blah blah.
	 * 
	 */
	protected String name;
	
	public Player(){
	}
	
	/**
	 * 
	 * Lets you make a player without using all the setters.
	 * 
	 * @param id
	 * @param name
	 */
	public Player(int id, String name){
		this.id = id;
		this.name = name;
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
		
		if (object == null || !(object instanceof Player)){
			return false;
		}
		
		Player otherPlayer = (Player)object;
		
		int otherId = otherPlayer.getId();
		
		if (id != otherId){
			return false;
		}
		
		String otherName = otherPlayer.getName();
		
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
		
		return true;
	}
	
	/**
	 * 
	 * Sends back a string with all the variables in this object.
	 * 
	 */
	@Override
	public String toString(){
		
		String thisObjectAsAString = "id = /*PEOPLE SHOULND'T HAVE IDS!!!* " + id + 
									 ", name = " + name;
		
		return thisObjectAsAString;
	}
}
