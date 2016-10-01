package nflpicks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class ApplicationContext {
	
	private static final Logger log = Logger.getLogger(ApplicationContext.class);
	
	protected boolean initialized = false;
	
	protected Map<String, Object> context;
	
	private static final ApplicationContext applicationContext = new ApplicationContext();
	
	protected static final String PROPERTIES_FILE_NAME = "nflpicks.properties.file";
	
	protected DataSource dataSource;
	
	protected static final String JDBC_DRIVER_CLASS_NAME = "nflpicks.jdbc.driverClassName";
	protected static final String JDBC_URL_KEY = "nflpicks.jdbc.url";
	protected static final String JDBC_USERNAME_KEY = "nflpicks.jdbc.username";
	protected static final String JDBC_PASSWORD_KEY = "nflpicks.jdbc.password";
	
	protected String jdbcDriverClassName;
	protected String jdbcUrl;
	protected String jdbcUsername;
	protected String jdbcPassword;
	
	private ApplicationContext(){
		
	}
	
	public static ApplicationContext getContext(){
		return applicationContext;
	}
	
	public void initialize(){
		String propertiesFileName = System.getProperty(PROPERTIES_FILE_NAME);
		initialize(propertiesFileName);
	}
	
	public void initialize(String propertiesFilename){
		log.info("Initializing application context...");
		
		if (initialized){
			log.info("Context already initialized.");
			return;
		}
		
		context = new HashMap<String, Object>();
		
		loadProperties(propertiesFilename);
		
		jdbcDriverClassName = getString(JDBC_DRIVER_CLASS_NAME);
		jdbcUrl = getString(JDBC_URL_KEY);
		jdbcUsername = getString(JDBC_USERNAME_KEY);
		jdbcPassword = getString(JDBC_PASSWORD_KEY);
		
		initializeDataSource();
		
		initialized = true;
		
		log.info("Application context initialized.");
	}
	
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
		poolProperties.setDefaultAutoCommit(false);
		
		dataSource = new DataSource(poolProperties);
		
		log.info("Data source initialized.");
	}
	
	public DataSource getDataSource(){
		return dataSource;
	}
	
	protected void loadProperties(String propertiesFilename){

		log.info("Loading properties... propertiesFileName = " + propertiesFilename);
		
		Properties properties = new Properties();
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(propertiesFilename));
			properties.load(reader);
			for (String key : properties.stringPropertyNames()){
		    	String value = properties.getProperty(key);
		    	context.put(key, value);
		    }
		}
		catch (Exception e){
			log.error("Error loading properties file! propertiesFilename = " + propertiesFilename, e);
		}
		finally {
			try {
				reader.close();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		
		log.info("Done loading properties.");
	}
	
	public Object get(String key){
		return context.get(key);
	}
	
	public String getString(String key){
		return (String)get(key);
	}
	
	public void put(String key, Object value){
		context.put(key, value);
	}

}
