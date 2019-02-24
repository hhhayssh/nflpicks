package nflpicks;

import java.util.Scanner;

/**
 * 
 * This class is here so we can do either importing or exporting from the command line.  You
 * can either import or export and, if you use the "nflpicks-data-manager" profile, you can
 * build a runnable jar (with a lib folder), so that you can run it like this:
 * 
 * 		java -jar nflpicks.jar import nflpicks.properties import-data.csv
 * 		java -jar nflpicks.jar export nflpicks.properties export-2018-01-01.csv
 * 
 * 		Or without the arguments and it'll ask for them: 
 * 
 * 		java -jar nflpicks.jar
 * 		
 * You have to have the "lib" folder right there with the jar too.  The manifest inside the
 * jar points to the "main" class (this one) and includes the "lib" folder on the class path
 * automatically.
 * 
 * @author albundy
 *
 */
public class NFLPicksDataManager {
	
	/**
	 * 
	 * This function will either import or export stuff.  You can tell it what to do on
	 * with the input arguments or, if you don't, it'll ask for them.  The arguments are:
	 * 
	 * 		1. What you want to do - either import or export.
	 * 		2. The full path to the properties file.
	 * 		3. The full path to the import or export file.
	 * 
	 * That's pretty much it.  If you build the project with the "nflpicks-data-manager" profile,
	 * it'll make a jar with the lib files and you can run it like: "java -jar nflpicks.jar" and 
	 * from there decide whether you want to import or export.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		//Steps to do:
		//	1. If there weren't any arguments, read them from the command line.
		//	2. Otherwise, if there were 3, read in the type, properties file, and 
		//	   import or export file from the command line.
		//	3. If there were some other number of arguments, the input's bad, so just quit.
		//	4. Initialize the context off the properties.
		//	5. Do the import or export.
		
		String type = null;
		String importType = null;
		String exportType = null;
		String propertiesFilename = null;
		String filename = null;
		
		if (args.length == 0){
			Scanner scanner = new Scanner(System.in);
			
			System.out.println("Properties file:");
			propertiesFilename = scanner.nextLine();
			
			System.out.println("Import or export:");
			type = scanner.nextLine();
			
			if (NFLPicksConstants.DATA_MANAGEMENT_TYPE_IMPORT.equals(type)){
				System.out.println("Import type (picks or team_data):");
				importType = scanner.nextLine();
				
				System.out.println("Import file:");
				filename = scanner.nextLine();
			}
			else if (NFLPicksConstants.DATA_MANAGEMENT_TYPE_EXPORT.equals(type)){
				System.out.println("Export type (picks or team_data):");
				exportType = scanner.nextLine();
				
				System.out.println("Export file:");
				filename = scanner.nextLine();
			}
			
			scanner.close();
		}
		else if (args.length == 3){
			propertiesFilename = args[0];
			type = args[1];
			
			if (NFLPicksConstants.DATA_MANAGEMENT_TYPE_IMPORT.equals(type)){
				importType = args[2];
			}
			else if (NFLPicksConstants.DATA_MANAGEMENT_TYPE_EXPORT.equals(type)){
				exportType = args[2];
			}
			
			filename = args[3];
		}
		else {
			System.out.println("Bad input!");
			return;
		}
		
		ApplicationContext.getContext().initialize(propertiesFilename);
		NFLPicksDataService dataService = new NFLPicksDataService(ApplicationContext.getContext().getDataSource());
		
		if (NFLPicksConstants.DATA_MANAGEMENT_TYPE_IMPORT.equals(type)){
			NFLPicksDataImporter dataImporter = new NFLPicksDataImporter(dataService);
			if (NFLPicksConstants.DATA_MANAGEMENT_IMPORT_TYPE_PICKS.equals(importType)){
				dataImporter.importPicksData(filename);
			}
			else if (NFLPicksConstants.DATA_MANAGEMENT_IMPORT_TYPE_TEAM_DATA.equals(importType)){
				dataImporter.importTeamData(filename);
			}
		}
		else if (NFLPicksConstants.DATA_MANAGEMENT_TYPE_EXPORT.equals(type)){
			NFLPicksDataExporter dataExporter = new NFLPicksDataExporter(dataService);
			
			if (NFLPicksConstants.DATA_MANAGEMENT_EXPORT_TYPE_PICKS.equals(exportType)){
				dataExporter.exportPicksData(filename);
			}
			else if (NFLPicksConstants.DATA_MANAGEMENT_EXPORT_TYPE_TEAM_DATA.equals(exportType)){
				dataExporter.exportTeamData(filename);
			}
		}
		else {
			System.out.println("You suck.");
			return;
		}
	}
}
