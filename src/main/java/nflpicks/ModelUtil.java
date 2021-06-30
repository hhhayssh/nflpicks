package nflpicks;

import java.util.ArrayList;
import java.util.List;

import nflpicks.model.Game;
import nflpicks.model.Pick;
import nflpicks.model.Player;
import nflpicks.model.Record;

/**
 * 
 * Here to hold "utility" functions for working with different kinds of model objects.
 * Here so that the "utility" functions for different model classes can be referenced
 * in one place and so they can easily refer to each other without needing an instance.
 * 
 * @author albundy
 *
 */
public class ModelUtil {
	
	/**
	 * 
	 * Gets the pick that the given player name for the given game from the given list.
	 * If it doesn't find the game in the list of picks or the player didn't make a pick
	 * for the game, it'll return null.
	 * 
	 * @param picks
	 * @param playerName
	 * @param gameId
	 * @return
	 */
	public static Pick getPick(List<Pick> picks, String playerName, int gameId){
		
		//Steps to do:
		//	1. Go through all the picks and look for ones made by the given player.
		//	2. If we find one that's for the game and was picked by the given player,
		//	   that's the one we're looking for.
		//	3. If we go through it all, that means the pick doesn't exist in the list.
		
		if (picks == null || playerName == null){
			return null;
		}
		
		for (int index = 0; index < picks.size(); index++){
			Pick pick = picks.get(index);
			
			int currentGameId = pick.getGame().getId();
			String currentPlayerName = pick.getPlayer().getName();
			
			if (gameId == currentGameId && playerName.equals(currentPlayerName)){
				return pick;
			}
		}
		
		return null;
	}

