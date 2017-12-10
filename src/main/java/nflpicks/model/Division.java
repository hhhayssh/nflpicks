package nflpicks.model;

import java.util.List;

/**
 * 
 * Represents the division that a team is in.  Each division
 * is in a conference.
 * 
 * @author albundy
 *
 */
public class Division {
	
	/**
	 * The division's id.
	 */
	protected int id;
	
	/**
	 * The conference the division is in.
	 */
	protected int conferenceId;
	
	/**
	 * The name of the division.
	 */
	protected String name;
	
	/**
	 * The teams in the division.
	 */
	protected List<Team> teams;
	
	public Division(){
	}
	
	/**
	 * 
	 * A convenience constructor for making a division without using all the setters.
	 * 
	 * @param id
	 * @param conferenceId
	 * @param name
	 * @param teams
	 */
	public Division(int id, int conferenceId, String name, List<Team> teams){
		this.id = id;
		this.conferenceId = conferenceId;
		this.name = name;
		this.teams = teams;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getConferenceId() {
		return conferenceId;
	}

	public void setConferenceId(int conferenceId) {
		this.conferenceId = conferenceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<Team> getTeams(){
		return teams;
	}
	
	public void setTeams(List<Team> teams){
		this.teams = teams;
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
		result = primeNumber * result + Integer.valueOf(conferenceId).hashCode();
		result = primeNumber * result + (name == null ? 0 : name.hashCode());
		result = primeNumber * result + (teams == null ? 0 : teams.hashCode());
		
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
		
		if (object == null || !(object instanceof Division)){
			return false;
		}
		
		Division otherDivision = (Division)object;
		
		int otherId = otherDivision.getId();
		
		if (id != otherId){
			return false;
		}
		
		int otherConferenceId = otherDivision.getConferenceId();
		
		if (conferenceId != otherConferenceId){
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
		
		List<Team> otherTeams = otherDivision.getTeams();
		
		if (teams != null){
			if (!teams.equals(otherTeams)){
				return false;
			}
		}
		else {
			if (otherTeams != null){
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
									 ", conferenceId = " + conferenceId +
									 ", name = " + name + 
									 ", teams = " + teams;
		
		return thisObjectAsAString;
	}
}
