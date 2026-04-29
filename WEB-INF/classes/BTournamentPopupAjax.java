import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// JAIME FERNANDEZ DE BETONO

public class BTournamentPopupAjax extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String idStr = request.getParameter("id");
            
            if (idStr != null) {
                try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
                    int id = Integer.parseInt(idStr);
                    BTournamentData t = BTournamentData.getTournamentById(conn, id);
                    
                    if (t != null) {
                        String safeName = t.tournament.replace("\"", "\\\"");
                        String safeLoc = t.location.replace("\"", "\\\"");
                        
                        String json = "{";
                        json += "\"id\": " + t.id + ",";
                        json += "\"name\": \"" + safeName + "\",";
                        json += "\"location\": \"" + safeLoc + "\",";
                        json += "\"price\": " + t.entry_price;
                        json += "}";
                        
                        out.print(json);
                    } else {
                        out.print("{\"error\": \"No encontrado\"}");
                    }
                } catch (Exception e) {
                    out.print("{\"error\": \"Fallo de BD\"}");
                }
            }
        }
    }
}