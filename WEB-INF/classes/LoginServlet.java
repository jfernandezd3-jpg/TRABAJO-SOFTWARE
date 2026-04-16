import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Recoger datos del formulario
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // 2. Conectar a la BD Access
            Connection conn = ConnectionUtils.getConnection(getServletConfig());
            
            if (conn != null) {
                // 3. Consultar la tabla users (ajusta los nombres de las columnas si en tu foto se llaman diferente)
                String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                
                ResultSet rs = pstmt.executeQuery();

                // 4. ¿Existe el usuario?
                if (rs.next()) {
                    // ¡Login correcto! 
                    HttpSession session = request.getSession();
                    
                    // Guardamos el email en la sesión. (Si en tu tabla tienes un campo 'username', podrías guardar ese)
                    session.setAttribute("userEmail", email); 
                    
                    // Redirigimos al EjemploServlet (la lista de torneos) para ver el menú cambiado
                    response.sendRedirect("EjemploServlet");
                    
                } else {
                    // Login fallido
                    // Le pasamos el request al Utils.header para que sepa si hay sesión (aquí no la hay)
                    out.println(Utils.header("Error de Login", request));
                    out.println("<div style='text-align: center;'>");
                    out.println("<p style='color: red; font-weight: bold;'>Email o contraseña incorrectos.</p>");
                    out.println("<a href='login.html'><button style='width:auto;'>Volver a intentar</button></a>");
                    out.println("</div>");
                    out.println(Utils.footer());
                }

                rs.close();
                pstmt.close();
                ConnectionUtils.close(conn);
            }
        } catch (Exception e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }
}