import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet encargado de la creación de nuevos torneos (FR13).
 * Autor: Paul
 */
@WebServlet("/PCreateTournamentServlet")
public class PCreateTournamentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // Llamada a ConnectionUtils para obtener la conexión al Access
        this.connection = ConnectionUtils.getConnection(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        try {
            // 1. Recoger datos del formulario (insertournament.html)
			String name = req.getParameter("tournamentName");
            String modality = req.getParameter("modality");
            String dateTime = req.getParameter("dateTime");
            String address = req.getParameter("address");
            String rules = req.getParameter("rules");
            
            // Conversión de tipos numéricos
			int organizerId = Integer.parseInt(req.getParameter("organizerId"));
            int maxParticipants = Integer.parseInt(req.getParameter("maxParticipants"));
			double prizes = Double.parseDouble(req.getParameter("prizes"));
            double entryPrice = Double.parseDouble(req.getParameter("entryPrice"));

            // 2. Crear objeto de datos y realizar la inserción
            PTournamentData torneo = new PTournamentData(organizerId, name, modality, address, dateTime, entryPrice, prizes, rules, maxParticipants);
            int result = PTournamentData.insertTournament(connection, torneo);

            // 3. Generar respuesta visual usando tu Utils.java actualizado
            // IMPORTANTE: Ahora pasamos 'req' como segundo parámetro
            out.println(Utils.header("Estado de Creacion", req));
            
            out.println("<div style='text-align: center; padding: 20px;'>");
            if (result > 0) {
                out.println("<h3 style='color: green;'>¡Exito!</h3>");
                out.println("<p>El torneo <strong>" + name + "</strong> ha sido creado correctamente.</p>");
            } else {
                out.println("<h3 style='color: red;'>Error</h3>");
                out.println("<p>No se pudo guardar el torneo en la base de datos.</p>");
            }
            out.println("<br><a href='index.html' class='button'>Volver al Inicio</a>");
            out.println("</div>");
            
            out.println(Utils.footer());

        } catch (NumberFormatException e) {
            // También actualizamos el header en los catch
            out.println(Utils.header("Error de Formato", req));
            out.println("<p style='color: red; text-align: center;'>Error: Los campos de precio o participantes deben ser numericos.</p>");
            out.println("<div style='text-align: center;'><a href='insertournament.html'>Reintentar</a></div>");
            out.println(Utils.footer());
        } catch (Exception e) {
            out.println(Utils.header("Error General", req));
            out.println("<p style='text-align: center;'>Ocurrio un error inesperado: " + e.getMessage() + "</p>");
            out.println(Utils.footer());
        }
    }
}