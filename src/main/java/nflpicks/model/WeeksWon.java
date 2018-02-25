package nflpicks.model;

import java.util.List;

public class WeeksWon {
	
	protected Player player;
	
	protected List<WeekRecord> weekRecords;

	public WeeksWon(){
	}
	
	public WeeksWon(Player player){
		this(player, null);
	}
	
	public WeeksWon(Player player, List<WeekRecord> weekRecords){
		this.player = player;
		this.weekRecords = weekRecords;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public List<WeekRecord> getWeekRecords() {
		return weekRecords;
	}

	public void setWeekRecords(List<WeekRecord> weekRecords) {
		this.weekRecords = weekRecords;
	}
}
