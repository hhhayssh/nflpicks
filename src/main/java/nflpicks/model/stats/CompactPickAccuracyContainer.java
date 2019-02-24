package nflpicks.model.stats;

import java.util.List;

public class CompactPickAccuracyContainer {
	
	//selection criteria....
	
	protected List<String> years;
	
	protected List<String> weeks;
	
	protected List<String> players;
	
	//get the label we show from what's selected, not from what's sent back
	//then, we can translate "AFC" to the teams in the AFC and then just show
	//AFC while using the teams for the query
	protected List<String> teams;
	
	//is there a reason to have this separated into a container?
	protected CompactPickAccuracy accuracy;
	
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
	
	//also want...
	//	player
	//		accuracies
	//			team	right	wrong ....
	//			team	right	wrong ....
	//
	//	i think a team needs to be on the level with right and wrong
	//	if multiple teams are selected, we show them individually
	//	would be nice to show them collectively too...
	//	
	//	teams: afc east temas
	//		right % wrong %

}
