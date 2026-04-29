import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


public class PopularityRankingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        out.println(Utils.header("Ranking de Torneos", request));
        
        out.println("<div class='container'>");
        out.println("<p style='text-align:center; color:#888; margin-bottom: 25px;'>Descubre cu&aacute;les son los torneos con m&aacute;s &eacute;xito.</p>");

        try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
            // 1. Sacamos todos los torneos primero
            String sqlT = "SELECT ID, tournament FROM tournaments";
            try (PreparedStatement psT = conn.prepareStatement(sqlT);
                 ResultSet rsT = psT.executeQuery()) {
                
                boolean hayDatos = false;
                
                while (rsT.next()) {
                    hayDatos = true;
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

                    out.println("<div class='info-box' style='display: flex; justify-content: space-between; align-items: center;'>");
                    out.println("  <span style='font-size: 16px; color: #1a4f2c;'><b>" + nombreT.toUpperCase() + "</b></span>");
                    out.println("  <span style='background: #2e8b57; color: white; padding: 5px 12px; border-radius: 20px; font-weight: bold; font-size: 14px;'>" + total + " inscritos</span>");
                    out.println("</div>");
                }
                
                if (!hayDatos) {
                    out.println("<p style='text-align:center;'>No hay torneos registrados todavía.</p>");
                }
            }
        } catch (Exception e) {
            out.println("<div class='info-box' style='border-left-color: red;'>");
            out.println("  <p style='color:red;'>Error: " + e.getMessage() + "</p>");
            out.println("</div>");
        }

        out.println("<div class='text-center' style='margin-top: 30px;'>");
        out.println("  <a href='home.html' class='btn' style='display:inline-block; width:auto; text-decoration:none;'>Volver a Mi Panel</a>");
        out.println("</div>");
        
        out.println("</div>");
        out.println(Utils.footer());
    }
}