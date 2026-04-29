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


public class ManageParticipantsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            response.sendRedirect("login.html");
            return;
        }

        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        out.println(Utils.header("Inscripciones Pendientes", request));

        out.println("<div class='container' style='max-width: 800px;'>");
        out.println("<p style='text-align:center; color:#888; margin-bottom: 25px;'>Revisa y gestiona las solicitudes de los usuarios a tus torneos.</p>");

        try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
            
            out.println("<table>");
            out.println("<tr><th>ID Usuario</th><th>ID Torneo</th><th style='text-align:center;'>Acci&oacute;n</th></tr>");

            String sql = "SELECT user_id, tournament_id FROM Registrations WHERE status = 'pending'";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                boolean hayDatos = false;
                
                while (rs.next()) {
                    hayDatos = true;
                    int uId = rs.getInt("user_id");
                    int tId = rs.getInt("tournament_id");
                    
                    out.println("<tr>");
                    out.println("  <td>" + uId + "</td>");
                    out.println("  <td>" + tId + "</td>");
                    out.println("  <td style='text-align:center;'>");
                    out.println("    <form action='ManageParticipantsServlet' method='POST' style='margin: 0;'>");
                    out.println("      <input type='hidden' name='uId' value='" + uId + "'>");
                    out.println("      <input type='hidden' name='tId' value='" + tId + "'>");
                    out.println("      <button type='submit' name='accion' value='validar' class='btn' style='padding: 8px 12px; font-size: 14px; width: auto; margin-right: 5px;'>Aceptar</button>");
                    out.println("      <button type='submit' name='accion' value='rechazar' class='btn' style='padding: 8px 12px; font-size: 14px; width: auto; background-color: #dc3545;'>Rechazar</button>");
                    out.println("    </form>");
                    out.println("  </td>");
                    out.println("</tr>");
                }
                
                if (!hayDatos) {
                    out.println("<tr><td colspan='3' style='text-align:center;'>No tienes ninguna inscripción pendiente en este momento.</td></tr>");
                }
            }
            out.println("</table>");
            
            out.println("<div class='text-center' style='margin-top: 30px;'>");
            out.println("  <a href='gestion_organizador.html' class='btn' style='display:inline-block; width:auto; text-decoration:none;'>Volver al Panel</a>");
            out.println("</div>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<div class='info-box' style='border-left-color: red;'>");
            out.println("  <p style='color:red;'>Error al cargar los datos: " + e.getMessage() + "</p>");
            out.println("</div>");
        }
        
        out.println("</div>");
        out.println(Utils.footer());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uId = request.getParameter("uId");
        String tId = request.getParameter("tId");
        String accion = request.getParameter("accion");
        
        String nuevoEstado = "accepted"; 
        if ("rechazar".equals(accion)) { nuevoEstado = "rejected"; }

        try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
            String sql = "UPDATE Registrations SET status = ? WHERE user_id = ? AND tournament_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nuevoEstado);
                ps.setInt(2, Integer.parseInt(uId));
                ps.setInt(3, Integer.parseInt(tId));
                ps.executeUpdate();
            }
            response.sendRedirect("ManageParticipantsServlet");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}