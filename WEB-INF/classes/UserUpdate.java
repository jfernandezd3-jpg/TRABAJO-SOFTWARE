import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;

public class UserUpdate extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            // Si es llamada AJAX, no podemos hacer sendRedirect, devolvemos JSON de error
            String format = req.getParameter("format");
            if ("json".equals(format)) {
                res.setContentType("application/json; charset=UTF-8");
                res.getWriter().println("{\"success\": false, \"message\": \"Sesión expirada. Inicia sesión de nuevo.\"}");
            } else {
                res.sendRedirect("login.html");
            }
            return;
        }

        String email = (String) session.getAttribute("userEmail");
        Connection connection = ConnectionUtils.getConnection(getServletConfig());
        UserData user = UserData.getUserByEmail(connection, email);

        // --- NUEVO: soporte AJAX/JSON ---
        String format = req.getParameter("format");
        if ("json".equals(format)) {
            res.setContentType("application/json; charset=UTF-8");
            PrintWriter out = res.getWriter();
            if (user == null) {
                out.println("{\"success\": false, \"message\": \"Usuario no encontrado.\"}");
                out.close();
                ConnectionUtils.close(connection);
                return;
            }
            user.username = req.getParameter("username");
            user.password = req.getParameter("password");
            user.phone    = req.getParameter("phone");
            int n = UserData.updateUser(connection, user);
            if (n > 0) {
                out.println("{\"success\": true, \"message\": \"Datos actualizados correctamente.\"}");
            } else {
                out.println("{\"success\": false, \"message\": \"No se pudo actualizar.\"}");
            }
            out.close();
            ConnectionUtils.close(connection);
            return;
        }
        // --- FIN NUEVO ---

        // Comportamiento original sin cambios
        res.setContentType("text/html; charset=UTF-8");
        PrintWriter out = res.getWriter();

        if (user == null) {
            out.println("Error: usuario no encontrado.");
            ConnectionUtils.close(connection);
            return;
        }

        user.username = req.getParameter("username");
        user.password = req.getParameter("password");
        user.phone    = req.getParameter("phone");

        int n = UserData.updateUser(connection, user);

        out.println(Utils.header("Perfil actualizado", req));
        if (n > 0) {
            out.println("<p style='color:green;'>Datos actualizados correctamente.</p>");
        } else {
            out.println("<p style='color:red;'>No se pudo actualizar.</p>");
        }
        out.println("<a href='UserEdit'>Volver</a>");
        out.println(Utils.footer());
        out.close();
        ConnectionUtils.close(connection);
    }
}
