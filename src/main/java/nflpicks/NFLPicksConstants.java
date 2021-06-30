package nflpicks;

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
	
	/**
	 * 
	 * The team id we use when there's a tie.
	 * 
	 */
	public static final int TIE_WINNING_TEAM_ID = -1;
	
	/**
	 * This says the week is in the regular season.
	 */
	public static final String WEEK_TYPE_REGULAR_SEASON = "regular_season";
	
	/**
	 * This says the week is in the playoffs.
	 */
	public static final String WEEK_TYPE_PLAYOFFS = "playoffs";
	
	/**
	 * This says the week is part of the wildcard round of the playoffs.
	 */
	public static final String WEEK_KEY_WILDCARD = "wildcard";
	
	/**
	 * This says the week is part of the divisional round of the playoffs.
	 */
	public static final String WEEK_KEY_DIVISIONAL = "divisional";
	
	/**
	 * This says the week is part of the conference championships blah.
	 */
	public static final String WEEK_KEY_CONFERENCE_CHAMPIONSHIP = "conference_championship";
	
	/**
	 * This says it's the superbowl.
	 */
	public static final String WEEK_KEY_SUPERBOWL = "superbowl";
	
	// Constants for the names of json fields.  They're here because I'm not doing any of that
	// auto crap and I wanted to use constants.
	
	public static final String JSON_SEASON_ID = "id";
	public static final String JSON_SEASON_YEAR = "year";
	public static final String JSON_SEASON_WEEKS = "weeks";
	
	public static final String JSON_WEEK_ID = "id";
	public static final String JSON_WEEK_SEASON_ID = "season_id";
	public static final String JSON_WEEK_WEEK_SEQUENCE_NUMBER = "weekSequenceNumber";
	public static final String JSON_WEEK_TYPE = "weekType";
	public static final String JSON_WEEK_KEY = "weekKey";
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
	public static final String JSON_TEAM_CITY = "city";
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
	
	public static final String JSON_WEEK_RECORD_FOR_PLAYER_SEASON = "season";
	public static final String JSON_WEEK_RECORD_FOR_PLAYER_WEEK = "week";
	public static final String JSON_WEEK_RECORD_FOR_PLAYER_RECORD = "record";

	public static final String JSON_WEEK_RECORD_FOR_PLAYER_PLAYER = "player";
	public static final String JSON_PLAYER_WEEK_RECORD_SEASON = "season";
	public static final String JSON_PLAYER_WEEK_RECORD_WEEK = "week";
	public static final String JSON_PLAYER_WEEK_RECORD_RECORD = "record";
	
	public static final String JSON_WEEK_RECORD_FOR_PLAYERS_PLAYERS = "players";
	public static final String JSON_WEEK_RECORD_FOR_PLAYERS_SEASON = "season";
	public static final String JSON_WEEK_RECORD_FOR_PLAYERS_WEEK = "week";
	public static final String JSON_WEEK_RECORD_FOR_PLAYERS_RECORD = "record";
	
	public static final String JSON_WEEK_RECORDS_FOR_PLAYER_PLAYER = "player";
	public static final String JSON_WEEK_RECORDS_FOR_PLAYER_RECORDS = "weekRecords";
	
	public static final String JSON_CHAMPIONSHIP_PLAYER = "player";
	public static final String JSON_CHAMPIONSHIP_SEASON = "season";
	public static final String JSON_CHAMPIONSHIP_RECORD = "record";
	
	public static final String JSON_CHAMPIONSHIPS_FOR_PLAYER_PLAYER = "player";
	public static final String JSON_CHAMPIONSHIPS_FOR_PLAYER_CHAMPIONSHIPS = "championships";
	
	public static final String JSON_PICK_GRID_PLAYERS = "players";
	public static final String JSON_PICK_GRID_GAMES = "games";
	public static final String JSON_PICK_GRID_PICKS = "picks";
	
	public static final String JSON_STANDINGS_RECORDS = "records";
	
	public static final String JSON_SELECTION_CRITERIA_YEARS = "years";
	public static final String JSON_SELECTION_CRITERIA_PLAYERS = "players";
	public static final String JSON_SELECTION_CRITERIA_TEAMS = "teams";
	
	public static final String JSON_CURRENT_YEAR = "currentYear";
	public static final String JSON_CURRENT_WEEK_KEY = "currentWeekKey";
	
	public static final String JSON_PICK_ACCURACY_SUMMARY_PLAYER = "player";
	public static final String JSON_PICK_ACCURACY_SUMMARY_TEAM = "team";
	public static final String JSON_PICK_ACCURACY_SUMMARY_ACTUAL_WINS = "actualWins";
	public static final String JSON_PICK_ACCURACY_SUMMARY_ACTUAL_LOSSES = "actualLosses";
	public static final String JSON_PICK_ACCURACY_SUMMARY_ACTUAL_TIES = "actualTies";
	public static final String JSON_PICK_ACCURACY_SUMMARY_PREDICTED_WINS = "predictedWins";
	public static final String JSON_PICK_ACCURACY_SUMMARY_PREDICTED_LOSSES = "predictedLosses";
	public static final String JSON_PICK_ACCURACY_SUMMARY_TIMES_RIGHT = "timesRight";
	public static final String JSON_PICK_ACCURACY_SUMMARY_TIMES_WRONG = "timesWrong";
	public static final String JSON_PICK_ACCURACY_SUMMARY_TIMES_PICKED_TO_WIN_RIGHT = "timesPickedToWinRight";
	public static final String JSON_PICK_ACCURACY_SUMMARY_TIMES_PICKED_TO_WIN_WRONG = "timesPickedToWinWrong";
	public static final String JSON_PICK_ACCURACY_SUMMARY_TIMES_PICKED_TO_LOSE_RIGHT = "timesPickedToLoseRight";
	public static final String JSON_PICK_ACCURACY_SUMMARY_TIMES_PICKED_TO_LOSE_WRONG = "timesPickedToLoseWrong";
	
	public static final String JSON_COMPACT_PICK_GRID_PLAYERS = "players";
	public static final String JSON_COMPACT_PICK_GRID_PICKS = "picks";
	
	public static final String JSON_COMPACT_PICK_YEAR = "year";
	public static final String JSON_COMPACT_PICK_WEEK_SEQUENCE_NUMBER = "weekSequenceNumber";
	public static final String JSON_COMPACT_PICK_WEEK_TYPE = "weekType";
	public static final String JSON_COMPACT_PICK_WEEK_KEY = "weekKey";
	public static final String JSON_COMPACT_PICK_WEEK_LABEL = "weekLabel";
	public static final String JSON_COMPACT_PICK_HOME_TEAM_ABBREVIATION = "homeTeamAbbreviation";
	public static final String JSON_COMPACT_PICK_AWAY_TEAM_ABBREVIATION = "awayTeamAbbreviation";
	public static final String JSON_COMPACT_PICK_WINNING_TEAM_ABBREVIATION = "winningTeamAbbreviation";
	public static final String JSON_COMPACT_PICK_PLAYER_PICKS = "playerPicks";
	
	public static final String JSON_COMPACT_PLAYER_PICK_PLAYER = "player";
	public static final String JSON_COMPACT_PLAYER_PICK_PICK = "pick";
	
	public static final String JSON_PICK_SPLIT_YEAR = "year";
	public static final String JSON_PICK_SPLIT_WEEK_SEQUENCE_NUMBER = "weekSequenceNumber";
	public static final String JSON_PICK_SPLIT_WEEK_TYPE = "weekType";
	public static final String JSON_PICK_SPLIT_WEEK_KEY = "weekKey";
	public static final String JSON_PICK_SPLIT_WEEK_LABEL = "weekLabel";
	public static final String JSON_PICK_SPLIT_HOME_TEAM_ABBREVIATION = "homeTeamAbbreviation";
	public static final String JSON_PICK_SPLIT_AWAY_TEAM_ABBREVIATION = "awayTeamAbbreviation";
	public static final String JSON_PICK_SPLIT_WINNING_TEAM_ABBREVIATION = "winningTeamAbbreviation";
	public static final String JSON_PICK_SPLIT_HOME_TEAM_PLAYERS = "homeTeamPlayers";
	public static final String JSON_PICK_SPLIT_AWAY_TEAM_PLAYERS = "awayTeamPlayers";
	
	
	public static final String JSON_SEASON_RECORD_FOR_PLAYER_PLAYER = "player";
	public static final String JSON_SEASON_RECORD_FOR_PLAYER_SEASON = "season";
	public static final String JSON_SEASON_RECORD_FOR_PLAYER_RECORD = "record";
	public static final String JSON_SEASON_RECORD_FOR_PLAYER_CHAMPIONSHIP = "championship";
	
	/**
	 * 
	 * Says we're doing an import of picks with the data manager.
	 * 
	 */
	public static final String DATA_MANAGEMENT_TYPE_IMPORT = "import";
	
	public static final String DATA_MANAGEMENT_IMPORT_TYPE_PICKS = "picks";
	
	public static final String DATA_MANAGEMENT_IMPORT_TYPE_TEAM_DATA = "team_data";
	
	/**
	 * 
	 * Says we're doing an export of picks with the data manager.
	 * 
	 */
	public static final String DATA_MANAGEMENT_TYPE_EXPORT = "export";
	
	public static final String DATA_MANAGEMENT_EXPORT_TYPE_PICKS = "picks";
	
	public static final String DATA_MANAGEMENT_EXPORT_TYPE_TEAM_DATA = "team_data";
}
