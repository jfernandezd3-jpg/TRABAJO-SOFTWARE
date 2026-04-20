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

public class BLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = ConnectionUtils.getConnection(getServletConfig());
            
            if (conn != null) {
                String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    // Login correcto
                    HttpSession session = request.getSession();
                    
                    // Guardar el email en la sesión
                    session.setAttribute("userEmail", email); 
                    
                    // Redirigir al inicio
                    response.sendRedirect("index.html");
                    
                } else {
                    // Login fallido
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