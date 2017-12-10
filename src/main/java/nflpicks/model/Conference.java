package nflpicks.model;

import java.util.List;

/**
 * 
 * Represents the conferences teams are in.  Hey nerds, a conference is made up
 * of divisions and divisions are made up of teams.
 * 
 * @author albundy
 *
 */
public class Conference {

	/**
	 * The conference's id.
	 */
	protected int id;
	
	/**
	 * AFC or NFC
	 */
	protected String name;
	
	/**
	 * The divisions the conference has.
	 */
	protected List<Division> divisions;
	
	public Conference(){
	}

	/**
	 * 
	 * A convenience constructor for making a conference without
	 * calling all the setters.
	 * 
	 * @param id
	 * @param name
	 * @param divisions
	 */
	public Conference(int id, String name, List<Division> divisions){
		this.id = id;
		this.name = name;
		this.divisions = divisions;
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
	
	public List<Division> getDivisions() {
		return divisions;
	}

	public void setDivisions(List<Division> divisions) {
		this.divisions = divisions;
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
		result = primeNumber * result + (divisions == null ? 0 : divisions.hashCode());
		
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
		
		if (object == null || !(object instanceof Conference)){
			return false;
		}
		
		Conference otherConference = (Conference)object;
		
		int otherId = otherConference.getId();
		
		if (id != otherId){
			return false;
		}
		
		String otherName = otherConference.getName();
		
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
		
		List<Division> otherDivisions = otherConference.getDivisions();
		
		if (divisions != null){
			if (!divisions.equals(otherDivisions)){
				return false;
			}
		}
		else {
			if (otherDivisions != null){
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
									 ", divisions = " + divisions;
		
		return thisObjectAsAString;
	}
}
