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

public class RegisterTournamentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            response.sendRedirect("login.html");
            return;
        }

        String email = (String) session.getAttribute("userEmail");
        String tIdParam = request.getParameter("tournamentId");
        int tId = Integer.parseInt(tIdParam);

        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
            if (conn != null) {
                // 1. Obtener ID y ROL
                int uId = -1;
                String role = "user";
                String sqlU = "SELECT id, role FROM Users WHERE email = ?";
                try (PreparedStatement psU = conn.prepareStatement(sqlU)) {
                    psU.setString(1, email);
                    try (ResultSet rsU = psU.executeQuery()) {
                        if (rsU.next()) {
                            uId = rsU.getInt("id");
                            role = rsU.getString("role");
                        }
                    }
                }

                // 2. Comprobar si ya existe
                String sqlCheck = "SELECT * FROM Registrations WHERE user_id = ? AND tournament_id = ?";
                try (PreparedStatement psC = conn.prepareStatement(sqlCheck)) {
                    psC.setInt(1, uId);
                    psC.setInt(2, tId);
                    if (psC.executeQuery().next()) {
                        // Pasamos el request para que Utils pinte la barra de navegación
                        pintarPantallaAviso(out, request, "Ya estas inscrito en este torneo.", "Atencion");
                        return;
                    }
                }

                // 3. Determinar estado
                String estado = "pending";
                String mensaje = "Solicitud enviada correctamente. Debes esperar a que un organizador acepte la solicitud.";
                
                if ("admin".equalsIgnoreCase(role) || "organizer".equalsIgnoreCase(role)) {
                    estado = "accepted";
                    mensaje = "Inscripcion realizada con exito. Al ser personal de organizacion, tu acceso es directo.";
                }

                // 4. Insertar
                String sqlIns = "INSERT INTO Registrations (user_id, tournament_id, status) VALUES (?, ?, ?)";
                try (PreparedStatement psI = conn.prepareStatement(sqlIns)) {
                    psI.setInt(1, uId);
                    psI.setInt(2, tId);
                    psI.setString(3, estado);
                    psI.executeUpdate();
                }

                // 5. Mostrar pantalla de exito personalizada
                pintarPantallaAviso(out, request, mensaje, "Registro Completado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            pintarPantallaAviso(out, request, "Hubo un error tecnico: " + e.getMessage(), "Error");
        }
    }

    private void pintarPantallaAviso(PrintWriter out, HttpServletRequest request, String mensaje, String titulo) {
        
        out.println(Utils.header(" ", request));
        
        out.println("<div class='container text-center'>");
        
        if (titulo.contains("Error") || titulo.contains("Atencion")) {
            out.println("  <h2 style='color: #dc3545;'>" + titulo + "</h2>");
        } else {
            out.println("  <h2 style='color: #2e8b57;'>" + titulo + "</h2>");
        }
        
        out.println("  <p style='color: #666; font-size: 16px; margin-bottom: 30px;'>" + mensaje + "</p>");
        out.println("  <a href='home.html' class='btn' style='display:inline-block; width:auto;'>Continuar</a>");
        out.println("</div>");
        
        out.println(Utils.footer());
    }
}