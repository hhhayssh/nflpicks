package nflpicks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

/**
 * 
 * This class holds common functions for dealing with the database.
 * 
 * @author albundy
 *
 */
public class DatabaseUtil {
	
	private static final Logger log = Logger.getLogger(DatabaseUtil.class);
	
	/**
	 * 
	 * A convenience function to close the results, statement, and connection all
	 * at once (in that order).
	 * 
	 * @param results
	 * @param statement
	 * @param connection
	 */
	public static void close(ResultSet results, PreparedStatement statement, Connection connection){
		closeResults(results);
		closeStatement(statement);
		closeConnection(connection);
	}
	
	/**
	 * 
	 * Closes the given result set.
	 * 
	 * @param results
	 */
	public static void closeResults(ResultSet results){
		try {
			if (results != null){
				results.close();
			}
		}
		catch (Exception e){
			log.error("Error closing results!", e);
		}
	}
	
	/**
	 * 
	 * Closes the given prepared statement.
	 * 
	 * @param statement
	 */
	public static void closeStatement(PreparedStatement statement){
		try {
			if (statement != null){
				statement.close();
			}
		}
		catch (Exception e){
			log.error("Error closing prepared statement!", e);
		}
	}
	
	/**
	 * 
	 * Closes the given connection.
	 * 
	 * @param connection
	 */
	public static void closeConnection(Connection connection){
		try {
			if (connection != null){
				connection.close();	
			}
		}
		catch (Exception e){
			log.error("Error closing connection!", e);
		}
	}
	
	/**
	 * 
	 * Tries to rollback the given connection and catches any errors that
	 * happen.
	 * 
	 * @param connection
	 */
	public static void rollback(Connection connection){
		
		try {
			if (connection != null){
				connection.rollback();
			}
		}
		catch (Exception e){
			log.error("Error rolling back connection!", e);
		}
	}
	
	/**
	 * 
	 * This function will create a string like "(?, ?, ?, ?, ...)"
	 * that can be used as part of an "in" clause in an sql query.
	 * 
	 * @param numberOfValues
	 * @return
	 */
	public static String createInClauseParameterString(int numberOfValues){
		
		//Steps to do:
		//	1. Add a parentheses to start with and then just keep adding commas
		//	   and question marks and that's it.
		
		StringBuilder stringBuilder = new StringBuilder("(");
		
		for (int index = 0; index < numberOfValues; index++){
			if (index > 0){
				stringBuilder.append(", ");
			}
			
			stringBuilder.append("?");
		}
		
		stringBuilder.append(")");
		
		String inParameterString = stringBuilder.toString();
		
		return inParameterString;
	}
}
