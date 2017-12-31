package nflpicks.model;

import java.util.List;

/**
 * 
 * Represents a week that happens during in a season.  It has a list of games
 * and the season ties it to other weeks during the season.
 * 
 * @author albundy
 *
 */
public class Week {

	/**
	 * The id of the week.  This isn't the week's number.
	 */
	protected int id;
	
	/**
	 * The season the week happened in.
	 */
	protected int seasonId;
	
	/**
	 * The year the week happened in.
	 */
	protected String year;
	
	/**
	 * The number of the week, starting from 1 and going through the superbowl (which is week 21).
	 */
	protected int weekNumber;
	
	/**
	 * The label (like Week 1 or AFC Championship) of the week.  Here because the week number doesn't
	 * make sense once we hit the playoffs.
	 */
	protected String label;
	
	/**
	 * The games that were played in the week.
	 */
	protected List<Game> games;
	
	public Week(){
	}
	
	/**
	 * 
	 * A convenience constructor so you don't have to use the setters to make an object.
	 * 
	 * @param id
	 * @param seasonId
	 * @param week
	 * @param label
	 * @param games
	 */
	public Week(int id, int seasonId, String year, int week, String label, List<Game> games){
		this.id = id;
		this.seasonId = seasonId;
		this.year = year;
		this.weekNumber = week;
		this.label = label;
		this.games = games;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(int seasonId) {
		this.seasonId = seasonId;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getWeekNumber() {
		return weekNumber;
	}

	public void setWeekNumber(int weekNumber) {
		this.weekNumber = weekNumber;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Game> getGames() {
		return games;
	}

	public void setGames(List<Game> games) {
		this.games = games;
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
		result = primeNumber * result + Integer.valueOf(seasonId).hashCode();
		result = primeNumber * result + (year == null ? 0 : year.hashCode());
		result = primeNumber * result + Integer.valueOf(weekNumber).hashCode();
		result = primeNumber * result + (label == null ? 0 : label.hashCode());
		result = primeNumber * result + (games == null ? 0 : games.hashCode());
		
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
		
		if (object == null || !(object instanceof Week)){
			return false;
		}
		
		Week otherWeek = (Week)object;
		
		int otherId = otherWeek.getId();
		
		if (id != otherId){
			return false;
		}
		
		int otherSeasonId = otherWeek.getSeasonId();
		
		if (seasonId != otherSeasonId){
			return false;
		}
		
		String otherYear = otherWeek.getYear();
		
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
		
		int otherWeekNumber = otherWeek.getWeekNumber();
		
		if (weekNumber != otherWeekNumber){
			return false;
		}
		
		String otherLabel = otherWeek.getLabel();
		
		if (label != null){
			if (!label.equals(otherLabel)){
				return false;
			}
		}
		else {
			if (otherLabel != null){
				return false;
			}
		}
		
		List<Game> otherGames = otherWeek.getGames();
		
		if (games != null){
			if (!games.equals(otherGames)){
				return false;
			}
		}
		else {
			if (otherGames != null){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * Asdf asdf, asdfasdfasdf asdf asdf. ... asdf.
	 * 
	 */
	@Override
	public String toString(){
		
		String thisObjectAsAString = "id = " + id + 
									 ", seasonId = " + seasonId +
									 ", year = " + year +
									 ", weekNumber = " + weekNumber +
									 ", label = " + label +
									 ", games = " + games;
		
		return thisObjectAsAString;
	}
}
