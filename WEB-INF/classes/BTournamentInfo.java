import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BTournamentInfo extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.connection = ConnectionUtils.getConnection(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");

        try (PrintWriter out = response.getWriter()) {
            
            String idParam = request.getParameter("id");
            
            if (idParam == null || idParam.trim().isEmpty()) {
                out.println(Utils.header("Error", request));
                out.println("<div class='container text-center'><h3 style='color:red;'>No se ha especificado ningun torneo.</h3></div>");
                out.println(Utils.footer());
                return;
            }

            int id = Integer.parseInt(idParam);

            BTournamentData torneo = BTournamentData.getTournamentById(connection, id);

            out.println(Utils.header(torneo != null ? torneo.tournament : "Torneo no encontrado", request));

            if (torneo != null) {
                out.println("<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css' />");
                out.println("<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>");

                out.println("<div class='container'>");
                
                // CAJA 1: Detalles principales
                out.println("  <div class='info-box'>");
                out.println("    <h3>Detalles Principales</h3>");
                out.println("    <p><strong>Modalidad:</strong> " + torneo.modality + "</p>");
                out.println("    <p><strong>Fecha y Hora:</strong> " + torneo.tournament_date + "</p>");
                out.println("    <p><strong>Ubicacion:</strong> " + torneo.location + "</p>");
                out.println("    <p><strong>Reglas:</strong> " + torneo.rules + "</p>");
                out.println("  </div>");

                // CAJA 2: Inscripción, Premios
                out.println("  <div class='info-box'>");
                out.println("    <h3>Inscripcion y Premios</h3>");
                out.println("    <p><strong>Precio de Entrada:</strong> " + torneo.entry_price + " EUR</p>");
                out.println("    <p><strong>Premio al Ganador:</strong> " + torneo.win_price + " EUR</p>");
                out.println("    <p><strong>Max. Participantes:</strong> " + torneo.max_partici + " personas</p>");
                out.println("  </div>");

                // CAJA 3: Mapa
                out.println("  <div class='info-box'>");
                out.println("    <h3>Ubicacion en el Mapa</h3>");
                
                if (torneo.latitude != 0.0 && torneo.longitude != 0.0) {
                    out.println("    <div id='map' style='height: 300px; border-radius: 8px; z-index: 1;'></div>");
                    
                    out.println("    <script>");
                    out.println("      var map = L.map('map').setView([" + torneo.latitude + ", " + torneo.longitude + "], 11);");
                    out.println("      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19, attribution: '&copy; OpenStreetMap' }).addTo(map);");
                    
                    String safeName = torneo.tournament.replace("'", "\\'");
                    out.println("      L.marker([" + torneo.latitude + ", " + torneo.longitude + "]).addTo(map)");
                    out.println("          .bindPopup('<b>" + safeName + "</b>').openPopup();");
                    out.println("    </script>");
                } else {
                    out.println("    <p style='color: #777;'><em>Las coordenadas GPS no estan disponibles para este torneo.</em></p>");
                }
                out.println("  </div>");

                out.println("  <div class='text-center' style='margin-top: 30px;'>");
                out.println("    <a href='home.html' class='btn' style='display: inline-block; width: auto; text-decoration: none;'>Volver al Inicio</a>");
                out.println("  </div>");
                
                out.println("</div>");
            } else {
                out.println("<div class='container text-center'>");
                out.println("  <h3 style='color: red;'>El torneo solicitado no existe en la base de datos.</h3>");
                out.println("  <br><a href='home.html' class='btn' style='display: inline-block; width: auto; text-decoration: none;'>Volver al Inicio</a>");
                out.println("</div>");
            }

            out.println(Utils.footer());
            
        } catch (NumberFormatException e) {
            PrintWriter out = response.getWriter();
            out.println(Utils.header("Error", request));
            out.println("<div class='container text-center'><h3 style='color:red;'>El formato del ID es incorrecto.</h3></div>");
            out.println(Utils.footer());
        } catch (Exception e) {
            e.printStackTrace();
            PrintWriter out = response.getWriter();
            out.println(Utils.header("Error del Servidor", request));
            out.println("<div class='container text-center'><h3 style='color:red;'>Ocurrio un error al cargar los datos.</h3></div>");
            out.println(Utils.footer());
        }
    }
}