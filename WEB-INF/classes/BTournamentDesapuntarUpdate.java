import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;

// JAIME FERNANDEZ DE BETOÑO

@SuppressWarnings("serial")
public class BTournamentDesapuntarUpdate extends HttpServlet {
    Connection connection;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        connection = ConnectionUtils.getConnection(config);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
        res.setContentType("text/html; charset=UTF-8");
        PrintWriter toClient = res.getWriter();
        
        HttpSession session = req.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("userEmail") : null;

        if (username == null) {
            toClient.println(Utils.header("Acceso Denegado", req));
            toClient.println("<div class='container text-center'>");
            toClient.println("<h3 style='color:red;'>Debes iniciar sesion para realizar esta accion.</h3>");
            toClient.println("<br><a href='login.html' class='btn' style='display:inline-block; width:auto;'>Ir al Login</a>");
            toClient.println("</div>");
            toClient.println(Utils.footer());
            toClient.close();
            return;
        }
        
        String tournamentIdStr = req.getParameter("tournamentId");
        
        toClient.println(Utils.header("Resultado de la Baja", req));
        toClient.println("<div class='container'>");

        if (tournamentIdStr != null) {
            int tournamentId = Integer.parseInt(tournamentIdStr);
            
            int n = BTournamentData.deleteRegistration(connection, tournamentId, username);
            
            if (n > 0) {
                toClient.println("<h3 style='text-align:center; color:#2e8b57;'>Exito: Te has desapuntado correctamente del torneo.</h3>");
            } else {
                toClient.println("<h3 style='color:#dc3545; text-align:center;'>Error: No se te pudo desapuntar. Revisa que realmente estuvieras apuntado a este torneo.</h3>");
            }
        } else {
            toClient.println("<h3 style='color:#dc3545; text-align:center;'>Faltan datos en el formulario.</h3>");
        }
        
        toClient.println("<br><div class='text-center'><a href='MyRegistrationsServlet' class='btn' style='display:inline-block; width:auto; text-decoration:none;'>Volver a Mis Inscripciones</a></div>");
        toClient.println("</div>");
        
        toClient.println(Utils.footer());
        toClient.close();
    }
}