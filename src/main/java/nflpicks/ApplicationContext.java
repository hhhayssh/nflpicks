package nflpicks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * 
 * This class is basically a globally (to the jvm) accessible object so that
 * we initialize things like the database connection in one place and have one
 * place to go to get it (aka a singleton with a map).  This way, all the classes
 * can access what they need pretty easily and they're easy to set up.
 * 
 * Right now, it pretty much only has the database connection
 * but might get more stuff later as I (maybe) add more features.
 * 
 * I thought (for like 2 seconds) about using some kind of "framework" that does
 * this kind of thing for you, but then I thought, "Hey, this is MY program and I'm
 * doing it how IIIII want to do it."
 * 
 * @author albundy
 *
 */
public class ApplicationContext {
	
	private static final Log log = LogFactory.getLog(ApplicationContext.class);
	
	/**
	 * 
	 * Whether the context has been initialized yet.
	 * 
	 */
	protected boolean initialized = false;
	
	/**
	 * 
	 * The name of the properties file that was used to make
	 * the context.
	 * 
	 */
	protected String propertiesFilename;
	
	/**
	 * 
	 * The map that has the variables the context holds.
	 * 
	 */
	protected Map<String, Object> context;
	
	/**
	 * 
	 * A map for holding properties of the context.  Making this
	 * separate from the context map so that we don't have accidental
	 * collisions.
	 * 
	 */
	protected Map<String, String> properties;

	/**
	 *
	 * The only instance we should have.  This is so we have one context that's shared
	 * by all the different classes in the program.
	 * 
	 */
	private static final ApplicationContext applicationContext = new ApplicationContext();
	
	/**
	 * 
	 * The object that lets us connect to the database.
	 * 
	 */
	protected DataSource dataSource;
	
	/**
	 * 
	 * The property that says the name of the jdbc driver we should use.
	 * 
	 */
	protected static final String JDBC_DRIVER_CLASS_NAME = "nflpicks.jdbc.driverClassName";
	
	/**
	 * 
	 * The property that has the url we use to connect to the database.
	 * 
	 */
	protected static final String JDBC_URL_KEY = "nflpicks.jdbc.url";
	
	/**
	 * 
	 * The property name that has the username we use to connect to the database.
	 * 
	 */
	protected static final String JDBC_USERNAME_KEY = "nflpicks.jdbc.username";
	
	/**
	 * 
	 * The property name that has the password we use to connect to the database.
	 * 
	 */
	protected static final String JDBC_PASSWORD_KEY = "nflpicks.jdbc.password";
	
	/**
	 * 
	 * The partial part of the key that's used to make sure not everybody can edit
	 * stuff.
	 * 
	 */
	protected static final String EDIT_KEY = "nflpicks.edit.key";
	
	/**
	 * 
	 * The key that will say whether divisions are enabled or not (so I can turn it off pretty quickly
	 * if I want to).
	 * 
	 */
	protected static final String DIVISIONS_ENABLED_KEY = "nflpicks.divisions.enabled";

	/**
	 * 
	 * The name of the driver we use to connect to the database.
	 * 
	 */
	protected String jdbcDriverClassName;
	
	/**
	 * 
	 * The url we use to connect to the database.
	 * 
	 */
	protected String jdbcUrl;
	
	/**
	 * 
	 * The username we use to connect to the database.
	 * 
	 */
	protected String jdbcUsername;
	
	/**
	 * 
	 * The password we use to connect to the database.
	 * 
	 */
	protected String jdbcPassword;
	
	/**
	 * 
	 * This lets it put different players in different context paths so we
	 * can kind of have multiple picks things going on at the same time.
	 * The keys are the context paths and the values are the player names that
	 * should be associated with the context. 
	 * 
	 */
	protected Map<String, List<String>> contextPathPlayerMapping; 
	
	/**
	 * 
	 * This class is a singleton so its constructor is private.
	 * 
	 */
	private ApplicationContext(){
	}

	/**
	 * 
	 * This class is a singleton and this is how other classes get at the 
	 * singleton instance.
	 * 
	 * @return
	 */
	public static ApplicationContext getContext(){
		return applicationContext;
	}
	
	/**
	 * 
	 * Initializes the context using the properties file that is set with the 
	 * nflpicks.properties.file system property.
	 * 
	 */
	public void initialize(){
		String propertiesFileName = System.getProperty(NFLPicksConstants.NFL_PICKS_PROPERTIES_FILENAME_PROPERTY);
		
		initialize(propertiesFileName);
	}
	
