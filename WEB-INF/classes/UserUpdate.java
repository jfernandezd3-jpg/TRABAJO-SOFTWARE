import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Connection;

@SuppressWarnings("serial")
public class UserUpdate extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html; charset=UTF-8");
        PrintWriter out = res.getWriter();

        Connection connection = ConnectionUtils.getConnection(getServletConfig());

        int id = Integer.parseInt(req.getParameter("id"));
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String phone = req.getParameter("phone");
        String role = req.getParameter("role");

        UserData user = new UserData(id, username, email, password, phone, role);

        int n = UserData.updateUser(connection, user);

        out.println(Utils.header("Actualizar Usuario", req));

        if (n > 0) {
            out.println("<p style='color:green; text-align:center;'>Usuario actualizado correctamente.</p>");
        } else {
            out.println("<p style='color:red; text-align:center;'>Error al actualizar usuario.</p>");
        }

        out.println("<a href='UserList'>Volver a la lista</a>");
        out.println(Utils.footer());
        out.close();
    }
}
