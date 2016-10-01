package nflpicks.model;

public class Team {

	protected int id;
	
	protected int divisionId;
	
	protected String name;
	
	protected String nickname;
	
	protected String abbreviation;
	
	public Team(){
	}
	
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
}
