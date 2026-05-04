import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// JAIME FERNANDEZ DE BETOÑO

public class BTournamentMap extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            
            out.println(Utils.header("Mapa de Torneos", request));

            out.println("<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css' />");
            out.println("<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>");
            out.println("<script src='js/mapa.js'></script>");
            
            out.println("<style>");
            out.println("  .map-card { max-width: 1000px; margin: 40px auto; padding: 20px; background-color: #fff; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }");
            out.println("  #map { height: 600px; border-radius: 8px; z-index: 1; }"); 
            out.println("</style>");

            out.println("<div class='map-card'>");
            out.println("  <div id='map'></div>");
            out.println("</div>");

            out.println("<script>");
            out.println("  var map = L.map('map').setView([40.4168, -3.7038], 6);");
            out.println("  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19, attribution: '&copy; OpenStreetMap' }).addTo(map);");

            try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
                if (conn != null) {
                    Vector<BTournamentData> torneos = BTournamentData.getTournamentsWithCoordinates(conn);

                    for (int i = 0; i < torneos.size(); i++) {
                        BTournamentData t = torneos.elementAt(i);
                        
                        out.println("  var marker_" + t.id + " = L.marker([" + t.latitude + ", " + t.longitude + "]).addTo(map);");
                        out.println("  cargarPopup(marker_" + t.id + ", " + t.id + ", map);");
                    }
                }
            } catch (Exception e) {
                out.println("console.error('Error cargando los torneos: " + e.getMessage() + "');");
            }

            out.println("</script>");
            out.println(Utils.footer());
        }
    }
}