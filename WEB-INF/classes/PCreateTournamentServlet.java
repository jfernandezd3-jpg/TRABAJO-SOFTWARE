import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet encargado de la creación de nuevos torneos (FR13).
 * Autor: Paul
 *
 * Cambios:
 *  - El organizerId se obtiene automáticamente de la sesión (no hace falta introducirlo manualmente).
 *  - Soporta AJAX: si viene format=json responde JSON, si no responde HTML normal.
 */
public class PCreateTournamentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.connection = ConnectionUtils.getConnection(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // --- 1. Verificar sesión ---
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            sendError(req, res, "Debes iniciar sesión para crear un torneo.");
            return;
        }

        // --- 2. Obtener organizer_id automáticamente desde la sesión ---
        String email = (String) session.getAttribute("userEmail");
        int organizerId = -1;
        try {
            String sqlUser = "SELECT id FROM Users WHERE email = ?";
            PreparedStatement psUser = connection.prepareStatement(sqlUser);
            psUser.setString(1, email);
            ResultSet rs = psUser.executeQuery();
            if (rs.next()) {
                organizerId = rs.getInt("id");
            }
            rs.close();
            psUser.close();
        } catch (Exception e) {
            sendError(req, res, "Error al obtener el usuario de la sesión: " + e.getMessage());
            return;
        }

        if (organizerId == -1) {
            sendError(req, res, "No se encontró el usuario en la base de datos.");
            return;
        }

        // --- 3. Recoger datos del formulario ---
        String format = req.getParameter("format");
        boolean isAjax = "json".equals(format);

        try {
            String name      = req.getParameter("tournamentName");
            String modality  = req.getParameter("modality");
            String dateTime  = req.getParameter("dateTime");
            String address   = req.getParameter("address");
            String rules     = req.getParameter("rules");
            int maxParticipants = Integer.parseInt(req.getParameter("maxParticipants"));
            double prizes    = Double.parseDouble(req.getParameter("prizes"));
            double entryPrice = Double.parseDouble(req.getParameter("entryPrice"));
            double latitude  = Double.parseDouble(req.getParameter("latitude"));
            double longitude = Double.parseDouble(req.getParameter("longitude"));

            // --- 4. Insertar en la BD ---
            PTournamentData torneo = new PTournamentData(
                organizerId, name, modality, address, dateTime,
                entryPrice, prizes, rules, maxParticipants, latitude, longitude
            );
            int result = PTournamentData.insertTournament(connection, torneo);

            // --- 5. Responder ---
            if (isAjax) {
                res.setContentType("application/json; charset=UTF-8");
                PrintWriter out = res.getWriter();
                if (result > 0) {
                    out.print("{\"success\": true, \"message\": \"Torneo '" + esc(name) + "' creado correctamente.\"}");
                } else {
                    out.print("{\"success\": false, \"message\": \"No se pudo guardar el torneo en la base de datos.\"}");
                }
                out.close();
            } else {
                res.setContentType("text/html; charset=UTF-8");
                PrintWriter out = res.getWriter();
                out.println(Utils.header("Estado de Creacion", req));
                out.println("<div style='text-align: center; padding: 20px;'>");
                if (result > 0) {
                    out.println("<h3 style='color: green;'>Exito</h3>");
                    out.println("<p>El torneo <strong>" + name + "</strong> ha sido creado correctamente.</p>");
                } else {
                    out.println("<h3 style='color: red;'>Error</h3>");
                    out.println("<p>No se pudo guardar el torneo en la base de datos.</p>");
                }
                out.println("<br><a href='gestion_organizador.html'>Volver al Panel</a>");
                out.println("</div>");
                out.println(Utils.footer());
            }

        } catch (NumberFormatException e) {
            if (isAjax) {
                res.setContentType("application/json; charset=UTF-8");
                res.getWriter().print("{\"success\": false, \"message\": \"Los campos numéricos tienen un formato incorrecto.\"}");
            } else {
                res.setContentType("text/html; charset=UTF-8");
                PrintWriter out = res.getWriter();
                out.println(Utils.header("Error de Formato", req));
                out.println("<p style='color: red; text-align: center;'>Error: Los campos de precio o participantes deben ser numéricos.</p>");
                out.println("<div style='text-align: center;'><a href='insertTournament.html'>Reintentar</a></div>");
                out.println(Utils.footer());
            }
        } catch (Exception e) {
            if (isAjax) {
                res.setContentType("application/json; charset=UTF-8");
                res.getWriter().print("{\"success\": false, \"message\": \"Error inesperado: " + esc(e.getMessage()) + "\"}");
            } else {
                res.setContentType("text/html; charset=UTF-8");
                PrintWriter out = res.getWriter();
                out.println(Utils.header("Error General", req));
                out.println("<p style='text-align: center;'>Ocurrió un error inesperado: " + e.getMessage() + "</p>");
                out.println(Utils.footer());
            }
        }
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void sendError(HttpServletRequest req, HttpServletResponse res, String mensaje) throws IOException {
        String format = req.getParameter("format");
        if ("json".equals(format)) {
            res.setContentType("application/json; charset=UTF-8");
            res.getWriter().print("{\"success\": false, \"message\": \"" + esc(mensaje) + "\"}");
        } else {
            res.setContentType("text/html; charset=UTF-8");
            PrintWriter out = res.getWriter();
            out.println(Utils.header("Error", req));
            out.println("<p style='color:red; text-align:center;'>" + mensaje + "</p>");
            out.println("<div style='text-align:center;'><a href='insertTournament.html'>Volver</a></div>");
            out.println(Utils.footer());
        }
    }
}
