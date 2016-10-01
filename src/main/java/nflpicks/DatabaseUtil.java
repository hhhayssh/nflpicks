package nflpicks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseUtil {
	
	public static void close(ResultSet results, PreparedStatement statement, Connection connection){
		closeResults(results);
		closeStatement(statement);
		closeConnection(connection);
	}
	
	public static void closeResults(ResultSet results){
		try {
			if (results != null){
				results.close();
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void closeStatement(PreparedStatement statement){
		try {
			if (statement != null){
				statement.close();
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void closeConnection(Connection connection){
		try {
			if (connection != null){
				connection.close();	
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

}
