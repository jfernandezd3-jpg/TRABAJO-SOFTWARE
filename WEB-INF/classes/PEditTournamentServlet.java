import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/PEditTournamentServlet")
public class PEditTournamentServlet extends HttpServlet {
    private Connection connection;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.connection = ConnectionUtils.getConnection(config);
    }

    // doGet: Carga los datos actuales y los "pinta" en el formulario
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        PTournamentData t = PTournamentData.getTournamentById(connection, id);

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        out.println(Utils.header("Editando: " + t.tournament, req));
        
        // Aquí "pintamos" el formulario con los valores (value='...') cargados de la BD
        out.println("<form action='PEditTournamentServlet' method='POST' style='padding:20px;'>");
        out.println("<input type='hidden' name='id' value='" + t.id + "'>");
        out.println("Nombre: <input type='text' name='tournamentName' value='" + t.tournament + "'><br><br>");
        out.println("Modalidad: <input type='text' name='modality' value='" + t.modality + "'><br><br>");
        out.println("Fecha: <input type='text' name='dateTime' value='" + t.tournament_date + "'><br><br>");
        out.println("Ubicacion: <input type='text' name='location' value='" + t.location + "'><br><br>");
        out.println("Precio: <input type='number' name='entryPrice' step='0.01' value='" + t.entry_price + "'><br><br>");
        out.println("Premios: <input type='number' name='prizes' step='0.01' value='" + t.win_price + "'><br><br>");
        out.println("Reglas: <textarea name='rules'>" + t.rules + "</textarea><br><br>");
        out.println("Participantes: <input type='number' name='maxParticipants' value='" + t.max_partici + "'><br><br>");
        out.println("Latitud: <input type='number' name='latitude' step='any' value='" + t.latitude + "'><br><br>");
        out.println("Longitud: <input type='number' name='longitude' step='any' value='" + t.longitude + "'><br><br>");
        out.println("<button type='submit'>Guardar Cambios</button>");
        out.println("</form>");
        
        out.println(Utils.footer());
    }

    // doPost: Recibe el formulario editado y hace el UPDATE
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            String name = req.getParameter("tournamentName");
            String modality = req.getParameter("modality");
            String dateTime = req.getParameter("dateTime");
            String location = req.getParameter("location");
            String rules = req.getParameter("rules");
            double prizes = Double.parseDouble(req.getParameter("prizes"));
            double entryPrice = Double.parseDouble(req.getParameter("entryPrice"));
            int maxPart = Integer.parseInt(req.getParameter("maxParticipants"));
            double latitude = Double.parseDouble(req.getParameter("latitude"));
            double longitude = Double.parseDouble(req.getParameter("longitude"));

            PTournamentData t = new PTournamentData(id, 0, name, modality, location, dateTime, entryPrice, prizes, rules, maxPart, latitude, longitude);
            int result = PTournamentData.updateTournament(connection, t);

            res.setContentType("text/html");
            PrintWriter out = res.getWriter();
            out.println(Utils.header("Resultado Edicion", req));
            if (result > 0) {
                out.println("<h3>Torneo actualizado con exito</h3>");
            } else {
                out.println("<h3>Error al actualizar.</h3>");
            }
            out.println("<a href='index.html'>Volver</a>");
            out.println(Utils.footer());
        } catch (Exception e) { e.printStackTrace(); }
    }
}