import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ListTournamentsServlet")
public class ListTournamentsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            response.sendRedirect("login.html");
            return;
        }

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><style>");
        out.println("body { font-family: 'Segoe UI', Arial; background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); min-height: 100vh; display: flex; justify-content: center; align-items: center; margin: 0; }");
        out.println(".container { background: white; padding: 40px; border-radius: 20px; box-shadow: 0 15px 35px rgba(0,0,0,0.2); width: 90%; max-width: 500px; text-align: center; }");
        out.println("h1 { color: #2d3436; font-size: 28px; margin-bottom: 30px; border-bottom: 3px solid #0984e3; display: inline-block; padding-bottom: 10px; }");
        out.println(".tournament-card { background: #f9f9f9; border-left: 6px solid #0984e3; margin: 15px 0; padding: 20px; border-radius: 8px; text-align: left; transition: all 0.3s ease; }");
        out.println(".tournament-card:hover { transform: scale(1.03); background: #edf2f7; }");
        out.println(".tournament-name { font-size: 18px; font-weight: bold; color: #2d3436; text-transform: uppercase; }");
        out.println(".back-link { display: inline-block; margin-top: 25px; text-decoration: none; color: #0984e3; font-weight: bold; font-size: 16px; }");
        out.println("</style></head><body>");

        out.println("<div class='container'><h1>Torneos Activos</h1>");

        try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
            String sql = "SELECT * FROM tournaments"; 
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                boolean hayDatos = false;

                while (rs.next()) {
                    hayDatos = true;
                    String nombreTorneo = "Sin nombre";
                    
                    // Buscamos dinámicamente la primera columna que sea texto (VARCHAR/String)
                    for (int i = 1; i <= columnCount; i++) {
                        String colName = rsmd.getColumnName(i);
                        // Si la columna se llama name, nombre o es un String largo, la usamos
                        if (colName.toLowerCase().contains("name") || colName.toLowerCase().contains("nom")) {
                            nombreTorneo = rs.getString(i);
                            break;
                        }
                    }

                    out.println("<div class='tournament-card'>");
                    out.println("<div class='tournament-name'>" + nombreTorneo + "</div>");
                    out.println("</div>");
                }
                
                if (!hayDatos) {
                    out.println("<p>No hay torneos disponibles en este momento.</p>");
                }
            }
        } catch (Exception e) {
            out.println("<p style='color:red;'>Error al acceder a los datos: " + e.getMessage() + "</p>");
        }

        out.println("<a href='index.html' class='back-link'>Volver al Inicio</a></div></body></html>");
    }
}