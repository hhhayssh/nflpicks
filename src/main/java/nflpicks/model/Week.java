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
	 * The sequence number of the week within the season, starting from 1 and going through the superbowl (which was week 21 ... and now week 22).
	 */
	protected int sequenceNumber;
	
	/**
	 * What kind of week it is (REGULAR_SEASON or PLAYOFFS).
	 */
	protected String type;
	
	/**
	 * The "key" for the week.  This should be something like "1" or "divisional" or "superbowl".  Here so we can
	 * get a week based on its "key" no matter what its sequence number is.  Didn't need this before Goodell and the owners
	 * got greedy and made a week 18, screwing everything up.  This way we'll be able to query for a week in the playoffs
	 * like the "wildcard" whether it happened in week 18 (before 2021) or week 19 (2021 and after).
	 */
	protected String key;
	
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
	 * A convenience constructor for when you have everything but the week id (you're making a week object
	 * from other stuff).
	 * 
	 * @param seasonId
	 * @param sequenceNumber
	 * @param type
	 * @param key
	 * @param label
	 */
	public Week(int seasonId, int sequenceNumber, String type, String key, String label){
		this(-1, seasonId, sequenceNumber, type, key, label, null);
	}
	
	/**
	 * 
	 * More convenience...
	 * 
	 * @param id
	 * @param seasonId
	 * @param sequenceNumber
	 * @param type
	 * @param key
	 * @param label
	 */
	public Week(int id, int seasonId, int sequenceNumber, String type, String key, String label){
		this(id, seasonId, sequenceNumber, type, key, label, null);
	}
	
	/**
	 * 
	 * A convenience constructor so you don't have to use the setters to make an object.
	 * 
	 * @param id
	 * @param seasonId
	 * @param sequenceNumber
	 * @param type
	 * @param key
	 * @param label
	 * @param games
	 */
	public Week(int id, int seasonId, int sequenceNumber, String type, String key, String label, List<Game> games){
		this.id = id;
		this.seasonId = seasonId;
		this.sequenceNumber = sequenceNumber;
		this.type = type;
		this.key = key;
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

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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
		result = primeNumber * result + Integer.valueOf(sequenceNumber).hashCode();
		result = primeNumber * result + (type == null ? 0 : type.hashCode());
		result = primeNumber * result + (key == null ? 0 : key.hashCode());
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
		
		int otherSequenceNumber = otherWeek.getSequenceNumber();
		
		if (sequenceNumber != otherSequenceNumber){
			return false;
		}
		
		String otherType = otherWeek.getType();
		
		if (type != null){
			if (!type.equals(otherType)){
				return false;
			}
		}
		else {
			if (otherType != null){
				return false;
			}
		}
		
		String otherKey = otherWeek.getKey();
		
		if (key != null){
			if (!key.equals(otherKey)){
				return false;
			}
		}
		else {
			if (otherKey != null){
				return false;
			}
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
									 ", sequenceNumber = " + sequenceNumber +
									 ", type = " + type + 
									 ", key = " + key + 
									 ", label = " + label +
									 ", games = " + games;
		
		return thisObjectAsAString;
	}
}
