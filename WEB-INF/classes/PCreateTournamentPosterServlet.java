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
 * Servlet para generar un cartel publicitario del torneo.
 * Autor: Paul
 */
@WebServlet("/PCreateTournamentPosterServlet")
public class PCreateTournamentPosterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.connection = ConnectionUtils.getConnection(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        try {
            // 1. Recoger el ID del torneo por la URL
            int id = Integer.parseInt(req.getParameter("id"));
            
            // 2. Reutilizamos tu método maestro para obtener los datos
            PTournamentData t = PTournamentData.getTournamentById(connection, id);

            if (t == null) {
                out.println(Utils.header("Error", req));
                out.println("<p align='center'>No se encontró el torneo.</p>");
                out.println(Utils.footer());
                return;
            }

            // 3. Lógica para elegir la imagen según la modalidad
            // Asegúrate de tener estas imágenes dentro de una carpeta "img" en tu WebContent
            String imagenMod = "default.jpg"; // Imagen por defecto
            String mod = t.modality.toLowerCase();
            
            if (mod.contains("mus")) {
                imagenMod = "mus.jpg";
            } else if (mod.contains("ajedrez") || mod.contains("chess")) {
                imagenMod = "chess.jpg";
            } else if (mod.contains("poker") || mod.contains("póker")) {
                imagenMod = "poker.jpg";
            }

            // 4. Dibujar el Cartel (Tabla de 1 fila y 1 columna)
            out.println(Utils.header("Cartel: " + t.tournament, req));
            
            out.println("<div style='display: flex; justify-content: center; padding: 40px;'>");
            
            // Inicio de la tabla de 1 fila y 1 columna con diseño de Cartel
            out.println("<table border='1' style='width: 400px; border-collapse: collapse; text-align: center; box-shadow: 5px 5px 15px rgba(0,0,0,0.3); border: 3px solid #0078ff; background-color: #fdfdfd;'>");
            out.println("<tr><td style='padding: 20px;'>");

            // Título en negrita y grande
            out.println("<h1 style='color: #333; margin-top: 0;'><b>" + t.tournament.toUpperCase() + "</b></h1>");
            
            // Imagen de la modalidad
            out.println("<img src='img/" + imagenMod + "' alt='" + t.modality + "' style='width: 250px; height: auto; border-radius: 8px; border: 1px solid #ccc; margin-bottom: 20px;'>");
            
            // Datos del torneo
            out.println("<h3 style='color: #0078ff; border-bottom: 1px solid #ccc; padding-bottom: 5px;'>Detalles del Evento</h3>");
            out.println("<p style='font-size: 18px;'><b>Modalidad:</b> " + t.modality + "</p>");
            out.println("<p style='font-size: 16px;'><b>Fecha y Hora:</b> " + t.tournament_date + "</p>");
            out.println("<p style='font-size: 16px;'><b>Lugar:</b> " + t.location + "</p>");
            
            out.println("<div style='background-color: #eee; padding: 10px; border-radius: 5px; margin: 15px 0;'>");
            out.println("<p style='margin: 5px 0;'><b>Reglas:</b></p>");
            out.println("<p style='margin: 0; font-style: italic;'>" + t.rules + "</p>");
            out.println("</div>");
            
            // Sección de Premios y Precios
            out.println("<p style='font-size: 18px; color: green;'><b>Premio al ganador:</b> " + t.win_price + " Euros</p>");
            out.println("<p style='font-size: 16px;'><b>Cuota de entrada:</b> " + t.entry_price + " Euros</p>");
            out.println("<p style='font-size: 14px; color: #666;'><b>Max. Participantes:</b> " + t.max_partici + " plazas</p>");

            out.println("</td></tr>");
            out.println("</table>");
            
            out.println("</div>");
            out.println("<div style='text-align: center;'><a href='searchorganizer.html' class='button'>Volver al Panel</a></div>");
            
            out.println(Utils.footer());

        } catch (Exception e) {
            out.println(Utils.header("Error", req));
            out.println("<p align='center'>Error al generar el cartel: " + e.getMessage() + "</p>");
            out.println(Utils.footer());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res); // Permitimos que funcione tanto por GET como por POST
    }
}