	/**
	 * 
	 * Gets the pick from the given list that's from the given player and matches either the home team, away team,
	 * or winning team abbreviation (it checks in that order).  If it can't find a pick that matches that, it'll return
	 * null.
	 * 
	 * @param picks
	 * @param playerName
	 * @param homeTeamAbbreviation
	 * @param awayTeamAbbreviation
	 * @param winningTeamAbbreviation
	 * @return
	 */
	public static Pick getPick(List<Pick> picks, String playerName, String homeTeamAbbreviation, String awayTeamAbbreviation, String winningTeamAbbreviation){
		
		if (picks == null || playerName == null){
			return null;
		}
		
		//Steps to do:
		//	1. Go through all the picks and check who made each one.
		//	2. If the player matches what we were given, then check if the game
		//	   has one of the teams and return it if it does.
		//	3. If we go through the whole list, then that means it wasn't in the list.
		
		for (int index = 0; index < picks.size(); index++){
			Pick pick = picks.get(index);
			Player player = pick.getPlayer();
			String pickPlayerName = player.getName();
			
			if (playerName.equals(pickPlayerName)){
				Game game = pick.getGame();
				
				String pickHomeTeam = game.getHomeTeam().getAbbreviation();
				String pickAwayTeam = game.getAwayTeam().getAbbreviation();
				String winningTeam = pick.getTeam() != null ? pick.getTeam().getAbbreviation() : null;
				
				if (homeTeamAbbreviation != null && homeTeamAbbreviation.equals(pickHomeTeam)){
					return pick;
				}
				
				if (awayTeamAbbreviation != null && awayTeamAbbreviation.equals(pickAwayTeam)){
					return pick;
				}
				
				if (winningTeamAbbreviation != null && winningTeamAbbreviation.equals(winningTeam)){
					return pick;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * This function will get the "result" we should use based on the winning team
	 * and the team that got picked. ... Yeah, completely stupid.  It's here so I only
	 * have to do this in one place and can "derive" whether it's a win or a loss based on
	 * the pick and the winning team.  Probably a stupid decision to do it like that, but, hey,
	 * what can I say, I'm a stupid person.
	 * 
	 * If they match, it'll return W.  If they don't, it'll return L.  If the winning team abbreviation
	 * is "TIE", it'll return T.  If either one is null, it'll return null.
	 * 
	 * @param winningTeamAbbreviation
	 * @param pickAbbreviation
	 * @return
	 */
	public static String getPickResult(String winningTeamAbbreviation, String pickAbbreviation){
		
		if (winningTeamAbbreviation == null || pickAbbreviation == null){
			return null;
		}
		
		//Steps to do:
		//	1. If the winning team was the "tie" team, then it was a tie.
		//	2. Otherwise, if they're the same, the result was a win.
		//	3. And if they're different, it was a loss.
		
		String pickResult = null;
		
		if (NFLPicksConstants.TIE_TEAM_ABBREVIATION.equals(winningTeamAbbreviation)){
			pickResult = NFLPicksConstants.RESULT_TIE;
		}
		else {
			if (winningTeamAbbreviation.equals(pickAbbreviation)){
				pickResult = NFLPicksConstants.RESULT_WIN;
			}
			else {
				pickResult = NFLPicksConstants.RESULT_LOSS;
			}
		}
		
		return pickResult;
	}
	
	/**
	 * 
	 * This function checks to see whether there are any ties in the list
	 * of records.  Kind of dumb, but sometimes we need to add a column or
	 * do something if we're showing records and there are ties.  If there
	 * aren't ties, we usually leave the tie part off.
	 * 
	 * @param records
	 * @return
	 */
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
	
	/**
	 * 
	 * This function gets the largest number of wins for the given records.
	 * Not much to it.
	 * 
	 * @param records
	 * @return
	 */
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
	
	/**
	 * 
	 * A stupid function that gets the record for the player with the give name
	 * from the list of records.  Here because I'm a moron.
	 * 
	 * And, yeah, I realize I could be using java 8 streams to do it like this:
	 * 
	 * 	records.stream().filter(p -> p.getName() == playerName).collect(Collectors.toList());
	 * 
	 * But I don't feel like doing it that way and, since it's my project, I'm not going to.
	 * 
	 * @param records
	 * @param playerName
	 * @return
	 */
	public static Record getRecordForPlayer(List<Record> records, String playerName){
		
		if (records == null || playerName == null){
			return null;
		}
		
		for (int index = 0; index < records.size(); index++){
			Record record = records.get(index);
			Player player = record.getPlayer();
			String recordPlayerName = player.getName();
			
			if (playerName.equals(recordPlayerName)){
				return record;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * Another dumb function that should be a one liner using java 8 streams.  It
	 * gets the names of all the players in the given list.  
	 * 
	 * @param players
	 * @return
	 */
	public static List<String> getPlayerNames(List<Player> players){
		
		if (players == null){
			return null;
		}
		
		List<String> playerNames = new ArrayList<String>();
		
		for (int index = 0; index < players.size(); index++){
			Player player = players.get(index);
			String playerName = player.getName();
			playerNames.add(playerName);
		}
		
		return playerNames;
		
		//need a programming language that only deals with the basic data structures and types like int, string, long...
		////// i think that's called c
	}
	
	/**
	 * 
	 * This gets the label we should use for the given "sequence number" for a week within a year.
	 * It used to be the "week number" instead of "sequence number", but then the nfl got greedy and
	 * added week 18... So that meant that the week numbers weren't consistent across seasons anymore.
	 * Anyway, it just works like this...
	 * 
	 * 		If the year is less than 2021, it'll take week 17 as the last regular season week, and 
	 * 		week 18 as the wildcard, week 19 as the divisional, week 20 as the conference championship,
	 * 		and week 21 as the superbowl.
	 * 
	 * 		If it's 2021 or after, it'll take week 18 as the last regular season week and just up the
	 * 		numbers for everything after.
	 * 
	 * @param year
	 * @param weekSequenceNumber
	 * @return
	 */
	public static String getWeekLabel(int year, int weekSequenceNumber){
		
		String weekLabel = null;
		
		if (year < 2021){
			if (weekSequenceNumber <= 17){
				weekLabel = "Week " + weekSequenceNumber;
			}
			else {
				if (weekSequenceNumber == 18){
					weekLabel = "Wildcard";
				}
				else if (weekSequenceNumber == 19){
					weekLabel = "Divisional";
				}
				else if (weekSequenceNumber == 20){
					weekLabel = "Conference Championship";
				}
				else if (weekSequenceNumber == 21){
					weekLabel = "Superbowl";
				}
			}
		}
		else {
			if (weekSequenceNumber <= 18){
				weekLabel = "Week " + weekSequenceNumber;
			}
			else {
				if (weekSequenceNumber == 19){
					weekLabel = "Wildcard";
				}
				else if (weekSequenceNumber == 20){
					weekLabel = "Divisional";
				}
				else if (weekSequenceNumber == 21){
					weekLabel = "Conference Championship";
				}
				else if (weekSequenceNumber == 22){
					weekLabel = "Superbowl";
				}
			}
		}
		
		return weekLabel;
	}
	
	/**
	 * 
	 * This function will just get the week type, either "regular_season" or "playoffs", for
	 * the week with the given "sequence number" in the given year.  Freakin' goodell...
	 * 
	 * @param year
	 * @param weekSequenceNumber
	 * @return
	 */
	public static String getWeekType(int year, int weekSequenceNumber){
		
		String weekType = null;
		
		if (year < 2021){
			if (weekSequenceNumber <= 17){
				weekType = NFLPicksConstants.WEEK_TYPE_REGULAR_SEASON;
			}
			else {
				weekType = NFLPicksConstants.WEEK_TYPE_PLAYOFFS;
			}
		}
		else {
			if (weekSequenceNumber <= 18){
				weekType = NFLPicksConstants.WEEK_TYPE_REGULAR_SEASON;
			}
			else {
				weekType = NFLPicksConstants.WEEK_TYPE_PLAYOFFS;
			}
		}
		
		return weekType;
	}
	
	/**
	 * 
	 * This function will get the "week key" for the week in the given year.
	 * The "week key" is basically the same kind of deal as the label, but more
	 * like a "value".  Like, the "week key" for week 1 is "1".  Week 2 is "2", and so on.
	 * It's "wildcard" for the wildcard round, "divisional", for the divisional round, and so on.
	 * 
	 * It's here so that we can handle the fact that week 18 became a regular season week starting in
	 * 2021.  For example, if the input to this function is "2018, 18", it'll return "wildcard" because
	 * week 18 was the wildcard week in 2021.  If it's "2021, 18", it'll return "18" because that
	 * was a regular week in 2021.
	 * 
	 * Basically, it's here so we can put the logic that says "if it's before 2021, do this ... if it's after
	 * do this..." in one place.
	 * 
	 * @param year
	 * @param weekSequenceNumber
	 * @return
	 */
	public static String getWeekKey(int year, int weekSequenceNumber){
		
		String weekKey = null;
		
		if (year < 2021){
			if (weekSequenceNumber <= 17){
				weekKey = createWeekKey(weekSequenceNumber);
			}
			else {
				if (weekSequenceNumber == 18){
					weekKey = NFLPicksConstants.WEEK_KEY_WILDCARD;
				}
				else if (weekSequenceNumber == 19){
					weekKey = NFLPicksConstants.WEEK_KEY_DIVISIONAL;
				}
				else if (weekSequenceNumber == 20){
					weekKey = NFLPicksConstants.WEEK_KEY_CONFERENCE_CHAMPIONSHIP;
				}
				else if (weekSequenceNumber == 21){
					weekKey = NFLPicksConstants.WEEK_KEY_SUPERBOWL;
				}
			}
		}
		else {
			if (weekSequenceNumber <= 18){
				weekKey = createWeekKey(weekSequenceNumber);
			}
			else {
				if (weekSequenceNumber == 19){
					weekKey = NFLPicksConstants.WEEK_KEY_WILDCARD;
				}
				else if (weekSequenceNumber == 20){
					weekKey = NFLPicksConstants.WEEK_KEY_DIVISIONAL;
				}
				else if (weekSequenceNumber == 21){
					weekKey = NFLPicksConstants.WEEK_KEY_CONFERENCE_CHAMPIONSHIP;
				}
				else if (weekSequenceNumber == 22){
					weekKey = NFLPicksConstants.WEEK_KEY_SUPERBOWL;
				}
			}
		}
		
		return weekKey;
	}
	
	/**
	 * 
	 * This gets the week sequence number for the given "week key" in the given year.
	 * Here so this logic is (hopefully) only in one place.
	 * 
	 * @param year
	 * @param weekKey
	 * @return
	 */
	public static int getWeekSequenceNumber(int year, String weekKey){
		
		int weekSequenceNumber = -1;
		
		if (year < 2021){
			if (isNumericWeekKey(weekKey)){
				weekSequenceNumber = getWeekSequenceNumberFromWeekKey(weekKey);
			}
			else {
				if (NFLPicksConstants.WEEK_KEY_WILDCARD.equals(weekKey)){
					weekSequenceNumber = 18;
				}
				else if (NFLPicksConstants.WEEK_KEY_DIVISIONAL.equals(weekKey)){
					weekSequenceNumber = 19;
				}
				else if (NFLPicksConstants.WEEK_KEY_CONFERENCE_CHAMPIONSHIP.equals(weekKey)){
					weekSequenceNumber = 20;
				}
				else if (NFLPicksConstants.WEEK_KEY_SUPERBOWL.equals(weekKey)){
					weekSequenceNumber = 21;
				}
			}
		}
		else {
			if (isNumericWeekKey(weekKey)){
				weekSequenceNumber = getWeekSequenceNumberFromWeekKey(weekKey);
			}
			else {
				if (NFLPicksConstants.WEEK_KEY_WILDCARD.equals(weekKey)){
					weekSequenceNumber = 19;
				}
				else if (NFLPicksConstants.WEEK_KEY_DIVISIONAL.equals(weekKey)){
					weekSequenceNumber = 20;
				}
				else if (NFLPicksConstants.WEEK_KEY_CONFERENCE_CHAMPIONSHIP.equals(weekKey)){
					weekSequenceNumber = 21;
				}
				else if (NFLPicksConstants.WEEK_KEY_SUPERBOWL.equals(weekKey)){
					weekSequenceNumber = 22;
				}
			}
		}
		
		return weekSequenceNumber;
	}
	
	/**
	 * 
	 * Just here so we have this in one place... dubm.
	 * 
	 * @param weekSequenceNumber
	 * @return
	 */
	public static String createWeekKey(int weekSequenceNumber){
		
		String weekKey = String.valueOf(weekSequenceNumber);
		
		return weekKey;
	}
	
	/**
	 * 
	 * Because I'm retarded.
	 * 
	 * @param weekKey
	 * @return
	 */
	public static boolean isNumericWeekKey(String weekKey){
		
		int value = Util.parseInt(weekKey, -1);

		if (value == -1){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * This gets the sequence number when the key is like "12".  It'll
	 * return 12 in that case.  If it can't find it, it'll return -1.
	 * 
	 * @param weekKey
	 * @return
	 */
	public static int getWeekSequenceNumberFromWeekKey(String weekKey){
		
		int weekSequenceNumber = Util.toInteger(weekKey);
		
		return weekSequenceNumber;
	}
}
