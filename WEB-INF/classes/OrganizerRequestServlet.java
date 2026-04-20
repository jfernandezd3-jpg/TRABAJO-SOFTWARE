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

@WebServlet("/OrganizerRequestServlet")
public class OrganizerRequestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // METODO GET: Para cuando el usuario pulsa el link en el index
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (session == null || session.getAttribute("userEmail") == null) {
            response.sendRedirect("login.html");
            return;
        }

        String email = (String) session.getAttribute("userEmail");

        try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
            String role = "";
            String sql = "SELECT role FROM Users WHERE email = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) role = rs.getString("role");
                }
            }

            // --- LOGICA DE RESTRICCION DE ACCESO ---
            if ("organizer".equalsIgnoreCase(role)) {
                pintarPantallaAviso(out, "Ya eres organizador, no necesitas enviar otra solicitud.", "Acceso No Necesario", "#f39c12");
            } else if ("admin".equalsIgnoreCase(role)) {
                pintarPantallaAviso(out, "Eres administrador. No puedes realizar esta peticion porque ya tienes un rango superior.", "Acceso Restringido", "#dc3545");
            } else {
                // SI ES USER: Le pintamos el formulario directamente
                pintarFormulario(out);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.html");
        }
    }

    // METODO POST: Para cuando el usuario rellena el motivo y da a "Enviar"
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("userEmail");
        String motivo = request.getParameter("motivo");
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
            // 1. Sacar ID
            int uId = -1;
            String sqlU = "SELECT id FROM Users WHERE email = ?";
            try (PreparedStatement psU = conn.prepareStatement(sqlU)) {
                psU.setString(1, email);
                try (ResultSet rsU = psU.executeQuery()) {
                    if (rsU.next()) uId = rsU.getInt("id");
                }
            }

            // 2. Insertar solicitud
            String sqlIns = "INSERT INTO organizer_requests (id_user, reason, status) VALUES (?, ?, 'pending')";
            try (PreparedStatement psI = conn.prepareStatement(sqlIns)) {
                psI.setInt(1, uId);
                psI.setString(2, motivo);
                psI.executeUpdate();
            }

            pintarPantallaAviso(out, "Tu solicitud se ha enviado correctamente. Un administrador la revisara.", "Solicitud Registrada", "#28a745");

        } catch (Exception e) {
            e.printStackTrace();
            pintarPantallaAviso(out, "Error al guardar: " + e.getMessage(), "Error", "red");
        }
    }

    // --- METODOS DE PINTADO HTML ---

    private void pintarFormulario(PrintWriter out) {
        out.println("<html><head><title>Solicitud de Organizador</title><style>");
        out.println("body { font-family: Arial; background-color: #f4f4f9; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }");
        out.println(".card { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); text-align: center; max-width: 500px; width: 90%; }");
        out.println("textarea { width: 100%; padding: 10px; margin: 15px 0; border: 1px solid #ccc; border-radius: 5px; resize: none; }");
        out.println(".btn { background-color: #28a745; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }");
        out.println("</style></head><body><div class='card'>");
        out.println("<h2>Solicitud de Rango Organizador</h2><p>Explica por que quieres organizar torneos:</p>");
        out.println("<form action='OrganizerRequestServlet' method='POST'>");
        out.println("<textarea name='motivo' rows='5' required placeholder='Escribe aqui tu motivo...'></textarea>");
        out.println("<button type='submit' class='btn'>Enviar Solicitud</button>");
        out.println("</form><br><a href='index.html' style='color: #666;'>Volver</a></div></body></html>");
    }

    private void pintarPantallaAviso(PrintWriter out, String mensaje, String titulo, String color) {
        out.println("<html><head><style>");
        out.println("body { font-family: Arial; background-color: #f4f4f9; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }");
        out.println(".card { background: white; padding: 40px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); text-align: center; max-width: 400px; }");
        out.println("h2 { color: " + color + "; } p { color: #666; margin-bottom: 25px; }");
        out.println(".btn { background-color: #007bff; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; }");
        out.println("</style></head><body><div class='card'><h2>" + titulo + "</h2>");
        out.println("<p>" + mensaje + "</p><a href='index.html' class='btn'>Continuar</a></div></body></html>");
    }
}