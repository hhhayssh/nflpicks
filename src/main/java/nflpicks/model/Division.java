package nflpicks.model;

import java.util.List;

public class Division {
	
	protected int id;
	
	protected int conferenceId;
	
	protected String name;
	
	protected List<Team> teams;
	
	public Division(){
	}
	
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
	
	public String toString(){
		
		String thisObjectAsAString = "id = " + id + 
									 ", conferenceId = " + conferenceId +
									 ", name = " + name + 
									 ", teams = " + teams;
		
		return thisObjectAsAString;
	}
}
