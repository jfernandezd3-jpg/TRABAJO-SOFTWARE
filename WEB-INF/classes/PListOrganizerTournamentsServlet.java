import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet para listar torneos del organizador que ha iniciado sesión.
 * Autor: Paul
 *
 * Cambios:
 *  - El organizerId se obtiene automáticamente de la sesión.
 *  - Soporta AJAX: si viene format=json responde JSON, si no responde HTML normal.
 */
public class PListOrganizerTournamentsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.connection = ConnectionUtils.getConnection(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doPost(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // --- 1. Verificar sesión ---
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            sendError(req, res, "Debes iniciar sesión para ver tus torneos.");
            return;
        }

        // --- 2. Obtener organizerId desde la sesión ---
        String email = (String) session.getAttribute("userEmail");
        int orgId = -1;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT id FROM Users WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) orgId = rs.getInt("id");
            rs.close();
            ps.close();
        } catch (Exception e) {
            sendError(req, res, "Error al obtener el usuario de la sesión: " + e.getMessage());
            return;
        }

        if (orgId == -1) {
            sendError(req, res, "No se encontró el usuario en la base de datos.");
            return;
        }

        // --- 3. Obtener torneos del organizador ---
        String format = req.getParameter("format");
        boolean isAjax = "json".equals(format);

        try {
            Vector<PTournamentData> torneos = PTournamentData.getTournamentsByOrganizer(connection, orgId);

            if (isAjax) {
                // --- Respuesta JSON ---
                res.setContentType("application/json; charset=UTF-8");
                PrintWriter out = res.getWriter();
                StringBuilder sb = new StringBuilder();
                sb.append("{\"success\": true, \"torneos\": [");
                for (int i = 0; i < torneos.size(); i++) {
                    PTournamentData t = torneos.get(i);
                    if (i > 0) sb.append(",");
                    sb.append("{");
                    sb.append("\"id\": ").append(t.id).append(",");
                    sb.append("\"tournament\": \"").append(esc(t.tournament)).append("\",");
                    sb.append("\"modality\": \"").append(esc(t.modality)).append("\",");
                    sb.append("\"tournament_date\": \"").append(esc(String.valueOf(t.tournament_date))).append("\",");
                    sb.append("\"location\": \"").append(esc(t.location)).append("\"");
                    sb.append("}");
                }
                sb.append("]}");
                out.print(sb.toString());
                out.close();

            } else {
                // --- Respuesta HTML normal (fallback) ---
                res.setContentType("text/html; charset=UTF-8");
                PrintWriter out = res.getWriter();
                out.println(Utils.header("Mis Torneos Organizados", req));
                out.println("<div style='padding: 20px; max-width: 900px; margin: auto;'>");
                out.println("<h3 align='center'>Panel de Control</h3>");

                if (torneos.isEmpty()) {
                    out.println("<p align='center'>No tienes torneos creados todavía.</p>");
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
                        out.println("<a href='PEditTournamentServlet?id=" + t.id + "' style='color:#0078ff; font-weight:bold; margin-right:15px;'>Editar</a>");
                        out.println("<a href='PCreateTournamentPosterServlet?id=" + t.id + "' style='color:#28a745; font-weight:bold;'>Crear Cartel</a>");
                        out.println("</td>");
                        out.println("</tr>");
                    }
                    out.println("</table>");
                }
                out.println("<br><div style='text-align:center;'><a href='home.html'>Volver al Inicio</a></div>");
                out.println("</div>");
                out.println(Utils.footer());
            }

        } catch (Exception e) {
            sendError(req, res, "Error al cargar la lista: " + e.getMessage());
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
            out.println("<div style='text-align:center;'><a href='gestion_organizador.html'>Volver al Panel</a></div>");
            out.println(Utils.footer());
        }
    }
}
