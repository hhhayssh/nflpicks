package nflpicks.model;

public class WeekRecord {
	
	protected Season season;
	
	protected Week week;
	
	protected Record record;
	
	public WeekRecord(){
	}
	
	public WeekRecord(Season season, Week week, Record record){
		this.season = season;
		this.week = week;
		this.record = record;
	}

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public Week getWeek() {
		return week;
	}

	public void setWeek(Week week) {
		this.week = week;
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}
}
