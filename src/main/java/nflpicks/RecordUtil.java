package nflpicks;

import java.util.List;

import nflpicks.model.Record;

public class RecordUtil {

	public static boolean areThereAnyTies(List<Record> records){
		
		if (records == null){
			return false;
		}
		
		for (int index = 0; index < records.size(); index++){
			Record record = records.get(index);
			
			if (record.getTies() > 0){
				return true;
			}
		}
		
		return false;
	}
	
	public static int getTopWins(List<Record> records){
		
		if (records == null){
			return -1;
		}
		
		int topWins = -1;
		
		for (int index = 0; index < records.size(); index++){
			Record record = records.get(index);
			
			if (record.getWins() > topWins){
				topWins = record.getWins();
			}
		}
		
		return topWins;
	}
	
	public static Record getRecordForPlayer(List<Record> records, String playerName){
		
		if (records == null || playerName == null){
			return null;
		}
		
		for (int index = 0; index < records.size(); index++){
			Record record = records.get(index);
			
			if (playerName.equals(record.getPlayer().getName())){
				return record;
			}
		}
		
		return null;
	}
}
