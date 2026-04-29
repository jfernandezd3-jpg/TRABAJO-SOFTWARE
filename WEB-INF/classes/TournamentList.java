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

        String modality = req.getParameter("modality");
        String location = req.getParameter("location");

        Vector<TournamentData> tournaments;
        if ((modality != null && !modality.trim().isEmpty()) ||
            (location != null && !location.trim().isEmpty())) {
            tournaments = TournamentData.searchTournaments(connection, modality, location);
        } else {
            tournaments = TournamentData.getTournamentList(connection);
        }

        // --- NUEVO: si viene format=json respondemos con JSON ---
        String format = req.getParameter("format");
        if ("json".equals(format)) {
            respondJson(res, tournaments);
            ConnectionUtils.close(connection);
            return;
        }
        // --- FIN NUEVO ---

        out.println(Utils.header("Lista de Torneos", req));

        // Formulario de filtro — sin action/method, el submit lo gestiona AJAX
        out.println("<form id='filterForm'>");
        out.println("Modalidad: <input type='text' id='modality' name='modality'> ");
        out.println("Localidad: <input type='text' id='location' name='location'> ");
        out.println("<input type='submit' value='Filtrar'>");
        out.println("<button type='button' id='btnTodos'>Ver todos</button>");
        out.println("</form><br>");

        // Div donde se renderiza la tabla via AJAX
        out.println("<div id='resultados'>");

        // Tabla inicial con los datos que ya tiene el servlet (carga normal)
        out.println(buildTable(tournaments));

        out.println("</div>");

        // AJAX: al filtrar, actualizamos solo la tabla sin recargar la página
        out.println("<script>");
        out.println("function cargarTorneos(modality, location) {");
        out.println("  var params = new URLSearchParams({ format: 'json' });");
        out.println("  if (modality) params.set('modality', modality);");
        out.println("  if (location)  params.set('location',  location);");
        out.println("  document.getElementById('resultados').innerHTML = '<p>Buscando...</p>';");
        out.println("  fetch('TournamentList?' + params.toString())");
        out.println("    .then(function(r) { return r.json(); })");
        out.println("    .then(function(data) {");
        out.println("      if (data.length === 0) {");
        out.println("        document.getElementById('resultados').innerHTML = '<p>No se encontraron torneos.</p>';");
        out.println("        return;");
        out.println("      }");
        out.println("      var html = '<table border=\\'1\\'><tr><th>ID</th><th>Organizer</th><th>Tournament</th><th>Modality</th><th>Location</th><th>Entry Price</th><th>Win Price</th><th>Rules</th><th>Max Participants</th></tr>';");
        out.println("      data.forEach(function(t) {");
        out.println("        html += '<tr><td>'+t.id+'</td><td>'+t.organizer_id+'</td><td>'+t.tournament+'</td><td>'+t.modality+'</td><td>'+t.location+'</td><td>'+t.entry_price+'</td><td>'+t.win_price+'</td><td>'+t.rules+'</td><td>'+t.max_partici+'</td></tr>';");
        out.println("      });");
        out.println("      html += '</table>';");
        out.println("      document.getElementById('resultados').innerHTML = html;");
        out.println("    })");
        out.println("    .catch(function() {");
        out.println("      document.getElementById('resultados').innerHTML = '<p style=\\'color:red;\\'>Error de conexión.</p>';");
        out.println("    });");
        out.println("}");
        out.println("document.getElementById('filterForm').addEventListener('submit', function(e) {");
        out.println("  e.preventDefault();");
        out.println("  cargarTorneos(document.getElementById('modality').value, document.getElementById('location').value);");
        out.println("});");
        out.println("document.getElementById('btnTodos').addEventListener('click', function() {");
        out.println("  cargarTorneos('', '');");
        out.println("});");
        out.println("</script>");

        out.println(Utils.footer());
        out.close();
        ConnectionUtils.close(connection);
    }

    // Devuelve JSON cuando se llama con format=json (para las llamadas AJAX del propio servlet)
    private void respondJson(HttpServletResponse res, Vector<TournamentData> tournaments) throws IOException {
        res.setContentType("application/json; charset=UTF-8");
        PrintWriter out = res.getWriter();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < tournaments.size(); i++) {
            TournamentData t = tournaments.get(i);
            sb.append("{");
            sb.append("\"id\": ").append(t.id).append(", ");
            sb.append("\"organizer_id\": ").append(t.organizer_id).append(", ");
            sb.append("\"tournament\": \"").append(esc(t.tournament)).append("\", ");
            sb.append("\"modality\": \"").append(esc(t.modality)).append("\", ");
            sb.append("\"location\": \"").append(esc(t.location)).append("\", ");
            sb.append("\"tournament_date\": \"").append(esc(t.tournament_date)).append("\", ");
            sb.append("\"entry_price\": ").append(t.entry_price).append(", ");
            sb.append("\"win_price\": ").append(t.win_price).append(", ");
            sb.append("\"rules\": \"").append(esc(t.rules)).append("\", ");
            sb.append("\"max_partici\": ").append(t.max_partici);
            sb.append("}");
            if (i < tournaments.size() - 1) sb.append(", ");
        }
        sb.append("]");
        out.println(sb.toString());
        out.close();
    }

    private String buildTable(Vector<TournamentData> tournaments) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        sb.append("<tr><th>ID</th><th>Organizer</th><th>Tournament</th><th>Modality</th>"
                + "<th>Location</th><th>Entry Price</th><th>Win Price</th><th>Rules</th><th>Max Participants</th></tr>");
        for (TournamentData t : tournaments) {
            sb.append("<tr>");
            sb.append("<td>").append(t.id).append("</td>");
            sb.append("<td>").append(t.organizer_id).append("</td>");
            sb.append("<td>").append(t.tournament).append("</td>");
            sb.append("<td>").append(t.modality).append("</td>");
            sb.append("<td>").append(t.location).append("</td>");
            sb.append("<td>").append(t.entry_price).append("</td>");
            sb.append("<td>").append(t.win_price).append("</td>");
            sb.append("<td>").append(t.rules).append("</td>");
            sb.append("<td>").append(t.max_partici).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
