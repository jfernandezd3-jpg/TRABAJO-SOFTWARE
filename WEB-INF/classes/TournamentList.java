import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.util.Vector;

@SuppressWarnings("serial")
public class TournamentList extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        res.setContentType("text/html; charset=UTF-8");
        PrintWriter out = res.getWriter();

        Connection connection = ConnectionUtils.getConnection(getServletConfig());

        Vector<TournamentData> tournaments = TournamentData.getTournamentList(connection);

        out.println(Utils.header("Lista de Torneos", req));

        out.println("<table border='1'>");
        out.println("<tr>"
                + "<th>ID</th>"
                + "<th>Organizer</th>"
                + "<th>Tournament</th>"
                + "<th>Modality</th>"
                + "<th>Location</th>"
                + "<th>Date</th>"
                + "<th>Entry Price</th>"
                + "<th>Win Price</th>"
                + "<th>Rules</th>"
                + "<th>Max Participants</th>"
                + "</tr>");

        for (int i = 0; i < tournaments.size(); i++) {
            TournamentData t = tournaments.elementAt(i);

            out.println("<tr>");
            out.println("<td>" + t.id + "</td>");
            out.println("<td>" + t.organizer_id + "</td>");
            out.println("<td>" + t.tournament + "</td>");
            out.println("<td>" + t.modality + "</td>");
            out.println("<td>" + t.location + "</td>");
            out.println("<td>" + t.tournament_date + "</td>");
            out.println("<td>" + t.entry_price + "</td>");
            out.println("<td>" + t.win_price + "</td>");
            out.println("<td>" + t.rules + "</td>");
            out.println("<td>" + t.max_partici + "</td>");
            out.println("</tr>");
        }

        out.println("</table>");
        out.println(Utils.footer());
        out.close();
    }
}
