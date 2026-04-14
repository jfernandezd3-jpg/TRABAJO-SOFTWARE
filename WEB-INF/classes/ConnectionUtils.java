import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class ConnectionUtils {
	
	private ConnectionUtils() {
	    throw new IllegalStateException("Utility class");
	  }

	public static Connection getConnection(ServletConfig config) {
		Connection connection = null;
		
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			ServletContext context = config.getServletContext();
			System.out.println("realPath: " + context.getRealPath("TournamentHub1.accdb"));
			String dbURL = "jdbc:ucanaccess://" + context.getRealPath("TournamentHub1.accdb");
			connection = DriverManager.getConnection(dbURL);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static Connection getConnection() {
		Connection connection = null;
		
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			String url = "jdbc:ucanaccess:TournamentHub1";
			connection = DriverManager.getConnection(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static Connection close(Connection connection) {
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
}