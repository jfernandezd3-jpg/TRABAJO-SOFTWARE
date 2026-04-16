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

@WebServlet("/RegisterTournamentServlet")
public class RegisterTournamentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // Verificar que el usuario esté logueado
        if (session == null || session.getAttribute("userEmail") == null) {
            response.sendRedirect("login.html");
            return;
        }

        String email = (String) session.getAttribute("userEmail");
        int tId = Integer.parseInt(request.getParameter("tournamentId"));
        
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = ConnectionUtils.getConnection(getServletConfig())) {
            if (conn != null) {
                // 1. Obtener ID de usuario
                int uId = -1;
                String sqlU = "SELECT id FROM Users WHERE email = ?";
                PreparedStatement psU = conn.prepareStatement(sqlU);
                psU.setString(1, email);
                ResultSet rsU = psU.executeQuery();
                if (rsU.next()) uId = rsU.getInt("id");

                // 2. Comprobar si ya existe la inscripción
                String sqlCheck = "SELECT * FROM Registrations WHERE user_id = ? AND tournament_id = ?";
                PreparedStatement psC = conn.prepareStatement(sqlCheck);
                psC.setInt(1, uId);
                psC.setInt(2, tId);
                if (psC.executeQuery().next()) {
                    imprimirRespuesta(out, "Ya estás inscrito en este torneo.", "orange", request);
                    return;
                }

                // 3. Validar capacidad
                String sqlCap = "SELECT current_participants, max_participants FROM Tournaments WHERE id = ?";
                PreparedStatement psCap = conn.prepareStatement(sqlCap);
                psCap.setInt(1, tId);
                ResultSet rsCap = psCap.executeQuery();

                if (rsCap.next()) {
                    int actual = rsCap.getInt("current_participants");
                    int maximo = rsCap.getInt("max_participants");

                    if (actual < maximo) {
                        conn.setAutoCommit(false);
                        try {
                            // Insertar registro
                            String sqlIns = "INSERT INTO Registrations (user_id, tournament_id, status) VALUES (?, ?, 'accepted')";
                            PreparedStatement psI = conn.prepareStatement(sqlIns);
                            psI.setInt(1, uId);
                            psI.setInt(2, tId);
                            psI.executeUpdate();

                            // Actualizar contador
                            String sqlUpd = "UPDATE Tournaments SET current_participants = current_participants + 1 WHERE id = ?";
                            PreparedStatement psUpd = conn.prepareStatement(sqlUpd);
                            psUpd.setInt(1, tId);
                            psUpd.executeUpdate();

                            conn.commit();
                            // Si todo sale bien, volvemos al index
                            response.sendRedirect("index.html?registration=success");
                        } catch (Exception e) {
                            conn.rollback();
                            throw e;
                        }
                    } else {
                        imprimirRespuesta(out, "El torneo está lleno.", "red", request);
                    }
                } else {
                    imprimirRespuesta(out, "El ID del torneo no existe.", "red", request);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.html");
        }
    }

    // Método para mostrar errores usando vuestro diseño sin usar JSP
    private void imprimirRespuesta(PrintWriter out, String mensaje, String color, HttpServletRequest req) {
        out.println(Utils.header("Atención", req));
        out.println("<div style='text-align:center; padding:20px;'>");
        out.println("<h3 style='color:" + color + ";'>" + mensaje + "</h3>");
        out.println("<a href='inscripcion.html'><button>Volver a intentarlo</button></a>");
        out.println("</div>");
        out.println(Utils.footer());
    }
}