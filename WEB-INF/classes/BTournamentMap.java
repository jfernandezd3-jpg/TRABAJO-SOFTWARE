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
                        
                        out.println("  marker_" + t.id + ".on('click', function(e) {");
                        out.println("      var popup = L.popup().setLatLng(e.latlng).setContent('<i>Cargando info...</i>').openOn(map);");
                        
                        out.println("      var request = new XMLHttpRequest();");
                        out.println("      request.open('GET', 'BTournamentPopupAjax?id=" + t.id + "', true);");
                        
                        out.println("      request.onload = function() {");
                        out.println("          if (request.status >= 200 && request.status < 400) {");
                        out.println("              var resp = request.responseText;");
                        out.println("              var data = JSON.parse(resp);");
                        
                        out.println("              if(data.error) {");
                        out.println("                  popup.setContent('<b style=\"color:red;\">Error: ' + data.error + '</b>');");
                        out.println("              } else {");
                        out.println("                  var html = \"<div style='text-align: center;'>\" + ");
                        out.println("                             \"<b style='color: #1a4f2c; font-size: 15px;'>\" + data.name + \"</b><br>\" +");
                        out.println("                             \"<span style='color: #666;'>Lugar: \" + data.location + \"</span><br>\" +");
                        out.println("                             \"<span style='font-weight: bold; color: #f39c12;'>Precio: \" + data.price + \" &euro;</span><br><br>\" +");
                        out.println("                             \"<a href='BTournamentInfo?id=\" + data.id + \"' class='btn' style='padding: 6px 12px; font-size: 13px; color: white !important; text-decoration: none;'>Ver Detalles</a>\" +");
                        out.println("                             \"</div>\";");
                        out.println("                  popup.setContent(html);");
                        out.println("              }");
                        out.println("          } else {");
                        out.println("              popup.setContent('<b style=\"color:red;\">Error del servidor</b>');");
                        out.println("          }");
                        out.println("      };");
                        
                        out.println("      request.send();");
                        
                        out.println("  });");
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