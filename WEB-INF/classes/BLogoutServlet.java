import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/BLogoutServlet")
public class BLogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Recuperar la sesión actual (sin crear una nueva si no existe)
        HttpSession session = request.getSession(false);
        
        // 2. Si existe, la destruimos
        if (session != null) {
            session.invalidate();
        }
        
        // 3. Mandamos al usuario de vuelta a la página principal
        response.sendRedirect("index.html");
    }
}