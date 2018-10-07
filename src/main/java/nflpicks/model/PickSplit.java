package nflpicks.model;

import java.util.List;

public class PickSplit {
	
	protected String year;
	
	protected int weekNumber;
	
	protected String homeTeamAbbreviation;
	
	protected String awayTeamAbbreviation;
	
	protected String winningTeamAbbreviation;
	
	protected List<String> homeTeamPlayers;
	
	protected List<String> awayTeamPlayers;
	
	public PickSplit(){
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

	public String getHomeTeamAbbreviation() {
		return homeTeamAbbreviation;
	}

	public void setHomeTeamAbbreviation(String homeTeamAbbreviation) {
		this.homeTeamAbbreviation = homeTeamAbbreviation;
	}

	public String getAwayTeamAbbreviation() {
		return awayTeamAbbreviation;
	}

	public void setAwayTeamAbbreviation(String awayTeamAbbreviation) {
		this.awayTeamAbbreviation = awayTeamAbbreviation;
	}

	public String getWinningTeamAbbreviation() {
		return winningTeamAbbreviation;
	}

	public void setWinningTeamAbbreviation(String winningTeamAbbreviation) {
		this.winningTeamAbbreviation = winningTeamAbbreviation;
	}

	public List<String> getHomeTeamPlayers() {
		return homeTeamPlayers;
	}

	public void setHomeTeamPlayers(List<String> homeTeamPlayers) {
		this.homeTeamPlayers = homeTeamPlayers;
	}

	public List<String> getAwayTeamPlayers() {
		return awayTeamPlayers;
	}

	public void setAwayTeamPlayers(List<String> awayTeamPlayers) {
		this.awayTeamPlayers = awayTeamPlayers;
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
		
		if (object == null || !(object instanceof PickSplit)){
			return false;
		}
		
		PickSplit otherPickSplit = (PickSplit)object;
		
		String otherYear = otherPickSplit.getYear();

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
		
		int otherWeekNumber = otherPickSplit.getWeekNumber();
		
		if (weekNumber != otherWeekNumber){
			return false;
		}
		
		String otherHomeTeamAbbreviation = otherPickSplit.getHomeTeamAbbreviation();

		if (homeTeamAbbreviation != null){
			if (!homeTeamAbbreviation.equals(otherHomeTeamAbbreviation)){
				return false;
			}
		}
		else {
			if (otherHomeTeamAbbreviation != null){
				return false;
			}
		}
		
		String otherAwayTeamAbbreviation = otherPickSplit.getAwayTeamAbbreviation();

		if (awayTeamAbbreviation != null){
			if (!awayTeamAbbreviation.equals(otherAwayTeamAbbreviation)){
				return false;
			}
		}
		else {
			if (otherAwayTeamAbbreviation != null){
				return false;
			}
		}
		
		String otherWinningAwayTeamAbbreviation = otherPickSplit.getWinningTeamAbbreviation();

		if (winningTeamAbbreviation != null){
			if (!winningTeamAbbreviation.equals(otherWinningAwayTeamAbbreviation)){
				return false;
			}
		}
		else {
			if (otherWinningAwayTeamAbbreviation != null){
				return false;
			}
		}
		
		List<String> otherHomeTeamPlayers = otherPickSplit.getHomeTeamPlayers();

		if (homeTeamPlayers != null){
			if (!homeTeamPlayers.equals(otherHomeTeamPlayers)){
				return false;
			}
		}
		else {
			if (otherHomeTeamPlayers != null){
				return false;
			}
		}
		
		List<String> otherAwayTeamPlayers = otherPickSplit.getAwayTeamPlayers();

		if (awayTeamPlayers != null){
			if (!awayTeamPlayers.equals(otherAwayTeamPlayers)){
				return false;
			}
		}
		else {
			if (otherAwayTeamPlayers != null){
				return false;
			}
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
		
		result = primeNumber * result + (year == null ? 0 : year.hashCode());
		result = primeNumber * result + Integer.valueOf(weekNumber).hashCode();
		result = primeNumber * result + (homeTeamAbbreviation == null ? 0 : homeTeamAbbreviation.hashCode());
		result = primeNumber * result + (awayTeamAbbreviation == null ? 0 : awayTeamAbbreviation.hashCode());
		result = primeNumber * result + (winningTeamAbbreviation == null ? 0 : winningTeamAbbreviation.hashCode());
		result = primeNumber * result + (homeTeamPlayers == null ? 0 : homeTeamPlayers.hashCode());
		result = primeNumber * result + (awayTeamPlayers == null ? 0 : awayTeamPlayers.hashCode());
		
		return result;
	}
	
	public String toString(){
		
		String thisObjectAsAString = "year = " + year + 
									 ", weekNumber = " + weekNumber + 
									 ", homeTeamAbbreviation = " + homeTeamAbbreviation + 
									 ", awayTeamAbbreviation = " + awayTeamAbbreviation + 
									 ", winningTeamAbbreviation = " + winningTeamAbbreviation + 
									 ", homeTeamPlayers = " + homeTeamPlayers + 
									 ", awayTeamPlayers = " + awayTeamPlayers;
		
		return thisObjectAsAString;
	}

}
