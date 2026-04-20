import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;

public class TournamentCheckIn extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        res.setContentType("text/html; charset=UTF-8");
        PrintWriter out = res.getWriter();

        Connection connection = ConnectionUtils.getConnection(getServletConfig());

        String userStr = req.getParameter("user_id");
        String tourStr = req.getParameter("tournament_id");

        out.println(Utils.header("Resultado del Check-In", req));

        try {
            int userId = Integer.parseInt(userStr);
            int tournamentId = Integer.parseInt(tourStr);

            String status = TournamentData.checkRegistration(connection, userId, tournamentId);

            switch (status) {
                case "accepted":
                    out.println("<h3 style='color:green;'>Check-In completado. ¡Bienvenido al torneo!</h3>");
                    break;

                case "pending":
                    out.println("<h3 style='color:orange;'>Tu inscripción está pendiente. No puedes hacer check-in todavia.</h3>");
                    break;

                case "rejected":
                    out.println("<h3 style='color:red;'>Tu inscripcion fue rechazada.</h3>");
                    break;

                default:
                    out.println("<h3 style='color:red;'>No estas inscrito en este torneo.</h3>");
                    break;
            }

        } catch (Exception e) {
            out.println("<h3 style='color:red;'>Error: datos invalidos.</h3>");
        }

        out.println(Utils.footer());
        out.close();

        ConnectionUtils.close(connection);
    }
}
