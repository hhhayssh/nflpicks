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
	 * The name (like the city) of the team.
	 */
	protected String name;
	
	/**
	 * The nickname (like the Bills).
	 */
	protected String nickname;
	
	/**
	 * The abbreviation of the team (like BUF or IND).
	 */
	protected String abbreviation;
	
	public Team(){
	}
	
	/**
	 * 
	 * A convenience constructor so you don't have to use the setters.
	 * 
	 * @param id
	 * @param divisionId
	 * @param name
	 * @param nickname
	 * @param abbreviation
	 */
	public Team(int id, int divisionId, String name, String nickname, String abbreviation){
		this.id = id;
		this.divisionId = divisionId;
		this.name = name;
		this.nickname = nickname;
		this.abbreviation = abbreviation;
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
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		result = primeNumber * result + (name == null ? 0 : name.hashCode());
		result = primeNumber * result + (nickname == null ? 0 : nickname.hashCode());
		result = primeNumber * result + (abbreviation == null ? 0 : abbreviation.hashCode());
		
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
		
		String otherName = otherTeam.getName();
		
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
									 ", name = " + name +
									 ", nickname = " + nickname +
									 ", abbreviation = " + abbreviation;
		
		return thisObjectAsAString;
	}
}