	/**
	 * 
	 * Initializes the application context using the properties file with the given name.
	 * 
	 * If the context is already initialized, it won't do anything and will just return.
	 * Otherwise, it'll load all the properties and use them to initialize the database
	 * connection.
	 * 
	 * @param propertiesFilename
	 */
	public void initialize(String propertiesFilename){
		
		//Steps to do:
		//	1. If the context has already been initialized, there's nothing to do
		//	   so just quit.
		//	2. Otherwise, load all the properties from the file with the given name.
		//	3. Pull out the values of the properties that let us connect to the
		//	   database and connect to it.
		//	4. Initialize the context to player name mapping.
		
		log.info("Initializing application context...");
		
		if (initialized){
			log.info("Context already initialized.");
			return;
		}
		
		this.propertiesFilename = propertiesFilename;
		this.context = new HashMap<String, Object>();
		this.properties = new HashMap<String, String>();
		
		loadProperties(propertiesFilename);
		
		this.jdbcDriverClassName = getProperty(JDBC_DRIVER_CLASS_NAME);
		this.jdbcUrl = getProperty(JDBC_URL_KEY);
		this.jdbcUsername = getProperty(JDBC_USERNAME_KEY);
		this.jdbcPassword = getProperty(JDBC_PASSWORD_KEY);
		
		initializeDataSource();
		
		this.initialized = true;
		
		log.info("Application context initialized.");
	}
	
	/**
	 * 
	 * Initializes the data source connection to the database so that
	 * we can get database connections.  Not much to it.  The only
	 * interesting thing is that it sets defaultAutoCommit to false
	 * because we only want to commit when we explicitly say so. 
	 * 
	 */
	protected void initializeDataSource(){

		log.info("Initializing data source...");
		log.info("jdbcDriverClassName = " + jdbcDriverClassName);
		log.info("jdbcUrl = " + jdbcUrl);
		log.info("jdbcUsername = " + jdbcUsername);
		
		PoolProperties poolProperties = new PoolProperties();
		poolProperties.setDriverClassName(jdbcDriverClassName);
		poolProperties.setUrl(jdbcUrl);
		poolProperties.setUsername(jdbcUsername);
		poolProperties.setPassword(jdbcPassword);
		//This is so we explicitly have to say when to commit.
		//I'm doing it that way because this is my program and I feel
		//like it.
		poolProperties.setDefaultAutoCommit(false);
		
		dataSource = new DataSource(poolProperties);
		
		log.info("Data source initialized.");
	}
	
	/**
	 * 
	 * Gets the data source object that can be used to get a database connection.
	 * 
	 * @return
	 */
	public DataSource getDataSource(){
		return dataSource;
	}
	
	/**
	 * 
	 * This function loads the properties from the file with the given name
	 * into this application's properties map so it can use them.
	 * 
	 * @param propertiesFilename
	 */
	protected void loadProperties(String propertiesFilename){

		log.info("Loading properties... propertiesFileName = " + propertiesFilename);
		
		Properties properties = new Properties();
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(propertiesFilename));
			properties.load(reader);
			for (String key : properties.stringPropertyNames()){
		    	String value = properties.getProperty(key);
		    	setProperty(key, value);
		    }
		}
		catch (Exception e){
			log.error("Error loading properties file! propertiesFilename = " + propertiesFilename, e);
		}
		finally {
			Util.closeReader(reader);
		}
		
		log.info("Done loading properties.");
	}
	
	/**
	 * 
	 * This function gets the property with the given key.
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key){
		return properties.get(key);
	}
	
	/**
	 * 
	 * This function sets the property with the given key to the given
	 * value.
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value){
		properties.put(key, value);
	}
	
	/**
	 * 
	 * This function gets the object from the context with the given key.
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key){
		return context.get(key);
	}
 	
	/**
	 * 
	 * This function sets the key in the context to the given value.
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, Object value){
		context.put(key, value);
	}

	/**
	 * 
	 * Gets the property that's used to see whether somebody can edit something.
	 * 
	 * @return
	 */
	public String getEditKey(){
		
		String editKey = getProperty(EDIT_KEY);
		
		return editKey;
	}
	
	/**
	 * 
	 * Sets the property that's used to see whether somebody can edit something.
	 * 
	 * @param editKey
	 */
	public void setEditKey(String editKey){
		setProperty(EDIT_KEY, editKey);
	}
	
	/**
	 * 
	 * Gets the property that says whether divisions are enabled or not.
	 * 
	 * @return
	 */
	public String getDivisionsEnabled(){
		
		String divisionsEnabled = getProperty(DIVISIONS_ENABLED_KEY);
		
		return divisionsEnabled;
	}
	
	/**
	 * 
	 * Sets the property that says whether divisions are enabled or not.
	 * 
	 * @param divisionsEnabled
	 */
	public void setDivisionsEnabled(String divisionsEnabled){
		setProperty(DIVISIONS_ENABLED_KEY, divisionsEnabled);
	}

	/**
	 * 
	 * So we can see the properties in the debugger.
	 * 
	 */
	public String toString(){
		
		String thisObjectAsAString = "propertiesFilename = " + propertiesFilename + 
									 ", context = " + context + 
									 ", properties = " + properties +
									 ", jdbcDriverClassName = " + jdbcDriverClassName + 
									 ", jdbcUrl = " + jdbcUrl + 
									 ", jdbcUsername = " + jdbcUsername + 
									 ", initialized = " + initialized;
		
		return thisObjectAsAString;
	}
}
