import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EjemploServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // Imprime la cabecera (con tu CSS y menú)
        out.println(Utils.header("Resultado del Registro"));
        
        // Tu contenido dinámico
        out.println("<p style='text-align:center; color:green;'>¡Te has registrado con éxito en el torneo!</p>");
        out.println("<div style='text-align:center;'><a href='search.html'>Volver a buscar</a></div>");
        
        // Imprime el final de la página
        out.println(Utils.footer());
    }
}