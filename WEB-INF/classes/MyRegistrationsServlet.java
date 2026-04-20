import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/MyRegistrationsServlet")
public class MyRegistrationsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("userEmail") == null) {
            response.sendRedirect("login.html");
            return;
        }

        String email = (String) session.getAttribute("userEmail");
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><style>");
        out.println("body { font-family: sans-serif; background-color: #f8f9fa; text-align: center; padding: 40px; }");
        out.println(".container { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); display: inline-block; min-width: 60%; }");
        out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        out.println("th, td { padding: 12px; border: 1px solid #dee2e6; text-align: center; }");
        out.println("th { background-color: #007bff; color: white; }");
        out.println(".status-pending { color: orange; font-weight: bold; }");
        out.println(".status-accepted { color: green; font-weight: bold; }");
        out.println(".status-rejected { color: red; font-weight: bold; }");
        out.println("</style></head><body>");
        out.println("<div class='container'><h1>Mis Inscripciones</h1>");
        out.println("<p>Usuario: <b>" + email + "</b></p>");

        try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
            
            // Query con parentesis para Access
            String sql = "SELECT t.name, r.status " +
                         "FROM (Registrations AS r " +
                         "INNER JOIN Users AS u ON r.user_id = u.id) " +
                         "INNER JOIN Tournaments AS t ON r.tournament_id = t.id " +
                         "WHERE u.email = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                
                try (ResultSet rs = ps.executeQuery()) {
                    out.println("<table><tr><th>Torneo</th><th>Estado</th></tr>");
                    
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        String tName = rs.getString("name");
                        String status = rs.getString("status");
                        String cssClass = "status-pending";
                        
                        if ("accepted".equalsIgnoreCase(status)) cssClass = "status-accepted";
                        if ("rejected".equalsIgnoreCase(status)) cssClass = "status-rejected";

                        out.println("<tr>");
                        out.println("<td>" + tName + "</td>");
                        out.println("<td class='" + cssClass + "'>" + status.toUpperCase() + "</td>");
                        out.println("</tr>");
                    }
                    
                    if (!found) {
                        out.println("<tr><td colspan='2'>No tienes ninguna inscripcion registrada.</td></tr>");
                    }
                    out.println("</table>");
                }
            }
        } catch (Exception e) {
            out.println("<div style='color:red; margin-top:20px;'>");
            out.println("<h3>Error en la Base de Datos</h3>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</div>");
        }

        // He quitado la flecha especial aqui para evitar el error de javac
        out.println("<br><a href='index.html' style='text-decoration:none; color:#007bff;'>Volver al Inicio</a>");
        out.println("</div></body></html>");
    }
}