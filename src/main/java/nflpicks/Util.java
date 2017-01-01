package nflpicks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
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
}
