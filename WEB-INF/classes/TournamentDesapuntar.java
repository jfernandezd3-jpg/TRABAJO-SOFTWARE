import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;

@SuppressWarnings("serial")
public class TournamentDesapuntar extends HttpServlet {
    Connection connection;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        connection = ConnectionUtils.getConnection(config);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
        res.setContentType("text/html");
        PrintWriter toClient = res.getWriter();
        
        toClient.println(Utils.header("Desapuntarse de Torneo"));
        
        toClient.println("<form action='TournamentDesapuntarUpdate' method='GET'>");
        toClient.println("<table border='1'>");
        
        toClient.println("<tr><td>Torneo</td>");
        toClient.println("<td><select name='tournamentId' required>");
        toClient.println("<option value='' disabled selected>-- Elige un torneo --</option>");
        
        Vector<TournamentData> tList = TournamentData.getTournamentList(connection);
        for(int i=0; i < tList.size(); i++){
            TournamentData t = tList.elementAt(i);
            toClient.println("<option value='" + t.id + "'>" + t.tournament + "</option>");
        }
        toClient.println("</select></td></tr>");
        
        toClient.println("<tr><td>Username</td>");
        toClient.println("<td><input name='username' required></td></tr>");
        
        toClient.println("</table>");
        toClient.println("<br><input type='submit' value='Desapuntarse'>");
        toClient.println("</form>");
        
        // Comentario limpio de tildes
        toClient.println(Utils.footer());
        toClient.close();
    }
}