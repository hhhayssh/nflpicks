package nflpicks;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import javax.sql.DataSource;

import nflpicks.model.Pick;

public class NFLPicksDataImporter {

	/*
	 insert into pick (game_id, player_id, team_id)  
values ((select g.id  
	 from game g  
	 where g.home_team_id in (select t.id  
				  from team t  
				  where t.abbreviation = ?) 
	       and g.away_team_id in (select t.id  
				      from team t  
				      where t.abbreviation = ?)  
	       and g.week_id in (select w.id  
				 from week w  
				 where week = ?  
				       and season_id in (select id 
							 from season 
							 where year = ?))),  
		(select p.id from player p where name = ?),  
		(select t.id from team t where t.abbreviation = ?));
		
		
insert into pick (game_id, player_id, team_id) values ((select g.id from game g where g.home_team_id in (select t.id from team t where t.abbreviation = ?) and g.away_team_id in (select t.id from team t where t.abbreviation = ?) and g.week_id in (select w.id from week w where week = ? and season_id in (select id from season where year = ?))), (select p.id from player p where name = ?), (select t.id from team t where t.abbreviation = ?));
	 
	 */
	protected static final String INSERT_PICK = "insert into pick (game_id, player_id, team_id) " + 
												"values ((select g.id " + 
														 "from game g " + 
														 "where g.home_team_id in (select t.id " + 
														 						  "from team t " + 
														 						  "where t.abbreviation = ?) " +
															   "and g.away_team_id in (select t.id " + 
															   					      "from team t " + 
															        				  "where t.abbreviation = ?) " + 
															   "and g.week_id in (select w.id " + 
															     	 			 "from week w " + 
															     	 			 "where week = ? " + 
															     	 				   "and season_id in (select id " + 
															     	 				    				 "from season " + 
															     	 				    				 "where year = ?))), " + 
														"(select p.id from player p where name = ?), " + 
														"(select t.id from team t where t.abbreviation = ?))";
	
	/*
	 
	 update pick
set team_id = (select t.id
				 from team t
				 where t.abbreviation = ?)
where game_id in (select g.id
		    from game g
		    where g.home_team_id in (select t.id
					     from team t
					     where t.abbreviation = ?)
			  and g.away_team_id in (select t.id
						 from team t
						 where t.abbreviation = ?)
			  and g.week_id in (select w.id
			  				    from week w
			  				    where w.week = ?
			  				    	  and w.season_id in (select s.id
			  				    	  					  from season s
			  				    	  					  where s.year = ?)
			  				   )
		   )
      and player_id in (select pl.id
			  from player pl
			  where pl.name = ?)
	 
	 */
	
	protected static final String UPDATE_PICK = "update pick " + 
												"set team_id = (select t.id " + 
																 "from team t " + 
																 "where t.abbreviation = ?) " + 
												"where game_id in (select g.id " + 
																	"from game g " + 
																	"where g.home_team_id in (select t.id " + 
																							 "from team t " + 
																							 "where t.abbreviation = ?) " + 
																		  "and g.away_team_id in (select t.id " + 
																		  						 "from team t " + 
																		  						 "where t.abbreviation = ?) " +
																		  "and g.week_id in (select w.id " + 
																		  					"from week w " + 
																		  					"where w.week = ? " + 
																		  						  "and w.season_id in (select s.id " + 
																		  						  					  "from season s " + 
																		  						  					  "where s.year = ?) " + 
																		  					") " +
																    ") " + 
													 "and player_id in (select pl.id " + 
													 					 "from player pl " + 
													 					 "where pl.name = ?) ";
	
	/*
	 
insert into game(week_id, home_team_id, away_team_id)  
values ((select w.id  
  	  from week w 
  	  where w.week = ? 
  	  	    and w.season_id in (select s.id  
  	  	    				    from season s 
  	  	    				    where year = ?)), 
  	  (select t.id  
  	  from team t 
  	  where t.abbreviation = ?),  
  	  (select t.id  
  	  from team t  
  	  where t.abbreviation = ?)) ;
  	  
insert into game(week_id, home_team_id, away_team_id) values ((select w.id from week w where w.week = ? and w.season_id in (select s.id from season s where year = ?)), (select t.id from team t where t.abbreviation = ?), (select t.id from team t where t.abbreviation = ?)) ;  	  	 
	 
	 */
	
