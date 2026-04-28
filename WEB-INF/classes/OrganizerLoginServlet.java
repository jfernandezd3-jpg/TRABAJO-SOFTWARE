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


public class OrganizerLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 1. Verificacion de sesion basica
        if (session == null || session.getAttribute("userEmail") == null) {
            response.sendRedirect("login.html");
            return;
        }

        String email = (String) session.getAttribute("userEmail");

        try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
            if (conn != null) {
                // 2. Consultar el rol en la base de datos
                String sql = "SELECT role FROM Users WHERE email = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, email);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String role = rs.getString("role");
                            
                            if ("organizer".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(role)) {
                                // SI TIENE PERMISO: Va directo al panel
                                response.sendRedirect("gestion_organizador.html");
                            } else {
                                // NO TIENE PERMISO: Mostramos la pantalla de aviso
                                pintarPantallaError(out, "No puedes acceder ya que no eres organizador.", "Acceso Denegado");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            pintarPantallaError(out, "Error al verificar permisos: " + e.getMessage(), "Error de Sistema");
        }
    }

    // Metodo para pintar la tarjeta de aviso (estilo similar al de inscripcion)
    private void pintarPantallaError(PrintWriter out, String mensaje, String titulo) {
        out.println("<html><head><title>" + titulo + "</title>");
        out.println("<style>");
        out.println("body { font-family: Arial; background-color: #f4f4f9; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }");
        out.println(".card { background: white; padding: 40px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); text-align: center; max-width: 400px; }");
        out.println("h2 { color: #dc3545; }"); // Rojo para indicar error/denegado
        out.println("p { color: #666; line-height: 1.5; margin-bottom: 30px; }");
        out.println(".btn { background-color: #007bff; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; }");
        out.println("</style></head><body>");
        out.println("<div class='card'>");
        out.println("<h2>" + titulo + "</h2>");
        out.println("<p>" + mensaje + "</p>");
        out.println("<a href='home.html' class='btn'>Volver al Inicio</a>");
        out.println("</div></body></html>");
    }
}