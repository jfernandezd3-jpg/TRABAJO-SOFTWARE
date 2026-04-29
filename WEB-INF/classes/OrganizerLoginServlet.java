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
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // 1. Verificación de sesión básica
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
                                // NO TIENE PERMISO: Mostramos la pantalla de aviso unificada
                                pintarPantallaError(out, request, "No puedes acceder ya que no tienes permisos de organizador.", "Acceso Denegado");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            pintarPantallaError(out, request, "Error al verificar permisos: " + e.getMessage(), "Error de Sistema");
        }
    }

    private void pintarPantallaError(PrintWriter out, HttpServletRequest request, String mensaje, String titulo) {
        
        out.println(Utils.header(" ", request));
        
        out.println("<div class='container text-center'>");
        
        out.println("  <h2 style='color: #dc3545;'>" + titulo + "</h2>");
        out.println("  <p style='color: #666; font-size: 16px; margin-bottom: 30px;'>" + mensaje + "</p>");
        out.println("  <a href='home.html' class='btn' style='display:inline-block; width:auto;'>Volver al Inicio</a>");
        
        out.println("</div>");
        
        out.println(Utils.footer());
    }
}