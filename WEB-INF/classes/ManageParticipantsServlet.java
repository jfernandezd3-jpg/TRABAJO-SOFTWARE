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
        PrintWriter out = response.getWriter();

        try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
            out.println("<html><head><style>");
            out.println("body { font-family: Arial; background-color: #f4f4f4; text-align: center; }");
            out.println("table { width: 85%; border-collapse: collapse; margin: 20px auto; background: white; }");
            out.println("th, td { padding: 12px; border: 1px solid #ddd; }");
            out.println("th { background-color: #f39c12; color: white; }");
            out.println("button { padding: 8px 12px; cursor: pointer; border: none; border-radius: 4px; font-weight: bold; }");
            out.println(".btn-ok { background-color: #28a745; color: white; }");
            out.println(".btn-no { background-color: #dc3545; color: white; }");
            out.println("</style></head><body>");
            
            out.println("<h1>Inscripciones Pendientes</h1>");
            out.println("<table><tr><th>ID Usuario</th><th>ID Torneo</th><th>Accion</th></tr>");

            String sql = "SELECT user_id, tournament_id FROM Registrations WHERE status = 'pending'";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int uId = rs.getInt("user_id");
                    int tId = rs.getInt("tournament_id");
                    out.println("<tr><td>" + uId + "</td><td>" + tId + "</td><td>");
                    out.println("<form action='ManageParticipantsServlet' method='POST' style='display:inline;'>");
                    out.println("<input type='hidden' name='uId' value='" + uId + "'>");
                    out.println("<input type='hidden' name='tId' value='" + tId + "'>");
                    out.println("<button type='submit' name='accion' value='validar' class='btn-ok'>Aceptar</button>");
                    out.println("<button type='submit' name='accion' value='rechazar' class='btn-no'>Rechazar</button>");
                    out.println("</form></td></tr>");
                }
            }
            out.println("</table><br><a href='gestion_organizador.html'>Volver</a></body></html>");
        } catch (Exception e) {
            e.printStackTrace();
        }
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