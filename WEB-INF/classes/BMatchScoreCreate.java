import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.sql.Connection;

@SuppressWarnings("serial")
public class BMatchScoreCreate extends HttpServlet {
    Connection connection;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        connection = ConnectionUtils.getConnection(config);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
        res.setContentType("text/html; charset=UTF-8");
        res.setCharacterEncoding("UTF-8");
        PrintWriter toClient = res.getWriter();
        
        try {
            int idTournament = Integer.parseInt(req.getParameter("id_tournament"));
            int idPlayer1 = Integer.parseInt(req.getParameter("id_player1"));
            int idPlayer2 = Integer.parseInt(req.getParameter("id_player2"));
            int pointsP1 = Integer.parseInt(req.getParameter("points_p1"));
            int pointsP2 = Integer.parseInt(req.getParameter("points_p2"));
            
            int n = BMatchData.insertMatchScore(connection, idTournament, idPlayer1, idPlayer2, pointsP1, pointsP2);
            
            toClient.println(Utils.header("Resultado Guardado", req));
            toClient.println("<div class='container text-center'>");
            if (n > 0) {
                toClient.println("<h3 style='color:green;'>&iexcl;Exito!, El partido ha sido registrado.</h3>");
                toClient.println("<p>Resultado Final: " + pointsP1 + " - " + pointsP2 + "</p>");
            } else {
                toClient.println("<h3 style='color:red;'>Error al guardar el partido en la base de datos.</h3>");
            }
            toClient.println("<br><a href='home.html' class='btn'>Volver al Inicio</a>");
            toClient.println("</div>");
            
        } catch (Exception e) {
            toClient.println(Utils.header("Error", req));
            toClient.println("<div class='container text-center'>");
            toClient.println("<h3 style='color:red;'>Ocurrio un error inesperado al procesar los datos.</h3>");
            toClient.println("<p>" + e.getMessage() + "</p>");
            toClient.println("</div>");
        }
        
        toClient.println(Utils.footer());
        toClient.close();
    }
}