	public static final String INSERT_GAME = "insert into game(week_id, home_team_id, away_team_id) " + 
											 "values ((select w.id " + 
											 	  	  "from week w " +
											 	  	  "where w.week = ? " +
											 	  	  	    "and w.season_id in (select s.id " + 
											 	  	  	    				    "from season s " +
											 	  	  	    				    "where year = ?)), " +
											 	  	  "(select t.id " + 
											 	  	  "from team t " +
											 	  	  "where t.abbreviation = ?), " + 
											 	  	  "(select t.id " + 
											 	  	  "from team t " + 
											 	  	  "where t.abbreviation = ?)) ";
	
	/*
	 update game g  
set winning_team_id = (select t.id 
				   from team t 
				   where t.abbreviation = ?) 
where g.week_id in (select w.id  
				from week w  
			    where w.week = ? 
					  and w.season_id in (select s.id 
					  					  from season s  
					  					  where s.year = ?))  
 and g.home_team_id in (select t.id  
					  	from team t  
					  	where t.abbreviation = ?)  
 and g.away_team_id in (select t.id  
					  	from team t  
					  	where t.abbreviation = ?) ;
	 
	 update game g set winning_team_id = (select t.id from team t where t.abbreviation = ?) where g.week_id in (select w.id  from week w where w.week = ? and w.season_id in (select s.id from season s where s.year = ?)) and g.home_team_id in (select t.id from team t where t.abbreviation = ?) and g.away_team_id in (select t.id from team t where t.abbreviation = ?) ;
	 
	 */
	
	public static final String UPDATE_GAME_RESULT = "update game g " + 
													"set winning_team_id = (select t.id " +
																		   "from team t " +
																		   "where t.abbreviation = ?) " +
												    "where g.week_id in (select w.id " + 
																		"from week w " + 
																	    "where w.week = ? " +
																			  "and w.season_id in (select s.id " +
																			  					  "from season s " + 
																			  					  "where s.year = ?)) " + 
														 "and g.home_team_id in (select t.id " + 
																			  	"from team t " + 
																			  	"where t.abbreviation = ?) " + 
														 "and g.away_team_id in (select t.id " + 
																			  	"from team t " + 
																			  	"where t.abbreviation = ?) ";
	
	protected DataSource dataSource;
	
	protected NFLPicksDataService dataService;
	
	public static void main(String[] args){
		
		String type = args[0];
		String targetYear = args[1];
		String targetWeeks = args[2];
		String targetPlayer = args[3];
		String filename = args[4];
		
		ApplicationContext.getContext().initialize();
		DataSource dataSource = ApplicationContext.getContext().getDataSource();
		NFLPicksDataImporter importer = new NFLPicksDataImporter(dataSource);
		
		importer.openOutputFile(type);
		String[] targetWeeksArray = targetWeeks.split(",");
		
		if ("games".equals(type)){
			for (int index = 0; index < targetWeeksArray.length; index++){
				String targetWeek = targetWeeksArray[index];
				importer.importGames(targetYear, targetWeek, filename);
			}
		}
		else if ("picks".equals(type)){
			for (int index = 0; index < targetWeeksArray.length; index++){
				String targetWeek = targetWeeksArray[index];
				importer.importPicks(targetYear, targetWeek, targetPlayer, filename);
			}
		}
		else if ("results".equals(type)){
			for (int index = 0; index < targetWeeksArray.length; index++){
				String targetWeek = targetWeeksArray[index];
				importer.importResults(targetYear, targetWeek, filename);
			}
		}
		else if ("sync".equals(type)){
			for (int index = 0; index < targetWeeksArray.length; index++){
				String targetWeek = targetWeeksArray[index];
				importer.importPicks(targetYear, targetWeek, targetPlayer, filename);
				importer.importResults(targetYear, targetWeek, filename);
			}
		}
		importer.flushOutputFile();
		importer.closeOutputFile();
	}
	
	public NFLPicksDataImporter(DataSource dataSource){
		this.dataSource = dataSource;
		this.dataService = new NFLPicksDataService(dataSource);
	}
	
