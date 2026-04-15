import java.io.IOException;
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

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
        res.setContentType("text/html");

        DatosRegistrarse user = new DatosRegistrarse(
            req.getParameter("username"),
            req.getParameter("email"),
            req.getParameter("password"),
            req.getParameter("phone")
        );

        int n = DatosRegistrarse.insertUser(connection, user);
        res.sendRedirect("login.html");
    }
}
