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

@WebServlet("/RegisterTournamentServlet")
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
                        pintarPantallaAviso(out, "Ya estas inscrito en este torneo.", "Atencion");
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
                pintarPantallaAviso(out, mensaje, "Registro Completado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            pintarPantallaAviso(out, "Hubo un error tecnico: " + e.getMessage(), "Error");
        }
    }

    // Metodo auxiliar para no repetir codigo HTML
    private void pintarPantallaAviso(PrintWriter out, String mensaje, String titulo) {
        out.println("<html><head><title>" + titulo + "</title>");
        out.println("<style>");
        out.println("body { font-family: Arial; background-color: #f4f4f9; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }");
        out.println(".card { background: white; padding: 40px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); text-align: center; max-width: 400px; }");
        out.println("h2 { color: #333; }");
        out.println("p { color: #666; line-height: 1.5; margin-bottom: 30px; }");
        out.println(".btn { background-color: #28a745; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; }");
        out.println("</style></head><body>");
        out.println("<div class='card'>");
        out.println("<h2>" + titulo + "</h2>");
        out.println("<p>" + mensaje + "</p>");
        out.println("<a href='home.html' class='btn'>Continuar</a>");
        out.println("</div></body></html>");
    }
}