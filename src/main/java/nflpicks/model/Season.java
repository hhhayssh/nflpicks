package nflpicks.model;

import java.util.List;

/**
 * 
 * Represents a season that ties all the weeks together.  Not too much to it.
 * 
 * @author albundy
 *
 */
public class Season {
	
	/**
	 * The id of the season.
	 */
	protected int id;
	
	/**
	 * The year the season happened in.
	 */
	protected String year;
	
	/**
	 * The weeks in the season.
	 */
	protected List<Week> weeks;
	
	public Season(){
	}
	
	/**
	 * 
	 * A convenience constructor if all you have is the year.
	 * 
	 * @param year
	 */
	public Season(String year){
		this(-1, year, null);
	}
	
	/**
	 * 
	 * A convenience constructor so you don't have to use all the setters.
	 * 
	 * @param id
	 * @param year
	 * @param weeks
	 */
	public Season(int id, String year, List<Week> weeks){
		this.id = id;
		this.year = year;
		this.weeks = weeks;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	
	public List<Week> getWeeks(){
		return weeks;
	}
	
	public void setWeeks(List<Week> weeks){
		this.weeks = weeks;
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
		result = primeNumber * result + (year == null ? 0 : year.hashCode());
		result = primeNumber * result + (weeks == null ? 0 : weeks.hashCode());
		
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
		
		if (object == null || !(object instanceof Season)){
			return false;
		}
		
		Season otherSeason = (Season)object;
		
		int otherId = otherSeason.getId();
		
		if (id != otherId){
			return false;
		}
		
		String otherYear = otherSeason.getYear();
		
		if (year != null){
			if (!year.equals(otherYear)){
				return false;
			}
		}
		else {
			if (otherYear != null){
				return false;
			}
		}
		
		List<Week> otherWeeks = otherSeason.getWeeks();
		
		if (weeks != null){
			if (!weeks.equals(otherWeeks)){
				return false;
			}
		}
		else {
			if (otherWeeks != null){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * More crap.
	 * 
	 */
	@Override
	public String toString(){
		
		String thisObjectAsAString = "id = " + id + 
									 ", year = " + year +
									 ", weeks = " + weeks;
		
		return thisObjectAsAString;
	}
}
