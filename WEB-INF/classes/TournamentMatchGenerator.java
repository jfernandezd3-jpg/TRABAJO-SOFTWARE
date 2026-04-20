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

            // Primera ronda real
            List<String> matches = generateMatches(modality, players);

            // arbol solo para chess
            List<List<String>> bracket = new ArrayList<>();
            if (modality.equalsIgnoreCase("chess")) {
                bracket = generateBracketStructure(matches);
            }

            out.println("<h2>Emparejamientos (" + modality + ")</h2>");
            out.println("<div id='matchArea'></div>");

            out.println("<script>");
            out.println("var modality = '" + modality + "';");
            out.println("var matches = " + toJson(matches) + ";");
            out.println("var bracket = " + toJson2D(bracket) + ";");
            out.println("</script>");

            out.println("<script src='js/matchVisualizer.js'></script>");

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
                result.add("Jugador " + p1 + " vs Jugador " + p2);
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

    // arbol solo para chess
    private List<List<String>> generateBracketStructure(List<String> firstRound) {
        List<List<String>> rounds = new ArrayList<>();
        rounds.add(firstRound);

        int matchCount = firstRound.size();
        int roundNumber = 1;

        while (matchCount > 1) {
            List<String> nextRound = new ArrayList<>();

            for (int i = 0; i < matchCount; i += 2) {
                String left = "(Ganador " + roundNumber + "." + (i + 1) + ")";
                String right = (i + 1 < matchCount)
                        ? "(Ganador " + roundNumber + "." + (i + 2) + ")"
                        : "BYE";

                nextRound.add(left + " vs " + right);
            }

            rounds.add(nextRound);
            matchCount = nextRound.size();
            roundNumber++;
        }

        return rounds;
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

    private String toJson2D(List<List<String>> data) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < data.size(); i++) {
            sb.append("[");
            List<String> round = data.get(i);
            for (int j = 0; j < round.size(); j++) {
                sb.append("\"").append(round.get(j)).append("\"");
                if (j < round.size() - 1) sb.append(",");
            }
            sb.append("]");
            if (i < data.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
