import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

public class PopularityRankingServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><style>");
        out.println("body { font-family: sans-serif; background: #f0f2f5; text-align: center; padding: 50px; }");
        out.println(".card { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 5px 15px rgba(0,0,0,0.1); display: inline-block; min-width: 400px; }");
        out.println(".item { background: #e3f2fd; margin: 10px 0; padding: 15px; border-radius: 8px; text-align: left; display: flex; justify-content: space-between; }");
        out.println("</style></head><body>");
        out.println("<div class='card'><h1>Ranking de Torneos</h1>");

        try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
            // 1. Sacamos todos los torneos primero
            String sqlT = "SELECT ID, tournament FROM tournaments";
            try (PreparedStatement psT = conn.prepareStatement(sqlT);
                 ResultSet rsT = psT.executeQuery()) {
                
                while (rsT.next()) {
                    int idT = rsT.getInt("ID");
                    String nombreT = rsT.getString("tournament");
                    
                    // 2. Por cada torneo, contamos sus inscripciones en una consulta aparte
                    String sqlR = "SELECT COUNT(*) FROM registrations WHERE tournament_id = ?";
                    int total = 0;
                    try (PreparedStatement psR = conn.prepareStatement(sqlR)) {
                        psR.setInt(1, idT);
                        try (ResultSet rsR = psR.executeQuery()) {
                            if (rsR.next()) total = rsR.getInt(1);
                        }
                    }

                    out.println("<div class='item'>");
                    out.println("<span><b>" + nombreT.toUpperCase() + "</b></span>");
                    out.println("<span>" + total + " inscritos</span>");
                    out.println("</div>");
                }
            }
        } catch (Exception e) {
            out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        }

        out.println("<br><a href='home.html'>Volver</a></div></body></html>");
    }
}