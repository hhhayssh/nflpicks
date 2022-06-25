package nflpicks.model;

import java.util.List;

public class DivisionRecord {
	
	protected Division division;
	
	protected List<Record> records;

	public DivisionRecord(){
	}

	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	public List<Record> getRecords() {
		return records;
	}

	public void setRecords(List<Record> records) {
		this.records = records;
	}
}
