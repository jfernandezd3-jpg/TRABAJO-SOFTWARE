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

            UserData user = UserData.getUserByEmail(conn, email);
            if (user == null) {
                out.println("<p style='color:red;'>Usuario no encontrado.</p>");
                out.println("</div>");
                out.println(Utils.footer());
                return;
            }

            String sql = "SELECT t.id AS tournament_id, t.tournament, r.status, " +
                         "  (SELECT COUNT(*) FROM CheckIns c WHERE c.user_id = ? AND c.tournament_id = t.id) AS ya_checkin " +
                         "FROM (Registrations AS r " +
                         "INNER JOIN Users AS u ON r.user_id = u.id) " +
                         "INNER JOIN Tournaments AS t ON r.tournament_id = t.id " +
                         "WHERE u.email = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, user.id);
                ps.setString(2, email);

                try (ResultSet rs = ps.executeQuery()) {
                    
                    out.println("<table><tr><th>Torneo</th><th>Estado</th><th>Check-In</th><th>Detalles</th><th>Desapuntarse</th></tr>");

                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        int tournamentId    = rs.getInt("tournament_id");
                        String tName        = rs.getString("tournament");
                        String status       = rs.getString("status");
                        int yaCheckin       = rs.getInt("ya_checkin");

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
                        out.println("<td style='color:" + color + "; font-weight:bold; text-align:center;'>" + estadoTraducido.toUpperCase() + "</td>");


                        out.println("<td style='text-align:center;'>");
                        if ("accepted".equalsIgnoreCase(status)) {
                            if (yaCheckin > 0) {
                                out.println("<span style='color:green;'>Realizado</span>");

                            } else {
                                out.println("<button onclick=\"hacerCheckin(" + user.id + "," + tournamentId + ",this)\" class='btn' style='padding: 4px 8px; font-size: 12px; width: auto;'>Check-In</button>");
                            }
                        } else {
                            out.println("<span style='color:#aaa;'>Tiene que estar aceptado</span>");
                        }
                        out.println("</td>");

                        out.println("<td style='text-align: center;'>");
                        out.println("<a href='BTournamentInfo?id=" + tournamentId + "' class='btn' style='padding: 4px 8px; font-size: 12px; text-decoration: none; white-space: nowrap;'>Ver Info</a>");
                        out.println("</td>");

                        out.println("<td style='text-align:center;'>");
                        out.println("<form action='BTournamentDesapuntarUpdate' method='GET' style='margin:0;' onsubmit=\"return confirm('¿Estas seguro de que quieres desapuntarte de este torneo? Esta accion no se puede deshacer.');\">");
                        out.println("<input type='hidden' name='tournamentId' value='" + tournamentId + "'>");
                        out.println("<button type='submit' class='btn' style='background-color:#dc3545; padding: 4px 8px; font-size: 12px; width: auto;'>Desapuntarse</button>");
                        out.println("</form>");
                        out.println("</td>");

                        out.println("</tr>");
                    }

                    if (!found) {
                        out.println("<tr><td colspan='5' style='text-align:center;'>No tienes ninguna inscripcion registrada.</td></tr>");
                    }
                    out.println("</table>");
                }
            }
        } catch (Exception e) {
            out.println("<div class='info-box' style='border-left-color:red;'>");
            out.println("<h3 style='color:red;'>Error en la Base de Datos</h3>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</div>");
        }

        out.println("<br><div class='text-center'><a href='home.html' class='btn' style='display:inline-block;width:auto;text-decoration:none;'>Volver a Mi Panel</a></div>");
        out.println("</div>");

        out.println("<script>" +
            "function hacerCheckin(userId, tournamentId, btn) {" +
            "  btn.disabled = true;" +
            "  btn.textContent = 'Procesando...';" +
            "  fetch('TournamentCheckIn?user_id=' + userId + '&tournament_id=' + tournamentId + '&format=json')" +
            "    .then(function(r) { return r.json(); })" +
            "    .then(function(data) {" +
            "      var td = btn.parentNode;" +
            "      if (data.status === 'checkin_ok') {" +
            "        td.innerHTML = \"<span style='color:green;'>Realizado</span>\";" +
            "      } else {" +
            "        btn.disabled = false;" +
            "        btn.textContent = 'Check-In';" +
            "        alert(data.message);" +
            "      }" +
            "    })" +
            "    .catch(function() {" +
            "      btn.disabled = false;" +
            "      btn.textContent = 'Check-In';" +
            "      alert('Error de conexión.');" +
            "    });" +
            "}" +
            "</script>");

        out.println(Utils.footer());
    }
}