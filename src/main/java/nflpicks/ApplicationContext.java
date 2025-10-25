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
	 * The properties for the database connection pool.
	 * 
	 */
	protected PoolProperties poolProperties;
	
	/**
	 * 
	 * The object that lets us connect to the database.
	 * 
	 */
	protected DataSource dataSource;
	
	/**
	 * 
	 * The property that says the name of the jdbc driver we should use (required).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_DRIVER_CLASS_NAME = "nflpicks.database.driverClassName";
	
	/**
	 * 
	 * The property that has the url we use to connect to the database (required).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_URL = "nflpicks.database.url";
	
	/**
	 * 
	 * The property name that has the username we use to connect to the database (required).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_USERNAME = "nflpicks.database.username";
	
	/**
	 * 
	 * The property name that has the password we use to connect to the database (required).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_PASSWORD = "nflpicks.database.password";
	
	/**
	 * 
	 * Says the max active db connections in the pool (optional).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_MAX_ACTIVE = "nflpicks.database.max.active";
	
	/**
	 * 
	 * Says how old a connection should be allowed to be (milliseconds, optional).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_MAX_AGE = "nflpicks.database.max.age";
	
	/**
	 * 
	 * Says how many connections should be in the pool to start with (optional).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_INITIAL_SIZE = "nflpicks.database.initial.size";
	
	/**
	 * 
	 * Says the max number of idle connections there should be in the pool (optional).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_MAX_IDLE = "nflpicks.database.max.idle";
	
	/**
	 * 
	 * Says the minimum number of idle connections there should be in the pool (optional).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_MIN_IDLE = "nflpicks.database.min.idle";
	
	/**
	 * 
	 * Says whether we should test whether a connection is ok when it's borrowed from the pool (optional).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_TEST_ON_BORROW = "nflpicks.database.test.on.borrow";
	
	/**
	 * 
	 * The validation query we should use to check the connection (optional).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_VALIDATION_QUERY = "nflpicks.database.validation.query";
	
	/**
	 * 
	 * Whether we should test a connection before returning it to the pool (optional).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_TEST_ON_RETURN = "nflpicks.database.test.on.return";
	
	/**
	 * 
	 * Whether we should test connections in the pool while they're idle (optional).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_TEST_WHILE_IDLE = "nflpicks.database.test.while.idle";
	
	/**
	 * 
	 * Whether we should remove abandoned connections from the pool (optional).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_REMOVE_ABANDONED = "nflpicks.database.remove.abandoned";
	
	/**
	 * 
	 * How long we should wait to remove an abandoned connection (in milliseconds, optional).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_REMOVE_ABANDONED_TIMEOUT = "nflpicks.database.remove.abandoned.timeout";
	
	/**
	 * 
	 * Whether we should log abandoned connections (optional).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_LOG_ABANDONED = "nflpicks.database.log.abandoned";
	
	/**
	 * 
	 * Whether we should log validation errors (optional).
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_LOG_VALIDATION_ERRORS = "nflpicks.database.log.validation.errors";
	
	/**
	 * 
	 * How often we should validate connections in the pool (milliseconds, optional).l
	 * 
	 */
	protected static final String NFL_PICKS_DATABASE_KEY_VALIDATION_INTERVAL = "nflpicks.database.validation.interval";
	
	/**
	 * 
	 * The partial part of the key that's used to make sure not everybody can edit
	 * stuff.
	 * 
	 */
	protected static final String EDIT_KEY = "nflpicks.edit.key";
	
	/**
	 * 
	 * Same deal here.  The partial part of the key that's used for requests to make sure
	 * it's not public.
	 * 
	 */
	protected static final String ADMIN_KEY = "nflpicks.admin.key";
	
	/**
	 * 
	 * The key that will say whether divisions are enabled or not (so I can turn it off pretty quickly
	 * if I want to).
	 * 
	 */
	protected static final String DIVISIONS_ENABLED_KEY = "nflpicks.divisions.enabled";

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
	 * A convenience function so you can re-initialize the application context.
	 * 
	 */
	public void reinitialize() {
		
		log.info("Reinitializing ...");
		
		//Flip the switch that says it's initialized.
		setInitialized(false);
		
		initialize();
		
		log.info("Done reinitializing.");
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
		
		boolean initialized = getInitialized();
		if (initialized){
			log.info("Context already initialized.");
			return;
		}
		
		setPropertiesFilename(propertiesFilename);
		setContext(new HashMap<String, Object>());
		setProperties(new HashMap<String, String>());
		
		loadProperties(propertiesFilename);
		
		initializeDataSource();
		
		setInitialized(true);
		
		log.info("Application context initialized.");
	}
	
	/**
	 * 
	 * Initializes the data source connection to the database so that
	 * we can get database connections.  Not much to it.
	 * 
	 */
	protected void initializeDataSource(){
		
		log.info("Initializing data source ...");
		
		PoolProperties poolProperties = createDatabaseConnectionPoolProperties();
		
		DataSource dataSource = new DataSource(poolProperties);
		
		setDataSource(dataSource);
		
		log.info("Data source initialized.");
		
	}
	
	/**
	 * 
	 * This initializes the database connection pool properties variable using
	 * the database connection variables.
	 * 
	 * I made this because, sometimes, we'd have a problem getting a good database connection.
	 * That's because I just went with the defaults on the properties (just used username, password,
	 * and url).  That worked ok until tomcat 11, when I think the jdbc connection management stuff
	 * must have changed or something.
	 * 
	 * Anyway, according to this page:
	 * 
	 * 		https://stackoverflow.com/questions/41998490/tomcat-jdbc-connection-pool-testonborrow-vs-testwhileidle
	 * 
	 * I should probably use "test on borrow = true".  I'm also going to use a validation query too, and add
	 * in all the other options too just in case I need them later.
	 * 
	 */
	protected PoolProperties createDatabaseConnectionPoolProperties() {
		
		//Steps to do:
		//	1. Pull out all the properties and put them in the ... properties object.
		
		//I think the ones I want to use are "test on borrow" and probably set the validation query:
		//	https://stackoverflow.com/questions/41998490/tomcat-jdbc-connection-pool-testonborrow-vs-testwhileidle
		//Basically, I don't want it to get stuck with a bad connection.
		//If it does, I'd like to be able to reinitialize the connections.
		//If that doesn't work, then there's something wrong with the database.
		
		log.info("Creating database connection pool properties ...");
		
		PoolProperties poolProperties = new PoolProperties();
		
		String databaseDriverClassName = getProperty(NFL_PICKS_DATABASE_KEY_DRIVER_CLASS_NAME);
		log.info("jdbcDriverClassName = " + databaseDriverClassName);
		poolProperties.setDriverClassName(databaseDriverClassName);
		
		String databaseUrl = getProperty(NFL_PICKS_DATABASE_KEY_URL);
		log.info("jdbcUrl = " + databaseUrl);
		poolProperties.setUrl(databaseUrl);
		
		String databaseUsername = getProperty(NFL_PICKS_DATABASE_KEY_USERNAME);
		log.info("jdbcUsername = " + databaseUsername);
		poolProperties.setUsername(databaseUsername);
		
		String databasePassword = getProperty(NFL_PICKS_DATABASE_KEY_PASSWORD);
		poolProperties.setPassword(databasePassword);
		
		//Optional properties ...
		String databaseInitialSize = getProperty(NFL_PICKS_DATABASE_KEY_INITIAL_SIZE);
		if (databaseInitialSize != null) {
			log.info("databaseInitialSize = " + databaseInitialSize);
			poolProperties.setInitialSize(Util.toInteger(databaseInitialSize));
		}
		else {
			log.info("databaseInitialSize = default");
		}
		
		String databaseMaxActive = getProperty(NFL_PICKS_DATABASE_KEY_MAX_ACTIVE);
		if (databaseMaxActive != null) {
			log.info("databaseMaxActive = " + databaseMaxActive);
			poolProperties.setMaxActive(Util.toInteger(databaseMaxActive));
		}
		else {
			log.info("databaseMaxActive = default");
		}
		
		String databaseMaxIdle = getProperty(NFL_PICKS_DATABASE_KEY_MAX_IDLE);
		if (databaseMaxIdle != null) {
			log.info("databaseMaxIdle = " + databaseMaxIdle);
			poolProperties.setMaxIdle(Util.toInteger(databaseMaxIdle));
		}
		else {
			log.info("databaseMaxIdle = default");
		}
		
		String databaseMinIdle = getProperty(NFL_PICKS_DATABASE_KEY_MIN_IDLE);
		if (databaseMinIdle != null) {
			log.info("databaseMinIdle = " + databaseMinIdle);
			poolProperties.setMinIdle(Util.toInteger(databaseMinIdle));
		}
		else {
			log.info("databaseMinIdle = default");
		}
		
		String databaseMaxAge = getProperty(NFL_PICKS_DATABASE_KEY_MAX_AGE);
		if (databaseMaxAge != null) {
			log.info("databaseMaxAge = " + databaseMaxAge);
			poolProperties.setMaxAge(Util.toLong(databaseMaxAge));
		}
		else {
			log.info("databaseMaxAge = default");
		}
		
		String databaseTestOnBorrow = getProperty(NFL_PICKS_DATABASE_KEY_TEST_ON_BORROW);
		if (databaseTestOnBorrow != null) {
			log.info("databaseTestOnBorrow = " + databaseTestOnBorrow);
			poolProperties.setTestOnBorrow(Util.toBoolean(databaseTestOnBorrow));
		}
		else {
			log.info("databaseTestOnBorrow = default");
		}
		
		String databaseValidationQuery = getProperty(NFL_PICKS_DATABASE_KEY_VALIDATION_QUERY);
		if (databaseValidationQuery != null) {
			log.info("databaseValidationQuery = " + databaseValidationQuery);
			poolProperties.setValidationQuery(databaseValidationQuery);
		}
		else {
			log.info("databaseValidationQuery = default");
		}
		
		String databaseValidationInterval = getProperty(NFL_PICKS_DATABASE_KEY_VALIDATION_INTERVAL);
		if (databaseValidationInterval != null) {
			log.info("databaseValidationInterval = " + databaseValidationInterval);
			poolProperties.setValidationInterval(Util.toLong(databaseValidationInterval));
		}
		else {
			log.info("databaseValidationInterval = default");
		}
		
		String databaseTestOnReturn = getProperty(NFL_PICKS_DATABASE_KEY_TEST_ON_RETURN);
		if (databaseTestOnReturn != null) {
			log.info("databaseTestOnReturn = " + databaseTestOnReturn);
			poolProperties.setTestOnReturn(Util.toBoolean(databaseTestOnReturn));
		}
		else {
			log.info("databaseTestOnReturn = default");
		}
		
		String databaseTestWhileIdle = getProperty(NFL_PICKS_DATABASE_KEY_TEST_WHILE_IDLE);
		if (databaseTestWhileIdle != null) {
			log.info("databaseTestWhileIdle = " + databaseTestWhileIdle);
			poolProperties.setTestWhileIdle(Util.toBoolean(databaseTestWhileIdle));
		}
		else {
			log.info("databaseTestWhileIdle = default");
		}
		
		String databaseRemoveAbandoned = getProperty(NFL_PICKS_DATABASE_KEY_REMOVE_ABANDONED);
		if (databaseRemoveAbandoned != null) {
			log.info("databaseRemoveAbandoned = " + databaseRemoveAbandoned);
			poolProperties.setRemoveAbandoned(Util.toBoolean(databaseRemoveAbandoned));
		}
		else {
			log.info("databaseRemoveAbandoned = default");
		}

		String databaseRemoveAbandonedTimeout = getProperty(NFL_PICKS_DATABASE_KEY_REMOVE_ABANDONED_TIMEOUT);
		if (databaseRemoveAbandonedTimeout != null) {
			log.info("databaseRemoveAbandonedTimeout = " + databaseRemoveAbandonedTimeout);
			poolProperties.setRemoveAbandonedTimeout(Util.toInteger(databaseRemoveAbandonedTimeout));
		}
		else {
			log.info("databaseRemoveAbandonedTimeout = default");
		}
		
		String databaseLogAbandoned = getProperty(NFL_PICKS_DATABASE_KEY_LOG_ABANDONED);
		if (databaseLogAbandoned != null) {
			log.info("databaseLogAbandoned = " + databaseLogAbandoned);
			poolProperties.setLogAbandoned(Util.toBoolean(databaseLogAbandoned));
		}
		else {
			log.info("databaseLogAbandoned = default");
		}
		
		//This is so we explicitly have to say when to commit.
		//I'm doing it that way because this is my program and I feel
		//like it.
		poolProperties.setDefaultAutoCommit(false);
		
		log.info("Database connection pool properties created.");
		
		return poolProperties;
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
	 * Gets the value of the property with the key and returns the default value
	 * if the property doesn't exist.
	 * 
	 * @param key
	 * @param defaulValue
	 * @return
	 */
	public String getProperty(String key, String defaultValue) {
		
		String value = getProperty(key);
		
		if (value == null) {
			value = defaultValue;
		}
		
		return value;
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
	 * Gets the property that's used to see whether somebody can use admin features.
	 * 
	 * @return
	 */
	public String getAdminKey(){
		
		String editKey = getProperty(ADMIN_KEY);
		
		return editKey;
	}
	
	/**
	 * 
	 * Sets the property that's used to see whether somebody can use admin features.
	 * 
	 * @param editKey
	 */
	public void setAdminKey(String editKey){
		setProperty(ADMIN_KEY, editKey);
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
	 * A convenience function for logging all the database connection proeprties when initializing.
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @param defaultPropertyValue
	 */
	protected void logDatabaseConnectionProperty(String propertyName, String propertyValue, String defaultPropertyValue) {
		
		if (propertyValue != null) {
			log.info(propertyName + " = " + propertyValue);
		}
		else {
			log.info(propertyName + " = " + propertyValue + " (default)");
		}
	}
	
	public boolean getInitialized() {
		return initialized;
	}
	
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public String getPropertiesFilename() {
		return propertiesFilename;
	}

	public void setPropertiesFilename(String propertiesFilename) {
		this.propertiesFilename = propertiesFilename;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public PoolProperties getPoolProperties() {
		return poolProperties;
	}

	public void setPoolProperties(PoolProperties poolProperties) {
		this.poolProperties = poolProperties;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 
	 * So we can see the properties in the debugger.
	 * 
	 */
	public String toString(){
		
		String thisObjectAsAString = "propertiesFilename = " + propertiesFilename + 
									 ", context = " + context + 
									 ", initialized = " + initialized;
		
		return thisObjectAsAString;
	}
}