	public void importResults(String targetYear, String targetWeek, String filename){
		System.out.println("Importing results... year = " + targetYear + ", week = " + targetWeek);
		
		//Year	Week	Away	Home
		String filterLine = targetYear + "," + targetWeek + ",";
		List<String> gameLines = Util.readLines(filename, filterLine);
		
		System.out.println("Read " + gameLines.size() + " games from file: " + filename);
		
		for (int index = 0; index < gameLines.size(); index++){
			String gameLine = gameLines.get(index);
			
			String[] split = gameLine.split(",");
			
			if (split.length != 5){
				System.out.println("Skipping result import.  Not enough info in line.  gameLine = " + gameLine);
				continue;
			}
			
			String year = Util.hardcoreTrim(split[0]);
			String week = Util.hardcoreTrim(split[1]);
			String awayTeamAbbreviation = Util.hardcoreTrim(split[2]);
			String homeTeamAbbreviation = Util.hardcoreTrim(split[3]);
			String winningTeamAbbreviation = Util.hardcoreTrim(split[4]);
			
			updateGameResult(year, week, awayTeamAbbreviation, homeTeamAbbreviation, winningTeamAbbreviation);
		}
	}
	
	public void importGames(String targetYear, String targetWeek, String filename){
		
		System.out.println("Importing games... year = " + targetYear + ", week = " + targetWeek);
		
		//Year	Week	Away	Home
		String filterLine = targetYear + "," + targetWeek + ",";
		List<String> gameLines = Util.readLines(filename, filterLine);
		
		System.out.println("Read " + gameLines.size() + " games from file: " + filename);
		
		for (int index = 0; index < gameLines.size(); index++){
			String gameLine = gameLines.get(index);
			
			String[] split = gameLine.split(",");
			
			String year = Util.hardcoreTrim(split[0]);
			String week = Util.hardcoreTrim(split[1]);
			String awayTeamAbbreviation = Util.hardcoreTrim(split[2]);
			String homeTeamAbbreviation = Util.hardcoreTrim(split[3]);
			
			insertGame(year, week, awayTeamAbbreviation, homeTeamAbbreviation);
		}
	}
	
