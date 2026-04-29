import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.sql.Connection;

// JAIME FERNANDEZ DE BETONO

@SuppressWarnings("serial")
public class BMatchScoreCreate extends HttpServlet {
    Connection connection;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        connection = ConnectionUtils.getConnection(config);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
        
        res.setContentType("application/json; charset=UTF-8");
        res.setCharacterEncoding("UTF-8");
        PrintWriter toClient = res.getWriter();
        
        try {
            int idTournament = Integer.parseInt(req.getParameter("id_tournament"));
            int idPlayer1 = Integer.parseInt(req.getParameter("id_player1"));
            int idPlayer2 = Integer.parseInt(req.getParameter("id_player2"));
            int pointsP1 = Integer.parseInt(req.getParameter("points_p1"));
            int pointsP2 = Integer.parseInt(req.getParameter("points_p2"));
            
            int n = BMatchData.insertMatchScore(connection, idTournament, idPlayer1, idPlayer2, pointsP1, pointsP2);
            
            if (n > 0) {
                toClient.print("{\"status\": \"ok\", \"message\": \"Partido registrado correctamente\"}");
            } else {
                toClient.print("{\"status\": \"error\", \"message\": \"No se ha podido guardar en la BD\"}");
            }
            
        } catch (Exception e) {
            String safeError = e.getMessage().replace("\"", "'");
            toClient.print("{\"status\": \"error\", \"message\": \"Excepcion: " + safeError + "\"}");
        }
        
        toClient.close();
    }
}