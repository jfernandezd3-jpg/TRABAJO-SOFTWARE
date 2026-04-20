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

public class BSelectTournament extends HttpServlet {
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
            
            out.println(Utils.header(" ", request));

            out.println("<h1 style='margin-top: 40px; text-align: center; color: #1a4f2c;'>DETALLES DEL TORNEO</h1>");
            out.println("<p style='text-align:center; margin-bottom: 30px;'>Selecciona un torneo para ver toda su informacion y ubicacion</p>");

            out.println("<div class='container'>");
            
            out.println("  <form action='BTournamentInfo' method='GET'>");
            out.println("    <label for='id'>Selecciona el torneo deseado:</label>");
            out.println("    <select name='id' id='id' required>");
            out.println("      <option value='' disabled selected>-- Haz clic para elegir --</option>");

            Vector<BTournamentData> listaTorneos = BTournamentData.getTournamentList(connection);

            if (listaTorneos.isEmpty()) {
                out.println("      <option value='' disabled>No hay torneos disponibles en la base de datos</option>");
            } else {
                for (int i = 0; i < listaTorneos.size(); i++) {
                    BTournamentData t = listaTorneos.elementAt(i);
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