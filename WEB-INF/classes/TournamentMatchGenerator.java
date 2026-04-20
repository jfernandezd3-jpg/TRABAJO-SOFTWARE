import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;
import java.util.*;

public class TournamentMatchGenerator extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        res.setContentType("text/html; charset=UTF-8");
        PrintWriter out = res.getWriter();
        Connection connection = ConnectionUtils.getConnection(getServletConfig());

        String tourStr = req.getParameter("tournament_id");

        out.println(Utils.header("Generador de Emparejamientos", req));

        try {
            int tournamentId = Integer.parseInt(tourStr);

            String modality = TournamentData.getTournamentModality(connection, tournamentId);
            List<Integer> players = TournamentData.getAcceptedPlayers(connection, tournamentId);

            if (modality == null || players.isEmpty()) {
                out.println("<h3>No hay jugadores aceptados para este torneo.</h3>");
                out.println(Utils.footer());
                return;
            }

            Collections.shuffle(players);

            // Generamos emparejamientos según modalidad
            List<String> matches = generateMatches(modality, players);

            // Contenedor donde JS pintará todo
            out.println("<h2>Emparejamientos (" + modality + ")</h2>");
            out.println("<div id='matchArea'></div>");

            // Pasamos los datos a JS
            out.println("<script>");
            out.println("var modality = '" + modality + "';");
            out.println("var matches = " + toJson(matches) + ";");
            out.println("</script>");

            // Cargamos JS externo
            out.println("<script src='matchVisualizer.js'></script>");

        } catch (Exception e) {
            out.println("<h3>Error: parámetro inválido.</h3>");
        }

        out.println(Utils.footer());
        ConnectionUtils.close(connection);
    }

    private List<String> generateMatches(String modality, List<Integer> players) {
        List<String> result = new ArrayList<>();

        if (modality.equalsIgnoreCase("chess")) {
            for (int i = 0; i < players.size(); i += 2) {
                String p1 = String.valueOf(players.get(i));
                String p2 = (i + 1 < players.size()) ? String.valueOf(players.get(i + 1)) : "BYE";
                result.add(p1 + " vs " + p2);
            }
        }

        else if (modality.equalsIgnoreCase("mus")) {
            for (int i = 0; i < players.size(); i += 4) {
                String mesa = "Mesa: ";
                for (int j = 0; j < 4; j++) {
                    mesa += (i + j < players.size()) ? players.get(i + j) : "BYE";
                    if (j < 3) mesa += ", ";
                }
                result.add(mesa);
            }
        }

        else if (modality.equalsIgnoreCase("poker")) {
            for (int i = 0; i < players.size(); i += 8) {
                String mesa = "Mesa: ";
                for (int j = 0; j < 8 && i + j < players.size(); j++) {
                    mesa += players.get(i + j);
                    if (j < 7 && i + j + 1 < players.size()) mesa += ", ";
                }
                result.add(mesa);
            }
        }

        return result;
    }

    private String toJson(List<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(list.get(i)).append("\"");
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
