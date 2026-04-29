import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class TournamentCheckIn extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        res.setContentType("application/json; charset=UTF-8");
        PrintWriter out = res.getWriter();
        Connection connection = ConnectionUtils.getConnection(getServletConfig());

        try {
            int userId       = Integer.parseInt(req.getParameter("user_id"));
            int tournamentId = Integer.parseInt(req.getParameter("tournament_id"));

            String status = TournamentData.checkRegistration(connection, userId, tournamentId);

            if (!"accepted".equals(status)) {
                String msg;
                switch (status) {
                    case "pending":  msg = "Tu inscripción está pendiente. No puedes hacer check-in todavía."; break;
                    case "rejected": msg = "Tu inscripción fue rechazada."; break;
                    default:         msg = "No estás inscrito en este torneo."; break;
                }
                out.println("{\"status\": \"" + status + "\", \"message\": \"" + msg + "\"}");
                return;
            }

            String sqlInsert = "INSERT INTO CheckIns (user_id, tournament_id) VALUES (?, ?)";
            PreparedStatement ps = connection.prepareStatement(sqlInsert);
            ps.setInt(1, userId);
            ps.setInt(2, tournamentId);
            int n = ps.executeUpdate();
            ps.close();

            if (n > 0) {
                out.println("{\"status\": \"checkin_ok\", \"message\": \"Check-In completado. ¡Bienvenido al torneo!\"}");
            } else {
                out.println("{\"status\": \"error\", \"message\": \"No se pudo registrar el check-in.\"}");
            }

        } catch (Exception e) {
            out.println("{\"status\": \"error\", \"message\": \"Datos inválidos.\"}");
        } finally {
            out.close();
            ConnectionUtils.close(connection);
        }
    }
}
