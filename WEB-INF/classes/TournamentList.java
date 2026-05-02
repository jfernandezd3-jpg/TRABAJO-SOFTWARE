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

        out.println("<style>");
        out.println("#resultados table { width: 100%; border-collapse: collapse; font-size: 14px; }");
        out.println("#resultados th, #resultados td { padding: 10px 6px !important; text-align: center; }");
        out.println("</style>");

        out.println("<div style='background-color: white; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); padding: 20px; margin: 20px auto; width: 98%; max-width: 1400px; box-sizing: border-box;'>");

        // Formulario de filtro — sin action/method, el submit lo gestiona AJAX
        out.println("<form id='filterForm' style='text-align: center; margin-bottom: 25px;'>");
        out.println("Modalidad: <input type='text' id='modality' name='modality' style='padding: 6px; border-radius: 4px; border: 1px solid #ccc; margin-right: 15px;'> ");
        out.println("Localidad: <input type='text' id='location' name='location' style='padding: 6px; border-radius: 4px; border: 1px solid #ccc; margin-right: 15px;'> ");
        out.println("<input type='submit' value='Filtrar' class='btn' style='padding: 8px 15px; font-size: 14px; width: auto; margin-right: 5px;'>");
        out.println("<button type='button' id='btnTodos' class='btn' style='padding: 8px 15px; font-size: 14px; width: auto; background-color: #6c757d; margin-right: 5px;'>Ver todos</button>");
        out.println("<button type='button' class='btn' style='padding: 8px 15px; font-size: 14px; width: auto; background-color: #17a2b8;' onclick=\"window.location.href='BTournamentMap'\">Ver en Mapa</button>");
        out.println("</form><br>");

        // Div donde se renderiza la tabla via AJAX
        out.println("<div id='resultados' style='overflow-x: auto;'>");

        // Tabla inicial con los datos que ya tiene el servlet (carga normal)
        out.println(buildTable(tournaments));

        out.println("</div>");

        // AJAX: al filtrar, actualizamos solo la tabla sin recargar la página
        out.println("<script>");
        out.println("function cargarTorneos(modality, location) {");
        out.println("  var params = new URLSearchParams({ format: 'json' });");
        out.println("  if (modality) params.set('modality', modality);");
        out.println("  if (location)  params.set('location',  location);");
        out.println("  document.getElementById('resultados').innerHTML = '<p style=\"text-align:center;\">Buscando...</p>';");
        out.println("  fetch('TournamentList?' + params.toString())");
        out.println("    .then(function(r) { return r.json(); })");
        out.println("    .then(function(data) {");
        out.println("      if (data.length === 0) {");
        out.println("        document.getElementById('resultados').innerHTML = '<p style=\"text-align:center; color:#666;\">No se encontraron torneos.</p>';");
        out.println("        return;");
        out.println("      }");
        out.println("      var html = '<table><tr><th>ID</th><th>Organizer</th><th>Tournament</th><th>Modality</th><th>Location</th><th>Entry Price</th><th>Win Price</th><th>Rules</th><th>Max Participants</th><th>Details</th></tr>';");
        out.println("      data.forEach(function(t) {");
        out.println("        html += '<tr><td>'+t.id+'</td><td>'+t.organizer_id+'</td><td>'+t.tournament+'</td><td>'+t.modality+'</td><td>'+t.location+'</td><td>'+t.entry_price+' &euro;</td><td>'+t.win_price+' &euro;</td><td>'+t.rules+'</td><td>'+t.max_partici+'</td><td style=\"text-align: center;\"><a href=\"BTournamentInfo?id='+t.id+'\" class=\"btn\" style=\"padding: 4px 8px; font-size: 12px; text-decoration: none; white-space: nowrap;\">Ver Info</a></td></tr>';");
        out.println("      });");
        out.println("      html += '</table>';");
        out.println("      document.getElementById('resultados').innerHTML = html;");
        out.println("    })");
        out.println("    .catch(function() {");
        out.println("      document.getElementById('resultados').innerHTML = '<p style=\"color:red; text-align:center;\">Error de conexión.</p>';");
        out.println("    });");
        out.println("}");
        out.println("document.getElementById('filterForm').addEventListener('submit', function(e) {");
        out.println("  e.preventDefault();");
        out.println("  cargarTorneos(document.getElementById('modality').value, document.getElementById('location').value);");
        out.println("});");
        out.println("document.getElementById('btnTodos').addEventListener('click', function() {");
        out.println("  document.getElementById('modality').value = '';");
        out.println("  document.getElementById('location').value = '';");
        out.println("  cargarTorneos('', '');");
        out.println("});");
        out.println("</script>");

        out.println("<div class='text-center' style='margin-top: 30px;'><a href='home.html' class='btn' style='display:inline-block; width:auto; text-decoration:none;'>Volver al Panel</a></div>");
        out.println("</div>");

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
        sb.append("<table>");
        sb.append("<tr><th>ID</th><th>Organizer</th><th>Tournament</th><th>Modality</th>"
                + "<th>Location</th><th>Entry Price</th><th>Win Price</th><th>Rules</th><th>Max Participants</th><th>Details</th></tr>");
        
        if (tournaments.isEmpty()) {
            sb.append("<tr><td colspan='10' style='text-align:center;'>No se encontraron torneos.</td></tr>");
        } else {
            for (TournamentData t : tournaments) {
                sb.append("<tr>");
                sb.append("<td>").append(t.id).append("</td>");
                sb.append("<td>").append(t.organizer_id).append("</td>");
                sb.append("<td>").append(t.tournament).append("</td>");
                sb.append("<td>").append(t.modality).append("</td>");
                sb.append("<td>").append(t.location).append("</td>");
                sb.append("<td>").append(t.entry_price).append(" &euro;</td>");
                sb.append("<td>").append(t.win_price).append(" &euro;</td>");
                sb.append("<td>").append(t.rules).append("</td>");
                sb.append("<td>").append(t.max_partici).append("</td>");
                sb.append("<td style='text-align: center;'><a href='BTournamentInfo?id=").append(t.id).append("' class='btn' style='padding: 4px 8px; font-size: 12px; text-decoration: none; white-space: nowrap;'>Ver Info</a></td>");
                sb.append("</tr>");
            }
        }
        sb.append("</table>");
        return sb.toString();
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}