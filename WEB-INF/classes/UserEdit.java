import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;

public class UserEdit extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        res.setContentType("text/html; charset=UTF-8");
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            res.sendRedirect("login.html");
            return;
        }

        String email = (String) session.getAttribute("userEmail");

        Connection connection = ConnectionUtils.getConnection(getServletConfig());

        UserData user = UserData.getUserByEmail(connection, email);

        if (user == null) {
            out.println("Error: usuario no encontrado.");
            ConnectionUtils.close(connection);
            return;
        }

        out.println(Utils.header("Editar mi perfil", req));

        out.println("<form action='UserUpdate' method='GET'>");
        out.println("<table border='1'>");

        out.println("<tr><td>ID</td><td><input name='id' value='" + user.id + "' readonly></td></tr>");
        out.println("<tr><td>Username</td><td><input name='username' value='" + user.username + "'></td></tr>");
        out.println("<tr><td>Email</td><td><input name='email' value='" + user.email + "' readonly></td></tr>");
        out.println("<tr><td>Password</td><td><input name='password' value='" + user.password + "'></td></tr>");
        out.println("<tr><td>Phone</td><td><input name='phone' value='" + user.phone + "'></td></tr>");
        out.println("<tr><td>Role</td><td><input name='role' value='" + user.role + "' readonly></td></tr>");

        out.println("</table>");
        out.println("<input type='submit' value='Actualizar'>");
        out.println("</form>");

        out.println(Utils.footer());
        out.close();

        ConnectionUtils.close(connection);
    }
}
