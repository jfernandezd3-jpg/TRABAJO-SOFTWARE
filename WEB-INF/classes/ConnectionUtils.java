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
            String realPath = context.getRealPath("/TournamentHub1.accdb");

            System.out.println("Using DB at: " + realPath);

            String dbURL = "jdbc:ucanaccess://" + realPath;
            connection = DriverManager.getConnection(dbURL);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    // LO ÚNICO NUEVO: El método close() que te piden los Servlets
    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}