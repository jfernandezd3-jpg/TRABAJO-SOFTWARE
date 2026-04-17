import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/BSelectTournament")
public class BSelectTournament extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // Nos conectamos a la BD usando la clase de utilidades
        this.connection = ConnectionUtils.getConnection(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");

        try (PrintWriter out = response.getWriter()) {
            
            // 1. Cabecera dinámica
            out.println(Utils.header(" ", request));

            // 2. Títulos y estructura HTML con las clases de tu CSS
            out.println("<h1 style='margin-top: 40px; text-align: center; color: #1a4f2c;'>DETALLES DEL TORNEO</h1>");
            out.println("<p style='text-align:center; margin-bottom: 30px;'>Selecciona un torneo para ver toda su informacion y ubicacion</p>");

            out.println("<div class='container'>");
            
            // 3. Formulario que envía el ID a BTournamentInfo
            out.println("  <form action='BTournamentInfo' method='GET'>");
            out.println("    <label for='id'>Selecciona el torneo deseado:</label>");
            out.println("    <select name='id' id='id' required>");
            out.println("      <option value='' disabled selected>-- Haz clic para elegir --</option>");

            // 4. LA MAGIA DINAMICA: Traemos los torneos de la base de datos usando TU clase
            Vector<BTournamentData> listaTorneos = BTournamentData.getTournamentList(connection);

            if (listaTorneos.isEmpty()) {
                out.println("      <option value='' disabled>No hay torneos disponibles en la base de datos</option>");
            } else {
                // Hacemos un bucle para pintar una opción por cada torneo
                for (int i = 0; i < listaTorneos.size(); i++) {
                    BTournamentData t = listaTorneos.elementAt(i);
                    // Value será el ID oculto, y el texto será el nombre y la ubicación
                    out.println("      <option value='" + t.id + "'>" + t.tournament + " (" + t.location + ")</option>");
                }
            }

            out.println("    </select>");
            
            out.println("    <button type='submit' class='btn' style='margin-top: 10px;'>Ver Informacion</button>");
            out.println("  </form>");

            out.println("  <div class='text-center' style='margin-top: 20px;'>");
            out.println("    <a href='index.html'>Volver al Inicio</a>");
            out.println("  </div>");

            out.println("</div>");

            // 5. Pie de página
            out.println(Utils.footer());

        } catch (Exception e) {
            e.printStackTrace();
            PrintWriter out = response.getWriter();
            out.println(Utils.header("Error del Servidor", request));
            out.println("<div class='container text-center'><h3 style='color:red;'>Ocurrio un error al cargar la lista de torneos.</h3></div>");
            out.println(Utils.footer());
        }
    }
}