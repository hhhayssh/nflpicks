package nflpicks;

import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	
	private static final Log log = LogFactory.getLog(NFLPicksDataManager.class);
	
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
		
		NFLPicksDataManager dataManager = new NFLPicksDataManager();
		
		if (args.length == 0){
			dataManager.importManually();
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
			
			ApplicationContext.getContext().initialize(propertiesFilename);
			
			NFLPicksModelDataService modelDataService = new NFLPicksModelDataService(ApplicationContext.getContext().getDataSource());
			
			NFLPicksStatsDataService statsDataService = new NFLPicksStatsDataService(ApplicationContext.getContext().getDataSource(), modelDataService);
			
			dataManager.process(type, importType, exportType, filename, modelDataService, statsDataService);
		}
		else {
			System.out.println("Bad input!");
			return;
		}
	}
	
	/**
	 * 
	 * This function will let us import manually from the console.  It will run in a loop so we can import or
	 * export more than one file without restarting the program.
	 * 
	 */
	protected void importManually(){
		
		//Steps to do:
		//	1. Get the properties file that says what database to connect to.
		//	2. Ask what to do.
		//	3. Do it.
		//	4. Ask if we should do something else.
		
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Properties file:");
		String propertiesFilename = scanner.nextLine();
		
		ApplicationContext.getContext().initialize(propertiesFilename);
		
		NFLPicksModelDataService modelDataService = new NFLPicksModelDataService(ApplicationContext.getContext().getDataSource());
		
		NFLPicksStatsDataService statsDataService = new NFLPicksStatsDataService(ApplicationContext.getContext().getDataSource(), modelDataService);
		
		boolean keepGoing = true;
		
		while (keepGoing){
			keepGoing = false;
			
			System.out.println("Import or export:");
			String type = scanner.nextLine();
			
			String filename = null;
			String importType = null;
			String exportType = null;
			
			if (NFLPicksConstants.DATA_MANAGEMENT_TYPE_IMPORT.equals(type)){
				System.out.println("Import type (picks, team_data, division_data, or player_division_data):");
				importType = scanner.nextLine();
				
				System.out.println("Import file:");
				filename = scanner.nextLine();
			}
			else if (NFLPicksConstants.DATA_MANAGEMENT_TYPE_EXPORT.equals(type)){
				System.out.println("Export type (picks or team_data, division_data, or player_division_data):");
				exportType = scanner.nextLine();
				
				System.out.println("Export file:");
				filename = scanner.nextLine();
			}
			
			process(type, importType, exportType, filename, modelDataService, statsDataService);
			
			System.out.println("Keep going (yes or no)?");
			
			String keepGoingValue = scanner.nextLine().trim();
			
			if ("yes".equals(keepGoingValue)){
				keepGoing = true;
			}
		}
		
		scanner.close();
	}
	
	/**
	 * 
	 * This will call the functions that do the work.  Put it in a separate function so it could be called by other functions.
	 * 
	 * @param type
	 * @param importType
	 * @param exportType
	 * @param filename
	 * @param modelDataService
	 * @param statsDataService
	 */
	protected void process(String type, String importType, String exportType, String filename, NFLPicksModelDataService modelDataService,
			NFLPicksStatsDataService statsDataService){
		
		if (NFLPicksConstants.DATA_MANAGEMENT_TYPE_IMPORT.equalsIgnoreCase(type)){
			NFLPicksDataImporter dataImporter = new NFLPicksDataImporter(modelDataService, statsDataService);
			
			if (NFLPicksConstants.DATA_MANAGEMENT_IMPORT_TYPE_PICKS.equalsIgnoreCase(importType)){
				dataImporter.importPicksData(filename);
			}
			else if (NFLPicksConstants.DATA_MANAGEMENT_IMPORT_TYPE_TEAM_DATA.equalsIgnoreCase(importType)){
				dataImporter.importTeamData(filename);
			}
			else if (NFLPicksConstants.DATA_MANAGEMENT_IMPORT_TYPE_DIVISION_DATA.equalsIgnoreCase(importType)){
				dataImporter.importDivisionData(filename);
			}
			else if (NFLPicksConstants.DATA_MANAGEMENT_IMPORT_TYPE_PLAYER_DIVISION_DATA.equalsIgnoreCase(importType)){
				dataImporter.importPlayerDivisionData(filename);
			}
			else {
				log.error("Well, that's just your opinion, man.");
			}
		}
		else if (NFLPicksConstants.DATA_MANAGEMENT_TYPE_EXPORT.equalsIgnoreCase(type)){
			NFLPicksDataExporter dataExporter = new NFLPicksDataExporter(modelDataService, statsDataService);
			
			if (NFLPicksConstants.DATA_MANAGEMENT_EXPORT_TYPE_PICKS.equalsIgnoreCase(exportType)){
				dataExporter.exportPicksData(filename);
			}
			else if (NFLPicksConstants.DATA_MANAGEMENT_EXPORT_TYPE_TEAM_DATA.equals(exportType)){
				dataExporter.exportTeamData(filename);
			}
			else if (NFLPicksConstants.DATA_MANAGEMENT_EXPORT_TYPE_DIVISION_DATA.equalsIgnoreCase(exportType)){
				dataExporter.exportDivisionData(filename);
			}
			else if (NFLPicksConstants.DATA_MANAGEMENT_EXPORT_TYPE_PLAYER_DIVISION_DATA.equalsIgnoreCase(exportType)){
				dataExporter.exportPlayerDivisionData(filename);
			}
			else {
				log.info("Huh?  What?");
			}
		}
	}
}
