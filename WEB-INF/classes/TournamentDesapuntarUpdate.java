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
        
        // 1. Extraemos el usuario de la SESIÓN (Seguridad)
        HttpSession session = req.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("userEmail") : null;

        // 2. Comprobación de seguridad por si accedió a esta URL directamente
        if (username == null) {
            toClient.println(Utils.header("Acceso Denegado", req));
            toClient.println("<h3 style='color:red; text-align:center;'>Debes iniciar sesión para realizar esta acción.</h3>");
            toClient.println("<div style='text-align:center;'><a href='login.html'>Ir al Login</a></div>");
            toClient.println(Utils.footer());
            toClient.close();
            return;
        }
        
        String tournamentIdStr = req.getParameter("tournamentId");
        
        if (tournamentIdStr != null) {
            int tournamentId = Integer.parseInt(tournamentIdStr);
            
            // 3. Ejecutamos el borrado usando el 'username' de su sesión
            int n = TournamentData.deleteRegistration(connection, tournamentId, username);
            
            toClient.println(Utils.header("Resultado de la Baja", req));
            
            if (n > 0) {
                toClient.println("<h3 style='text-align:center; color:green;'>Exito: Te has desapuntado correctamente del torneo.</h3>");
            } else {
                toClient.println("<h3 style='color:red; text-align:center;'>Error: No se te pudo desapuntar. Revisa que realmente estuvieras apuntado a este torneo.</h3>");
            }
        } else {
            toClient.println(Utils.header("Error", req));
            toClient.println("<h3 style='color:red; text-align:center;'>Faltan datos en el formulario.</h3>");
        }
        
        toClient.println("<br><div style='text-align:center;'><a href='TournamentDesapuntar'>Volver al formulario</a></div>");
        
        toClient.println(Utils.footer());
        toClient.close();
    }
}