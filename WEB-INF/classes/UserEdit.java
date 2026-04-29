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

        out.println("<form id='editForm'>");
        out.println("<table border='1'>");
        out.println("<tr><td>Username</td><td><input id='username' name='username' value='" + user.username + "'></td></tr>");
        out.println("<tr><td>Email</td><td><input name='email' value='" + user.email + "' readonly></td></tr>");
        out.println("<tr><td>Password</td><td><input id='password' name='password' value='" + user.password + "'></td></tr>");
        out.println("<tr><td>Phone</td><td><input id='phone' name='phone' value='" + user.phone + "'></td></tr>");
        out.println("</table>");
        out.println("<input type='submit' value='Actualizar'>");
        out.println("</form>");
        out.println("<div id='resultado' style='margin-top:12px; font-weight:bold;'></div>");

        // Todo el bloque JS en un solo println para evitar problemas de parsing
        out.println("<script>" +
            "document.getElementById('editForm').addEventListener('submit', function(e) {" +
            "  e.preventDefault();" +
            "  var params = new URLSearchParams({" +
            "    username: document.getElementById('username').value," +
            "    password: document.getElementById('password').value," +
            "    phone:    document.getElementById('phone').value," +
            "    format:   'json'" +
            "  });" +
            "  var div = document.getElementById('resultado');" +
            "  div.style.color = 'gray';" +
            "  div.textContent = 'Guardando...';" +
            "  fetch('UserUpdate?' + params.toString())" +
            "    .then(function(r) { return r.json(); })" +
            "    .then(function(data) {" +
            "      div.style.color = data.success ? 'green' : 'red';" +
            "      div.textContent = data.message;" +
            "    })" +
            "    .catch(function() {" +
            "      div.style.color = 'red';" +
            "      div.textContent = 'Error de conexión con el servidor.';" +
            "    });" +
            "});" +
            "</script>");

        out.println(Utils.footer());
        out.close();
        ConnectionUtils.close(connection);
    }
}
