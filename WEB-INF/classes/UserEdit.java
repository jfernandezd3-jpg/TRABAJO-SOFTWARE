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
            out.println(Utils.header("Editar mi perfil", req));
            out.println("<div class='container'><p style='color:red;'>Error: usuario no encontrado.</p></div>");
            out.println(Utils.footer());
            ConnectionUtils.close(connection);
            return;
        }

        out.println(Utils.header("Editar mi perfil", req));

        out.println("<div class='container'>");

        out.println("<form id='editForm'>");

        out.println("  <label for='username'>Username</label>");
        out.println("  <input type='text' id='username' name='username' value='" + user.username + "'>");

        out.println("  <label for='email'>Email</label>");
        out.println("  <input type='email' id='email' name='email' value='" + user.email + "' readonly"
                  + " style='background:#eee; cursor:not-allowed;' title='El email no se puede cambiar'>");

        out.println("  <label for='password'>Nueva password</label>");
        out.println("  <input type='password' id='password' name='password' placeholder='Deja en blanco para no cambiarla'>");

        out.println("  <label for='phone'>Telefono</label>");
        out.println("  <input type='text' id='phone' name='phone' value='" + user.phone + "'>");

        out.println("  <button type='submit'>Guardar cambios</button>");
        out.println("</form>");

        out.println("<div id='resultado' style='margin-top:12px; font-weight:bold; text-align:center;'></div>");

        out.println("</div>"); // cierra .container

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