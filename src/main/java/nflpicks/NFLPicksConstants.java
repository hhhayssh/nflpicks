package nflpicks;

import java.util.List;

import nflpicks.model.Game;

/**
 * 
 * This is so there's one place for the constants that get used
 * everywhere so at least I know where to look for them and should
 * help make everything more consistent.
 * 
 * @author albundy
 *
 */
public interface NFLPicksConstants {
	
	/**
	 * 
	 * This is the system property that tells us the name of the properties file to use.
	 * By default, it's nflpicks.properties but this lets us change it to something else
	 * if we want.
	 * 
	 */
	public static final String NFL_PICKS_PROPERTIES_FILENAME_PROPERTY = "nflpicks.properties.file";
	
	/**
	 * 
	 * The default name of the properties file that holds stuff like the database connection url
	 * and other stuff.
	 * 
	 */
	public static final String DEFAULT_NFL_PICKS_PROPERTIES_FILENAME = "nflpicks.properties";
	
	/**
	 * 
	 * What we usually use to represent a win.
	 * 
	 */
	public static final String RESULT_WIN = "W";
	
	/**
	 * 
	 * What we usually use to represent a loss.
	 * 
	 */
	public static final String RESULT_LOSS = "L";
	
	/**
	 * 
	 * What we usually use to represent a tie.
	 * 
	 */
	public static final String RESULT_TIE = "T";
	
	/**
	 * 
	 * The abbreviation we use for the winning team when there's a tie game.
	 * 
	 */
	public static final String TIE_TEAM_ABBREVIATION = "TIE";
	
	// Constants for the names of json fields.  They're here because I'm not doing any of that
	// auto crap and I wanted to use constants.
	
	public static final String JSON_SEASON_ID = "id";
	public static final String JSON_SEASON_YEAR = "year";
	public static final String JSON_SEASON_WEEKS = "weeks";
	
	public static final String JSON_WEEK_ID = "id";
	public static final String JSON_WEEK_SEASON_ID = "season_id";
	public static final String JSON_WEEK_WEEK_NUMBER = "week_number";
	public static final String JSON_WEEK_LABEL = "label";
	public static final String JSON_WEEK_GAMES = "games";
	
	public static final String JSON_GAME_ID = "id";
	public static final String JSON_GAME_WEEK_ID = "weekId";
	public static final String JSON_GAME_HOME_TEAM = "homeTeam";
	public static final String JSON_GAME_AWAY_TEAM = "awayTeam";
	public static final String JSON_GAME_WINNING_TEAM = "winningTeam";
	public static final String JSON_GAME_TIE = "tie";
	
	public static final String JSON_TEAM_ID = "id";
	public static final String JSON_TEAM_DIVISION_ID = "divisionId";
	public static final String JSON_TEAM_NAME = "name";
	public static final String JSON_TEAM_NICKNAME = "nickname";
	public static final String JSON_TEAM_ABBREVIATION = "abbreviation";
	
	public static final String JSON_PLAYER_ID = "id";
	public static final String JSON_PLAYER_NAME = "name";
	
	public static final String JSON_PICK_ID = "id";
	public static final String JSON_PICK_GAME = "game";
	public static final String JSON_PICK_PLAYER = "player";
	public static final String JSON_PICK_TEAM = "team";
	public static final String JSON_PICK_RESULT = "result";
	
	public static final String JSON_RECORD_PLAYER = "player";
	public static final String JSON_RECORD_WINS = "wins";
	public static final String JSON_RECORD_LOSSES = "losses";
	public static final String JSON_RECORD_TIES = "ties";
	
	public static final String JSON_WEEK_RECORD_SEASON = "season";
	public static final String JSON_WEEK_RECORD_WEEK = "week";
	public static final String JSON_WEEK_RECORD_RECORD = "record";

	public static final String JSON_PLAYER_WEEK_RECORD_PLAYER = "player";
	public static final String JSON_PLAYER_WEEK_RECORD_SEASON = "season";
	public static final String JSON_PLAYER_WEEK_RECORD_WEEK = "week";
	public static final String JSON_PLAYER_WEEK_RECORD_RECORD = "record";
	
	public static final String JSON_WEEKS_WON_PLAYER = "player";
	public static final String JSON_WEEKS_WON_WEEK_RECORDS = "weekRecords";
	
	public static final String JSON_CHAMPIONSHIP_PLAYER = "player";
	public static final String JSON_CHAMPIONSHIP_SEASON = "season";
	public static final String JSON_CHAMPIONSHIP_RECORD = "record";
	
	public static final String JSON_PLAYER_CHAMPIONSHIPS_PLAYER = "player";
	public static final String JSON_PLAYER_CHAMPIONSHIPS_CHAMPIONSHIPS = "championships";
	
	public static final String DATA_MANAGEMENT_TYPE_IMPORT = "import";
	
	public static final String DATA_MANAGEMENT_TYPE_EXPORT = "export";
}
