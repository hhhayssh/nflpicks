package nflpicks.model;

/**
 * 
 * Represents a team.  Pretty soon I think I'll try to make a team history object
 * since teams move.
 * 
 * @author albundy
 *
 */
public class Team {

	/**
	 * The id of the team.
	 */
	protected int id;
	
	/**
	 * The division they belong to.
	 */
	protected int divisionId;
	
	/**
	 * The city the team is in.
	 */
	protected String city;
	
	/**
	 * The nickname (like the Bills).
	 */
	protected String nickname;
	
	/**
	 * The abbreviation of the team (like BUF or IND).
	 */
	protected String abbreviation;
	
	/**
	 * The year the team started.
	 */
	protected String startYear;
	
	/**
	 * If the team moved, this is the year it ended.
	 */
	protected String endYear;
	
	/**
	 * The current abbreviation of the team.  If the team has moved, this is
	 * the abbreviation of the current team for it.
	 */
	protected String currentAbbreviation;
	
	public Team(){
	}
	
	/**
	 * 
	 * A convenience constructor so you don't have to use the setters.
	 * 
	 * @param id
	 * @param divisionId
	 * @param city
	 * @param nickname
	 * @param abbreviation
	 * @param startYear
	 * @param endYear
	 * @param currentAbbeviation
	 */
	public Team(int id, int divisionId, String city, String nickname, String abbreviation, String startYear, String endYear, String currentAbbreviation){
		this.id = id;
		this.divisionId = divisionId;
		this.city = city;
		this.nickname = nickname;
		this.abbreviation = abbreviation;
		this.startYear = startYear;
		this.endYear = endYear;
		this.currentAbbreviation = currentAbbreviation;
	}
	
	public int getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(int divisionId) {
		this.divisionId = divisionId;
	}

	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
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
	
	public String getCurrentAbbreviation() {
		return currentAbbreviation;
	}

	public void setCurrentAbbreviation(String currentAbbreviation) {
		this.currentAbbreviation = currentAbbreviation;
	}

	/**
	 * 
	 * A convenience function for figuring out whether this team is active or not
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
		result = primeNumber * result + Integer.valueOf(divisionId).hashCode();
		result = primeNumber * result + (city == null ? 0 : city.hashCode());
		result = primeNumber * result + (nickname == null ? 0 : nickname.hashCode());
		result = primeNumber * result + (abbreviation == null ? 0 : abbreviation.hashCode());
		result = primeNumber * result + (startYear == null ? 0 : startYear.hashCode());
		result = primeNumber * result + (endYear == null ? 0 : endYear.hashCode());
		result = primeNumber * result + (currentAbbreviation == null ? 0 : currentAbbreviation.hashCode());
		
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
		
		if (object == null || !(object instanceof Team)){
			return false;
		}
		
		Team otherTeam = (Team)object;
		
		int otherId = otherTeam.getId();
		
		if (id != otherId){
			return false;
		}
	
		int otherDivisionId = otherTeam.getDivisionId();
		if (divisionId != otherDivisionId){
			return false;
		}
		
		String otherCity = otherTeam.getCity();
		
		if (city != null){
			if (!city.equals(otherCity)){
				return false;
			}
		}
		else {
			if (otherCity != null){
				return false;
			}
		}
		
		String otherNickname = otherTeam.getNickname();
		
		if (nickname != null){
			if (!nickname.equals(otherNickname)){
				return false;
			}
		}
		else {
			if (otherNickname != null){
				return false;
			}
		}
		
		String otherAbbreviation = otherTeam.getAbbreviation();
		
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
		
		String otherStartYear = otherTeam.getStartYear();
		
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
		
		String otherEndYear = otherTeam.getEndYear();
		
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
		
		String otherCurrentAbbreivation = otherTeam.getCurrentAbbreviation();
		
		if (currentAbbreviation != null){
			if (!currentAbbreviation.equals(otherCurrentAbbreivation)){
				return false;
			}
		}
		else {
			if (otherCurrentAbbreivation != null){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * Even more crap.
	 * 
	 */
	@Override
	public String toString(){
		
		String thisObjectAsAString = "id = " + id + 
									 ", divisionId = " + divisionId +
									 ", city = " + city +
									 ", nickname = " + nickname +
									 ", abbreviation = " + abbreviation + 
									 ", startYear = " + startYear + 
									 ", endYear = " + endYear + 
									 ", currentAbbreviation = " + currentAbbreviation;
		
		return thisObjectAsAString;
	}
}
