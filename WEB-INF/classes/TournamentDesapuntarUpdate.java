import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;

@SuppressWarnings("serial")
public class TournamentDesapuntarUpdate extends HttpServlet {
    Connection connection;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        connection = ConnectionUtils.getConnection(config);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
        res.setContentType("text/html");
        PrintWriter toClient = res.getWriter();
        
        String tournamentIdStr = req.getParameter("tournamentId");
        String username = req.getParameter("username");
        
        if (tournamentIdStr != null && username != null) {
            int tournamentId = Integer.parseInt(tournamentIdStr);
            
            int n = TournamentData.deleteRegistration(connection, tournamentId, username);
            
            toClient.println(Utils.header("Resultado de la Baja"));
            
            if (n > 0) {
                toClient.println("<h3>Exito: El usuario '" + username + "' se ha desapuntado correctamente del torneo.</h3>");
            } else {
                toClient.println("<h3 style='color:red;'>Error: No se pudo desapuntar al usuario '" + username + "'. Revisa que el nombre sea correcto y que este apuntado al torneo seleccionado.</h3>");
            }
        } else {
            toClient.println(Utils.header("Error"));
            toClient.println("<h3 style='color:red;'>Faltan datos en el formulario.</h3>");
        }
        
        toClient.println("<br><a href='TournamentDesapuntar'>Volver al formulario</a>");
        
        // Comentario limpio de tildes
        toClient.println(Utils.footer());
        toClient.close();
    }
}