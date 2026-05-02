import java.io.*;
import java.util.Vector;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;

// JAIME FERNANDEZ DE BETOÑO

public class BMatchSetup extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        connection = ConnectionUtils.getConnection(config);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html; charset=UTF-8");
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            res.sendRedirect("login.html");
            return;
        }

        String idTournamentStr = req.getParameter("id_tournament");

        out.println(Utils.header("Preparar Partido", req));
        out.println("<div class='container' style='max-width: 600px;'>");
        out.println("<h2 style='text-align:center; color:#1a4f2c; margin-bottom: 20px;'>Configurar Nuevo Partido</h2>");

        try {
            if (idTournamentStr == null) {
                out.println("<form action='BMatchSetup' method='GET' style='text-align: center;'>");
                out.println("<div style='margin-bottom: 20px;'>");
                out.println("<label style='font-weight: bold; display: block; margin-bottom: 10px; text-align: left;'>1. Selecciona el Torneo:</label>");
                out.println("<select name='id_tournament' required style='width: 100%; padding: 10px; border-radius: 4px; border: 1px solid #ccc;'>");
                out.println("<option value='' disabled selected>-- Elige un torneo --</option>");

                Vector<BTournamentData> torneos = BTournamentData.getTournamentList(connection);
                for (BTournamentData t : torneos) {
                    out.println("<option value='" + t.id + "'>" + t.tournament + "</option>");
                }
                
                out.println("</select>");
                out.println("</div>");
                out.println("<button type='submit' class='btn' style='padding: 10px 20px; font-size: 16px; width: auto;'>Siguiente</button>");
                out.println("</form>");
            } else {
                int idTournament = Integer.parseInt(idTournamentStr);
                
                BTournamentData torneo = BTournamentData.getTournamentById(connection, idTournament);
                String tName = (torneo != null) ? torneo.tournament : "Torneo Desconocido";

                out.println("<form action='BMatchScoreConsole' method='POST' style='text-align: center;'>");
                out.println("<input type='hidden' name='id_tournament' value='" + idTournament + "'>");
                
                out.println("<div style='margin-bottom: 20px; background-color: #f8f9fa; padding: 15px; border-radius: 8px; border: 1px solid #ddd;'>");
                out.println("<p style='margin: 0 0 10px 0;'>Torneo seleccionado: <strong>" + tName + "</strong></p>");
                out.println("<a href='BMatchSetup' class='btn' style='font-size: 12px; padding: 4px 8px; background-color: #6c757d; width: auto;'>Cambiar torneo</a>");
                out.println("</div>");

                Vector<String[]> jugadores = BTournamentData.getAcceptedPlayers(connection, idTournament);

                out.println("<div style='margin-bottom: 20px;'>");
                out.println("<label style='font-weight: bold; display: block; margin-bottom: 10px; text-align: left;'>2. Selecciona al Jugador 1:</label>");
                out.println("<select name='id_player1' required style='width: 100%; padding: 10px; border-radius: 4px; border: 1px solid #ccc;'>");
                out.println("<option value='' disabled selected>-- Elige al Jugador 1 --</option>");
                for (String[] j : jugadores) {
                    out.println("<option value='" + j[0] + "'>" + j[1] + "</option>");
                }
                out.println("</select>");
                out.println("</div>");

                out.println("<div style='margin-bottom: 30px;'>");
                out.println("<label style='font-weight: bold; display: block; margin-bottom: 10px; text-align: left;'>3. Selecciona al Jugador 2:</label>");
                out.println("<select name='id_player2' required style='width: 100%; padding: 10px; border-radius: 4px; border: 1px solid #ccc;'>");
                out.println("<option value='' disabled selected>-- Elige al Jugador 2 --</option>");
                for (String[] j : jugadores) {
                    out.println("<option value='" + j[0] + "'>" + j[1] + "</option>");
                }
                out.println("</select>");
                out.println("</div>");

                out.println("<button type='submit' class='btn' style='padding: 10px 20px; font-size: 16px; width: auto; background-color: #28a745;'>Empezar a Arbitrar</button>");
                out.println("</form>");
            }
        } catch (Exception e) {
            out.println("<div class='info-box' style='border-left-color:red;'><p style='color:red;'>Error al cargar los datos: " + e.getMessage() + "</p></div>");
        }

        out.println("<div style='text-align:center; margin-top: 30px;'>");
        out.println("<a href='gestion_organizador.html' class='btn' style='display:inline-block; width:auto; text-decoration:none; background-color:#6c757d;'>Volver al Panel</a>");
        out.println("</div>");

        out.println("</div>");
        out.println(Utils.footer());
    }
}