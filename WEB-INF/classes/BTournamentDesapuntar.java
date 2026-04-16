import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;

@SuppressWarnings("serial")
public class BTournamentDesapuntar extends HttpServlet {
    Connection connection;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        connection = ConnectionUtils.getConnection(config);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
        res.setContentType("text/html");
        PrintWriter toClient = res.getWriter();
        
        // 1. Recuperar la sesión
        HttpSession session = req.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("userEmail") : null;

        // 2. Bloqueo si no hay sesión
        if (username == null) {
            toClient.println(Utils.header("Acceso Denegado", req));
            toClient.println("<h3 style='color:red; text-align:center;'>Debes iniciar sesión para desapuntarte de un torneo.</h3>");
            toClient.println("<div style='text-align:center;'><a href='login.html'>Ir al Login</a></div>");
            toClient.println(Utils.footer());
            toClient.close();
            return;
        }
        
        toClient.println(Utils.header("Desapuntarse de Torneo", req));
        
        // Apuntamos al nuevo Servlet de Update
        toClient.println("<form action='BTournamentDesapuntarUpdate' method='GET'>");
        toClient.println("<table style='margin: 0 auto;'>"); 
        
        toClient.println("<tr><td><b>Torneo:</b></td>");
        toClient.println("<td><select name='tournamentId' style='padding:5px;' required>");
        toClient.println("<option value='' disabled selected>-- Elige un torneo --</option>");
        
        // Usamos la nueva clase BTournamentData
        Vector<BTournamentData> tList = BTournamentData.getTournamentList(connection);
        for(int i=0; i < tList.size(); i++){
            BTournamentData t = tList.elementAt(i);
            toClient.println("<option value='" + t.id + "'>" + t.tournament + "</option>");
        }
        toClient.println("</select></td></tr>");
        
        toClient.println("<tr><td style='padding-top:10px;'><b>Usuario actual:</b></td>");
        toClient.println("<td style='padding-top:10px; color:#0078ff;'>" + username + "</td></tr>");
        
        toClient.println("</table>");
        toClient.println("<br><div style='text-align:center;'><input type='submit' value='Desapuntarse'></div>");
        toClient.println("</form>");
        
        toClient.println(Utils.footer());
        toClient.close();
    }
}