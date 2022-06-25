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
public class TeamConference {

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
	protected List<TeamDivision> divisions;
	
	/**
	 * The year the conference started.
	 */
	protected String startYear;
	
	/**
	 * If the conference was changed, this is the year it ended.
	 */
	protected String endYear;
	
	/**
	 * The current name of the conference, if it's changed and is linked to a current
	 * one.
	 */
	protected String currentName;
	
	public TeamConference(){
	}

	/**
	 * 
	 * A convenience constructor for making a conference without
	 * calling all the setters.
	 * 
	 * @param id
	 * @param name
	 * @param divisions
	 * @param startYear
	 * @param endYear
	 * @param currentName
	 */
	public TeamConference(int id, String name, List<TeamDivision> divisions, String startYear, String endYear, String currentName){
		this.id = id;
		this.name = name;
		this.divisions = divisions;
		this.startYear = startYear;
		this.endYear = endYear;
		this.currentName = currentName;
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
	
	public List<TeamDivision> getDivisions() {
		return divisions;
	}

	public void setDivisions(List<TeamDivision> divisions) {
		this.divisions = divisions;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	public String getEndYear() {
		return endYear;
	}

	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}
	
	public String getCurrentName() {
		return currentName;
	}

	public void setCurrentName(String currentName) {
		this.currentName = currentName;
	}

	/**
	 * 
	 * A convenience function for figuring out whether this conference is active or not
	 * without having to do "endYear != null".
	 * 
	 * @return
	 */
	public boolean isActive(){
		
		if (endYear != null){
			return false;
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
		
		result = primeNumber * result + Integer.valueOf(id).hashCode();
		result = primeNumber * result + (name == null ? 0 : name.hashCode());
		result = primeNumber * result + (divisions == null ? 0 : divisions.hashCode());
		result = primeNumber * result + (startYear == null ? 0 : startYear.hashCode());
		result = primeNumber * result + (endYear == null ? 0 : endYear.hashCode());
		result = primeNumber * result + (currentName == null ? 0 : currentName.hashCode());
		
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
		
		if (object == null || !(object instanceof TeamConference)){
			return false;
		}
		
		TeamConference otherConference = (TeamConference)object;
		
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
		
		List<TeamDivision> otherDivisions = otherConference.getDivisions();
		
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
		
		String otherStartYear = otherConference.getStartYear();
		
		if (startYear != null){
			if (!startYear.equals(otherStartYear)){
				return false;
			}
		}
		else {
			if (otherStartYear != null){
				return false;
			}
		}
		
		String otherEndYear = otherConference.getEndYear();
		
		if (endYear != null){
			if (!endYear.equals(otherEndYear)){
				return false;
			}
		}
		else {
			if (otherEndYear != null){
				return false;
			}
		}
		
		String otherCurrentName = otherConference.getCurrentName();
		
		if (currentName != null){
			if (!currentName.equals(otherCurrentName)){
				return false;
			}
		}
		else {
			if (otherCurrentName != null){
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
									 ", divisions = " + divisions + 
									 ", startYear = " + startYear + 
									 ", endYear = " + endYear + 
									 ", currentName = " + currentName;
		
		return thisObjectAsAString;
	}
}
