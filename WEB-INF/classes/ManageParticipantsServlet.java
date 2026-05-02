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

@WebServlet("/ManageParticipantsServlet")
public class ManageParticipantsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Metodo para MOSTRAR la tabla
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            response.sendRedirect("login.html");
            return;
        }

        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Cabecera y carga del archivo JavaScript corregido
        out.println(Utils.header("Inscripciones Pendientes", request));
        out.println("<script src='js/gestion.js'></script>"); 

        out.println("<div class='container' style='max-width: 800px;'>");
        out.println("<p style='text-align:center; color:#888; margin-bottom: 25px;'>Revisa y gestiona las solicitudes sin recargar la pagina.</p>");

        try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
            
            out.println("<table id='tablaInscripciones' style='width:100%; border-collapse: collapse;'>");
            out.println("<tr><th>ID Usuario</th><th>ID Torneo</th><th style='text-align:center;'>Acci&oacute;n</th></tr>");

            String sql = "SELECT user_id, tournament_id FROM Registrations WHERE status = 'pending'";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                boolean hayDatos = false;
                
                while (rs.next()) {
                    hayDatos = true;
                    int uId = rs.getInt("user_id");
                    int tId = rs.getInt("tournament_id");
                    
                    // ID unico para identificar la fila en el JavaScript
                    String filaId = "fila_" + uId + "_" + tId;
                    
                    out.println("<tr id='" + filaId + "' style='border-bottom: 1px solid #ddd;'>");
                    out.println("  <td style='padding: 10px;'>" + uId + "</td>");
                    out.println("  <td style='padding: 10px;'>" + tId + "</td>");
                    out.println("  <td style='text-align:center; padding: 10px;'>");
                    
                    // Botones que llaman a la funcion de gestion.js
                    out.println("    <button type='button' class='btn' style='padding: 8px 12px; width:auto; margin-right:5px;' " +
                                "onclick=\"gestionarInscripcion(" + uId + "," + tId + ",'validar')\">Aceptar</button>");
                    
                    out.println("    <button type='button' class='btn' style='padding: 8px 12px; width:auto; background-color: #dc3545;' " +
                                "onclick=\"gestionarInscripcion(" + uId + "," + tId + ",'rechazar')\">Rechazar</button>");
                    
                    out.println("  </td>");
                    out.println("</tr>");
                }
                
                if (!hayDatos) {
                    out.println("<tr id='sinDatos'><td colspan='3' style='text-align:center; padding: 20px;'>No tienes ninguna inscripcion pendiente.</td></tr>");
                }
            }
            out.println("</table>");
            
            // Boton para volver
            out.println("<div class='text-center' style='margin-top: 30px;'>");
            out.println("  <a href='gestion_organizador.html' class='btn' style='display:inline-block; width:auto; text-decoration:none;'>Volver al Panel</a>");
            out.println("</div>");

        } catch (Exception e) {
            out.println("<p style='color:red;'>Error al cargar los datos: " + e.getMessage() + "</p>");
        }
        
        out.println("</div>");
        out.println(Utils.footer());
    }

    // Metodo para PROCESAR la accion (AJAX)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uId = request.getParameter("uId");
        String tId = request.getParameter("tId");
        String accion = request.getParameter("accion");
        
        // Mantenemos tu logica de estados
        String nuevoEstado = "validar".equals(accion) ? "accepted" : "rejected";

        // Respuesta tipo texto para el AJAX
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
            String sql = "UPDATE Registrations SET status = ? WHERE user_id = ? AND tournament_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nuevoEstado);
                ps.setInt(2, Integer.parseInt(uId));
                ps.setInt(3, Integer.parseInt(tId));
                ps.executeUpdate();
                
                // Si todo va bien, enviamos OK al JavaScript
                out.print("OK");
            }
        } catch (Exception e) {
            // Si hay error, enviamos el mensaje al JavaScript
            out.print("ERROR: " + e.getMessage());
        }
    }
}