	public void importPicks(String targetYear, String targetWeek, String targetPlayer, String filename){
		
		System.out.println("Importing games... year = " + targetYear + ", week = " + targetWeek + ", player = " + targetPlayer);
		
		//Year	Week	Away	Home	Benny boy	Bruce	Chance	Jonathan	Mark	Teddy	Tim

		String filterLine = targetYear + "," + targetWeek + ",";
		
		List<String> pickLines = Util.readLines(filename, filterLine);
		
		System.out.println("Read " + pickLines.size() + " picks from file: " + filename);
		
		for (int index = 0; index < pickLines.size(); index++){
			String pickLine = pickLines.get(index);
			
			String[] split = pickLine.split(",");

			if (split.length < 3){
				System.out.println("Skipping pick line! Not enough information.  pickLine = " + pickLine);
				continue;
			}
			
			if (split.length != 12){
				System.out.println("Pick line missing information.  pickLine = " + pickLine);
				split = Util.fillArray(split, 12, "");
			}
			
			//year = pick[0].replace(u'\xa0', u'').replace(u'\xc2', u'')
			String year = Util.hardcoreTrim(split[0]);
			String week = Util.hardcoreTrim(split[1]);
			String awayTeamAbbreviation = Util.hardcoreTrim(split[2]);
			String homeTeamAbbreviation = Util.hardcoreTrim(split[3]);
			String bennyPick = Util.hardcoreTrim(split[5]);
			String brucePick = Util.hardcoreTrim(split[6]);
			String chancePick = Util.hardcoreTrim(split[7]);
			String myPick = Util.hardcoreTrim(split[8]);
			String markPick = Util.hardcoreTrim(split[9]);
			String teddyPick = Util.hardcoreTrim(split[10]);
			String timPick = Util.hardcoreTrim(split[11]);
			
			if ("all".equals(targetPlayer)){
				savePick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, "Benny boy", bennyPick);
				savePick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, "Bruce", brucePick);
				savePick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, "Chance", chancePick);
				savePick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, "Jonathan", myPick);
				savePick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, "Mark", markPick);
				savePick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, "Teddy", teddyPick);
				savePick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, "Tim", timPick);
			}
			else if ("Benny boy".equals(targetPlayer)){
				savePick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, "Benny boy", bennyPick);
			}
			else if ("Bruce".equals(targetPlayer)){
				savePick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, "Bruce", brucePick);
			}
			else if ("Chance".equals(targetPlayer)){
				savePick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, "Chance", chancePick);
			}
			else if ("Jonathan".equals(targetPlayer)){
				savePick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, "Jonathan", myPick);
			}
			else if ("Mark".equals(targetPlayer)){
				savePick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, "Mark", markPick);
			}
			else if ("Teddy".equals(targetPlayer)){
				savePick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, "Teddy", teddyPick);
			}
			else if ("Tim".equals(targetPlayer)){
				savePick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, "Tim", timPick);
			}
		}
	}
	
	protected int insertGame(String year, String week, String awayTeamAbbreviation, String homeTeamAbbreviation){
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		int numberOfAffectedRows = 0;
		
		System.out.println("Inserting game: year = " + year + ", week = " + week +
						   ", awayTeamAbbreviation = " + awayTeamAbbreviation +
						   ", homeTeamAbbreviation = " + homeTeamAbbreviation);
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(INSERT_GAME);
			int weekInt = Integer.parseInt(week);
			statement.setInt(1, weekInt);
			statement.setString(2, year);
			statement.setString(3, homeTeamAbbreviation);
			statement.setString(4, awayTeamAbbreviation);
			writeSqlStatementToOutputFile(statement);
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			System.out.println("Error inserting game! year = " + year + ", week = " + week + ", awayTeamAbbreviation = " + awayTeamAbbreviation + ", homeTeamAbbreviation = " + homeTeamAbbreviation);
			e.printStackTrace();
		}
		finally {
			DatabaseUtil.closeStatement(statement);
			DatabaseUtil.closeConnection(connection);
		}
		
		return numberOfAffectedRows;
	}
	
	protected int updateGameResult(String year, String week, String awayTeamAbbreviation, String homeTeamAbbreviation, String winningTeamAbbreviation){
		
		Connection connection = null;
		PreparedStatement statement = null;
		
		int numberOfAffectedRows = 0;
		
		System.out.println("Updating game result: year = " + year + ", week = " + week +
						   ", awayTeamAbbreviation = " + awayTeamAbbreviation +
						   ", homeTeamAbbreviation = " + homeTeamAbbreviation + 
						   ", winningTeamAbbreviation = " + winningTeamAbbreviation);
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(UPDATE_GAME_RESULT);
			statement.setString(1, winningTeamAbbreviation);
			int weekInt = Integer.parseInt(week);
			statement.setInt(2, weekInt);
			statement.setString(3, year);
			statement.setString(4, homeTeamAbbreviation);
			statement.setString(5, awayTeamAbbreviation);
			writeSqlStatementToOutputFile(statement);
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			numberOfAffectedRows = -1;
			System.out.println("Error updating game result! year = " + year + ", week = " + week + ", awayTeamAbbreviation = " + awayTeamAbbreviation + ", homeTeamAbbreviation = " + homeTeamAbbreviation + ", winningTeamAbbreviation = " + winningTeamAbbreviation);
			e.printStackTrace();
		}
		finally {
			DatabaseUtil.closeStatement(statement);
			DatabaseUtil.closeConnection(connection);
		}
		
		return numberOfAffectedRows;
	}
	
	protected int savePick(String year, String week, String awayTeamAbbreviation, String homeTeamAbbreviation, String playerName, String pickAbbreviation){
		
		boolean doesPickExist = doesPickExist(year, week, awayTeamAbbreviation, homeTeamAbbreviation, playerName);
		
		int numberOfAffectedRows = 0;
		
		if (doesPickExist){
			numberOfAffectedRows = updatePick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, playerName, pickAbbreviation);
		}
		else {
			numberOfAffectedRows = insertPick(year, week, awayTeamAbbreviation, homeTeamAbbreviation, playerName, pickAbbreviation);
		}
		
		return numberOfAffectedRows;
	}
	
	protected boolean doesPickExist(String year, String week, String awayTeamAbbreviation, String homeTeamAbbreviation, String playerName){
		int weekInt = Integer.parseInt(week);
		Pick pick = dataService.getPick(playerName, year, weekInt, homeTeamAbbreviation, awayTeamAbbreviation);
		
		if (pick != null){
			return true;
		}
		
		return false;
	}
	
	protected int updatePick(String year, String week, String awayTeamAbbreviation, String homeTeamAbbreviation, String player, String pickAbbreviation){
		
		System.out.println("Updating pick: year = " + year + ", week = " + week + 
				   		   ", awayTeamAbbreviation = " + awayTeamAbbreviation + 
				   		   ", homeTeamAbbreviation = " + homeTeamAbbreviation + 
				   		   ", player = " + player + ", pickAbbreviation = " + pickAbbreviation);
		
		Connection connection = null;
		PreparedStatement statement = null;
		int numberOfAffectedRows = 0;
		
		try {
			connection = dataSource.getConnection();
			/*
			 protected static final String UPDATE_PICK = "update pick p " + 
												"set p.team_id = (select t.id " + 
																 "from team t " + 
																 "where t.abbreviation = ?) " + 
												"where p.game_id in (select g.id " + 
																	"from game g " + 
																	"where g.home_team_id in (select t.id " + 
																							 "from team t " + 
																							 "where t.abbreviation = ?) " + 
																		  "and g.away_team_id in (select t.id " + 
																		  						 "from team t " + 
																		  						 "where t.abbreviation = ?) " +
																		  "and g.week_id in (select w.id " + 
																		  					"from week w " + 
																		  					"where w.week = ? " + 
																		  						  "and w.season_id in (select s.id " + 
																		  						  					  "from season s " + 
																		  						  					  "where s.year = ?) " + 
																		  					") " +
																    ") " + 
													 "and p.player_id in (select pl.id " + 
													 					 "from player pl " + 
													 					 "where pl.name = ?) ";
			 */
			statement = connection.prepareStatement(UPDATE_PICK);
			statement.setString(1, pickAbbreviation);
			statement.setString(2, homeTeamAbbreviation);
			statement.setString(3, awayTeamAbbreviation);
			int weekInt = Integer.parseInt(week);
			statement.setInt(4, weekInt);
			statement.setString(5, year);
			statement.setString(6, player);
			writeSqlStatementToOutputFile(statement);
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			System.out.println("Error updating pick! year = " + year + ", week = " + week + 
							   ", awayTeamAbbreviation = " + awayTeamAbbreviation + 
							   ", homeTeamAbbreviation = " + homeTeamAbbreviation + 
							   ", player = " + player + ", pickAbbreviation = " + pickAbbreviation);
			numberOfAffectedRows = -1;
			e.printStackTrace();
		}
		finally {
			DatabaseUtil.closeStatement(statement);
			DatabaseUtil.closeConnection(connection);
		}
		
		return numberOfAffectedRows;
	}
	
	protected int insertPick(String year, String week, String awayTeamAbbreviation, String homeTeamAbbreviation, String player, String pickAbbreviation){
		
		Connection connection = null;
		PreparedStatement statement = null;
		int numberOfAffectedRows = 0;
		//cursor.execute("""insert into pick (game_id, player_id, team_id) values ((select g.id from game g where g.home_team_id in (select t.id from team t where t.abbreviation = %s) and g.away_team_id in (select t.id from team t where t.abbreviation = %s) and g.week_id in (select w.id from week w where week = %s and season_id in (select id from season where year = %s))), (select p.id from player p where name = %s), (select t.id from team t where t.abbreviation = %s));""", (home_team, away_team, week, year, 'Benny boy', ben_pick))

		System.out.println("Inserting pick: year = " + year + ", week = " + week + 
						   ", awayTeamAbbreviation = " + awayTeamAbbreviation + 
						   ", homeTeamAbbreviation = " + homeTeamAbbreviation + 
						   ", player = " + player + ", pickAbbreviation = " + pickAbbreviation);
		
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(INSERT_PICK);
			statement.setString(1, homeTeamAbbreviation);
			statement.setString(2, awayTeamAbbreviation);
			int weekInt = Integer.parseInt(week);
			statement.setInt(3, weekInt);
			statement.setString(4, year);
			statement.setString(5, player);
			statement.setString(6, pickAbbreviation);
			writeSqlStatementToOutputFile(statement);
			numberOfAffectedRows = statement.executeUpdate();
			
			connection.commit();
		}
		catch (Exception e){
			System.out.println("Error inserting pick! year = " + year + ", week = " + week + 
							   ", awayTeamAbbreviation = " + awayTeamAbbreviation + 
							   ", homeTeamAbbreviation = " + homeTeamAbbreviation + 
							   ", player = " + player + ", pickAbbreviation = " + pickAbbreviation);
			numberOfAffectedRows = -1;
			e.printStackTrace();
		}
		finally {
			DatabaseUtil.closeStatement(statement);
			DatabaseUtil.closeConnection(connection);
		}
		
		return numberOfAffectedRows;
	}
	
	protected PrintWriter outputFileWriter;
	
	protected void openOutputFile(String type){
		try {
			outputFileWriter = new PrintWriter("/home/albundy/Desktop/nflpicks/" + type + "-output.sql");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	protected void flushOutputFile(){
		outputFileWriter.flush();
	}
	
	protected void writeSqlStatementToOutputFile(PreparedStatement statement){
		writeToOutputFile(statement.toString() + ";");
	}
	
	protected void writeToOutputFile(String value){
		outputFileWriter.println(value);
	}
	
	protected void closeOutputFile(){
		try {
			outputFileWriter.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
