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

        out.println(Utils.header("Mis Inscripciones", request));

        out.println("<div class='container'>");
        out.println("<p style='text-align:center;'>Usuario: <b>" + email + "</b></p>");

        try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
            
            String sql = "SELECT t.tournament, r.status " +
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
                        String tName = rs.getString("tournament");
                        String status = rs.getString("status");
                        
                        String color = "orange";
                        String estadoTraducido = "Pendiente";
                        
                        if ("accepted".equalsIgnoreCase(status)) {
                            color = "green";
                            estadoTraducido = "Aceptada";
                        } else if ("rejected".equalsIgnoreCase(status)) {
                            color = "red";
                            estadoTraducido = "Rechazada";
                        }

                        out.println("<tr>");
                        out.println("<td>" + tName + "</td>");
                        out.println("<td style='color: " + color + "; font-weight: bold; text-align: center;'>" + estadoTraducido.toUpperCase() + "</td>");
                        out.println("</tr>");
                    }
                    
                    if (!found) {
                        out.println("<tr><td colspan='2' style='text-align:center;'>No tienes ninguna inscripcion registrada.</td></tr>");
                    }
                    out.println("</table>");
                }
            }
        } catch (Exception e) {
            out.println("<div class='info-box' style='border-left-color: red;'>");
            out.println("<h3 style='color:red;'>Error en la Base de Datos</h3>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</div>");
        }

        out.println("<br><div class='text-center'><a href='home.html' class='btn' style='display:inline-block; width:auto; text-decoration:none;'>Volver a Mi Panel</a></div>");
        out.println("</div>");
        
        out.println(Utils.footer());
    }
}