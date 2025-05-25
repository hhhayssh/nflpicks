package nflpicks;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Util {
	
	private static final Log log = LogFactory.getLog(Util.class);
	
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
		
		if (reader == null){
			return;
		}
		
		try {
			reader.close();
		}
		catch (Exception e){
			log.error("Error closing reader!", e);
		}
	}
	
	public static void closeWriter(Writer writer){
		
		if (writer == null){
			return;
		}
		
		try {
			writer.close();
		}
		catch (Exception e){
			log.error("Error closing writer!", e);
		}
	}
	
	public static void closeOutputStream(OutputStream stream){
		
		if (stream == null){
			return;
		}
		
		try {
			stream.close();
		}
		catch (Exception e){
			log.error("Error closing output stream!", e);
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
	
	public static List<String> getCsvValues(String line){
		
		List<String> values = delimitedStringToList(line, ",");
		
		return values;
	}
	
	public static List<String> getCsvValues(String line, boolean skipNull){
		
		List<String> values = delimitedStringToList(line, ",", skipNull);
		
		return values;
	}

	public static List<String> delimitedStringToList(String value, String delimiter){
		
		if (value == null || delimiter == null){
			return null;
		}
		
		List<String> values = delimitedStringToList(value, delimiter, true);
		
		return values;
	}
	
	public static List<String> delimitedStringToList(String value, String delimiter, boolean skipNull){
		
		if (value == null || delimiter == null){
			return null;
		}
		
		String[] valuesArray = value.split(delimiter, -1);
		
		List<String> values = new ArrayList<String>(valuesArray.length);
		
		for (int index = 0; index < valuesArray.length; index++){
			String currentValue = valuesArray[index];
			
			currentValue = hardcoreTrim(currentValue);
			
			if (currentValue == null && skipNull) {
				continue;
			}
			
			values.add(currentValue);
		}
		
		return values;
	}
	
	public static List<Integer> delimitedStringToIntegerList(String value, String delimiter){
		
		if (value == null || delimiter == null){
			return null;
		}
		
		String[] valuesArray = value.split(delimiter, -1);
		
		List<Integer> values = new ArrayList<Integer>(valuesArray.length);
		
		for (int index = 0; index < valuesArray.length; index++){
			String currentValue = valuesArray[index];
			currentValue = hardcoreTrim(currentValue);
			values.add(Integer.parseInt(currentValue));
		}
		
		return values;
	}
	
	public static String toCsvString(List<String> values){
		
		String csvString = toDelimitedString(values, ",", true);
		
		return csvString;
	}
	
	public static String toDelimitedString(List<String> values, String delimiter, boolean trimValues){
		
		if (values == null || delimiter == null){
			return null;
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int index = 0; index < values.size(); index++){
			String value = values.get(index);
			
			String valueToUse = value;
			if (trimValues){
				valueToUse = valueToUse.trim();
			}
			
			if (index > 0){
				stringBuilder.append(delimiter);
			}
			
			stringBuilder.append(valueToUse);
		}
		
		String delimitedString = stringBuilder.toString();
		
		return delimitedString;
		
	}
	
	public static List<Integer> convertStringsToIntegers(List<String> stringValues){
		
		if (stringValues == null){
			return null;
		}
		
		List<Integer> integerValues = new ArrayList<Integer>();
		
		for (int index = 0; index < stringValues.size(); index++){
			String stringValue = stringValues.get(index);
			
			Integer integerValue = Integer.parseInt(stringValue);
			
			integerValues.add(integerValue);
		}
		
		return integerValues;
		
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
	
	public static String toEmptyStringIfNull(String value){
		
		if (value == null){
			return "";
		}
		
		return value;
	}
	
	public static List<Integer> toIntegers(List<String> values){
		
		if (values == null){
			return null;
		}
		
		List<Integer> integerValues = new ArrayList<Integer>(values.size());
		
		for (int index = 0; index < values.size(); index++){
			String value = values.get(index);
			Integer integerValue = toInteger(value);
			integerValues.add(integerValue);
		}
		
		return integerValues;
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
		
		if (intString == null){
			return defaultValue;
		}
		
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
		
		if (value == null){
			return null;
		}
		
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
	
	public static String getCurrentYear(){
		
		int year = Calendar.getInstance().get(Calendar.YEAR);
		
		String currentYear = String.valueOf(year);
		
		return currentYear;
	}
	
	public static void writeBufferedBytes(byte[] bytes, OutputStream outputStream){
		writeBufferedBytes(bytes, 8192, outputStream);
	}
	
	public static void writeBufferedBytes(byte[] bytes, int bufferSize, OutputStream outputStream){
		
		ByteArrayInputStream inputStream = null;
		
		int bytesRead = 0;
		int totalBytesRead = 0;
		
		try {
			byte[] buffer = new byte[bufferSize];

			inputStream = new ByteArrayInputStream(bytes);

			while ((bytesRead = inputStream.read(buffer)) != -1){
				totalBytesRead = totalBytesRead + bytesRead;
				outputStream.write(buffer, 0, bytesRead);
			}
		}
		catch (Exception e){
			log.error("Error writing buffered bytes!", e);
		}
		finally {
		}
		
	}
	
	public static boolean hasSomething(String value){
		
		if (value == null || value.length() == 0){
			return false;
		}
		
		return true;
	}
	
	public static boolean hasSomething(List values){
		
		if (values == null || values.size() == 0){
			return false;
		}
		
		return true;
	}
	
	public static int getLineCount(String filename){
		
		int lineCount = 0;
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			
			String line = "";
			while ((line = reader.readLine()) != null){
				lineCount++;
			}
		}
		catch (Exception e){
			log.error("Error getting line count!  current line = " + lineCount + ", filename = " + filename);
		}
		finally {
			closeReader(reader);
		}
		
		return lineCount;
	}
	
	public static List<String> filterListWithOtherList(List<String> values1, List<String> values2){
		
		if (values1 == null || values2 == null){
			return null;
		}
		
		List<String> filteredValues = new ArrayList<String>();
		
		for (int index = 0; index < values1.size(); index++){
			String value1 = values1.get(index);
			
			if (values2.contains(value1)){
				filteredValues.add(value1);
			}
		}
		
		return filteredValues;
	}
	
	public static List<String> combineLists(List<String> values1, List<String> values2){
		
		List<String> combinedValues = new ArrayList<String>();
		
		if (values1 != null){
			combinedValues.addAll(values1);
		}
		
		if (values2 != null){
			combinedValues.addAll(values2);
		}
		
		return combinedValues;
	}
}
