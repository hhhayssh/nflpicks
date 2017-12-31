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
	
	public static final String DEFAULT_NFL_PICKS_PROPERTIES_FILENAME = "nflpicks.properties";
	
	public static final String RESULT_WIN = "W";
	
	public static final String RESULT_LOSS = "L";
	
	public static final String RESULT_TIE = "T";
	
	public static final String TIE_TEAM_ABBREVIATION = "TIE";

}
