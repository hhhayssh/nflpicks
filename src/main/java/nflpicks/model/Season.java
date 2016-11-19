package nflpicks.model;

import java.util.List;

public class Season {
	
	protected int id;
	
	protected String year;
	
	protected List<Week> weeks;
	
	public Season(){
	}
	
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
	
	public String toString(){
		
		String thisObjectAsAString = "id = " + id + 
									 ", year = " + year +
									 ", weeks = " + weeks;
		
		return thisObjectAsAString;
	}
}
