import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;

@SuppressWarnings("serial")
public class UserEdit extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html; charset=UTF-8");
        PrintWriter out = res.getWriter();

        Connection connection = ConnectionUtils.getConnection(getServletConfig());

        out.println(Utils.header("Editar Usuario"));

        String idStr = req.getParameter("id");
        UserData user = UserData.getUser(connection, idStr);

        out.println("<form action='UserUpdate' method='GET'>");
        out.println("<table border='1'>");

        out.println("<tr><td>ID</td>");
        out.println("<td><input name='id' value='" + user.id + "' readonly></td></tr>");

        out.println("<tr><td>Username</td>");
        out.println("<td><input name='username' value='" + user.username + "'></td></tr>");

        out.println("<tr><td>Email</td>");
        out.println("<td><input name='email' value='" + user.email + "'></td></tr>");

        out.println("<tr><td>Password</td>");
        out.println("<td><input name='password' value='" + user.password + "'></td></tr>");

        out.println("<tr><td>Phone</td>");
        out.println("<td><input name='phone' value='" + user.phone + "'></td></tr>");

        out.println("<tr><td>Role</td>");
        out.println("<td><input name='role' value='" + user.role + "'></td></tr>");

        out.println("</table>");
        out.println("<input type='submit' value='Actualizar'>");
        out.println("</form>");

        out.println(Utils.footer());
        out.close();
    }
}
