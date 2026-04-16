import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/BTournamentMap")
public class BTournamentMap extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");

        try (PrintWriter out = response.getWriter()) {
            
            // 1. Cabecera
            out.println(Utils.header("Mapa de Torneos", request));

            // 2. Importar recursos de Leaflet
            out.println("<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css' />");
            out.println("<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>");
            
            // 3. Estilos
            out.println("<style>");
            out.println("  .map-card { max-width: 1000px; margin: 40px auto; padding: 20px; background-color: #fff; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }");
            out.println("  #map { height: 600px; border-radius: 8px; z-index: 1; }"); 
            out.println("  .page-title { text-align: center; color: #333; margin-bottom: 20px; }");
            out.println("</style>");

            // 4. HTML
            out.println("<div class='map-card'>");
            out.println("  <h2 class='page-title'>Mapa de Torneos de Espana</h2>");
            out.println("  <div id='map'></div>");
            out.println("</div>");

            // 5. JAVASCRIPT INICIAL
            out.println("<script>");
            out.println("  var map = L.map('map').setView([40.4168, -3.7038], 6);");
            out.println("  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19, attribution: '&copy; OpenStreetMap' }).addTo(map);");

            // 6. CONEXIÓN A BASE DE DATOS Y BUCLE DE PINES
            Connection conn = null;
            try {
                conn = ConnectionUtils.getConnection(getServletConfig());
                if (conn != null) {
                    // Solo cogemos torneos que tengan latitud y longitud rellenadas
                    String sql = "SELECT ID, tournament, location, entry_price, latitude, longitude " +
                                 "FROM tournaments WHERE latitude IS NOT NULL AND longitude IS NOT NULL";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();

                    // Por cada torneo, inyectamos una línea de JavaScript para crear su marcador
                    while (rs.next()) {
                        String name = rs.getString("tournament").replace("'", "\\'"); // Evita que una comilla simple rompa el JS
                        String loc = rs.getString("location").replace("'", "\\'");
                        double price = rs.getDouble("entry_price");
                        double lat = rs.getDouble("latitude");
                        double lng = rs.getDouble("longitude");

                        // Creamos el marcador dinámico
                        out.println("  L.marker([" + lat + ", " + lng + "]).addTo(map)");
                        out.println("      .bindPopup('<b>" + name + "</b><br>Lugar: " + loc + "<br>Precio: " + price + " EUR');");
                    }
                    rs.close();
                    ps.close();
                }
            } catch (Exception e) {
                out.println("console.error('Error cargando la base de datos: " + e.getMessage() + "');");
            } finally {
                if (conn != null) {
                    try { conn.close(); } catch (Exception ignore) {}
                }
            }

            // Cerramos script y ponemos pie de página
            out.println("</script>");
            out.println(Utils.footer());
        }
    }
}