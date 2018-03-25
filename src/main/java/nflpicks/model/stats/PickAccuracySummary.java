package nflpicks.model.stats;

import nflpicks.model.Player;
import nflpicks.model.Team;

public class PickAccuracySummary {

	protected Player player;
	
	protected Team team;
	
	protected int actualWins;
	
	protected int actualLosses;
	
	protected int actualTies;
	
	protected int predictedWins;
	
	protected int predictedLosses;
	
	protected int timesRight;
	
	protected int timesWrong;
	
	protected int timesPickedToWinRight;
	
	protected int timesPickedToWinWrong;
	
	protected int timesPickedToLoseRight;
	
	protected int timesPickedToLoseWrong;
	
	public PickAccuracySummary(){
	}
	
	public PickAccuracySummary(Player player, Team team, int actualWins, int actualLosses, int actualTies, int predictedWins, int predictedLosses, int timesRight, int timesWrong,
							   int timesPickedToWinRight, int timesPickedToWinWrong, int timesPickedToLoseRight, int timesPickedToLoseWrong){
		this.player = player;
		this.team = team;
		this.actualWins = actualWins;
		this.actualLosses = actualLosses;
		this.actualTies = actualTies;
		this.predictedWins = predictedWins;
		this.predictedLosses = predictedLosses;
		this.timesRight = timesRight;
		this.timesWrong = timesWrong;
		this.timesPickedToWinRight = timesPickedToWinRight;
		this.timesPickedToWinWrong = timesPickedToWinWrong;
		this.timesPickedToLoseRight = timesPickedToLoseRight;
		this.timesPickedToLoseWrong = timesPickedToLoseWrong;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public int getActualWins() {
		return actualWins;
	}

	public void setActualWins(int actualWins) {
		this.actualWins = actualWins;
	}

	public int getActualLosses() {
		return actualLosses;
	}

	public void setActualLosses(int actualLosses) {
		this.actualLosses = actualLosses;
	}

	public int getActualTies() {
		return actualTies;
	}

	public void setActualTies(int actualTies) {
		this.actualTies = actualTies;
	}

	public int getPredictedWins() {
		return predictedWins;
	}

	public void setPredictedWins(int predictedWins) {
		this.predictedWins = predictedWins;
	}

	public int getPredictedLosses() {
		return predictedLosses;
	}

	public void setPredictedLosses(int predictedLosses) {
		this.predictedLosses = predictedLosses;
	}

	public int getTimesRight() {
		return timesRight;
	}

	public void setTimesRight(int timesRight) {
		this.timesRight = timesRight;
	}

	public int getTimesWrong() {
		return timesWrong;
	}

	public void setTimesWrong(int timesWrong) {
		this.timesWrong = timesWrong;
	}

	public int getTimesPickedToWinRight() {
		return timesPickedToWinRight;
	}

	public void setTimesPickedToWinRight(int timesPickedToWinRight) {
		this.timesPickedToWinRight = timesPickedToWinRight;
	}

	public int getTimesPickedToWinWrong() {
		return timesPickedToWinWrong;
	}

	public void setTimesPickedToWinWrong(int timesPickedToWinWrong) {
		this.timesPickedToWinWrong = timesPickedToWinWrong;
	}

	public int getTimesPickedToLoseRight() {
		return timesPickedToLoseRight;
	}

	public void setTimesPickedToLoseRight(int timesPickedToLoseRight) {
		this.timesPickedToLoseRight = timesPickedToLoseRight;
	}

	public int getTimesPickedToLoseWrong() {
		return timesPickedToLoseWrong;
	}

	public void setTimesPickedToLoseWrong(int timesPickedToLoseWrong) {
		this.timesPickedToLoseWrong = timesPickedToLoseWrong;
	}
	
	public String toString(){
		
		String thisObjectAsAString = "player = " + player + 
								     ", team = " + team + 
								     ", actualWins = " + actualWins + 
								     ", actualLosses = " + actualLosses + 
								     ", actualTies = " + actualTies +
								     ", predictedWins = " + predictedWins +
								     ", predictedLosses = " + predictedLosses +
								     ", timesRight = " + timesRight + 
								     ", timesWrong = " + timesWrong +
								     ", timesPickedToWinRight = " + timesPickedToWinRight + 
								     ", timesPickedToWinWrong = " + timesPickedToWinWrong + 
								     ", timesPickedToLoseRight = " + timesPickedToLoseRight + 
								     ", timesPickedToLoseWrong = " + timesPickedToLoseWrong;
		
		return thisObjectAsAString;
	}
}
