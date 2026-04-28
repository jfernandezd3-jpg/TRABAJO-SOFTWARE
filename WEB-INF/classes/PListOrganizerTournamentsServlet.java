import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet para listar torneos de un organizador específico.
 * Autor: Paul
 */
@WebServlet("/PListOrganizerTournamentsServlet")
public class PListOrganizerTournamentsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // Usamos tu clase ConnectionUtils
        this.connection = ConnectionUtils.getConnection(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        try {
            // 1. Obtener el ID del organizador del formulario
            int orgId = Integer.parseInt(req.getParameter("organizerId"));
            
            // 2. Llamar al método que creamos en PTournamentData
            Vector<PTournamentData> torneos = PTournamentData.getTournamentsByOrganizer(connection, orgId);

            // 3. Pintar la respuesta usando tu Utils
            out.println(Utils.header("Mis Torneos Organizados", req));
            
            out.println("<div style='padding: 20px; max-width: 900px; margin: auto;'>");
            out.println("<h3 align='center'>Panel de Control - Organizador ID: " + orgId + "</h3>");

            if (torneos.isEmpty()) {
                out.println("<p align='center'>No tienes torneos creados todavia.</p>");
                out.println("<p align='center'><a href='insertTournament.html'>Crea tu primer torneo aqui</a></p>");
            } else {
                out.println("<table border='1' style='width:100%; border-collapse: collapse; margin-top: 20px;'>");
                out.println("<tr style='background-color: #0078ff; color: white;'>");
                out.println("<th>Torneo</th><th>Modalidad</th><th>Fecha</th><th>Ubicacion</th><th>Accion</th>");
                out.println("</tr>");

                for (PTournamentData t : torneos) {
                    out.println("<tr>");
                    out.println("<td style='padding: 10px;'>" + t.tournament + "</td>");
                    out.println("<td style='padding: 10px;'>" + t.modality + "</td>");
                    out.println("<td style='padding: 10px;'>" + t.tournament_date + "</td>");
                    out.println("<td style='padding: 10px;'>" + t.location + "</td>");
                    
                    out.println("<td style='padding: 10px; text-align: center;'>");
                    out.println("<a href='PEditTournamentServlet?id=" + t.id + "' style='color: #0078ff; font-weight: bold; margin-right: 15px;'>Editar</a>");
                    out.println("<a href='PCreateTournamentPosterServlet?id=" + t.id + "' style='color: #28a745; font-weight: bold;'>Crear Cartel</a>");
                    out.println("</td>");
                    
                    out.println("</tr>");
                }
                out.println("</table>");
            }
            
            out.println("<br><div style='text-align: center;'><a href='home.html'>Volver al Inicio</a></div>");
            out.println("</div>");
            out.println(Utils.footer());

        } catch (NumberFormatException e) {
            out.println(Utils.header("Error de ID", req));
            out.println("<p align='center' style='color: red;'>El ID del organizador debe ser un número valido.</p>");
            out.println(Utils.footer());
        } catch (Exception e) {
            out.println(Utils.header("Error", req));
            out.println("<p align='center'>Error al cargar la lista: " + e.getMessage() + "</p>");
            out.println(Utils.footer());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doPost(req, res);
    }
}