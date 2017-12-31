package nflpicks;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	
	public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";
	
	public static String formatDateAsISODate(Date date){
		
		String formattedDate = formatDate(date, ISO_DATE_FORMAT);
		
		return formattedDate;
	}
	
	public static String formatDate(Date date){
		
		String formattedDate = formatDate(date, DEFAULT_DATE_FORMAT);
		
		return formattedDate;
	}
	
	public static String formatDate(Date date, String formatString){
		
		if (date == null){
			return null;
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat(formatString);
		
		String formattedDate = formatter.format(date);
		
		return formattedDate;
	}

}
