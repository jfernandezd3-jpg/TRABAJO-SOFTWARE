import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Esta anotación es la URL donde podrás ver este servlet en tu navegador
@WebServlet("/EjemploServlet")
public class EjemploServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Configurar el tipo de respuesta a HTML y codificación para tildes/eñes
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 2. Usar tu Utils.java para pintar la parte de arriba de la página
        out.println(Utils.header("Lista de Torneos desde Access"));

        // 3. Conectar a la base de datos
        try {
            // Usamos tu método ConnectionUtils que busca el archivo en el servidor
            Connection conn = ConnectionUtils.getConnection(getServletConfig());
            
            if (conn == null) {
                out.println("<p style='color:red; text-align:center;'><b>Error:</b> No se pudo conectar a TournamentHub1.accdb. ¿Está el archivo en la carpeta correcta?</p>");
            } else {
                // Mensaje de éxito
                out.println("<p style='text-align:center; color:green; font-weight:bold;'>¡Conexión exitosa a la base de datos!</p>");
                
                // 4. Preparar la consulta a la tabla 'tournaments'
                String sql = "SELECT id, name, location, date, entry_price FROM tournaments";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                // 5. Imprimir la cabecera de la tabla HTML
                out.println("<table>");
                out.println("<tr>");
                out.println("<th>ID</th>");
                out.println("<th>Nombre del Torneo</th>");
                out.println("<th>Ubicación</th>");
                out.println("<th>Fecha Inicio</th>");
                out.println("<th>Precio (€)</th>");
                out.println("</tr>");

                boolean hayDatos = false;
                
                // 6. Recorrer los resultados fila por fila
                while (rs.next()) {
                    hayDatos = true;
                    out.println("<tr>");
                    out.println("<td>" + rs.getInt("id") + "</td>");
                    out.println("<td>" + rs.getString("name") + "</td>");
                    out.println("<td>" + rs.getString("location") + "</td>");
                    
                    // Extraemos la fecha (si puede ser nula, la manejamos)
                    java.sql.Date fecha = rs.getDate("date");
                    out.println("<td>" + (fecha != null ? fecha.toString() : "Sin fecha") + "</td>");
                    
                    out.println("<td>" + rs.getDouble("entry_price") + "</td>");
                    out.println("</tr>");
                }

                out.println("</table>");

                // Si la tabla de Access está vacía, mostramos un mensaje
                if (!hayDatos) {
                    out.println("<p style='text-align:center; margin-top:20px; color:#555;'>No hay torneos registrados. ¡Añade algunos datos en Access para verlos aquí!</p>");
                }

                // 7. Limpiar y cerrar conexiones
                rs.close();
                stmt.close();
                ConnectionUtils.close(conn);
            }
        } catch (Exception e) {
            // Si hay un error SQL (por ejemplo, la tabla no se llama así), lo mostramos
            out.println("<p style='color:red; text-align:center;'>Error en la consulta: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }

        // 8. Pintar el final de la página
        out.println(Utils.footer());
    }
}