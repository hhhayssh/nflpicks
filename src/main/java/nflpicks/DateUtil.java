package nflpicks;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 
 * This class is for utility functions that deal with dates.
 * 
 * @author albundy
 *
 */
public class DateUtil {
	
	private static final Logger log = Logger.getLogger(DateUtil.class);
	
	/**
	 * 
	 * The default format we use for dates.
	 * 
	 */
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	
	/**
	 * 
	 * The default format with time added on.
	 * 
	 */
	public static final String DEFAULT_DATE_FORMAT_WITH_TIME = "yyyy-MM-dd-HH-mm-ss";
	
	/**
	 * 
	 * For formatting ISO dates ... in "local" time, not in UTC time.
	 * 
	 */
	public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	
	/**
	 * 
	 * A convenience function for formatting the given date as an iso date without
	 * having to put in the constant.
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateAsISODate(Date date){
		
		String formattedDate = formatDate(date, ISO_DATE_FORMAT);
		
		return formattedDate;
	}
	
	/**
	 * 
	 * Formats the given date using the default date format.
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date){
		
		String formattedDate = formatDate(date, DEFAULT_DATE_FORMAT);
		
		return formattedDate;
	}
	
	/**
	 * 
	 * Formats the given date according to the given format string.  It'll
	 * return null if the given date is null or if there's a problem formatting.
	 * 
	 * @param date
	 * @param formatString
	 * @return
	 */
	public static String formatDate(Date date, String formatString){
		
		if (date == null || formatString == null){
			return null;
		}
		
		String formattedDate = null;
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(formatString);
			formattedDate = formatter.format(date);
		}
		catch (Exception e){
			log.error("Error formatting date!  date = " + date + ", formatString = " + formatString, e);
		}
		
		return formattedDate;
	}
}
