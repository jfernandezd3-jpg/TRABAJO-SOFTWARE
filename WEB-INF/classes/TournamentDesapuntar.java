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
        
        // 1. Recuperar la sesión para saber quién es el usuario actual
        HttpSession session = req.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("userEmail") : null;

        // 2. Si no hay nadie logueado, le bloqueamos el paso
        if (username == null) {
            toClient.println(Utils.header("Acceso Denegado", req));
            toClient.println("<h3 style='color:red; text-align:center;'>Debes iniciar sesión para desapuntarte de un torneo.</h3>");
            toClient.println("<div style='text-align:center;'><a href='login.html'>Ir al Login</a></div>");
            toClient.println(Utils.footer());
            toClient.close();
            return;
        }
        
        toClient.println(Utils.header("Desapuntarse de Torneo", req));
        
        toClient.println("<form action='TournamentDesapuntarUpdate' method='GET'>");
        toClient.println("<table style='margin: 0 auto;'>"); // Centramos un poco la tabla
        
        toClient.println("<tr><td><b>Torneo:</b></td>");
        toClient.println("<td><select name='tournamentId' style='padding:5px;' required>");
        toClient.println("<option value='' disabled selected>-- Elige un torneo --</option>");
        
        Vector<TournamentData> tList = TournamentData.getTournamentList(connection);
        for(int i=0; i < tList.size(); i++){
            TournamentData t = tList.elementAt(i);
            toClient.println("<option value='" + t.id + "'>" + t.tournament + "</option>");
        }
        toClient.println("</select></td></tr>");
        
        // 3. Ya no pedimos el username con un input, se lo mostramos en texto plano
        toClient.println("<tr><td style='padding-top:10px;'><b>Usuario actual:</b></td>");
        toClient.println("<td style='padding-top:10px; color:#0078ff;'>" + username + "</td></tr>");
        
        toClient.println("</table>");
        toClient.println("<br><div style='text-align:center;'><input type='submit' value='Desapuntarse'></div>");
        toClient.println("</form>");
        
        toClient.println(Utils.footer());
        toClient.close();
    }
}