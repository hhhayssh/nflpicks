package nflpicks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Util {
	
	public static List<String> readLines(String filename){
		
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			
			String line = "";
			
			while (line != null){
				line = reader.readLine();
				if (line != null){
					lines.add(line);
				}
			}
		}
		catch (Exception e){
			lines = null;
			e.printStackTrace();
		}
		
		return lines;
	}
	
	public static String readHeader(String filename){
		
		BufferedReader reader = null;
		
		String header = null;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			
			header = reader.readLine();
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		return header;
	}
	
	public static List<String> readHeaderValues(String filename){
		
		String header = readHeader(filename);
		
		if (header == null){
			return null;
		}
		
		List<String> headerValues = delimitedStringToList(header, ",");
		
		return headerValues;
	}
	
	public static List<String> readLines(String filename, String filterString){
		
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			
			String line = "";
			
			while (line != null){
				line = reader.readLine();
				if (line != null){
					int index = line.indexOf(filterString);
					if (index != -1){
						lines.add(line);
					}
				}
			}
		}
		catch (Exception e){
			lines = null;
			e.printStackTrace();
		}
		finally {
			closeReader(reader);
		}
		
		return lines;
	}
	
	public static void closeReader(Reader reader){
		
		try {
			reader.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static String hardcoreTrim(String value){
		
		if (value == null){
			return null;
		}
		
		int startOfFirstNonSpaceCharacter = -1;
		int startOfLastNonSpaceCharacter = -1;
		
		int firstIndex = 0;
		int lastIndex = value.length() - 1;
		boolean keepGoing = firstIndex < lastIndex;
		
		int valueLength = value.length();
		
		while (keepGoing){
			
			if (startOfFirstNonSpaceCharacter == -1 && firstIndex < valueLength){
				char firstChar = value.charAt(firstIndex);
				if (!Character.isSpaceChar(firstChar)){
					startOfFirstNonSpaceCharacter = firstIndex;
				}
				else {
					firstIndex++;
				}
			}
			
			if (startOfLastNonSpaceCharacter == -1 && lastIndex > 0){
				char lastChar = value.charAt(lastIndex);
				if (!Character.isSpaceChar(lastChar)){
					startOfLastNonSpaceCharacter = lastIndex;
				}
				else {
					lastIndex--;
				}
			}
			
			keepGoing = false;
			
			if (firstIndex > 0 && firstIndex < valueLength &&
					lastIndex > 0 && lastIndex < valueLength &&
					firstIndex < lastIndex){
				if (startOfFirstNonSpaceCharacter == -1 || startOfLastNonSpaceCharacter == -1){
					keepGoing = true;
				}
			}
		}
		
		String hardcoreTrimmed = null;
		int hardcoreTrimmedLength = (startOfLastNonSpaceCharacter + 1) - startOfFirstNonSpaceCharacter;
		if (hardcoreTrimmedLength == valueLength){
			return value;
		}
		
		if (startOfFirstNonSpaceCharacter != -1 && startOfLastNonSpaceCharacter != -1){
			hardcoreTrimmed = value.substring(startOfFirstNonSpaceCharacter, startOfLastNonSpaceCharacter + 1);
		}
		
		return hardcoreTrimmed;
	}

	public static List<String> delimitedStringToList(String value, String delimiter){
		
		if (value == null || delimiter == null){
			return null;
		}
		
		String[] valuesArray = value.split(delimiter);
		
		List<String> values = new ArrayList<String>(valuesArray.length);
		
		for (int index = 0; index < valuesArray.length; index++){
			String currentValue = valuesArray[index];
			currentValue = hardcoreTrim(currentValue);
			values.add(currentValue);
		}
		
		return values;
	}
	
	public static String[] fillArray(String[] array, int size, String fillString){
		
		if (array == null){
			return null;
		}
		
		String[] fillArray = new String[size];
		
		for (int index = 0; index < fillArray.length; index++){
			String fillValue = fillString;
			if (index < array.length){
				fillValue = array[index];
			}
			fillArray[index] = fillValue;
		}
		
		return fillArray;
	}
	
	public static String unNull(String value){
		
		if (value == null){
			return "";
		}
		
		return value;
	}
	
	public static Integer toInteger(String value){
		
		Integer integer = null;
		try {
			integer = Integer.parseInt(value);
		}
		catch (Exception e){
			integer = null;
		}
		
		return integer;
	}
	
	public static int parseInt(String intString, int defaultValue){
		
		int parsedInt = defaultValue;
		
		try {
			parsedInt = Integer.parseInt(intString);
		}
		catch (Exception e){
			parsedInt = defaultValue;
		}
		
		return parsedInt;
	}
	
	public static String replaceUrlCharacters(String value){
		
		String replacedValue = value.replace("%20", " ");
		
		return replacedValue;
	}
	
	public static String formatNormalDouble(double value){
		
		String formattedDouble = formatDouble(value, "0.000");
		
		return formattedDouble;
	}
	
	public static String formatDouble(double value, String format){
		
		if (format == null){
			return null;
		}
		
		DecimalFormat formatter = new DecimalFormat(format);
		
		String formattedValue = formatter.format(value);
		
		return formattedValue;
	}
	
	public static boolean isBlank(String value){
		
		if (value == null){
			return true;
		}
		
		if (value.trim().length() == 0){
			return true;
		}
		
		return false;
	}
}
