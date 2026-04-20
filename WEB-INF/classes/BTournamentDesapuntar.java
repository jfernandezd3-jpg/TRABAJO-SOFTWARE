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
        res.setContentType("text/html; charset=UTF-8");
        res.setCharacterEncoding("UTF-8");
        PrintWriter toClient = res.getWriter();
        
        HttpSession session = req.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("userEmail") : null;

        if (username == null) {
            toClient.println(Utils.header("Acceso Denegado", req));
            toClient.println("<div class='container text-center'>");
            toClient.println("  <h3 style='color:red;'>Debes iniciar sesion para desapuntarte de un torneo.</h3>");
            toClient.println("  <br><a href='login.html' class='btn' style='display:inline-block; width:auto;'>Ir al Login</a>");
            toClient.println("</div>");
            toClient.println(Utils.footer());
            toClient.close();
            return;
        }
        
        toClient.println(Utils.header(" ", req));
        
        toClient.println("<div class='container'>");
        toClient.println("<h2 style='text-align:center; color:#1a4f2c; margin-bottom:20px;'>Cancelar Inscripcion</h2>");
        
        toClient.println("<form action='BTournamentDesapuntarUpdate' method='GET' onsubmit=\"return confirm('\\u00BFEstas seguro de que quieres desapuntarte de este torneo? Esta accion no se puede deshacer.');\">");
        
        Vector<BTournamentData> tList = BTournamentData.getTournamentsByUserEmail(connection, username);
        
        if (tList.isEmpty()) {
            toClient.println("<div class='info-box' style='text-align:center; border-left-color: orange;'>");
            toClient.println("  <h3 style='color: orange;'>Sin inscripciones</h3>");
            toClient.println("  <p>No estas apuntado a ningun torneo actualmente.</p>");
            toClient.println("</div>");
            toClient.println("<br><div class='text-center'><button type='submit' class='btn' disabled style='background-color:#ccc; cursor:not-allowed;'>Desapuntarse</button></div>");
        } else {
            toClient.println("<label for='tournamentId'>Selecciona el torneo a abandonar:</label>");
            toClient.println("<select name='tournamentId' id='tournamentId' required>");
            toClient.println("<option value='' disabled selected>-- Tus torneos activos --</option>");
            
            for(int i=0; i < tList.size(); i++){
                BTournamentData t = tList.elementAt(i);
                toClient.println("<option value='" + t.id + "'>" + t.tournament + "</option>");
            }
            toClient.println("</select>");
            
            toClient.println("<p style='margin-top:15px;'><b>Usuario actual:</b> <span style='color:#2e8b57; font-weight:bold;'>" + username + "</span></p>");
            
            toClient.println("<br><div class='text-center'><input type='submit' class='btn' value='Confirmar Baja'></div>");
        }
        
        toClient.println("</form>");
        
        toClient.println("<div class='text-center' style='margin-top: 20px;'><a href='index.html'>Volver al Inicio</a></div>");
        toClient.println("</div>");
        
        toClient.println(Utils.footer());
        toClient.close();
    }
}