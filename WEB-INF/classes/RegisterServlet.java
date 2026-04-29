import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterServlet extends HttpServlet {
    Connection connection;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        connection = ConnectionUtils.getConnection(config);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        DatosRegistrarse user = new DatosRegistrarse(
            req.getParameter("username"),
            req.getParameter("email"),
            req.getParameter("password"),
            req.getParameter("phone")
        );

        // --- NUEVO: si viene format=json respondemos con JSON (llamada AJAX) ---
        String format = req.getParameter("format");
        if ("json".equals(format)) {
            res.setContentType("application/json; charset=UTF-8");
            PrintWriter out = res.getWriter();
            try {
                int n = DatosRegistrarse.insertUser(connection, user);
                if (n > 0) {
                    out.println("{\"success\": true, \"message\": \"Usuario registrado correctamente.\"}");
                } else {
                    out.println("{\"success\": false, \"message\": \"No se pudo registrar el usuario.\"}");
                }
            } catch (Exception e) {
                out.println("{\"success\": false, \"message\": \"Error: " + e.getMessage() + "\"}");
            }
            out.close();
            return;
        }
        // --- FIN NUEVO ---

        // Comportamiento original: insertar y redirigir
        int n = DatosRegistrarse.insertUser(connection, user);
        res.sendRedirect("login.html");
    }
}
